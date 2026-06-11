# 博客系统安全模块重构方案

## 一、现状分析

### 1.1 当前架构

```
请求 → JwtAuthenticationFilter → SecurityFilterChain → DynamicAuthorizationManager → Controller
         (解析JWT,建立认证)      (白名单+动态鉴权)      (URL模式匹配权限)
```

**认证流程：**

- 用户登录 → 签发 JWT（负载：`sub`=ID, `email`=邮箱, `T`="u"或"a"）
- 每次请求 → `JwtAuthenticationFilter` 提取 `Authorization: Bearer <token>` → 验证 JWT
  - 管理员 token (`T="a"`)：查询 DB 加载权限列表，构建完整 `CustomUserDetails`
  - 用户 token (`T="u"`)：构建空的 `CustomUserDetails`（无权限，无角色）
- 无效 token → 静默放行，`SecurityContextHolder` 保持空状态

**授权流程（DynamicAuthorizationManager）：**

```
1. URI 以 /admin 开头 且 未认证 → 拒绝(403)
2. 查询权限表，该 URI 是否需要权限 → 不需要 → 放行
3. URI 以 /user 开头 且 未认证 → 拒绝(403)
4. 比对用户权限列表与 URI 需要的权限 → AntPathMatcher 匹配 → 放行/拒绝
```

**白名单机制：** 在 `application.yml` 中配置 `security.ignored.urls`，`SecurityConfig` 中直接 `permitAll()`，完全绕过所有过滤器。

### 1.2 存在的问题

#### 核心缺陷

| 问题 | 说明 |
|------|------|
| **无"可选认证"能力** | 用户端接口只能在"完全放开"和"必须登录"之间二选一。无法实现"有 token 就解析用户信息（如是否已点赞），没有也能正常浏览" |
| **授权逻辑硬编码分流** | `DynamicAuthorizationManager.check()` 用 `if (uri.startsWith("/admin"))` 和 `if (uri.startsWith("/user"))` 区分两端，新增模块就得改核心代码 |
| **注解安全能力空置** | 已启用 `@EnableMethodSecurity`，但零个 Controller 使用 `@PreAuthorize` 或自定义注解，全靠 URL 匹配 |

#### 代码质量

| 问题 | 位置 |
|------|------|
| `isWitheList()` 死代码（从未被调用，拼写也错了） | `JwtAuthenticationFilter.java:119` |
| 重复 Bean `dynamicSecurityMetadataSourceWithJwt`（copy-paste 遗留） | `CommonSecurityConfig.java:45` |
| `refreshToken()` 刷新后丢失 email 字段 | `JwtTokenUtil.java:192-197` |
| `RoleConstant.DISABLE = 1` 应为 `0` | `RoleConstant.java:6` |
| 包名拼写错误 `fliter` → 应为 `filter` | `blog-security/.../fliter/` |
| 字符串常量重复定义 | `AdminConstant` 与 `PermissionVerificationConstant` 各有"请先登录"/"权限不足" |
| 类型标识用单字母字符串 `"u"` / `"a"` | `JwtTokenUtil.java:29-31` |

#### 架构问题

| 问题 | 说明 |
|------|------|
| 安全模块耦合持久层 | `blog-security` 直接依赖 `blog-mapper`，`AdminUserDetailService` 注入了 `AdminMapper`、`RoleMapper`、`PermissionMapper` |
| 无 Token 失效机制 | 没有黑名单，登出无法真正让 JWT 失效 |
| JWT 过期时间极长 | 604800000ms = 7 天，无 Access/Refresh 分离 |
| 权限无缓存 | 每次管理员请求都查库（虽然有 Map 缓存，但不支持分布式） |

---

## 二、目标架构

### 2.1 分层设计

```
┌─────────────────────────────────────────────────┐
│                    请求                          │
└──────────────────┬──────────────────────────────┘
                   │
          ┌────────▼────────┐
          │  JwtAuthFilter  │  ← 认证层：解析 token，建立身份（不再做鉴权判断）
          └────────┬────────┘
                   │
          ┌────────▼────────┐
          │ AccessLevel     │  ← 访问级别层：@Anonymous / @AuthOptional / @AuthRequired
          │ AuthzManager    │     决定"这个接口要不要登录"
          └────────┬────────┘
                   │
          ┌────────▼────────┐
          │  DynamicAuthz   │  ← 授权层：管理端 RBAC 动态权限匹配
          │  Manager        │     决定"你有没有权限做这个操作"
          └────────┬────────┘
                   │
          ┌────────▼────────┐
          │   Controller    │
          └─────────────────┘
```

### 2.2 模块结构

```
blog-security/
├── annotation/              # 自定义安全注解
│   ├── Anonymous.java       #  完全公开，无需 token
│   ├── AuthOptional.java    #  可选认证，有 token 则解析，无则匿名
│   └── AuthRequired.java    #  必须认证，无有效 token → 401
│
├── enums/
│   └── AccountType.java     #  USER, ADMIN（替代 "u"/"a" 硬编码）
│
├── config/
│   ├── AdminSecurityConfig.java       #  管理端 SecurityFilterChain（order=1, 匹配 /admin/**）
│   ├── UserSecurityConfig.java        #  用户端 SecurityFilterChain（order=2, 匹配 /**）
│   └── SecurityBeanConfig.java        #  统一 Bean 注册
│
├── filter/
│   └── JwtAuthenticationFilter.java   #  只做一件事：解析 token → 建立 Authentication
│
├── authorization/
│   ├── AccessLevelAuthorizationManager.java  #  注解级别判断（匿名/可选/必须）
│   └── DynamicAuthorizationManager.java      #  管理端：DB 权限 + URL 模式匹配
│
├── metadata/
│   └── DynamicSecurityMetadataSource.java    #  权限元数据（内存 + Redis 二级缓存）
│
├── service/
│   ├── CustomUserDetails.java
│   └── AdminUserDetailService.java
│
├── util/
│   ├── JwtTokenUtil.java              #  Access + Refresh Token 生成/验证/刷新
│   └── SecurityContextUtil.java       #  从 SecurityContext 获取当前用户
│
└── handler/
    ├── RestAuthenticationEntryPoint.java     #  401 处理
    └── RestfulAccessDeniedHandler.java       #  403 处理
```

---

## 三、自定义注解体系（替代白名单配置）

### 3.1 注解定义

```java
// === @Anonymous ===
// 用途：登录、注册、搜索等完全公开的接口
// 行为：不检查 token，直接放行
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {
}

// === @AuthOptional ===
// 用途：文章详情、评论列表等"登录后体验更好"的接口
// 行为：尝试解析 token，成功则填充用户上下文，失败也放行
//       Controller 层通过 SecurityContextUtil.getId() 判断是否为 null 来区分
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthOptional {
}

// === @AuthRequired ===
// 用途：发表文章、点赞、关注等必须登录的接口（无需细粒度权限的场景）
// 行为：无有效 token → 401
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthRequired {
}
```

### 3.2 注解优先级

- **方法级注解 > 类级注解**
- 如果方法和类都没有注解 → **默认为 `@AuthRequired`**（安全优先原则）

### 3.3 使用示例

```java
// ====== 用户端 ======

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Anonymous
    @GetMapping("/search")
    public Result search(String keyword) {
        // 未登录也能搜索
    }

    @AuthOptional
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        // 未登录可以看文章，登录后额外返回是否已点赞、是否已关注
        Integer userId = SecurityContextUtil.getId();
        ArticleDetailVO vo = articleService.getDetail(id);
        if (userId != null) {
            enrichPersonalData(vo, userId);
        }
        return Result.success(vo);
    }

    @AuthRequired
    @PostMapping("/save")
    public Result save(@RequestBody ArticleDTO dto) {
        // 必须登录才能发文章
    }
}

// ====== 管理端 ======

@RestController
@RequestMapping("/admin")
@AuthRequired  // 类级别：所有管理端接口默认需要认证
public class AdminController {

    @Anonymous  // 方法级别覆盖：登录接口不需要认证
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO dto) {
    }

    @PostMapping("/save")   // 继承类级别的 @AuthRequired
    public Result save(@RequestBody AdminDTO dto) {
        // 走到这里说明：已认证 + 动态鉴权通过
    }
}
```

### 3.4 注解扫描与缓存

在应用启动时，扫描所有 `@Controller` / `@RestController` Bean：

```
1. 遍历 Spring 容器中所有带 @Controller/@RestController 的 Bean
2. 反射读取每个方法上的 @RequestMapping 路径
3. 读取方法上的 @Anonymous / @AuthOptional / @AuthRequired
   若方法无注解，读取类上的
   若类也无注解，默认 AuthRequired
4. 构建路径 → 访问级别的映射缓存
```

---

## 四、认证层：JWT Filter 重构

### 4.1 设计原则

**Filter 只做一件事：解析 token 并建立 Authentication 对象。不判断白名单，不做授权决策。**

### 4.2 处理流程

```
请求进入 doFilterInternal()
  │
  ├─ 无 Authorization 头 → 放行（Authentication 保持为 null）
  │
  ├─ Authorization 头格式不对 → 放行
  │
  ├─ token 解析失败/过期/签名无效 → 记录日志，放行
  │    关键：不抛异常！留给 AccessLevelAuthorizationManager 判断是否需要登录
  │
  └─ token 有效
       │
       ├─ type = USER
       │    → 构建 UsernamePasswordAuthenticationToken
       │      principal = CustomUserDetails { id, email, authorities=["ROLE_USER"] }
       │    → 存入 SecurityContextHolder
       │
       └─ type = ADMIN
            → 调用 AdminUserDetailService.loadUserByUsername(email)
            → 构建 UsernamePasswordAuthenticationToken
              principal = CustomUserDetails { id, email, authorities=[权限URL列表] }
            → 存入 SecurityContextHolder
```

### 4.3 关键变化

| 旧 | 新 |
|----|----|
| 用户 token 的 authorities 为空列表 | 用户 token 的 authorities 包含 `ROLE_USER` |
| Filter 中有 `isWitheList()` 死代码 | 全部删除 |
| Filter 中白名单逻辑由 `SecurityConfig.permitAll()` 处理 | 由 `AccessLevelAuthorizationManager` 统一处理 |
| `getCustomUserDetails()` 在 Filter 内部 | 保持，但逻辑简化 |
| 包名 `fliter` | 修正为 `filter` |

---

## 五、访问级别层：AccessLevelAuthorizationManager

### 5.1 职责

判断"当前请求需要什么级别的认证"，并据此做出通过/拒绝决策。**不关心用户有什么权限。** 这是接入管理端动态鉴权之前的**前置关卡**。

### 5.2 实现

```java
public class AccessLevelAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    // 启动时扫描注解构建的缓存：请求路径模式 → 访问级别
    private Map<String, AccessLevel> cache;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier,
                                        RequestAuthorizationContext ctx) {
        HttpServletRequest request = ctx.getRequest();
        AccessLevel level = lookupAccessLevel(request);

        return switch (level) {
            case ANONYMOUS -> new AuthorizationDecision(true);
            case OPTIONAL  -> new AuthorizationDecision(true);  // 始终放行
            case REQUIRED  -> new AuthorizationDecision(hasValidAuth(supplier.get()));
        };
    }

    /**
     * 判断 Authentication 是否有效
     * 有效 = 非 null 且 principal 是 CustomUserDetails 实例
     */
    private boolean hasValidAuth(Authentication auth) {
        return auth != null && auth.getPrincipal() instanceof CustomUserDetails;
    }
}
```

### 5.3 路径匹配优先级

注解扫描产生的路径可能有重叠（如 `/article/detail` 和 `/article/**`），匹配时遵循：

1. **精确匹配优先** — `/article/detail` > `/article/**`
2. **最长前缀优先** — `/article/detail/**` > `/article/**`

---

## 六、管理端授权层：动态鉴权优化

### 6.1 当前方案保留的部分

- `permission` 表存储 URL 权限模式
- `role` → `role_permission_relation` → `permission` 的 RBAC 关联
- `AdminUserDetailService` 登录时加载权限列表
- `DynamicSecurityMetadataSource` 作为权限元数据源
- `DynamicAuthorizationManager` 做 URL 模式匹配

### 6.2 优化点

#### 6.2.1 分离 SecurityFilterChain

```java
@Configuration
public class AdminSecurityConfig {

    @Bean
    @Order(1)  // 管理端优先匹配
    public SecurityFilterChain adminFilterChain(HttpSecurity http) {
        http.securityMatcher("/admin/**")   // 只拦截管理端请求
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login").permitAll()  // 登录放行
                .anyRequest().access(dynamicAuthorizationManager)  // 动态鉴权
            )
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restfulAccessDeniedHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}

@Configuration
public class UserSecurityConfig {

    @Bean
    @Order(2)  // 用户端作为默认匹配
    public SecurityFilterChain userFilterChain(HttpSecurity http) {
        http.securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().access(accessLevelAuthorizationManager)  // 注解级别判断
            )
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restfulAccessDeniedHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
```

**为什么要拆分两条链？**

- 管理端：需要动态查询 DB 权限表做 URL 匹配（`DynamicAuthorizationManager`）
- 用户端：只需要检查访问级别（`AccessLevelAuthorizationManager`），不需要查权限表
- 拆开后两条链互不干扰，职责清晰

#### 6.2.2 权限元数据加 Redis 缓存

```
┌──────────┐     ┌──────────┐     ┌──────────┐
│  请求    │────→│  内存 Map │────→│  Redis   │────→│  MySQL   │
└──────────┘     │  (L1)    │     │  (L2)    │     │  (L3)    │
                 │  实时    │     │ TTL=30m  │     │  持久化   │
                 └──────────┘     └──────────┘     └──────────┘

查询流程：
  1. 查内存 Map → 命中 → 返回
  2. 未命中 → 查 Redis → 命中 → 回填内存 + 返回
  3. 未命中 → 查 MySQL → 回填 Redis + 内存 + 返回

权限变更时：
  1. 更新 MySQL
  2. 清除 Redis 中的权限缓存 key
  3. 调用 DynamicSecurityMetadataSource.clearDataSource() 清除内存缓存
```

#### 6.2.3 优化后的 DynamicAuthorizationManager

```java
public class DynamicAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier,
                                        RequestAuthorizationContext ctx) {
        Authentication auth = supplier.get();

        // 1. 未登录 → 拒绝
        //    能到这里说明已通过 AccessLevelAuthorizationManager 的 @AuthRequired 检查
        //    但以防万一，再次校验
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return new AuthorizationDecision(false);
        }

        // 2. 检查是否是管理员
        if (!hasAdminRole(auth)) {
            return new AuthorizationDecision(false);
        }

        // 3. 查询该 URL 需要的权限
        Collection<ConfigAttribute> requiredAttrs =
            metadataSource.getAllConfigAttributes(ctx.getRequest());

        // 4. 不需要权限（URL 不在 permission 表中）→ 登录即可访问
        if (CollectionUtils.isEmpty(requiredAttrs)) {
            return new AuthorizationDecision(true);
        }

        // 5. 判断该管理员是否拥有匹配的权限
        AntPathMatcher matcher = new AntPathMatcher();
        for (ConfigAttribute attr : requiredAttrs) {
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

---

## 七、可选认证的实现细节

### 7.1 用户端"可选认证"场景

这是用户端最高频的认证模式。典型场景：

| 接口 | 未登录 | 已登录 |
|------|--------|--------|
| `/article/detail/{id}` | 返回文章内容 | 额外返回 `isLiked`、`isFollowed` |
| `/comment/list/{articleId}` | 返回评论列表 | 额外标记"我的评论" |
| `/user/detail/{id}` | 返回用户公开信息 | 额外返回"是否已关注该用户" |

### 7.2 SecurityContextUtil 获取当前用户

```java
public class SecurityContextUtil {

    /**
     * 获取当前登录用户 ID
     * @return 用户 ID，未登录返回 null
     */
    public static Integer getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails user) {
            return user.getId();
        }
        return null;
    }

    /**
     * 获取当前登录用户邮箱
     * @return 邮箱，未登录返回 null
     */
    public static String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails user) {
            return user.getEmail();
        }
        return null;
    }

    /**
     * 判断是否已登录
     */
    public static boolean isAuthenticated() {
        return getId() != null;
    }

    /**
     * 获取当前用户权限列表
     */
    public static List<GrantedAuthority> getAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(user.getAuthorities());
    }
}
```

### 7.3 Controller 中使用

```java
@AuthOptional
@GetMapping("/detail/{id}")
public Result<ArticleDetailVO> detail(@PathVariable Long id) {
    ArticleDetailVO article = articleService.getDetail(id);

    Integer currentUserId = SecurityContextUtil.getId();
    if (currentUserId != null) {
        // 已登录：填充个性化数据
        article.setIsLiked(favoriteService.hasLiked(currentUserId, id));
        article.setIsFollowed(followService.hasFollowed(currentUserId, article.getAuthorId()));
        article.setIsBookmarked(bookmarkService.hasBookmarked(currentUserId, id));
    }

    return Result.success(article);
}
```

---

## 八、Token 管理增强

### 8.1 双 Token 机制

| 类型 | 有效期 | 存储位置 | 用途 |
|------|--------|----------|------|
| Access Token | 30 分钟 | 前端内存 | 日常 API 请求 |
| Refresh Token | 7 天 | HttpOnly Cookie 或前端 localStorage | 无感刷新 Access Token |

**刷新流程：**

```
Access Token 过期
  → 前端拦截器捕获 401
  → 携带 Refresh Token 请求 POST /token/refresh
  → 服务端验证 Refresh Token + 检查黑名单
  → 返回新的 Access Token
  → 前端用新 Access Token 重试原请求

Refresh Token 也过期
  → /token/refresh 返回 401
  → 前端跳转登录页
```

### 8.2 JWT 负载设计

```json
{
  "sub": "1001",
  "email": "u@example.com",
  "type": "USER",
  "roles": ["ROLE_USER"],
  "jti": "550e8400-e29b-41d4-a716-446655440000",
  "iat": 1718000000,
  "exp": 1718001800
}
```

| 字段 | 说明 |
|------|------|
| `sub` | 用户/管理员 ID |
| `email` | 邮箱 |
| `type` | 账号类型：`USER` 或 `ADMIN` |
| `roles` | 角色列表 |
| `jti` | JWT ID（UUID），用于黑名单标识 |
| `iat` | 签发时间 |
| `exp` | 过期时间 |

### 8.3 登出与 Token 黑名单

```
登出流程：
  1. 前端调用 POST /logout
  2. 服务端解析 Access Token → 提取 jti 和 exp
  3. 计算剩余有效时间 TTL = exp - now
  4. 写入 Redis：
     jwt:blacklist:<jti> → "1"，TTL = 剩余有效时间
  5. 同时将 Refresh Token 的 jti 也写入黑名单
  6. 前端清除本地存储的 token

每次请求时 JwtAuthenticationFilter 额外校验：
  1. 解析 token 获取 jti
  2. 检查 Redis 中 jwt:blacklist:<jti> 是否存在
  3. 存在 → 视为无效 token → 不建立认证

优点：
  - 不需要维护全局黑名单（TTL 到期自动清除）
  - 不破坏 JWT 无状态特性（黑名单是附加校验）
```

---

## 九、权限矩阵

### 9.1 各注解下的访问结果

| 注解 | 无 token | 有效 token（用户） | 有效 token（管理员） | token 过期/无效 |
|------|----------|-------------------|---------------------|----------------|
| `@Anonymous` | 通过 | 通过 | 通过 | 通过 |
| `@AuthOptional` | 通过（匿名） | 通过（含用户信息） | 通过（含管理员信息） | 通过（匿名） |
| `@AuthRequired` | 401 | 通过 | 通过 | 401 |

### 9.2 示例接口权限矩阵

| 接口 | 注解 | 未登录 | 已登录(用户) | 已登录(管理员) |
|------|------|--------|-------------|---------------|
| `/article/search` | `@Anonymous` | 可访问 | 可访问 | 可访问 |
| `/article/detail/{id}` | `@AuthOptional` | 可访问（无个性化数据） | 可访问（含点赞/关注） | 可访问 |
| `/article/save` | `@AuthRequired` | 401 | 可访问 | 可访问 |
| `/user/login` | `@Anonymous` | 可访问 | — | — |
| `/user/save`（注册） | `@Anonymous` | 可访问 | — | — |
| `/user/update` | `@AuthRequired` | 401 | 可访问（只能改自己） | 可访问 |
| `/admin/login` | `@Anonymous` | 可访问 | — | — |
| `/admin/**`（其他） | `@AuthRequired` | 401 | 403（非管理员） | 匹配权限后允许 |
| `/admin/save` | `@AuthRequired` | 401 | 403 | 有 `admin:save` 权限才行 |

---

## 十、接口安全决策树

```
收到请求
  │
  ├─ 请求路径匹配 /admin/** ？
  │   ├─ 是 → AdminFilterChain
  │   │   ├─ /admin/login → @Anonymous → 放行
  │   │   └─ 其他 → JWT Filter 解析 token
  │   │       ├─ 无有效 token → 401
  │   │       ├─ token 类型不是 ADMIN → 403
  │   │       └─ 是管理员 → DynamicAuthorizationManager
  │   │           ├─ 该 URL 不在权限表 → 可访问（登录即可）
  │   │           ├─ 用户有匹配权限 → 可访问
  │   │           └─ 无匹配权限 → 403
  │   │
  │   └─ 否 → UserFilterChain
  │       ├─ JWT Filter 尝试解析 token（成功/失败都继续）
  │       └─ AccessLevelAuthorizationManager
  │           ├─ @Anonymous → 放行
  │           ├─ @AuthOptional → 放行（Controller 内自行判断是否有用户）
  │           └─ @AuthRequired
  │               ├─ 有有效 token → 放行
  │               └─ 无有效 token → 401
```

---

## 十一、实施路线

### 阶段一：修 Bug（低风险，直接提交）

1. 修正 `RoleConstant.DISABLE = 0`
2. 修正 `JwtTokenUtil.refreshToken()` 补全 email 字段
3. 修正包名 `fliter` → `filter`
4. 删除 `CommonSecurityConfig` 中的重复 Bean `dynamicSecurityMetadataSourceWithJwt`
5. 删除 `JwtAuthenticationFilter.isWitheList()` 死代码
6. 合并重复的字符串常量到一处

### 阶段二：引入枚举 + 清理常量（低风险）

1. 新增 `AccountType` 枚举：`USER`, `ADMIN`
2. `JwtTokenUtil` 内部使用枚举，token 负载中写入 `"USER"` / `"ADMIN"`（全大写，可读）
3. 向后兼容：解析时同时支持旧的 `"u"`/`"a"` 和新的 `"USER"`/`"ADMIN"`
4. 删除 `AccountConstant.java`（未被安全模块使用）
5. 合并 `AdminConstant` 和 `PermissionVerificationConstant` 中的重复字符串

### 阶段三：拆分 FilterChain + 引入注解体系（核心改造）

1. 创建三个注解：`@Anonymous`、`@AuthOptional`、`@AuthRequired`
2. 实现注解扫描器，构建路径→访问级别缓存
3. 实现 `AccessLevelAuthorizationManager`
4. 拆分为 `AdminSecurityConfig` 和 `UserSecurityConfig` 两条链
5. 简化 `JwtAuthenticationFilter`（去掉白名单相关逻辑）
6. 给 Controller 逐个添加注解，逐步去掉 `application.yml` 中的白名单
7. 验证通过后删除 `IgnoreUrlsConfig` 及相关配置

### 阶段四：管理端鉴权增强

1. 优化 `DynamicAuthorizationManager`（精简 if/else，去掉硬编码前缀判断）
2. `DynamicSecurityMetadataSource` 加 Redis 二级缓存
3. 提供权限缓存刷新接口（供权限管理后台调用）

### 阶段五：Token 机制增强

1. 实现双 Token（Access + Refresh）
2. 实现登出接口 + Token 黑名单（Redis）
3. 缩短 Access Token 有效期至 30 分钟

---

## 十二、附录：关键类签名速览

```java
// === 注解 ===
@Anonymous                    // 完全公开
@AuthOptional                 // 可选认证
@AuthRequired                 // 必须认证

// === 枚举 ===
AccountType { USER, ADMIN }   // 账号类型

// === 配置 ===
AdminSecurityConfig            // @Order(1), securityMatcher("/admin/**")
UserSecurityConfig             // @Order(2), securityMatcher("/**")
SecurityBeanConfig             // 统一注册所有安全相关的 Bean

// === 过滤器 ===
JwtAuthenticationFilter        // OncePerRequestFilter: 解析 token → Authentication

// === 授权管理器 ===
AccessLevelAuthorizationManager  // AuthorizationManager: 注解 → 访问级别判断
DynamicAuthorizationManager      // AuthorizationManager: URL → DB权限匹配

// === 元数据 ===
DynamicSecurityMetadataSource   // 权限元数据: 内存 + Redis 二级缓存

// === 服务 ===
AdminUserDetailService          // UserDetailsService: 管理员 + 权限加载

// === 工具 ===
JwtTokenUtil           // Access/Refresh Token 生成、验证、刷新
SecurityContextUtil    // 从 SecurityContext 获取当前用户 id/email/权限

// === 用户详情 ===
CustomUserDetails      // implements UserDetails { id, email, password, authorities }
```

---

## 十三、待修复问题清单（截至 2026-06-11）

### 1.【编译错误】登录代码调用了已注释的方法

| 文件 | 行号 | 问题 |
|------|------|------|
| `blog-service/.../service/user/impl/UserServiceImpl.java` | 111 | 调用 `jwtTokenUtil.generateUserToken(user)`，方法已在 JwtTokenUtil 中注释掉 |
| `blog-service/.../service/admin/impl/AdminServiceImpl.java` | 59 | 调用 `jwtTokenUtil.generateAdminToken(admin)`，方法已在 JwtTokenUtil 中注释掉 |
| `JwtTokenUtil.java` | 68-93 | 旧方法已注释但未删除 |

**修法：**
- `UserServiceImpl.java:111` 改为调用 `generateUserAccessToken(user)` + `generateRefreshToken()`
- `AdminServiceImpl.java:59` 改为调用 `generateAdminAccessToken(admin)` + `generateRefreshToken()`
- `UserLoginVo` 和 `AdminLoginVo` 需加 `refreshToken` 字段
- 删除 `JwtTokenUtil.java:68-93` 注释掉的废弃代码

### 2. `SecurityContextUtil.getAuthorities()` 类型判断写错

- 文件：`blog-security/.../util/SecurityContextUtil.java:56`
- 当前：`!(auth.getAuthorities() instanceof CustomUserDetails user)`
- 应改为：`!(auth.getPrincipal() instanceof CustomUserDetails user)`
- 原因：`auth.getAuthorities()` 返回 `Collection<GrantedAuthority>`，永远不可能是 `CustomUserDetails`

### 3. `SecurityContextUtil.getId()` / `getEmail()` 缺少 null 检查

- 文件：`blog-security/.../util/SecurityContextUtil.java:22`、`:39`
- `auth` 可能为 null（未登录时），直接调用 `auth.getPrincipal()` 会 NPE
- 在每处 `auth.getPrincipal()` 前加 `if (auth == null) return null;`

### 4. `TokenController` 路径缺少 `/`

- 文件：`blog-service/.../controller/token/TokenController.java:69`
- 当前：`@PostMapping("logout")`
- 应改为：`@PostMapping("/logout")`

### 5. 未完成的改造（与设计文档对照）

| 阶段 | 内容 | 状态 |
|------|------|------|
| Phase 3.6 | Controller 添加 `@Anonymous` / `@AuthOptional` / `@AuthRequired` 注解 | ❌ 未做 |
| Phase 4.2 | `DynamicSecurityMetadataSource` Redis 三级缓存 | ✅ 已完成 |
| Phase 4.3 | `/admin/permission/refresh` 缓存刷新接口 | ✅ 已完成 |
| Phase 5 | 双 Token 生成 + jti 提取 | ✅ 已完成 |
| Phase 5 | `JwtAuthenticationFilter` 黑名单检查 | ✅ 已完成 |
| Phase 5 | `TokenController` `/token/refresh` + `/logout` | ✅ 已完成 |
| Phase 5 | 登录接口返回双 Token（access + refresh） | ❌ 编译错误 |
| Phase 5 | `UserLoginVo` / `AdminLoginVo` 添加 `refreshToken` 字段 | ❌ 未做 |
