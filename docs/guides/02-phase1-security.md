# 阶段 1 · 安全止血

> 目标：把**已经失效的安全边界**修回来，并写出本项目的**第一个回归测试**。
> 预计：3–5 天。前置：[阶段 0](./01-phase0-baseline.md) 已验收通过。

## 0. 验收标准

1. 普通用户 token 访问 `/admin/**` 返回 403（**有测试证明**）；
2. 未登录访问受保护接口返回 401；
3. `POST /menu/save` 不再存在（或已归入 `/admin/menu/save`）；
4. 登出后的 token 立即失效，且 Redis 挂掉时**不会放行**（fail-closed）；
5. `spring.main.allow-circular-references` 已删除，应用仍能启动；
6. `mvn -B test` 从 "No tests to run" 变成 "Tests run: N"。

---

## 1. 前置知识：Spring Security 6 的两条过滤器链

这是本阶段**最重要**的概念，理解了它，所有漏洞都一目了然。

```
HTTP 请求
   │
   ▼
FilterChainProxy  ── 按 @Order 升序找【第一个 securityMatcher 命中的链】，命中即用，不再看后面的
   │
   ├── Order(1) AdminSecurityConfig  securityMatcher("/admin/**")   ── 管理端
   └── Order(2) UserSecurityConfig   securityMatcher("/**")          ── 其余全部
```

### 三个关键结论

1. **`securityMatcher` 决定"这条链负责哪些 URL"**；请求只走**第一条**命中的链，不会叠加。
   所以 `/admin/**` 永远不会走到 user 链，反之亦然。

2. **从不调用 `authorizeHttpRequests(...)`，就等于没有任何授权。**
   Spring Security 的授权是由 `AuthorizationFilter` 完成的；只有当你配置了授权规则，这个过滤器才会被注册。
   `UserSecurityConfig` 里的 `authorizeHttpRequests` 被整段注释掉 ⇒ **user 链一个授权规则都没有** ⇒
   所有用户端接口实际上"只要过滤器不拦就放行"。这就是 P0-3。

3. **`AuthorizationManager` 是 Spring Security 6 的授权决策接口**（旧的 `AccessDecisionManager` 已淘汰）。
   `DynamicAuthorizationManager` 就是管理端链的决策器：它返回 `AuthorizationDecision(true/false)`。
   你现在的 bug 在 `check()`：

```java
if (hasAdmin(auth)) {
    return adminDecision(auth, context);
} else {
    return new AuthorizationDecision(true);   // ← 普通用户：直接放行！越权
}
```

> 记住这句话：**"默认放行"是安全设计里最危险的词。**

---

## Step 1.1 先写会失败的测试（复现越权）

**永远先写能复现 bug 的测试。** 这一步会让"测试从红到绿"，比任何文档都有说服力。

新建 `blog-service/src/test/java/com/jxcia/blog/service/security/DynamicAuthorizationManagerTest.java`：

```java
package com.jxcia.blog.service.security;

import com.jxcia.blog.blog.security.authorization.DynamicAuthorizationManager;
import com.jxcia.blog.blog.security.metadata.DynamicSecurityMetadataSource;
import com.jxcia.blog.blog.security.service.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DynamicAuthorizationManagerTest {

    private RequestAuthorizationContext adminRequest(String uri) {
        return new RequestAuthorizationContext(new MockHttpServletRequest("GET", uri));
    }

    private UsernamePasswordAuthenticationToken userAuth() {
        CustomUserDetails principal = CustomUserDetails.builder()
                .id(1)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    /** P0-2 回归：普通用户绝不能访问管理端接口 */
    @Test
    void nonAdminUserMustBeDeniedOnAdminEndpoint() {
        DynamicSecurityMetadataSource metadataSource = mock(DynamicSecurityMetadataSource.class);
        DynamicAuthorizationManager manager = new DynamicAuthorizationManager(metadataSource);

        var decision = manager.check(this::userAuth, adminRequest("/admin/list"));

        assertThat(decision.isGranted())
                .as("普通用户访问 /admin/** 必须被拒绝")
                .isFalse();
    }
}
```

> 注意：这个测试假设 `DynamicAuthorizationManager` 已经改成**构造器注入**（见 Step 1.2）。
> 如果你先不想改，可以用 `ReflectionTestUtils.setField(manager, "dynamicSecurityMetadataSource", metadataSource)`。

运行它：

```powershell
mvn -B -pl blog-service -am test -Dtest=DynamicAuthorizationManagerTest
# 期望：FAIL —— 因为你现在的代码会放行。这就是"红"。
```

---

## Step 1.2 修复 `DynamicAuthorizationManager`

`blog-security/.../authorization/DynamicAuthorizationManager.java` 改成：

```java
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final DynamicSecurityMetadataSource metadataSource;

    // 构造器注入：依赖显式、可测试，不依赖 Spring 容器也能 new 出来
    public DynamicAuthorizationManager(DynamicSecurityMetadataSource metadataSource) {
        this.metadataSource = metadataSource;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                       RequestAuthorizationContext context) {
        Authentication auth = authenticationSupplier.get();

        // 1) 必须已认证，且主体是本系统的 CustomUserDetails
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return new AuthorizationDecision(false);
        }

        // 2) 本条链是管理端链：非管理员一律拒绝（修掉越权）
        if (!hasAdmin(auth)) {
            return new AuthorizationDecision(false);
        }

        // 3) 管理员仍需命中权限表
        return adminDecision(auth, context);
    }

    private boolean hasAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> ROLE_ADMIN.equals(a.getAuthority()));
    }

    private AuthorizationDecision adminDecision(Authentication auth, RequestAuthorizationContext context) {
        Collection<ConfigAttribute> required = metadataSource.getAllConfigAttributes(context.getRequest());
        if (CollectionUtils.isEmpty(required)) {
            return new AuthorizationDecision(true); // 未纳管接口：管理员可访问
        }
        AntPathMatcher matcher = new AntPathMatcher();
        for (ConfigAttribute attr : required) {
            for (GrantedAuthority granted : auth.getAuthorities()) {
                if (matcher.match(attr.getAttribute(), granted.getAuthority())) {
                    return new AuthorizationDecision(true);
                }
            }
        }
        return new AuthorizationDecision(false);
    }
}
```

同步改 `SecurityBeanConfig`：

```java
@Bean
public DynamicAuthorizationManager dynamicAuthorizationManager(DynamicSecurityMetadataSource metadataSource) {
    return new DynamicAuthorizationManager(metadataSource);
}
```

再跑测试 → **绿**。补两个用例（管理员有权限放行 / 管理员无权限拒绝），完整覆盖三种分支。

> 学习点：Spring Security 里 `AuthorizationDecision(false)` 会走到 `RestfulAccessDeniedHandler` → 403；
> 而"未认证"会走 `RestAuthenticationEntryPoint` → 401。两者不要混。

---

## Step 1.3 恢复用户端授权（P0-3）

打开 `UserSecurityConfig`，把注释掉的授权块换成**显式白名单 + 其余全部需要登录**：

```java
http.securityMatcher("/**")
    .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // CORS 预检
        // ↓↓↓ 只有这里列出的接口才免登录，其余一律需要登录 ↓↓↓
        .requestMatchers(
            "/user/login", "/user/save",
            "/user/verificationCode/**", "/user/resetCode/**", "/user/resetPassword",
            "/category/list",
            "/article/search", "/article/detail",
            "/comment/detail/**",
            "/ws/**"
        ).permitAll()
        .anyRequest().authenticated()
    )
    .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
    .exceptionHandling(ex -> ex
        .authenticationEntryPoint(restAuthenticationEntryPoint)   // 401
        .accessDeniedHandler(restfulAccessDeniedHandler))         // 403
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    .csrf(AbstractHttpConfigurer::disable);
```

### 白名单必须"逐个决定"，不能拍脑袋

请把 27 个 Controller 的接口过一遍，用这张表做判断（示例，你要补齐）：

| 接口 | 是否公开 | 理由 |
|------|----------|------|
| `POST /user/login`、`POST /user/save` | ✅ | 登录/注册本身 |
| `GET /category/list` | ✅ | 注册页要选分类 |
| `POST /article/search`、`GET /article/detail` | ✅ | 游客浏览文章 |
| `GET /comment/detail/**` | ✅ | 文章页展示评论 |
| `GET /user/detail/**` | ⚠️ 你决定 | 个人主页是否允许游客看 |
| `/user/**` 其余（点赞、关注、历史、消息、重置） | ❌ | 必须登录 |
| `/file/upload/image` | ❌ | 必须登录 |
| `/ws/**` | ⚠️ | 见下方警告 |

> ⚠️ **`/ws/**` 目前是 `setAllowedOrigins("*")` 且无鉴权**，邮件推送通道可能被任何人订阅。
> 阶段 1 先维持可用（WS 握手不方便带 `Authorization` 头），但**必须在阶段 6 加 token 校验**（查询参数或 STOMP CONNECT 帧）。
> 请在这行代码旁写一个 `// TODO(phase6)`，别让它被遗忘。

> 学习点：白名单是"**默认拒绝 + 显式放行**"。新增接口如果忘了加白名单，结果是 401 而不是裸奔——这才是安全的默认值。

---

## Step 1.4 堵住绕过管理端的写接口（P0-4）

`MenuController` 映射在 `/menu/save`，**不在 `/admin/**`**，所以管理端链管不到它，任何人可写菜单。
而 `/admin/menu/save`（`MenuManageController` + `MenuManageServiceImpl`）功能完全重复。

```powershell
# 删除重复且不安全的整套实现
git rm blog-service/src/main/java/com/jxcia/blog/service/controller/admin/MenuController.java
git rm blog-service/src/main/java/com/jxcia/blog/service/service/admin/MenuService.java
git rm blog-service/src/main/java/com/jxcia/blog/service/service/admin/impl/MenuServiceImpl.java
```

```powershell
# 确认没有别处引用（应该只剩 MenuManage* 与 MenuMapper）
Select-String -Path (Get-ChildItem -Recurse -File -Filter *.java blog-service/src | % FullName) -SimpleMatch 'MenuService'
```

> 规则：**所有管理端接口都必须位于 `/admin/**` 之下**。这是一条可以写进 ArchUnit 测试的硬规则（阶段 2 做）。

---

## Step 1.5 修正语义相反的接口

`CommentManageController` 的 `GET /admin/comment/delete/{id}` 实际是**查询详情**。改成：

```java
@GetMapping("/detail/{id}")
public Result<Comment> detail(@PathVariable Long id) { ... }
```

（真正的"删除评论"接口等阶段 3 按最终 API 设计再补，现在先不要造。）

---

## Step 1.6 令牌黑名单改为 fail-closed

`JwtAuthenticationFilter.isBlacklisted()` 现在 Redis 异常时 `return false`（放行已登出的 token）。改成：

```java
private boolean isBlacklisted(String jti) {
    try {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti));
    } catch (Exception e) {
        // fail-closed：宁可暂时拒绝，也不放行可能已登出的令牌
        log.error("Redis 不可用，无法校验令牌黑名单，拒绝本次请求。jti={}", jti, e);
        return true;
    }
}
```

**权衡（要理解，不要盲抄）**：

| 策略 | 安全性 | 可用性 |
|------|--------|--------|
| fail-open（现状） | ❌ Redis 一挂，登出形同虚设 | ✅ 不受影响 |
| fail-closed（改后） | ✅ | ❌ Redis 挂 = 全站需登录接口不可用 |

生产上更优雅的做法是**降低对黑名单的依赖**：access token 缩短到 15–30 分钟 + refresh token 轮换（阶段 6）。
现阶段先 fail-closed 并加监控告警即可。

---

## Step 1.7 移除 `allow-circular-references`

```yaml
# application.yml 删除这两行
spring:
  main:
    allow-circular-references: true
```

然后启动应用：

- **能起来** → 说明这个开关只是历史残留，删掉即可；
- **报 `The dependencies of some of the beans in the application context form a cycle`** →
  错误信息会直接画出环（`A ──> B ──> A`）。把环贴给我，我带你用四种标准手法之一打破它：
  抽接口、`@Lazy`、事件解耦、或上移编排到 application 层。

> 静态扫描显示 service 之间没有互调，所以很可能是安全链路里的环。别用 `@Lazy` 糊过去，那是掩盖问题。

---

## 2. 验收命令

```powershell
# ① 测试从 0 变有（阶段 1 至少 3 个用例）
mvn -B -pl blog-service -am test -Dtest='*AuthorizationManager*'
# 期望：Tests run: 3, Failures: 0

# ② 确认 /menu/save 已消失
Select-String -Recurse -Path blog-service/src -SimpleMatch '"/menu"'
# 期望：无输出

# ③ 确认授权块已启用且不再 allow-circular-references
Select-String -Path blog-service/src/main/resources/application.yml -SimpleMatch 'allow-circular-references'
Select-String -Path (Get-ChildItem -Recurse -File -Filter UserSecurityConfig.java blog-security/src | % FullName) -SimpleMatch 'anyRequest'
# 期望：第一条无输出；第二条有 anyRequest().authenticated()

# ④ 全量构建
mvn -B clean verify
```

手动冒烟（启动后）：

| 场景 | 期望 |
|------|------|
| 不带 token 访问 `/user/detail` | 401 |
| 普通用户 token 访问 `/admin/list` | **403** |
| 管理员 token 访问 `/admin/list` | 200 |
| 登出后用旧 token 访问 | 401/403 |
| 停掉 Redis 后用旧 token 访问 | 401/403（不是 200） |

---

## 3. 常见坑

| 现象 | 原因 / 处理 |
|------|-------------|
| 启用白名单后大量接口 401 | 说明它们本就需要登录，属于**预期**。把确实要公开的加进白名单即可，不要图省事写 `anyRequest().permitAll()` |
| 管理员访问 `/admin/xxx` 403 | 该 URL 在 `permission` 表里存在，但管理员角色没被分配该权限；去超管角色补映射 |
| 普通用户 200 而不是 403 | 检查 `AdminSecurityConfig` 的 `@Order` 是否仍是 1、`securityMatcher` 是否仍是 `/admin/**` |
| 测试里 `CustomUserDetails.builder()` 找不到 | 确认该类有 `@Builder`；没有就先用 `new CustomUserDetails(...)` |
| `mvn test` 不跑新测试 | 确认目录是 `blog-service/src/test/java/...`（现在整个仓库还没有 test 目录，你需要新建） |
| Redis 挂了整个站 401 | fail-closed 的代价。先接受，阶段 6 用短 token + 轮换 refresh 降低依赖 |

---

## 4. 完成后告诉我

1. `mvn -B -pl blog-service -am test` 的完整输出；
2. 冒烟表里 5 个场景的实际结果；
3. 如果 Step 1.7 报循环依赖，把完整错误贴我。

> 下一份（待写）：**阶段 2 · 测试与 CI 地基**——Testcontainers、JaCoCo、ArchUnit、GitHub Actions、pre-commit hooks。
> 完成后我就动笔。
