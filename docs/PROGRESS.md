# 项目长期记忆 · 需求与进度

> **这是本项目的唯一进度真相源（single source of truth）。**
> 任何会话/任何人继续工作前，按顺序读：**本文件 → `REFACTORING_PLAN.md` → 对应阶段的指南**。
> 每完成一个 Step，请更新 §5 进度看板与 §10 变更日志。

最后更新：**2026-09-18**

---

## 1. 项目与分工

| 项 | 说明 |
|----|------|
| 项目 | 个人博客系统，Spring Boot 多模块 + MySQL + Redis（作者的**练手项目**） |
| 仓库 | `D:\project\java\blog`，远端 `https://github.com/Hello-1660/blog.git` |
| 分工 | **用户本人写代码与执行**；AI 只做架构设计、决策、写文档、答疑，**不改源码** |
| 含义 | `docs/` 是施工图，实现由用户按图施工；AI 的产出只有文档 |

> ⚠️ AI 请勿直接修改 `blog-*/src/**` 下的业务代码，除非用户明确改口。

---

## 2. 需求

### 2.1 原始需求（用户提出，6 项）

1. **接口划分**：确保每个接口没有语义冲突；
2. **层级职能分工**：每一层分工明确，不出现循环依赖；
3. **框架革新**：去除老旧框架，使用更高效、安全、易拓展的技术；
4. **数据库结构优化**：删除冗余表，添加索引；
5. **自动化测试流程**：添加规范化的测试流程；
6. **Git 提交流程规范化**。

### 2.2 用户补充约束（2026-09-18）

- 可以对项目整体**随意修改**，不用兼容旧接口；**前端会在后端重构完成后再重构**；
- **不坚持 MyBatis**，可引入其他持久层技术 → 最终选定 **MyBatis-Plus**；
- 测试库/生产库**几乎没有数据**，影响不大；
- **允许数据库停机**；
- **允许新增依赖**；
- 若有利于后续新增与维护，**可以拆模块**；
- 提交与分支规范**用英文**；
- 用户**未接触过凭据管理** → 已写 Runbook（`REFACTORING_PLAN.md` §6）；
- 时间充裕，**不赶工**。

---

## 3. 已确认决策（不要随意推翻）

| # | 议题 | 结论 |
|---|------|------|
| 1 | 破坏性 API 变更 | ✅ 允许；不需要兼容别名/过渡层 |
| 2 | 持久层 | ✅ **MyBatis-Plus 3.5.17**（+ `mybatis-plus-jsqlparser`，见 §6） |
| 3 | 数据迁移 | ✅ 无需；库内几乎无数据 |
| 4 | 停机 | ✅ 允许；数据库可直接 DROP 重建 |
| 5 | 新增依赖 | ✅ 允许（Flyway / springdoc / Testcontainers / ArchUnit / MapStruct…） |
| 6 | 模块拆分 | ✅ 采用 4 模块六边形结构 |
| 7 | Git | ✅ `main` + PR，**英文** Conventional Commits |
| 8 | 凭据 | ✅ 全部轮换 + 环境变量注入 |
| 9 | 角色分工 | ✅ 用户写代码，AI 只写文档/指导 |
| 10 | 数据库 schema | ✅ **不导入旧脚本**（`docs/init.sql` 是一次性的）；改为**从零设计 + Flyway V1** —— 见 §13 |

---

## 4. 目标架构（摘要）

**依赖方向（Maven 编译期强制，ArchUnit 固化）：**

```
blog-common  →  blog-core  →  blog-infrastructure  →  blog-web
工具/错误码     领域+应用      适配器(MP/Redis/OSS/Mail)   Controller/安全/启动
```

**技术选型：** MyBatis-Plus + Flyway（持久层）｜MapStruct（映射）｜springdoc 2.9.1（API 文档）｜
RFC 7807 ProblemDetail（错误模型）｜Spring Security 官方密码编码器 + 双 token｜
`@RequirePermission` 注解 + 权限缓存｜JUnit5+MockMvc+Testcontainers+ArchUnit（测试）｜
Actuator+Micrometer（可观测）｜spring-boot-starter-mail（邮件）｜Jackson（删 Gson）。

**关键版本（2026-09-18 实测阿里云镜像）：** Spring Boot 3.4.13（建议升 **3.5.16**）｜Java 21｜
MyBatis-Plus **3.5.17**｜springdoc **2.9.1**（Boot 3 用 2.x，3.x 是 Boot 4）｜Testcontainers 1.21.4｜ArchUnit 1.5.0。

---

## 5. 进度看板

| 项 | 交付物 | 状态 | 负责 |
|----|--------|------|------|
| 现状审计 | `REFACTORING_PLAN.md` §1 | ✅ 完成 | AI |
| 重构方向 + 流程 | `REFACTORING_PLAN.md` §2–§3 | ✅ 完成 | AI |
| 决策记录 / 蓝图 / Runbook | `REFACTORING_PLAN.md` §4–§7 | ✅ 完成 | AI |
| 指南索引 | `guides/00-index.md` | ✅ 完成 | AI |
| 阶段 0 指南 | `guides/01-phase0-baseline.md` | ✅ 完成 | AI |
| 阶段 1 指南 | `guides/02-phase1-security.md` | ✅ 完成 | AI |
| **阶段 0 执行** | 0.1–0.8 **全部完成** ✅（含 `java -jar` 实测启动 + 本地密钥移出 jar） | ✅ **完成** | 用户 |
| 环境（新 VM 192.168.48.128） | MySQL **8.4.11** ✅ ｜ Redis **8.10.2** ✅ ｜ Docker **29.8.1** + 镜像源 ✅ ｜ `application-local.yml` 已改 ✅ | ✅ 完成 | 用户 |
| **阶段 1 执行** | 越权修复 + 第一个回归测试 | 🟡 **进行中** | 用户 |
| 阶段 2–7 指南 | —— | ⬜ 未编写（等阶段 0/1 结果） | AI |
| 阶段 2–7 执行 | —— | ⬜ 未开始 | 用户 |
| 凭据轮换 | OSS/DB/Redis/邮箱/JWT/VM 口令 | 🟡 大部分已做；**VM 弱口令待改**；旧 `application-{dev,test,pro}.yml` 已删 | 用户 |
| **目标 schema 设计** | 全新设计（不继承旧表）→ Flyway V1 | ⬜ 未开始（建议紧随阶段 2，见 §13） | AI 设计 + 用户实施 |

> 图例：✅ 完成 ｜ 🟡 进行中 ｜ ⬜ 未开始

---

## 6. 下一步（Resume Here）

**进度（2026-09-19 复核）**：阶段 0 的 0.1–0.4 与 0.5 前半已完成，且已实测：

| 已验证项 | 证据 |
|----------|------|
| 资源进包 | 产物含 `application.yml` + `ip2region*.xdb`（Step 0.2 通过） |
| 占位符自洽 | `java -jar` 已能激活 profile `local`、初始化 Tomcat/Hikari，**不再报 `Could not resolve placeholder`** |
| wrapper 可用 | `.mvnw -v` → Apache Maven 3.9.16 ✅ |
| profile 精简 | `src/main/resources` 只剩 `application.yml` + `application-local.yml`（dev/test/pro 已删） |

**状态（2026-09-19）：应用已成功启动 ✅**

```
BlogHikariPool - Added connection com.mysql.cj.jdbc.ConnectionImpl@...   ← MySQL 真连上
WARN DynamicSecurityMetadataSource : 启动期加载权限失败，将延迟到首次请求时重试: Table 'blog.permission' doesn't exist
 Started BlogServiceApplication in 2.572 seconds                          ← Tomcat 8080
```

👉 容错改动按设计工作：**空库不再阻塞启动**（只留一条 WARN）。

**阶段 0：✅ 全部完成（2026-09-19 AI 复核通过）**

| 验收项 | 证据 |
|--------|------|
| 0.1–0.5 构建 / 配置 / wrapper | ✅ |
| 0.6 docs 归档 | ✅ `docs/legacy/` |
| 0.7 Git 基线 | ✅ `main` + `feature/phase0-baseline`，均已推送（远端 `main` = c97e23c） |
| 0.8 Boot 升 3.5.16 | ✅ jar 内含 `spring-boot-3.5.16.jar` |
| **jar 实测启动** | ✅ AI 实跑 `java -jar`：`Started BlogServiceApplication`，连上 MySQL |
| **密钥不随产物分发** | ✅ `config/application-local.yml` 已移出 classpath；jar 内**只剩** `application.yml` |

⇒ **P0-1（打包缺配置）与"本地密钥进 jar"均已关闭。**

**下一步：阶段 1 · 安全止血** —— 见 [`guides/02-phase1-security.md`](guides/02-phase1-security.md)。
从 **Step 1.1** 开始：**先写一个会失败的单元测试**复现「普通用户能访问 `/admin/**`」——
纯单元测试，**不需要数据库**。

实测连通性：

| 目标 | 结果 |
|------|------|
| `192.168.48.128:3306` (MySQL) | ✅ 可达，服务端版本 **8.4.11**（从握手包读出） |
| `192.168.48.128:6379` (Redis) | ✅ 可达，返回 `NOAUTH` ⇒ **已设密码** |
| `192.168.48.128:22` (SSH) | ✅ **已开放**（旧虚拟机是关闭的） |
| `192.168.48.128:2375/2376` (Docker API) | ❌ 未暴露 —— **这是正确的安全默认值** |
| MySQL 看到的客户端 IP | **`192.168.48.1`**（NAT 网关）⇒ 授权必须覆盖 `'user'@'%'` 或该 IP |

驱动兼容性（实测）：**Connector/J 8.0.33 与 MySQL 8.4.11 协议兼容** —— 能完成握手并走到认证阶段
（返回干净的 `Access denied`，而非协议错误）⇒ **暂不需要改 pom**。

**启动期唯一的 DB 依赖（已全量扫描确认）**：

| 启动期钩子 | 是否碰数据库 |
|------------|--------------|
| `DynamicSecurityMetadataSource.afterPropertiesSet()` | ⚠️ **是** —— 查 `permission` 表 |
| `JwtTokenUtil.@PostConstruct` | 否（只用配置里的密钥） |
| `RedisConfig.afterPropertiesSet()` | 否（RedisTemplate） |
| `DatabaseHealthCheck.@Scheduled` | 否（`SELECT 1`，且启动 30s 后才跑） |
| `CommandLineRunner` / `ApplicationRunner` | 无（0 个） |

⇒ 两条路：
- **A（推荐）**：把 `afterPropertiesSet()` 改成**容错**（catch + warn，留空由懒加载重试）——
  这样空库也能启动，并顺手修掉"DB 一抖动就起不来"的真实脆弱点。这本来就是阶段 1 的改进项。
- **B**：只建 `permission` 一张表 + 种子数据做临时脚手架（阶段 5 会连同其它表一起删）。

**要做的事**：
1. 采用 A：改 `DynamicSecurityMetadataSource.afterPropertiesSet()`；✅ 已做（启动越过了这一关）
2. 补齐缺失的占位符 `email.password`（见下）；⬜ 待做
3. 改掉虚拟机的弱口令（**口令不入库**，见 §12）；⬜ 待做
4. 启动验证：`java -jar ...` → 期望 `Started BlogServiceApplication`。⬜ 待做

**占位符全量体检（2026-09-19）**：`@Value` 硬依赖共 13 个，**只有 1 个缺失**：

| key | 状态 |
|-----|------|
| `email.password` | ❌ `application.yml` 为 `${MAIL_PASSWORD}`（无默认值）且 `application-local.yml` 未覆盖 |
| 其余 12 个 | ✅ 有默认值或被 local 覆盖 |

修法：在 `application-local.yml` 追加 `email.password: <DirectMail 发信地址的 SMTP 密码>`（或设环境变量 `MAIL_PASSWORD`）。

> ⚠️ **澄清：阿里云邮件推送 ≠ 邮箱授权码，也不是 AccessKey**
> （依据官方文档 *Send emails using SMTP* / 快速入门）：
> - **用户名 = 发信地址**（即 `email.send-address`）
> - **密码 = 在邮件推送控制台为该发信地址设置的「SMTP 密码」**（"设置 SMTP 密码"）
> - 参数：host `smtpdm.aliyun.com` ｜ 未加密端口 **80** ｜ TLS/SSL 端口 **465**
>   —— 与 `SampleMailUtil` 里硬编码的 host/80 完全一致 ✓
>
> 临时不想配也可以填占位值（启动只是注入字符串，不会立刻连 SMTP），
> 但验证码发信会失败（`sendHtmlMail` 里 catch 后只打日志、返回 false）。

报错症状是 `Injection of autowired dependencies failed` —— **真正的 key 名在最后一个 `Caused by` 里**。

> 旧虚拟机 `192.168.1.104` 已废弃。

修完后回传以下三样，AI 才写「阶段 2 · 测试与 CI 地基」指南：

1. `jar tf blog-service/target/*.jar | Select-String 'application|ip2region'` 的输出（证明资源已进包）；
2. `java -jar` 启动日志最后 20 行（证明无 `Could not resolve placeholder`）；
3. `mvn -B -pl blog-service -am test` 的完整输出（证明测试从 0 变有）。

若阶段 1 的 Step 1.7（移除 `allow-circular-references`）报循环依赖，用户需把完整错误贴回。

### 阶段 0 的三个必须动作（用户）

1. `git checkout -- docs/` **恢复被删除的 5 个文件**（schema 与历史设计，是重构依据）；
   > 该命令只恢复**已跟踪**文件；`PROGRESS.md`、`REFACTORING_PLAN.md`、`guides/` 是新增的未跟踪文件，**不会被删除**。
2. **轮换全部凭据**（AI 无法代做）；
3. **把 `docs/` 提交入库**：`git add docs/ && git commit -m "docs: add refactoring plan, guides and progress record"`。
   只有提交过的内容才是真正不会丢的"长期记忆"。

---

## 7. 关键发现索引

详见 `REFACTORING_PLAN.md` §1。摘要：

- **P0 阻断**：Maven 打包缺 `application.yml`（资源在 `src/resources`，只靠 `.iml`）、管理端越权（非管理员放行）、用户端授权被整段注释、`POST /menu/save` 绕过管理链、凭据明文、`.gitignore` 的 `*.yml` 一刀切、`allow-circular-references: true`。
- **P1 接口**：`assignPermission` 双路径、菜单新增两套、`GET /admin/comment/delete/{id}` 语义相反、举报/申诉两套、6 处 GET 做状态变更、`UserController` 302 行 22 端点。
- **P2 数据库**：无权威 schema（`article_collection`/`material`/`appeal` 脚本里没有）；死表 `article_category_relation`/`hot_article`/`label`/`theme`/`test`（均 0 引用）；除主键外几乎无索引。
- **P3 流程**：0 个测试（5 模块都无 `src/test`，`mvn test` 全部 `No tests to run`）、无 CI、无 hooks、提交信息 `demo`/`add`/`run`、同一人两个 git 身份。
- **启动强依赖 DB（新发现）**：`DynamicSecurityMetadataSource.afterPropertiesSet()` 在**启动时**就查 `permission` 表，
  导致数据库一挂应用**完全起不来**。阶段 1/6 应改为懒加载 + 容错（Redis 已经做了降级，DB 没有）。

**量化事实（已实测）：** 28 个 Controller 文件（27 个带 `@RestController`）｜127 个方法映射｜
admin 链 52 个端点｜`permission` 表仅 51 条且以 URL 为键｜22 个 Mapper（14  XML + 8 纯注解）。

---

## 8. 环境事实（2026-09-18 实测）

| 项 | 结论 |
|----|------|
| 文件权限 | DSH 当前 `danger-full-access`，可读写工作区外；**审批提示已禁用**，不要请求提权 |
| 出站网络 | `workspace-write` 下 HTTPS 不通；**`danger-full-access` 下正常** |
| Maven 本地仓库 | `E:\maven-repository`（在工作区之外） |
| Maven 镜像 | `https://maven.aliyun.com/repository/public` 已配置可用 |
| Maven / Java | Maven 3.9.16（`D:\software\maven\...`）｜JDK 21.0.10 |
| 离线构建 | 依赖齐全时 `mvn -o ...` 可用；缺依赖会直接失败 |
| MySQL 可达性 | **`192.168.48.128:3306` 可达** ✅（服务端版本 **8.4.11**） |
| Redis 可达性 | **`192.168.48.128:6379` 可达** ✅ 但返回 `NOAUTH` ⇒ **已设密码**；不再需要 cpolar 隧道 |
| 虚拟机端口探测（新 VM） | `3306` ✅ ｜ `6379` ✅ ｜ `22`(SSH) **✅** ｜ `2375`/`2376`(Docker API) ❌ ｜ `80/443` ❌ |
| MySQL 看到的客户端 IP | `192.168.48.1`（NAT 网关）⇒ GRANT 要覆盖 `'user'@'%'` 或该 IP |
| 驱动兼容性（实测） | Connector/J **8.0.33 ↔ MySQL 8.4.11 协议兼容**（握手/认证协商正常） |
| Docker | 宿主机**未安装** Docker（无 CLI、无 Desktop、`C:\Program Files\Docker` 不存在）；WSL 存在但**无发行版**；虚拟机也**未开放 Docker API** ⇒ 阶段 2 测试策略需选择（见 §11） |
| Maven Wrapper | `.mvnw -v` → Maven 3.9.16（已生成并修正 distributionUrl） |

**实测命令备忘：**

```powershell
mvn -o -B clean package -DskipTests     # 离线打包
jar tf blog-service/target/*.jar        # 检查产物内容
mvn -B -pl blog-service -am test        # 跑某模块测试（-am 必加）
```

---

## 9. 文档地图

| 文档 | 内容 |
|------|------|
| `docs/PROGRESS.md` | **本文件**：需求、决策、进度、下一步 |
| `docs/REFACTORING_PLAN.md` | 总方案：§1 审计 ｜ §2 方向 ｜ §3 流程 ｜ §4 决策 ｜ §5 架构与技术选型（含 §5.4 MyBatis-Plus）｜ §6 凭据 Runbook ｜ §7 环境约束 |
| `docs/guides/00-index.md` | 指南用法、提交/分支约定、阶段路线图 |
| `docs/guides/01-phase0-baseline.md` | 阶段 0 实操：资源归位、配置分层、凭据、wrapper、Git |
| `docs/guides/02-phase1-security.md` | 阶段 1 实操：先写失败测试，再修越权/授权/绕过/fail-closed/循环依赖 |
| `docs/*.sql`（应移到 `docs/db/`） | 旧 schema 归档，阶段 5 由 Flyway 取代 |
| `docs/security-design.md`、`docs/token-and-security-flow.md` | 上一轮安全重构的设计（历史参考，做到一半停了） |

> ⚠️ 上述 `docs/*.sql`、`security-design.md`、`token-and-security-flow.md` **当前处于「已删除未提交」状态**，
> 需执行 `git checkout -- docs/` 恢复。

---

## 10. 变更日志

| 日期 | 变更 |
|------|------|
| 2026-09-19 | 新 VM（192.168.48.128）就绪：MySQL 8.4.11 / Redis 8.10.2 / Docker 29.8.1 + 镜像源；确认 10 项决策（含 schema 从零设计）；产出指南 03；应用首次成功启动 ✅ |
| 2026-09-18 | 完成全项目审计；产出 `REFACTORING_PLAN.md` v1→v3；确认 9 项决策（含 MyBatis-Plus）；编写指南 00/01/02；实测环境约束与依赖版本；建立本文件作为长期记忆与进度真相源。 |

---

## 11. 阶段 2 测试策略（待用户决定）

背景：MySQL 与 Redis 都在虚拟机 `192.168.48.128`（均可达）；宿主机没有 Docker；虚拟机已装 Docker 但**未暴露 API**。
测试（JVM）跑在**宿主机**上，Testcontainers 需要通过 `DOCKER_HOST` 找到 Docker 守护进程 —— Docker 在虚拟机里属于**远程 Docker**。

| 方案 | 复杂度 | 隔离性 / 可复现 | 安全 | 备注 |
|------|--------|------------------|------|------|
| **C. 直连虚拟机上的 MySQL/Redis**（推荐先做） | 低 | 一般（靠 `@Transactional` 回滚 + 独立 `blog_test` 库） | 好 | 无需 Docker；依赖虚拟机在线 |
| A. 宿主机装 Docker Desktop | 低 | 好 | 好 | 需装 Desktop（可能依赖 WSL2）；测试本地化 |
| B. 虚拟机里 Docker + 远程访问 | 中 | 好 | ⚠️ **需加固** | **推荐 SSH 隧道**：`DOCKER_HOST=ssh://<user>@192.168.48.128`（需先配免密密钥）。若走 TCP 则 `DOCKER_HOST=tcp://192.168.48.128:2375` + `TESTCONTAINERS_HOST_OVERRIDE=192.168.48.128`，且**裸 2375 = 把虚拟机 root 开放给局域网**，必须 TLS/白名单 |
| D. 在虚拟机里跑 Maven 测试 | 中高 | 好 | 好 | 虚拟机是 Linux，虚拟机的 `localhost:2375` 就是本地 —— 但要在虚拟机里装 JDK/Maven 并同步代码 |

**推荐：先用 C**（成本最低、无安全风险），等真的需要"每次测试从干净容器起"时再上 Testcontainers（A 或 B）。

> **更新（新虚拟机）**：`22` 已开放，方案 B 可走更安全的 **SSH 隧道**（不必暴露 2375）。
> 前置：宿主机生成密钥 → `ssh-copy-id <user>@192.168.48.128` → `DOCKER_HOST=ssh://<user>@192.168.48.128`。

---

## 12. 基础设施版本基线（2026-09 新虚拟机）

> 用户重建了虚拟机，需要安装 MySQL / Redis / Docker。以下是**与当前代码库对齐**的版本要求。

### 唯一的硬约束：MySQL 服务端 ↔ JDBC 驱动

根 `pom.xml` 的 `dependencyManagement` 里**显式钉住**了驱动版本：

| 项 | 值 | 说明 |
|----|----|------|
| `mysql.version`（项目钉住） | **8.0.33** | **覆盖**了 Spring Boot 的托管版本 |
| Spring Boot 3.4.13 托管的 mysql-connector-j | **9.5.0** | 被上面的钉版覆盖，实际不生效 |

⇒ 原建议：MySQL 装 8.0.x 最省事。

**实测更新（2026-09-19）：用户实际装了 MySQL 8.4.11。**
用 Connector/J **8.0.33** 直连该实例，得到的是干净的
`Access denied for user 'blog'@'192.168.48.1' (using password: YES)` ——
说明**协议层完全兼容**（握手、认证协商都正常），并非驱动不支持。

**结论：8.0.33 驱动可以连 MySQL 8.4.11，暂不必改 pom。**
但 8.4 的官方支持线是 Connector/J 8.1+/9.x，建议在 **Step 0.8 升 Boot 时**顺手删掉这条钉版，
让 Spring Boot 托管 9.5.0（向下兼容 8.0/8.4/9.x）—— 一次只变一件事。

> Redis 实测版本：**8.10.2** ✅ 满足 ≥ 7.x（Lettuce 6.4.2 兼容；项目只用基础命令）。
>
> ⚠️ **不要在命令行里带密码**：`redis-cli -a '<密码>'` 会把口令写进 `~/.bash_history`，
> 也会暴露在 `ps` 进程列表里（redis-cli 自己也会警告）。改用：
> ```bash
> export REDISCLI_AUTH='<密码>'   # 推荐，仅当前会话环境变量
> redis-cli INFO server | grep redis_version
> ```

### 推荐组合

| 组件 | 推荐 | 理由 |
|------|------|------|
| MySQL | **8.0.x（最新 8.0.4x）** 最省事 ｜ 或 **8.4 LTS**（需同步升驱动） | 8.0 与现有驱动/Flyway 零冲突；8.4 更现代但多一处变量 |
| Redis | **7.2 / 7.4，或 8.x** | 应用只用基础命令；Lettuce 6.4.2 兼容 Redis 6/7/8。**不要装 5.x/6.x** |
| Docker | **Docker Engine CE 最新稳定版**（官方 apt 源） | Linux 装 Engine 即可，**不需要 Docker Desktop**；Testcontainers 要求 Docker API ≥ 1.41（Engine 20.10+） |
| Flyway 附加依赖 | `org.flywaydb:flyway-mysql` | Flyway 10 起 MySQL 支持被拆成独立模块，只引 `flyway-core` 会报"不支持"（阶段 5 会用） |

### 其他必须对齐的项（最容易踩）

- **MySQL 必须监听 LAN**：`bind-address = 0.0.0.0`，并给远程用户授权（`'user'@'%'` 或宿主机 IP），否则宿主机连不上（症状就是 `CannotGetJdbcConnectionException`）。
- **Redis 同理**：`bind 0.0.0.0` + `requirepass` + 防火墙；**不要再依赖 cpolar 隧道**（新 VM 上 `192.168.48.128:6379` 可直连，且已设密码）。
- **时区**：VM 与 MySQL 时区设为 `Asia/Shanghai`，与 JDBC 的 `serverTimezone=Asia/Shanghai` 一致。
- **字符集**：MySQL 8 默认 `utf8mb4`；建库时显式指定。
- **VM 内存**：≥ 4 GB（MySQL + Redis + Docker 镜像）。
- **虚拟机口令安全（重要）**：用户当前的虚拟机口令是**弱口令**（用户已在会话中告知，**按约定不落盘、不入库**）。
  该机同时跑着 Docker（**docker 组 ≈ root**）、MySQL、Redis，且 22 端口开放 ⇒
  **必须改强密码，并改为 SSH 密钥登录 + 关闭密码登录**（`PasswordAuthentication no`）。
- **Docker（实测）**：Client **29.8.1**，守护进程**在运行**，但当前用户不在 `docker` 组 ⇒
  `permission denied while trying to connect to the docker API at unix:///var/run/docker.sock`。
  修复：`sudo usermod -aG docker $USER` 然后 `newgrp docker`（或重新登录）。
  注意 `docker` 组等价于 root 权限 —— 这也是上面"口令必须改强"的原因之一。
- **Docker Hub 不可达（实测 2026-09-19）**：`registry-1.docker.io` 被解析到 `199.59.148.102`（**DNS 污染**），
  `/v2/` 返回 000 ⇒ 拉镜像报 `dial tcp ...:443: connect: connection refused`。
  宿主机实测**可用**的镜像源：`docker.m.daocloud.io`(401)、`docker.1ms.run`(401)、
  `docker.1panel.live`(200)、`dockerproxy.net`(200)、`docker.xuanyuan.me`(401)；
  **不可用**：`hub-mirror.c.163.com`、`mirror.ccs.tencentyun.com`。
  ⇒ 在 `/etc/docker/daemon.json` 配 `registry-mirrors`，再 `systemctl restart docker`。
  ⚠️ 公共镜像源**经常失效**，此清单会过期；有阿里云账号时优先用控制台提供的个人加速地址
  `https://<id>.mirror.aliyuncs.com`（最稳定）。
  > Docker **只影响阶段 2**，且我们倾向的方案 C 并不需要它 ⇒ **不阻塞主线**。
- 若选**远程 Docker**（方案 B），需额外开放 Docker API 端口 —— **必须加 TLS 或防火墙白名单**（裸 2375 = 把虚拟机 root 开放给局域网）。

### 建库 DDL（建议不要用 root 远程连接）

```sql
CREATE DATABASE blog      CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE DATABASE blog_test CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;  -- 阶段 2 方案 C 用

CREATE USER 'blog'@'%' IDENTIFIED BY '<强密码>';
GRANT ALL PRIVILEGES ON blog.*      TO 'blog'@'%';
GRANT ALL PRIVILEGES ON blog_test.* TO 'blog'@'%';
FLUSH PRIVILEGES;
```

> 提醒：改了 MySQL 账号/端口后，记得同步更新 `application-local.yml`（见 §6 的卡点）。

---

## 13. 数据库 schema：改为「从零设计」（2026-09-19 决策）

### 决策
**不导入** 旧的 `docs/init.sql` / `blog.sql`。理由：模块要重构、安全流程要重做，表结构必然变动；
导进去只会产生一次性垃圾（何况其中还有 5 张已验证 0 引用的死表）。

### 因此阶段 5 的性质变了

| 原计划 | 现计划 |
|--------|--------|
| 以现有库为 baseline → 删冗余表 → 补索引 | **从零设计目标 schema** → 一条 Flyway `V1__init.sql` 落地（索引/约束内建） |

原审计结论**仍然有用，但用法变了**：
- 「死表清单」不再需要"删"，而是**根本不建**（`article_category_relation` / `hot_article` / `label` / `theme` / `test`）；
- 「索引清单」变成**设计输入**（见 `REFACTORING_PLAN.md` 附录 A）；
- 「字段/命名问题」（`icon` 用 mediumtext、状态用魔法数、关系表多余自增 `id`、无逻辑删除/乐观锁）在设计时一次性纠正。

### 排期调整（已确认 2026-09-19）

原计划把数据库放最后（阶段 5），但**没有表 ⇒ 阶段 2 的集成测试、阶段 3 的接口冒烟都做不了**。建议改为：

```
阶段 1   安全止血（纯单元测试，不需要库）        ← 当前位置
阶段 2   测试与 CI 地基（单元 + MockMvc 切片）
阶段 2.5 目标 schema 设计 + Flyway V1            ← 提前到这里
阶段 3   接口规范化（此时可以真跑 HTTP 冒烟）
阶段 4   分层与依赖治理（实体重写基于新 schema）
阶段 5   索引/查询复核（EXPLAIN 验证）
阶段 6   框架与能力升级
```

> **已确认**。设计指南已产出：[`guides/03-schema-design.md`](guides/03-schema-design.md)。
