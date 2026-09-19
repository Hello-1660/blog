# 实施指南 · 总索引

> 这套指南面向**你自己动手实现**的重构。我（AI）负责架构、决策、施工图与答疑；你负责写代码与执行。
> 每份指南都包含：**目标 → 原理 → 分步操作 → 验收命令 → 常见坑**。

> 📌 **先读进度真相源**：[`../PROGRESS.md`](../PROGRESS.md) —— 需求、决策、当前进度与"下一步"都记录在那里。

## 一、怎么使用这套指南

1. **一次只做一份指南、一个 Step**。不要跳步，也不要同时改两件事（改配置和加依赖分开提交）。
2. **每个 Step 结束先跑「验收命令」**，通过再往下。
3. **每个 Step 一个 commit**，英文 Conventional Commits（见下）。
4. **遇到 bug 先写测试复现**（红），再修（绿），最后提交。这是本项目的核心学习方法。
5. 卡住时把这三样发我：**完整报错 + 相关代码 + 你期望的行为**。

### 提交信息约定（英文）

```
<type>(<scope>): <subject>

type:  feat | fix | refactor | chore | test | docs | build | ci | perf | style
示例:
  chore(build): move resources to src/main/resources
  fix(security): deny non-admin access to /admin/**
  test(security): add regression test for admin authorization bypass
  docs(guides): add phase 0 baseline guide
```

### 分支约定

```
main            受保护，禁止直推
feature/<topic> 如 feature/phase0-baseline
fix/<topic>     如 fix/admin-authz-bypass
```

## 二、阶段路线图

| 阶段 | 文档 | 目标 | 你会学到 | 预计 |
|------|------|------|----------|------|
| 0 | `01-phase0-baseline.md` | 让命令行产物能跑、配置与密钥归位、Git 基线 | Maven 资源与打包、Spring 配置分层与占位符、环境变量、wrapper、分支保护 | 1–2 天 |
| 1 | `02-phase1-security.md` | 修越权、恢复用户端授权、堵住绕过入口 | Spring Security 6 过滤器链、`AuthorizationManager`、如何写第一个回归测试 | 3–5 天 |
| 2 | （待写） | 测试与 CI 地基 | JUnit5/MockMvc、Testcontainers、JaCoCo、ArchUnit、GitHub Actions、hooks | 5–8 天 |
| 3 | （待写） | 接口规范化 | REST 语义、DTO/VO、统一错误模型、springdoc、路径唯一性测试 | 8–12 天 |
| 4 | （待写） | 分层与依赖治理 | 六边形分层、端口/适配器、构造器注入、事务边界、ArchUnit 规则 | 10–15 天 |
| 5 | （待写） | 数据库重构 | Flyway、索引设计、`EXPLAIN`、联合主键、枚举与 TypeHandler | 8–12 天 |
| 6 | （待写） | 框架与能力升级 | MyBatis-Plus、MapStruct、权限注解化与缓存、双 token、Actuator | 8–12 天 |
| 7 | （待写） | 收尾 | 文档、压测、运行手册 | 2–3 天 |

> 指南是**按需增量编写**的：后面的阶段依赖前面阶段的结果（例如阶段 3 的路径规范依赖阶段 2 的测试），
> 提前写会写偏。你完成当前阶段后告诉我，我写下一份。

## 三、三条铁律

1. **先让它能验证，再让它更好**：没有测试的重构等于赌博。阶段 0–2 没做完，不要动接口和分层。
2. **一次只变一件事**：升级 Spring Boot、加依赖、改配置要分开提交，出问题才知道是谁干的。
3. **不要发明安全算法**：密码、令牌、加密一律用 Spring Security 官方实现。

## 四、环境备忘（2026-09-18 实测）

| 项 | 结论 |
|----|------|
| 文件权限 | 当前 `danger-full-access`，可读写工作区外（`E:\maven-repository`），也**不再需要授权确认** |
| 出站网络 | 可用；已配置阿里云镜像 `https://maven.aliyun.com/repository/public` |
| 本地 Maven 仓库 | `E:\maven-repository` |
| 离线构建 | 依赖齐全时 `mvn -o ...` 可离线跑；`-o` 在缺依赖时会直接失败 |
| 关键版本 | Spring Boot 3.4.13（可升 3.5.16）、Java 21、MyBatis-Plus 3.5.17、springdoc 2.9.1（Boot 3 用 2.x）、Testcontainers 1.21.4、ArchUnit 1.5.0 |

## 五、参考文档

- 完整审计与方向：[`../REFACTORING_PLAN.md`](../REFACTORING_PLAN.md)
- 凭据轮换与环境变量：`../REFACTORING_PLAN.md` §6
- 环境约束说明：`../REFACTORING_PLAN.md` §7
