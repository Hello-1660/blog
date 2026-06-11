# 博客系统——双 Token 机制与安全流转详解

---

## 一、Token 结构

### 1.1 Access Token vs Refresh Token

| | Access Token | Refresh Token |
|---|---|---|
| **有效期** | 30 分钟（`jwt.accessExpiration`） | 7 天（`jwt.refreshExpiration`） |
| **用途** | 每次 API 请求携带，证明身份 | 只用于 `/token/refresh` 换取新 Access Token |
| **前端存放** | 内存变量（推荐）或 localStorage | localStorage |
| **payload 含 roles** | 是 | **否**（无 roles 字段） |

### 1.2 JWT Payload 结构

**Access Token:**
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

**Refresh Token:**
```json
{
  "sub": "1001",
  "email": "u@example.com",
  "type": "USER",
  "jti": "660e8400-e29b-41d4-a716-446655440111",
  "iat": 1718000000,
  "exp": 1718604800
}
```

Refresh Token 没有 `roles` 字段，因此**不能**用来访问业务接口——走到 `JwtAuthenticationFilter.createAuthentication()` 时会触发 NPE（roles 为 null 时调 `.stream()`）。

### 1.3 请求头格式

```
Authorization: Bearer <token>
```

两个 token 都用 `Authorization` 头 + `Bearer ` 前缀，不做区分。

---

## 二、登录流程——双 Token 签发

```
POST /user/login  （或 /admin/login）
Body: { email, password }
```

```
┌─────────────────────────────────────────────────────────────────┐
│  UserServiceImpl.login()                                        │
│                                                                 │
│  1. userMapper.findByEmail(email)  → 查出用户                   │
│  2. passwordEncoder.matches(明文, 密文)  → 校验密码              │
│  3. jwtTokenUtil.generateUserAccessToken(user)  → Access Token  │
│  4. jwtTokenUtil.generateRefreshToken(id, email, USER)  → Refresh Token │
│  5. UserLoginVo.setToken(accessToken)                           │
│  6. UserLoginVo.setRefreshToken(refreshToken)                   │
│  7. 返回 UserLoginVo                                            │
└─────────────────────────────────────────────────────────────────┘
```

**JwtTokenUtil 内部逻辑：**

```
generateUserAccessToken(user):
  构建 claims: { sub=id, email, type="USER", roles=["ROLE_USER"] }
  → generateToken(claims, accessExpiration=30min)
  → Jjwt 签名 → 返回 JWT 字符串

generateRefreshToken(id, email, type):
  构建 claims: { sub=id, email, type="USER" }
  // 注意：没有 roles
  → generateToken(claims, refreshExpiration=7天)
  → Jjwt 签名 → 返回 JWT 字符串
```

**前端收到响应后：**
```json
{
  "code": 200,
  "data": {
    "id": 1001,
    "email": "u@example.com",
    "token": "eyJhbG...（Access Token）",
    "refreshToken": "eyJhbG...（Refresh Token）"
  }
}
```

前端将 accessToken 存内存/localStorage，refreshToken 存 localStorage。

---

## 三、每次请求的安全流转

```
请求进入（带 Authorization: Bearer <accessToken>）
         │
         ▼
┌────────────────────┐
│  路径匹配分流       │
│  adminFilterChain  │  Order=1, 匹配 /admin/**
│  userFilterChain   │  Order=2, 匹配 /**
└───────┬────────────┘
        │
        ▼
┌────────────────────────────────────────────────┐
│  JwtAuthenticationFilter                       │
│  （两个 FilterChain 共用，最先执行）             │
│                                                │
│  1. parseJwt(request)                          │
│     从 Authorization 头提取 token 字符串         │
│     headerAuth.startsWith("Bearer ")  → 截掉前缀 │
│                                                │
│  2. validateToken(token)                       │
│     失败（过期/格式错/签名错）→ 静默放行          │
│     SecurityContext 保持空                      │
│                                                │
│  3. 检查黑名单（Redis）                          │
│     redisTemplate.hasKey("token:blacklist:<jti>") │
│     存在 → 已登出，放行不建认证                   │
│                                                │
│  4. 解析 token payload                         │
│     getClaimsEmailFromToken() → email           │
│     getClaimsTypeFromToken()   → "USER"/"ADMIN" │
│     getClaimsIdFromToken()     → id             │
│     getClaimsRolesFromToken()  → ["ROLE_USER"]  │
│                                                │
│  5. createAuthentication(type, email, id, roles)│
│     ┌─ type="ADMIN"                            │
│     │   → AdminUserDetailService.loadUserByUsername(email) │
│     │   → 查 DB 加载权限列表                     │
│     │   → 构建 CustomUserDetails { id, email, authorities } │
│     │                                          │
│     └─ type="USER"                             │
│         → 用 token 里的 roles 构建 authorities  │
│         → 构建 CustomUserDetails { id, email, authorities } │
│         → 不查 DB                              │
│                                                │
│  6. 存入 SecurityContextHolder                  │
│     new UsernamePasswordAuthenticationToken(    │
│         userDetails, null, authorities)        │
│                                                │
│  7. filterChain.doFilter()  → 继续              │
└───────────────────┬────────────────────────────┘
                    │
                    ▼
         ┌──────────────────────┐
         │  授权管理器（二选一）   │
         └──────┬───────────────┘
                │
    ┌───────────┴───────────┐
    │                       │
    ▼                       ▼
/admin/**                 /**
DynamicAuthzManager      AccessLevelAuthzManager
（管理端动态鉴权）         （注解访问级别判断）
```

---

## 四、两条 FilterChain 的分流规则

### 4.1 AdminFilterChain（Order=1）

```
只匹配 /admin/** 的请求：
  ├─ /admin/login  → permitAll()（放行）
  └─ 其他          → DynamicAuthorizationManager 动态鉴权
```

`/admin/login` 的两层保护：
1. `AdminSecurityConfig` 中 `permitAll()` — 不走 DynamicAuthorizationManager
2. Controller 上 `@Anonymous` — 即使走到了 AccessLevelAuthzManager 也放行（实际上走不到，因为 `/admin/**` 被 AdminFilterChain 拦截了）

**DynamicAuthorizationManager 的判断逻辑：**

```
1. auth == null || principal 不是 CustomUserDetails → 拒绝（403）
2. auth 中无 "ROLE_ADMIN" 权限 → 拒绝（403）
3. 查 DynamicSecurityMetadataSource 获取该 URL 需要的权限
   ├─ 该 URL 不在 permission 表中 → 放行（登录即可访问）
   └─ 在表中有配置 → AntPathMatcher 比对管理员权限列表
       ├─ 匹配 → 放行
       └─ 不匹配 → 403
```

权限数据来自 `permission` 表，通过 `DynamicSecurityMetadataSource` 做三级缓存：
```
内存 Map → Redis（30min TTL）→ MySQL
```

### 4.2 UserFilterChain（Order=2）

```
匹配 /** 的所有请求（除已被 AdminFilterChain 截获的 /admin/**）：
  → AccessLevelAuthorizationManager 注解级别判断
```

**AccessLevelAuthorizationManager 判断逻辑：**

```
1. 查找当前请求路径对应的访问级别（从启动时扫描的注解缓存中查）
   匹配优先级：精确匹配 > 长路径 > 短路径

2. switch (level):
   ANONYMOUS  → 直接通过（不管有没有 token）
   OPTIONAL   → 直接通过（不管有没有 token）
   REQUIRED   → hasValidAuth(supplier.get())
       auth != null && principal instanceof CustomUserDetails
       ├─ true  → 放行
       └─ false → 401
```

**注解默认值：**
- 方法有注解 → 用方法级别的
- 方法无注解 → 用类级别的
- 类也无注解 → 默认 `REQUIRED`（安全优先）

---

## 五、Controller 层获取当前用户

```java
// 必须登录的接口
@AuthRequired
@PostMapping("/save")
public Result save(@RequestBody ArticleDTO dto) {
    Integer userId = SecurityContextUtil.getId();   // 一定不为 null
    String email = SecurityContextUtil.getEmail();
    ...
}

// 可选登录的接口（核心场景）
@AuthOptional
@GetMapping("/detail/{id}")
public Result<ArticleDetailVO> detail(@PathVariable Long id) {
    Integer currentUserId = SecurityContextUtil.getId();

    ArticleDetailVO article = articleService.getDetail(id);

    if (currentUserId != null) {
        // 已登录：填充个性化数据
        article.setIsLiked(favoriteService.hasLiked(currentUserId, id));
        article.setIsFollowed(followService.hasFollowed(currentUserId, article.getAuthorId()));
    }
    // 未登录也能看到文章内容，只是没有个性化数据

    return Result.success(article);
}
```

**SecurityContextUtil 内部实现：**
```java
SecurityContextHolder.getContext()
    .getAuthentication()            // → UsernamePasswordAuthenticationToken
    .getPrincipal()                 // → CustomUserDetails
```

| 场景 | `getId()` 返回值 |
|------|-----------------|
| 已登录，有效 token | 用户 ID |
| 未登录，`@Anonymous` / `@AuthOptional` | null |
| 未登录，`@AuthRequired` | 不会走到 Controller（被拦在 401） |

---

## 六、Token 刷新流程

```
Access Token 过期（或即将过期）
         │
         ▼
POST /token/refresh
Authorization: Bearer <refreshToken>

         │
         ▼
TokenController.refresh()：

1. 取出 refreshToken，验证有效性
2. 检查 Redis 黑名单（是否已登出）
3. 解析 payload 拿到 type、id、email
4. 根据 type 重新生成新的 Access Token：
   ├─ ADMIN → generateAdminAccessToken(adminMapper.getByEmail(email))
   └─ USER  → generateUserAccessToken(userMapper.getUserById(id))
5. 返回新的 AccessToken { token, tokenHead }
```

**注意：** 刷新接口只返回新的 Access Token，Refresh Token 本身不刷新。Refresh Token 过期后只能重新登录。

---

## 七、登出流程

```
POST /token/logout
Authorization: Bearer <accessToken>

         │
         ▼
TokenController.logout()：

1. 取出 accessToken，验证有效性
2. 获取 jti（JWT ID）和过期时间 exp
3. 计算剩余有效时间 TTL = exp - now
4. 写入 Redis：
   token:blacklist:<jti> = "1"，TTL = 剩余有效时间
5. 前端清除所有本地 token
```

**黑名单的作用：** 已登出的 token 在剩余有效期内再次被使用时，`JwtAuthenticationFilter` 会在 Redis 中查到并拒绝建立认证。TTL 到期后自动清理，不需要维护全局黑名单。

---

## 八、请求决策树（完整版）

```
收到请求
  │
  ├─ JwtAuthenticationFilter
  │   ├─ 无 Authorization 头 → 放行，auth=null
  │   ├─ token 无效/过期 → 放行，auth=null
  │   ├─ token 在黑名单中 → 放行，auth=null
  │   └─ token 有效 → 解析并建立 Authentication
  │       ├─ type=USER  → CustomUserDetails { id, email, authorities=[ROLE_USER] }
  │       └─ type=ADMIN → CustomUserDetails { id, email, authorities=[权限URL列表] }
  │
  ├─ 路径匹配 /admin/** ？
  │   ├─ 是 → AdminFilterChain
  │   │   ├─ /admin/login → permitAll → 到达 Controller
  │   │   └─ 其他 → DynamicAuthorizationManager
  │   │       ├─ auth==null → 403
  │   │       ├─ 无 ROLE_ADMIN → 403
  │   │       ├─ URL 不在权限表 → 放行
  │   │       ├─ 有匹配权限 → 放行
  │   │       └─ 无匹配权限 → 403
  │   │
  │   └─ 否 → UserFilterChain
  │       └─ AccessLevelAuthorizationManager
  │           ├─ @Anonymous → 放行
  │           ├─ @AuthOptional → 放行（Controller 内自行判断）
  │           └─ @AuthRequired
  │               ├─ auth 有效 → 放行
  │               └─ auth 无效 → 401
  │
  └─ Controller 执行业务逻辑
```

---

## 九、各注解下的访问结果速查表

| 注解 | 无 token | 有效 token（USER） | 有效 token（ADMIN） | token 过期/无效 |
|------|----------|-------------------|---------------------|----------------|
| `@Anonymous` | 通过 | 通过 | 通过 | 通过 |
| `@AuthOptional` | 通过（匿名） | 通过（含用户信息） | 通过（含管理员信息） | 通过（匿名） |
| `@AuthRequired` | **401** | 通过 | 通过 | **401** |

---

## 十、关键类索引

| 类 | 位置 | 职责 |
|----|------|------|
| `JwtTokenUtil` | `blog-security/util/` | Access/Refresh Token 生成、解析、验证、jti 提取 |
| `JwtAuthenticationFilter` | `blog-security/filter/` | 每个请求解析 JWT → 建立 Authentication |
| `AccessLevelAuthorizationManager` | `blog-security/authorization/` | 扫描注解 → 缓存路径↔级别映射 → 判断是否放行 |
| `DynamicAuthorizationManager` | `blog-security/authorization/` | 管理端：DB 权限 + AntPathMatcher 匹配 |
| `DynamicSecurityMetadataSource` | `blog-security/metadata/` | 权限元数据：内存→Redis→MySQL 三级缓存 |
| `AdminUserDetailService` | `blog-security/service/` | 管理员登录时加载权限列表 |
| `CustomUserDetails` | `blog-security/service/` | 用户主体对象：{ id, email, authorities } |
| `SecurityContextUtil` | `blog-security/util/` | 从 SecurityContextHolder 取当前用户 id/email/权限 |
| `AdminSecurityConfig` | `blog-security/config/` | 管理端 FilterChain（Order=1, `/admin/**`） |
| `UserSecurityConfig` | `blog-security/config/` | 用户端 FilterChain（Order=2, `/**`） |
| `RestAuthenticationEntryPoint` | `blog-security/handler/` | 401 时返回 JSON `"请先登录"` |
| `RestfulAccessDeniedHandler` | `blog-security/handler/` | 403 时返回 JSON `"权限不足"` |
| `TokenController` | `blog-service/controller/token/` | `/token/refresh` + `/token/logout` |
