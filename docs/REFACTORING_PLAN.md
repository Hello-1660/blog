# 博客项目重构方案（现状审计 · 重构方向 · 实施流程）

> 审计对象：`D:\project\java\blog`（Spring Boot 3.4.13 / Java 21 / Maven 多模块 / MyBatis / MySQL / Redis）
> 审计方式：全量源码扫描 + Maven 实测构建 + 从 git HEAD 复原已删除的 `docs/` 建表脚本
> 审计基准：工作区当前状态（master，114 次提交，工作区有 5 个未提交的 docs 删除）

---

## 0. 结论摘要

项目**技术选型的"骨架"并不老旧**（Spring Boot 3.4 + Java 21 属于当前主流），真正的问题在于四件事：

1. **可运行性依赖 IDE**：命令行打出的 jar 缺少配置文件，只能"在 IntelliJ 里跑"；
2. **安全边界失效**：管理端存在越权路径，用户端授权规则被整段注释掉；
3. **语义与边界混乱**：接口重复/冲突、模块职责错位、SQL 定义双份、数据库脚本与代码漂移；
4. **工程流程缺失**：0 个测试、无 CI、无提交规范、真实凭据明文散落在被忽略的 yml 里。

因此建议的重构不是"推倒重写"，而是：**先止血（可运行 + 安全）→ 再固化（测试 + CI + Git 流程）→ 后重构（接口 / 分层 / 数据库）→ 最后升级框架**。

---

## 1. 现状体检（按严重度排序）

### 1.1 P0 · 阻断级（不修则重构无法验证）

| # | 问题 | 证据 |
|---|------|------|
| P0-1 | **Maven 打包产物无法启动**：`mvn clean package` 生成的 `blog-service-0.0.1-SNAPSHOT.jar` 内 **没有** `application.yml`、`application-*.yml`、`ip2region*.xdb` | 实测：干净打包后 `jar tf` 中 `BOOT-INF/classes/` 下无任何 yml；资源实际放在 `blog-service/src/resources/`，只靠 `blog-service.iml` 的 `<sourceFolder ... type="java-resource"/>` 被 IDEA 标记为资源根，Maven 不认 |
| P0-2 | **管理端越权**：普通用户 token 可访问全部 `/admin/**` | `DynamicAuthorizationManager.check()`：非管理员直接 `new AuthorizationDecision(true)`；`/admin/**` 链只对 `/admin/login` permitAll，其余走该 manager |
| P0-3 | **用户端授权完全关闭** | `UserSecurityConfig` 中 `authorizeHttpRequests(...)` 整段被注释（提交 `3e0d3c4 cancel user security chain`），且 `securityMatcher("/**")` 兜底 |
| P0-4 | **绕过管理链路的写接口**：`POST /menu/save` | `MenuController` 映射在 `/menu`（不在 `/admin/**`），管理端 filter chain 匹配不到，无任何鉴权即可写菜单 |
| P0-5 | **凭据明文硬编码** | `application-dev/pro/test.yml` 含 DB 密码、Redis 密码、OSS AccessKeyId/Secret、邮箱密码、`wanwei.api-key`、JWT secret；且三份 profile 大量重复 |
| P0-6 | **`.gitignore` 用 `*.yml` 全局忽略配置**，导致配置永远无法入库，只能本地保存 | `.gitignore` 末尾 `*.yml`（注释写着"含敏感信息"） |
| P0-7 | 显式开启循环依赖：`spring.main.allow-circular-references: true` | `application.yml:10`（静态扫描未发现 service 互调，说明该开关要么是历史残留、要么掩盖了安全链路里的环，**必须移除并用测试证明无环**） |

### 1.2 P1 · 接口语义冲突（你关心的第 1 点）

已确认的**同义/冲突接口**：

| 冲突 | 位置 | 说明 |
|------|------|------|
| 同一操作用户两个地址 | `POST /admin/role/assignPermission` 与 `POST /admin/permission/assignPermission` | permission 表里 id=30 与 id=41 也是同一件事的两条记录 |
| 菜单新增两份 | `POST /menu/save`（`MenuController`+`MenuServiceImpl`）与 `POST /admin/menu/save`（`MenuManageController`+`MenuManageServiceImpl`） | 整套 Service/Mapper 调用链重复 |
| URL 语义与行为相反 | `GET /admin/comment/delete/{id}` | 方法名 `detail`，返回评论详情；路径却写 `delete` |
| 举报/申诉两套并行 | `POST|GET /report`（`Report` 实体，`objectType/objectId`）与 `POST /appeal/save`+`GET /appeal/list`（`Appeal` 实体，`type/objectId`） | 字段语义重复，且 `Appeal` 表在仓库脚本中不存在 |
| 用 GET 做状态变更 | `GET /email/read/{id}`、`GET /email/allRead`、`GET /admin/permission/refresh`、`GET /user/verificationCode/{email}`、`GET /user/refreshToken`、`GET /admin/comment/delete/{id}` | 可被预取/被代理缓存，且刷新 token 走 GET |
| 请求体绑定不一致 | `ReportController.report(ReportDto)` **无** `@RequestBody`；`AppealController.save(@RequestBody AppealDto)` | 同一项目两种绑定方式 |
| DELETE 带 body | `DELETE /favorite/removeArticle` | HTTP 语义与部分客户端/网关兼容性差 |
| 命名风格混乱 | `/articleCollection`、`/commonApi`、`/material/createFolder`（缺前导 `/`）、`/user/historyDel`、`/favorite/removeAllArticles/{id}`、`/user/subscribePin` | 驼峰/短横线/省略动词混用 |
| God Controller | `UserController` 302 行 / 22 个端点 | 登录、资料、文章列表、点赞、关注、粉丝、邮件、验证码、重置密码、浏览历史、IP 归属、token 刷新、站内消息全塞在一起 |
| 返回模型不统一 | `Result<Comment>`、`Result<List<Report>>`、`Result<Menu>` 直接暴露 entity；同时又有大量 VO | 分页用 PageHelper + `PageResult` 散落在 Service |
| 死接口 | `IdentifyController` 是空类；`/test` 有 Controller/Service/Mapper/实体/数据库表 | Demo 遗留 |

> 另有 127 个映射注解，而 `permission` 表只登记了 51 条且**权限以 URL 为键**；`DynamicAuthorizationManager` 对"表里没有的 URL"直接放行 → **新增接口默认不受保护**。

### 1.3 P1 · 层级与依赖（你关心的第 2 点）

- **模块职责错位**：`blog-service` 同时承载 web 层（controller）、业务层（service）、配置（config）、工具（util）、WebSocket；`blog-mapper` 是持久层；`blog-pojo` 混装 entity/dto/vo/validation；`blog-security` 只定义接口，实现在 `blog-service`（`AdminUserDetailService`、`DynamicSecurityServiceImpl`）。
- **包名多一层**：`blog-security` 的包是 `com.jxcia.blog.blog.security`。
- **注入风格**：`@Autowired` 字段注入 115 处，`@RequiredArgsConstructor` 0 处。
- **事务边界缺失**：全项目 `@Transactional` 仅 6 处（文章、合集、评论点赞、邮件、收藏），而多处跨表写（发布文章=文章+浏览记录、删除用户=多表级联）没有事务。
- **God Class**：`UserServiceImpl` 609 行、注入 11 个 Mapper；`UserService` 接口 168 行。
- **持久层双写**：一个 Mapper 同时有 XML 和注解 SQL（如 `FavoriteMapper.java` 有 4 处 `select *` 注解，同时存在 `FavoriteMapper.xml`）；22 个 Mapper 里 **14 个有 XML、8 个纯注解**；`HotArticleMapper` 接口与 XML 都是空的。
- **配置残留**：`blog-security/src/main/resources/application.properties` 里的 `spring.application.name=blog-security` 会覆盖 `application.yml` 的 `blog-service`（同位置 `.properties` 优先于 `.yml`）。
- **构建残留**：`blog-service/pom.xml` 重复声明 `pagehelper-spring-boot-starter`（一次带 version），Maven 报 "malformed project"；`.mvn/wrapper` 在根和 4 个子模块（blog-common / blog-pojo / blog-security / blog-service）各一份，但没有 `mvnw`/`mvnw.cmd` 脚本，wrapper 实际不可用。
- **异常/响应**：`Result`/`ResultCode` 字段拼写为 `massage`；`code` 与 HTTP status 重复；业务异常一律返回 500；安全过滤器与 `GlobalExceptionHandler` 两处都在处理 401/403。

### 1.4 P2 · 数据库（你关心的第 4 点）

**仓库没有权威 schema**：`docs/blog.sql`、`docs/init.sql` 是最旧的脚本，且**在工作区被删除未提交**（仍可从 git HEAD 复原）。代码里在用的 `article_collection`、`article_collection_relation`、`material`、`material_folder`、`appeal` 等表**在脚本中根本不存在** → 表在测试库上手工建的，schema 已漂移。

**冗余/死表（已交叉验证源码引用数）**：

| 表 | 结论 | 依据 |
|----|------|------|
| `article_category_relation` | 死表 | 全源码 0 引用；`article.category_id` 已表达同一关系 |
| `hot_article` | 死表 | 表名 0 引用，`HotArticleMapper` 为空接口 + 空 XML |
| `label` | 死表 | 仅 `Label` 实体 + `ReportController` 一处未使用 import |
| `theme` | 死字典表 | 只用到 `user.theme_id` 字段，`theme` 表从不查询 |
| `test` | Demo 遗留 | 仅 `TestMapper/TestService/TestController/TestMessage` |
| `identify_type` | 可枚举化 | 只作 `user_identify.type` 的字典 join |

**索引**：除主键/唯一键外几乎**没有任何索引**。`article(user_id)`、`article(category_id)`、`article(status, create_time)`、`user_comment(article_id)`、`user_comment(user_id)`、`user_comment(f_id)`、`email(receiver_id, status)`、`report(object_type, object_id)`、`user_article_browse_log(article_id)`、`user_article_browse_log(user_id, create_time)`、`subscribe(sub_user_id)`、`user_like_article(article_id)`、`favorite(user_id)` 全部缺失。

**结构问题**：
- 所有关系表都带自增 `id` 主键，应改联合主键（`admin_role_relation`、`role_permission_relation`、`role_menu_relation`、`favorite_article_relation`、`identify_user_relation`、`user_like_article`、`user_like_comment`、`subscribe`）；
- 状态字段用 `tinyint` + 魔法数，常量类写成 `public static int`（可变、非 final），且 `ArticleStatusConstant`/`ReportStatusConstant` 风格都不一致；
- 无外键、无逻辑删除、无乐观锁；删除靠代码手工级联；
- `article.content` 为 `text`（64KB 上限），`article.icon`/`user.icon` 用 `mediumtext` 存 URL（应为 `varchar`）；
- 存在 `select *`（`ArticleMapper.xml`、`UserMapper.xml`、`MenuMapper.xml`、`PermissionMapper.xml`、`MaterialMapper.xml` 等）。

### 1.5 P3 · 测试与工程流程（你关心的第 5、6 点）

- **0 个测试**：5 个模块**都不存在** `src/test` 目录；实测 `mvn test` 五个模块全部输出 `No tests to run`；只有 `blog-service` 引了 `spring-boot-starter-test`。
- **无 CI**：没有 `.github/`、没有 Dockerfile、没有 `.editorconfig`、没有 Checkstyle/SpotBugs/JaCoCo。
- **无提交规范**：无 commitlint、无 git hooks（`.git/hooks` 全是 `.sample`）；提交信息形如 `demo`、`add`、`run`、`build`、`admin demo02`。
- **Git 现状**：只有 `master` 一个分支、无 tag、无 PR 记录；同一人有两个身份（`liu <2075722023@qq.com>` 86 次、`liu <eve@email.com>` 28 次）；工作区有 5 个未提交的 `docs/` 删除。
- **设计文档流失**：`docs/security-design.md`（645 行，已含一套"安全模块重构方案 + 五阶段实施路线"）与 `docs/token-and-security-flow.md` 在工作区被删且未提交 —— 说明上一轮重构做到一半就停了，本次重构应**接续而非推翻**。

---

## 2. 重构方向（对应你提出的 6 点）

### 2.1 接口划分：一个资源一个控制器，消灭同义路径

- **统一前缀**：`/api/v1/public/**`（免登录）、`/api/v1/user/**`、`/api/v1/admin/**`、`/api/v1/{public|admin}/auth/**`。
- **命名规则**：资源复数 + kebab-case；只有非 CRUD 动作用语义化子资源（如 `POST /articles/{id}/publish`），**禁止** `/deleteXxx`、`/xxxDel`。
- **方法语义**：查询 GET、创建 POST、全量更新 PUT、局部更新 PATCH、删除 DELETE；**状态变更一律不许用 GET**。
- **拆分 God Controller**：`UserController` 拆为 `AuthController` / `UserProfileController` / `UserArticleController` / `FollowController` / `UserMessageController` / `UserHistoryController`。
- **合并冲突**：`assignPermission` 只保留一个（建议 `PUT /admin/roles/{id}/permissions`）；删除 `MenuController` 与 `MenuService/MenuServiceImpl`，只留 `MenuManageController`；举报与申诉合并为一个"内容治理"领域（`report` + `appealType` 字段，或明确二者边界并在文档中固化）。
- **统一契约**：请求 DTO 一律 `@RequestBody @Valid`；响应只暴露 VO（禁止直接返回 entity）；统一分页对象 `PageResult<T>`；统一错误模型（建议 RFC 7807 `ProblemDetail`）与 HTTP 状态码（400/401/403/404/409/422/500，业务异常不再一律 500）。
- **权限注解化**：用 `@RequirePermission("article:delete")`（或 `@PreAuthorize`）替代"URL 即权限键"，解决"表里漏配即放行"的根因；URL 改动不再需要改权限表。
- **接口文档即契约**：引入 springdoc-openapi，CI 产出 `openapi.json` 并做快照对比，阻止破坏性变更。
- **验收**：写一个测试断言所有 `@RequestMapping` 组合后路径全局唯一（当前会失败），并在迁移期对旧路径保留 301/别名 + `Deprecation` 响应头。

### 2.2 层级职能分工：单向下沉 + 依赖倒置

目标分层（web → application → domain ← infrastructure）：

```
blog-web            controller / filter / exception / request-response 模型
   ↓
blog-application    用例编排、事务边界、DTO↔领域模型转换、端口(interface)
   ↓
blog-domain         实体、值对象、领域服务、领域事件（不依赖 Spring/MyBatis）
   ↑ (实现端口)
blog-infrastructure MyBatis、OSS、Mail、Redis、WebSocket 实现
blog-common         纯工具、错误码、通用返回
```

- **规则固化**：用 **ArchUnit** 写测试断言：禁止 domain 依赖框架、禁止 web 直接依赖 infrastructure、禁止跨层回调、**禁止任何循环依赖**（配合移除 `allow-circular-references`）。
- **现实降本方案**：如果不想拆 5 个 Maven 模块，至少在 `blog-service` 内按 `web/application/domain/infrastructure` 分包，并保持同样的 ArchUnit 规则；模块拆分可作为后续独立 PR。
- **清理**：`blog-security` 只留技术安全设施（JWT 解析、Filter、密码编码、SecurityContext），把 `DynamicSecurityService` 实现与 `AdminUserDetailService` 移入 infrastructure；包名去掉多余的 `blog` 层级。
- **编码规范**：全面改**构造器注入**（`@RequiredArgsConstructor`）；服务之间不互相注入，跨聚合编排放 application 层或用领域事件；补齐 `@Transactional` 边界（只加在用例入口，明确传播与回滚）。
- **持久层统一**：**XML only**（或注解 only，二选一），删除重复 SQL 与空 Mapper；`select *` 一律改显式列 + `resultMap`。

### 2.3 框架革新：不换栈，换掉"老旧零件"

Spring Boot 3.4/Java 21 本身不老旧，重点是清理积木：

| 现状 | 问题 | 建议 |
|------|------|------|
| `javax.mail:1.6.2` + `javax.activation:1.1.1` | javax 命名空间，与 Jakarta EE 10 栈不一致，1.6.2 老旧 | `spring-boot-starter-mail`（Eclipse Angus `jakarta.mail`） |
| `gson 2.8.6` | 与已存在的 Jackson 重复；2.8.6 属受影响版本 | 删除 Gson，统一 Jackson |
| `pagehelper 2.1.0` + 手写分页混用 | 分页方式不统一，SQL 改写不透明 | 统一分页（MyBatis-Plus `IPage` 或统一 `RowBounds`/`LIMIT` + 明确规范） |
| 手写 `Pbkdf2PasswordEncoder`（310k 迭代） | 自研密码学；每次登录 CPU 开销大且无升级路径 | 用 Spring Security 官方 `Pbkdf2PasswordEncoder`（同格式可平滑迁移）或 Argon2/BCrypt |
| 每请求查库鉴权 | `JwtAuthenticationFilter` 对 admin 每请求查 admin+roles+permissions 共 3 次 DB | 权限快照进 Caffeine/Redis（已有 `dynamicSecurityMetadataSource` 缓存思路），token 只带身份，权限取缓存 |
| Token 黑名单 fail-open | Redis 挂掉时登出的 token 仍有效 | fail-closed，或改为"短 TTL access + 可撤销 refresh + token 版本号" |
| 无 refresh token | `application.yml` 声明了 `jwt.refreshExpiration` 但任何 profile 未定义、代码也没用；`/user/refreshToken` 是 GET 且只重签 access | 落地 refresh token（落 Redis/DB + 轮换 + 复用检测），access 缩短到 15–30 分钟 |
| 无 API 文档 | 无 springdoc | `springdoc-openapi-starter-webmvc-ui` |
| 手写 `DatabaseHealthCheck`（30s 心跳） | Hikari 已配 `keepalive-time` 足够 | 删除；如需健康检查用 Actuator |
| 无可观测性 | 无 Actuator/Micrometer | 引入 Actuator + Micrometer，暴露 health/metrics |
| 无 schema 迁移工具 | 表靠手工建 | **Flyway**（或 Liquibase）纳入版本 |
| 无映射工具 | 手写 entity↔VO 拷贝 | 可选 MapStruct |
| 无模块边界工具 | 靠自觉 | 可选 Spring Modulith（配合 2.2） |

> 版本升级建议：Spring Boot 升到 3.5.x 最新补丁并锁定；所有依赖版本集中到根 pom 的 `dependencyManagement`（当前子模块无版本，基本 OK，只需清理重复项）。

### 2.4 数据库结构优化

1. **建立唯一权威 schema**：从**测试库/生产库导出真实结构**，用 Flyway 生成 `V1__baseline.sql`；仓库只认 Flyway，手工脚本归档。
2. **删冗余表/列**（先确认线上无数据或做数据迁移）：`article_category_relation`、`hot_article`、`label`、`theme`、`test`；对应空实体/Mapper（`HotArticleMapper`、`TestMapper` 等）一并删除。
3. **关系表去 `id`**，改联合主键；统一外键命名（`xxx_id`）与 `ON DELETE` 策略（明确物理删 or `deleted_at` 软删，二选一并全项目一致）。
4. **加索引**（按上面的清单），逐个用 `EXPLAIN` 验证；高频列表加**覆盖索引**（如 `article(status, create_time, id)`）。
5. **状态字段枚举化**：`tinyint` 保留，但 Java 侧改 enum（`ArticleStatus`、`ReportStatus`…），删除 `public static int` 常量类，用 MyBatis `TypeHandler` 映射。
6. **字段类型修正**：`icon` 系 URL 字段 `mediumtext → varchar(512)`；`article.content` 评估 `mediumtext/longtext`；统一 `create_time/update_time` 为 `DEFAULT CURRENT_TIMESTAMP` / `ON UPDATE`，字符集统一 `utf8mb4_0900_ai_ci`。
7. **补唯一约束**：`admin.email`、`category.name` 等业务唯一键。
8. **可选检索**：文章标题需要模糊搜索时，先 FULLTEXT，规模上来再上 ES（P2，不进本次主流程）。
9. **验收**：Flyway `migrate` 从空库可一键建出完整结构；集成测试跑在 Testcontainers MySQL 上；关键查询 `EXPLAIN` 无 `ALL` 全表扫描。

### 2.5 自动化测试流程

**测试金字塔**（配合 CI 门禁）：

| 层 | 工具 | 覆盖对象 | 门禁 |
|----|------|----------|------|
| 单元 | JUnit 5 + Mockito | domain / application 用例 | 改动代码行覆盖 ≥ 70% |
| 切片 | `@WebMvcTest` + MockMvc | Controller 契约、参数校验、状态码 | 全部通过 |
| 集成 | `@SpringBootTest` + **Testcontainers**（MySQL 8 + Redis 7）+ Flyway | Mapper SQL、事务、缓存 | 全部通过 |
| 架构 | **ArchUnit** | 分层规则、无循环依赖、命名规范 | 全部通过 |
| 安全回归 | 上一层的用例集 | 越权矩阵（普通用户→/admin、未登录→受保护接口、登出 token 复用） | 全部通过 |
| 契约 | springdoc 快照 + RestAssured | OpenAPI 破坏性变更 | 变更需显式确认 |

**落地要点**：
- 第一个测试就写**能复现当前越权 bug** 的用例（普通用户 token 访问 `/admin/list` 必须 403），先红后绿。
- Testcontainers 保证"测试环境 = 生产 MySQL 8/Redis 7"，禁止 H2 mock SQL。
- 覆盖率先卡"新增/改动代码"（JaCoCo `changed-files`），避免一次性补齐历史代码的负担。
- 禁止长期存在 `@Disabled`；PR 不带测试不允许合并。

### 2.6 Git 提交流程规范化

**分支模型**：`main`（保护，禁止直推）+ `feature/*` + `fix/*` + `hotfix/*`；全部走 PR，至少 1 人 review + 状态检查通过，squash merge。

**提交规范**：Conventional Commits（`feat(article): 支持文章置顶`），用 commitlint + `commit-msg` hook；统一语言与 scope 列表。

**身份统一**：合并/统一作者身份（当前两个 email），设置仓库级 `user.name/user.email`。

**忽略规则重写**（关键）：
- 跟踪 `application.yml` / `application-dev.yml`（**占位符**形式，如 `${DB_PASSWORD}`）；
- 忽略 `application-local.yml`、`*.env`、`secrets/`；
- 提供 `application-example.yml`；
- 删除 `.gitignore` 里的 `*.yml` 与无意义的 `!**/src/main/**/target/` 取反规则。

**凭据治理（配合 P0-5）**：现有 DB/Redis/OSS/邮箱/JWT 凭据**全部轮换**（明文已落在本地文件与历史中），改用环境变量/CI secrets；用 gitleaks/trufflehog 扫历史后再清理。

**Hooks 与 CI**：`pre-commit`（格式化 + 秘密扫描 + 编译）、`pre-push`（单测）、CI（build + test + JaCoCo + ArchUnit + 依赖漏洞扫描 + openapi diff）。

**文档资产化**：恢复并提交 `docs/`（schema + 安全设计 + token 流程），补 README、CONTRIBUTING、`.editorconfig`、PR 模板、CODEOWNERS。

---

## 3. 实施流程（阶段化，含验收标准）

> 原则：**每个阶段独立可交付、可回滚**；先让"能跑 + 能测 + 能拦"，再动结构。

### 阶段 0 · 冻结与基线（1–2 人日）
- 处理工作区 5 个未提交的 `docs/` 删除（默认建议**恢复并提交**，这轮重构需要它们）；
- 从测试库导出真实 schema，确认 drift；
- **轮换所有泄露凭据**；
- 建 `main` 保护 + PR 模板 + 最小 CI 骨架；
- 先把资源目录从 `blog-service/src/resources` 移到 `src/main/resources`，删除 `.iml` 里的资源根 hack。
- **验收**：`mvn clean package` 产出的 jar **可 `java -jar` 启动**（连上测试库）。

### 阶段 1 · 止血（P0 安全 + 可运行）（3–5 人日）
- 修 `blog-service` pom 重复依赖、移除 `blog-security/application.properties`、移除 `allow-circular-references`（报错则定位真实环并消除）；
- **修越权**：`DynamicAuthorizationManager` 在 admin 链对非管理员直接拒绝；恢复 user chain 的 `authorizeHttpRequests`（默认拒绝 + 显式白名单：`/user/login`、`/user/save`、`/category/list`、`/ws/**`（需补鉴权））；
- `/menu/save` 归入 `/admin`，`/admin/comment/delete/{id}` 语义纠正；
- token 黑名单改 fail-closed。
- **验收**：越权回归测试全绿；`mvn clean verify` 通过；jar 可启动。

### 阶段 2 · 测试与流程地基（5–8 人日）
- 引入 Testcontainers + JaCoCo + ArchUnit + Checkstyle/Spotless；
- CI 流水线 + pre-commit/pre-push hooks + commitlint + Conventional Commits；
- 统一 Git 身份、重写 `.gitignore`、凭据改环境变量。
- **验收**：任意 PR 自动触发流水线并在超阈值/越权/循环依赖时红灯拦截。

### 阶段 3 · 接口规范化（8–12 人日）
- 按 §2.1 拆分 Controller、消除全部冲突路径、统一 DTO/VO/分页/错误模型与方法语义；
- 前端随后端重构后再重构 ⇒ **无需兼容别名/过渡层**，直接落最终路径；
- 引入 springdoc + OpenAPI 快照测试。
- **验收**：路径唯一性测试通过；OpenAPI 快照稳定；无遗留旧路径。

### 阶段 4 · 分层与依赖治理（10–15 人日）
- 按 §2.2 拆包（或拆模块）；`UserServiceImpl` 按用例拆分为若干服务；
- 全面构造器注入；补齐事务边界；统一 Mapper 风格（XML only）+ 删除空 Mapper/死代码；
- ArchUnit 规则全绿。
- **验收**：无循环依赖、无跨层调用；Service 单测覆盖核心用例。

### 阶段 5 · 数据库重构（8–12 人日）
- 库中几乎无数据且允许停机 ⇒ **直接 DROP → 用 Flyway 重建**，无需双写/数据迁移；
- Flyway baseline → 删冗余表/列 → 加索引 → 枚举化状态 → 字段类型/时间戳统一；
- **验收**：空库一键 `flyway migrate`；集成测试全绿；关键查询 `EXPLAIN` 无全表扫描。

### 阶段 6 · 框架与能力升级（8–12 人日）
- Mail/Gson/PageHelper 治理；权限注解化 + 权限缓存；refresh token 落地；
- 删除 `DatabaseHealthCheck`，引入 Actuator/Micrometer；可选 MapStruct/Spring Modulith。
- **验收**：登录耗时下降（消除每请求 3 次 DB 与自研 PBKDF2 热点）；监控面板可用。

### 阶段 7 · 收尾（2–3 人日）
- README/CONTRIBUTING/架构文档/运行手册；压测与 `EXPLAIN` 复核；灰度与回滚预案。

> 总量级：约 **40–60 人日**（因免去 API 兼容层与数据迁移而下调）。若人力有限，**阶段 0–2 是必须完成的**（否则后续重构无法验证、无法回归）。

---

## 4. 决策记录（已确认 2026-09-18）

| # | 议题 | 结论 | 对方案的影响 |
|---|------|------|--------------|
| 1 | 破坏性 API 变更 | ✅ 允许；前端在**后端重构完成后**再重构 | 阶段 3 **不需要**兼容别名/过渡层，直接落最终路径 |
| 2 | 持久层技术 | ✅ **已定：MyBatis-Plus** | 落地要点见 §5.4；弃用 PageHelper |
| 3 | 测试/生产库数据 | ✅ 几乎无数据 | 阶段 5 **无需数据迁移/双写** |
| 4 | 停机窗口 | ✅ 允许停机 | 无需兼容视图/在线迁移，可直接重建 |
| 5 | 新增依赖 | ✅ 允许 | Flyway、springdoc、Testcontainers、ArchUnit、MapStruct 等（注意 §7 环境约束） |
| 6 | 模块拆分 | ✅ 若利于新增与维护即可拆 | 采用 4 模块六边形结构（见 §5） |
| 7 | 分支与提交语言 | ✅ `main` + PR，提交用**英文** | Conventional Commits，type/scope 全英文 |
| 8 | 凭据管理 | ❓ 无经验 → 见 §6 Runbook | 列为阶段 0 强制项 |

### 持久层决策（已关闭）

选定 **MyBatis-Plus**：迁移成本最低（现有 14 个 XML Mapper 可平滑迁移）、SQL 完全可控、内置分页/逻辑删除/乐观锁，契合"多条件后台列表 + RBAC join + 统计"的场景。落地要点见 §5.4。

## 5. 目标架构蓝图（建议）

### 5.1 模块与依赖方向（编译期强制）

```
blog-common          纯工具 / 错误码 / 统一响应，零业务依赖
   ↑
blog-core            领域层 + 应用层：entity / 值对象 / 领域服务 / 用例 / 端口(interface)
   ↑
blog-infrastructure  适配器：MyBatis-Plus、Redis、OSS、Mail、WebSocket（实现 core 的端口）
   ↑
blog-web             Controller、安全过滤器、异常处理、OpenAPI、启动类
```

- 依赖只能向上（`web → infrastructure → core → common`），用 **ArchUnit** 固化，禁止回环与跨层。
- `blog-security` 退役：JWT 编解码/密码编码进 `blog-infrastructure`，Filter/安全上下文进 `blog-web`。
- 对比现有 5 模块：`blog-pojo` 并入 core，`blog-mapper` 并入 infrastructure，`blog-security` 拆分解散。
- 依赖方向由 Maven 模块天然强制（比单纯分包更硬），符合"利于后续新增与维护"。

### 5.2 包结构（以 blog-core 为例）

```
com.jxcia.blog.core
├── domain/{article,comment,user,rbac,report,...}      实体 / 值对象 / 领域服务
└── application/{article,comment,user,rbac,...}        用例(AppService) / 命令 / 查询 / 端口
```

### 5.3 技术选型（阶段 6 落地）

| 能力 | 选型 | 说明 |
|------|------|------|
| 持久层 | **MyBatis-Plus（已定）+ Flyway** | 去掉 PageHelper；分页插件需单独引 jsqlparser（见 §5.4） |
| 对象映射 | MapStruct | 替代手写 entity↔VO |
| API 文档 | springdoc-openapi | OpenAPI 快照进 CI |
| 校验 | Jakarta Validation | 统一 `@RequestBody @Valid` |
| 错误模型 | RFC 7807 `ProblemDetail` | 替代 `Result.massage` 拼写与"业务异常一律 500" |
| 密码 / 令牌 | Spring Security 官方 `Pbkdf2PasswordEncoder` / Argon2；双 token + 轮换 refresh | 删除自研 PBKDF2 与死配置 |
| 权限 | `@RequirePermission` 注解 + 权限快照缓存 | 替代"URL 即权限键" |
| 测试 | JUnit5/Mockito + MockMvc + Testcontainers + ArchUnit | 见 §2.5 |
| 可观测 | Actuator + Micrometer | 删除手写 `DatabaseHealthCheck` |
| 邮件 | `spring-boot-starter-mail`（Jakarta Angus） | 替代 javax.mail 1.6.2 |
| JSON | Jackson（删 Gson） | 统一序列化 |

### 5.4 持久层落地要点（MyBatis-Plus，已定）

**依赖（Spring Boot 3.x 必须用带 `spring-boot3` 后缀的 starter）：**

```xml
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
  <version>3.5.17</version>
</dependency>
<!-- v3.5.9 起分页等 JSqlParser 插件被拆出，必须单独引入，
     否则 PaginationInnerInterceptor 类找不到 -->
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-jsqlparser</artifactId>
  <version>3.5.17</version>
</dependency>
```

**必须同时删除**：`mybatis-spring-boot-starter`（与 MP 冲突）、`pagehelper-spring-boot-starter`（分页交给 MP）。

**配置类 `MybatisPlusConfig`**：注册 `MybatisPlusInterceptor`，按需加
`PaginationInnerInterceptor(DbType.MYSQL)`（分页）、`OptimisticLockerInnerInterceptor`（乐观锁）、
`BlockAttackInnerInterceptor`（防全表 update/delete）。

**编码约定**：
- 实体：`@TableName` / `@TableId(type = IdType.AUTO)` / `@TableField` / `@TableLogic`（逻辑删除）/ `@Version`；
- `create_time` / `update_time` 用 `MetaObjectHandler` 自动填充，业务代码不再手写；
- **简单单表 CRUD 用 `BaseMapper` + `LambdaQueryWrapper`；复杂查询（RBAC join、统计）继续用 XML**；
- 禁止 `select *`；XML 一律显式列 + `resultMap`。

**与现状的差异**：目前 14 个 XML Mapper 与 8 个注解 Mapper 混用 → 统一为「XML 承载复杂查询 + BaseMapper 承载单表 CRUD」。

## 6. 凭据轮换与环境变量管理（Runbook）

### 6.1 为什么必须做

当前这些"钥匙"以**明文**写在 `application-{dev,test,pro}.yml` 里：数据库密码、Redis 密码、OSS AccessKeyId/Secret、邮箱密码、`wanwei.api-key`、JWT 签名密钥。

风险：
- 拿到这份仓库或这台电脑的人，可直接连数据库、用你的 OSS（产生费用/删文件）、用你的邮箱发信；
- `.gitignore` 里的 `*.yml` 只保证"没提交"，**文件本身仍在磁盘上**，且会被 IDE 索引/备份/复制，随时可能外泄；
- JWT secret 泄露 = 任何人都能自行签发管理员 token。

### 6.2 第一步：轮换（Revoke & Rotate）

"轮换"= 去服务商处把旧密码/密钥作废，换新的。逐个做：

| 凭据 | 去哪里改 | 注意 |
|------|----------|------|
| MySQL 密码 | 库执行 `ALTER USER ... IDENTIFIED BY '...'` | 同步更新使用方 |
| Redis 密码 | `redis.conf` 的 `requirepass` | 需重载/重启 |
| OSS AccessKey | 阿里云控制台 → RAM 访问控制 | **新建 Key → 替换 → 删除旧 Key**；建议改用 RAM 子账号 + 最小权限（不要再用主账号 Key）。当前泄露的 AccessKeyId 以 `LTAI5t7…` 开头，必须优先处理 |
| 邮箱授权码 | 邮箱服务商后台重新生成 | 旧的立即失效 |
| `wanwei.api-key` | 对应平台后台 | |
| JWT secret | 自行生成 ≥32 字节随机值 | 换掉后**所有已签发 token 立即失效**；当前无生产用户，零成本 |

> 生成密钥：`openssl rand -base64 48`，或 PowerShell `[Convert]::ToBase64String((1..48|%{Get-Random -Max 256}))`。

### 6.3 第二步：配置改为「占位符 + 外部注入」

`application.yml`（**提交进 Git**，只放占位符）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:blog}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}
  data:
    redis:
      password: ${REDIS_PASSWORD}
jwt:
  secret: ${JWT_SECRET}
oss:
  access-key-id: ${OSS_ACCESS_KEY_ID}
  access-key-secret: ${OSS_ACCESS_KEY_SECRET}
```

`application-local.yml`（**加入 .gitignore，不提交**，本地放真实值）：
```yaml
spring:
  datasource:
    password: 本地明文密码
```

`.gitignore`（替换现有 `*.yml` 一刀切）：
```gitignore
application-local.yml
application-*.local.yml
.env
.env.local
secrets/
```

### 6.4 三种环境的取值方式

| 环境 | 方式 |
|------|------|
| 本地开发 | IDEA Run Configuration 的 Environment variables；或 `application-local.yml`（已忽略）；或 `.env` + spring-dotenv |
| CI | GitHub Actions Secrets（`${{ secrets.DB_PASSWORD }}` 注入为环境变量） |
| 测试/生产服务器 | 由部署平台注入环境变量；更稳妥用密钥管理服务（阿里云 KMS / Secrets Manager、Vault） |

### 6.5 第三步：防止再次泄露

- `pre-commit` 挂 **gitleaks**，命中密钥直接拒绝提交；
- CI 增加 `gitleaks detect` 扫全历史（历史里可能已存在）；
- 代码评审 checklist 增加"无明文密钥"；
- 新接入任何第三方 SDK，先确认"key 能否走环境变量"。

## 7. 环境约束（重要，需先了解）

本机 DSH 沙箱实测（2026-09-18）：

| 项 | 现状 | 结论 |
|----|------|------|
| Maven 本地仓库 | `E:\maven-repository`（在工作区 `D:\project\java\blog` **之外**） | `workspace-write` 下**写入被拒绝** |
| 出站 HTTPS | `workspace-write` 下**全部失败**（example.com、baidu.com 均不通）；HTTP 正常 | 无法下载依赖 |
| 出站 HTTPS（`danger-full-access`） | **正常**，镜像 `https://maven.aliyun.com/repository/public` 已配置 | 可下载 |
| 实测 | `archunit`、`springdoc-openapi-starter-webmvc-ui` 均 BUILD SUCCESS | ✅ 可行 |

**推荐工作流**（把提权次数降到最低）：
1. 把这一轮要用的依赖**一次性**写进各 pom；
2. 用**一条** `danger-full-access` 命令预热本地仓库：`mvn -B dependency:go-offline`；
3. 之后日常 `mvn -o clean verify`（离线）在默认 `workspace-write` 下即可正常跑；
4. 每新增一批依赖，重复 1–2。

> 这是环境层面的限制，不是代码问题。

---

## 附录 A · 死表 / 索引速查

**建议删除**：`article_category_relation`、`hot_article`、`label`、`theme`、`test`
**建议去 `id` 改联合主键**：`admin_role_relation`、`role_permission_relation`、`role_menu_relation`、`favorite_article_relation`、`identify_user_relation`、`user_like_article`、`user_like_comment`、`subscribe`
**建议新增索引**：
```sql
article(user_id), article(category_id), article(status, create_time),
user_comment(article_id, create_time), user_comment(user_id), user_comment(f_id),
email(receiver_id, status), report(object_type, object_id), report(user_id),
user_article_browse_log(article_id), user_article_browse_log(user_id, create_time),
subscribe(sub_user_id), user_like_article(article_id), favorite(user_id)
```

## 附录 B · 死代码 / 重复代码速查

- 空类：`IdentifyController`、`HotArticleMapper`（接口与 XML 皆空）
- Demo 遗留：`controller/test/*`、`service/test/*`、`mapper/test/TestMapper`、`TestMessage` + `test` 表
- 重复链：`MenuController` + `MenuService` + `MenuServiceImpl`（与 `MenuManage*` 重复）
- 双份 SQL：`FavoriteMapper`、`UserMapper`、`RoleMapper`、`MenuMapper`、`AdminMapper` 等同时存在注解 SQL 与 XML
- 8 个无 XML 的 Mapper：`TestMapper`、`AppealMapper`、`CategoryMapper`、`IdentifyMapper`、`MaterialFolderMapper`、`SubscribeMapper`、`UserLikeArticleMapper`、`UserLikeCommentMapper`

## 附录 C · 本次审计用到的可复现命令

```powershell
# 1) 验证打包缺配置（P0-1）
mvn -o clean package -DskipTests
jar tf blog-service/target/blog-service-0.0.1-SNAPSHOT.jar | Select-String 'application.*yml|xdb'

# 2) 验证零测试（P3）
mvn -o test          # 五个模块均输出 "No tests to run"

# 3) 复原被删除的 schema 与设计文档
git show HEAD:docs/init.sql
git show HEAD:docs/security-design.md

# 4) 死表交叉引用验证
Select-String -Path (Get-ChildItem -Recurse -File -Include *.java,*.xml blog-*/src | % FullName) -SimpleMatch 'article_category_relation','hot_article','label'
```
