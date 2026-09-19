# 指南 · 目标 Schema 设计（阶段 2.5）

> **这是设计指南，不是最终 DDL。**
> 你按 §2 的规矩和 §5 的清单产出建表脚本，我按 §8 的清单审。
> 前置：审计结论见 [`../REFACTORING_PLAN.md`](../REFACTORING_PLAN.md) §1 与附录 A；架构决策见 §4/§5。

---

## 0. 为什么把它提前

原来的排期把数据库放最后（阶段 5）。但**空库会让阶段 2 的集成测试、阶段 3 的接口冒烟全部做不了**。
既然已经决定"不导入旧 schema、从零设计"，那就一次设计到位 —— 后续每个阶段都有真实库可测，也不再产生一次性表。

---

## 1. 目标产出（四件）

1. **表清单文档**：每张表的用途、关键列、索引、约束；
2. **`db/migration/V1__init_schema.sql`**：自包含，从空库能跑出完整结构；
3. **`db/migration/R__seed_rbac.sql`**：可重复执行的种子数据（超管、角色、权限、菜单）；
4. **索引—查询对照表**：每个索引都写出它服务的具体查询。

---

## 2. 硬性设计规矩（12 条，先定下来）

| # | 规则 | 取值 | 理由 |
|---|------|------|------|
| 1 | 引擎 | `InnoDB` | 事务、行锁、外键 |
| 2 | 字符集/排序 | `utf8mb4` / `utf8mb4_0900_ai_ci` | MySQL 8 默认，支持 emoji |
| 3 | 表名 | **snake_case 复数**：`users` 而非 `user` | 避开关键字、语义上是集合 |
| 4 | 主键 | `id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` | 旧的 `INT` 长期会溢出 |
| 5 | 时间戳 | `created_at` / `updated_at` `DATETIME(3)`，DEFAULT CURRENT_TIMESTAMP(3)，updated 带 ON UPDATE | 毫秒精度、统一命名（替换旧的 `create_time`） |
| 6 | 软删除 | `deleted_at DATETIME(3) NULL` | 比 boolean 多保留"何时删" |
| 7 | 乐观锁 | `version INT UNSIGNED NOT NULL DEFAULT 0` | **只加在会被并发修改的聚合**（如 articles），别到处加 |
| 8 | 状态/类型 | 语义用 `VARCHAR(32)`，布尔用 `TINYINT(1)` | 避免魔法数字（审计发现的 `public static int` 问题） |
| 9 | 计数 | `BIGINT UNSIGNED` | 浏览/点赞数可能很大 |
| 10 | 外键 | 真父子关系用 FK + 明确 `ON DELETE`；多态/弱引用**不加** FK | 兼顾完整性与灵活性 |
| 11 | 命名 | 索引 `idx_表_列`、唯一 `uk_表_列`、外键 `fk_表_引用` | 可读、可批量运维 |
| 12 | 注释 | **每列都要 `COMMENT`**；状态列的注释里**列全枚举值** | 表结构即文档 |

---

## 3. 审计结论如何落到设计里

| 审计发现 | 设计对策 |
|----------|----------|
| 5 张死表（0 引用） | **不建**：`article_category_relation` / `hot_article` / `label` / `theme` / `test` |
| `article.category_id` 与 `article_category_relation` 表达同一关系 | 只保留 `articles.category_id` |
| 关系表都带多余自增 `id` | 全部改**联合主键** |
| `icon`/`content` 用 `mediumtext` | URL 类字段 `VARCHAR(512)`；正文才用 `MEDIUMTEXT` |
| 除主键外几乎无索引 | 见 §6 |
| 无逻辑删除、无乐观锁 | 见规矩 6/7 |
| 权限以 **URL 为键** | 改为 `code`（见 §4.1） |
| 状态用魔法数字 `public static int` | 改 `VARCHAR(32)` 枚举（见规矩 8） |

---

## 4. 关键设计决策

### 4.1 权限模型：从「URL 即权限」改为「资源:动作」

**现状**：`permissions.url = '/admin/article/delete/**'`，鉴权靠 URL 匹配。
→ 改接口路径就得改数据；表里漏配一个 URL，接口就**默认放行**（审计已确认）。

**目标**：
```sql
permissions(
  id, code VARCHAR(64) NOT NULL,      -- 如 article:delete
  name, group_name, description, created_at, updated_at,
  UNIQUE KEY uk_permissions_code (code)
)
```

- 权限是**稳定的业务能力标识**，与 HTTP 路径彻底解耦；
- 路由/菜单信息只留在 `menus` 表；
- 鉴权改为注解（阶段 6 落地）：`@RequirePermission("article:delete")`。

> 这是本次 schema 设计里**最重要的一条** —— 它决定了 `permissions`/`menus`/`role_permissions` 三张表的形态。

### 4.2 举报与申诉合并

现状是两套并行（`report` 与 `appeal`，字段语义重复，且 `appeal` 表在脚本里根本不存在）。
**目标**：一张 `reports` 表 + 两个区分维度：

- `kind`：`REPORT`（举报）/ `APPEAL`（申诉）
- `target_type` + `target_id`：多态目标（`USER`/`ARTICLE`/`COMMENT`）

顺带把原来那张没用的 `label` 表**用起来**，作为举报标签：`report_labels` + `report_label_rels`。

### 4.3 身份/徽章：三张混乱表 → 两张清晰表

现状：`user_identify`（身份定义）+ `identify_user_relation`（关系）+ `identify_type`（类型字典），命名混乱、无外键。
**目标**：`badges`（徽章定义，用 `level` 枚举替代 `identify_type` 表）+ `user_badges`（联合主键）。

### 4.4 主题与热门

- `themes` 表**从不被查询**，只有 `users.theme_id` 在用 → 删表；主题用 `VARCHAR(32)` 枚举保留在用户上（或直接删列）。
- `hot_articles` 表 0 引用、`HotArticleMapper` 是空接口 → **合进 `articles`**：`is_featured TINYINT(1)` + `sort`。

### 4.5 令牌不要建表

JWT 是无状态的，**不要为 access token 建表**。将来做 refresh token 建议存 **Redis**（带 TTL + 轮换 + 复用检测），也**不建表**。

---

## 5. 表清单（27 张）

> 「关键索引」列只是提示，你要在 §1 的对照表里写出**具体查询**。

### 身份与权限域（10）
| 表 | 用途 | 关键列 | 关键索引 |
|----|------|--------|----------|
| `users` | 用户 | `email` `nickname` `password_hash` `avatar_url` `bio` `theme` `show_likes` `status` | uk(email)；idx(status, created_at) |
| `admins` | 管理员 | `email` `nickname` `password_hash` `avatar_url` `status` | uk(email) |
| `roles` | 角色 | `code` `name` `description` `status` | uk(code) |
| `permissions` | 权限 | `code` `name` `group_name` `description` | uk(code)；idx(group_name) |
| `admin_roles` | 管理员↔角色 | PK(admin_id, role_id) | — |
| `role_permissions` | 角色↔权限 | PK(role_id, permission_id) | — |
| `menus` | 后台菜单 | `parent_id` `name` `route` `icon` `sort` `visible` | idx(parent_id, sort) |
| `role_menus` | 角色↔菜单 | PK(role_id, menu_id) | — |
| `badges` | 徽章定义 | `code` `name` `level` `description` | uk(code) |
| `user_badges` | 用户↔徽章 | PK(user_id, badge_id)；`granted_at` `granted_by` | idx(badge_id) |

### 内容域（12）
| 表 | 用途 | 关键列 | 关键索引 |
|----|------|--------|----------|
| `categories` | 分类 | `name` `sort` | uk(name) |
| `articles` | 文章 | `author_id` `category_id` `title` `summary` `content` `cover_url` `status` `is_featured` `sort` `published_at` `view_count` `like_count` `comment_count` `version` | idx(author_id, status, created_at)；idx(category_id, status)；idx(status, published_at)；可选 FULLTEXT(title, summary) |
| `comments` | 评论 | `article_id` `user_id` `parent_id` `content` `status` `is_pinned` `like_count` | idx(article_id, created_at)；idx(user_id)；idx(parent_id) |
| `article_browse_logs` | 浏览日志 | `user_id`(可空=游客) `article_id` `ip` | idx(article_id, created_at)；idx(user_id, created_at) |
| `article_likes` | 文章点赞 | PK(user_id, article_id) | idx(article_id) |
| `comment_likes` | 评论点赞 | PK(user_id, comment_id) | idx(comment_id) |
| `favorites` | 收藏夹 | `user_id` `name` `is_default` `sort` | idx(user_id) |
| `favorite_articles` | 收藏夹↔文章 | PK(favorite_id, article_id) | idx(article_id) |
| `collections` | 合集 | `user_id` `name` `description` `cover_url` `sort` | idx(user_id) |
| `collection_articles` | 合集↔文章 | PK(collection_id, article_id)；`sort` | — |
| `material_folders` | 素材文件夹 | `user_id` `parent_id` `name` | idx(user_id, parent_id) |
| `materials` | 素材 | `user_id` `folder_id` `url` `type` `size` `name` | idx(user_id, folder_id) |

### 社交与消息域（2）
| 表 | 用途 | 关键列 | 关键索引 |
|----|------|--------|----------|
| `subscriptions` | 关注 | PK(follower_id, followee_id)；`is_pinned` | idx(followee_id) |
| `messages` | 站内信 | `sender_id` `receiver_id` `title` `content` `is_read` `read_at` | idx(receiver_id, is_read, created_at) |

### 治理域（3）
| 表 | 用途 | 关键列 | 关键索引 |
|----|------|--------|----------|
| `reports` | 举报/申诉 | `kind` `target_type` `target_id` `reporter_id` `reason` `status` `handler_id` `result` `handled_at` | idx(status, created_at)；idx(target_type, target_id)；idx(reporter_id) |
| `report_labels` | 举报标签 | `name` | uk(name) |
| `report_label_rels` | 举报↔标签 | PK(report_id, label_id) | — |

**明确不建**：`article_category_relation`、`hot_article`、`theme`、`test`、`identify_type`、旧的 `label`、`access_token`。

---

## 6. 索引策略

**原则**：复合索引按「等值 → 范围 → 排序」排列；某列一旦用了范围条件，它后面的列就无法再走索引。

**常见查询模式 → 索引**：

| 查询 | 索引 |
|------|------|
| `WHERE status='PUBLISHED' ORDER BY published_at DESC LIMIT 20` | `(status, published_at)` |
| `WHERE author_id=? AND status=? ORDER BY created_at DESC` | `(author_id, status, created_at)` |
| `WHERE category_id=? ORDER BY created_at DESC` | `(category_id, created_at)` |
| `WHERE receiver_id=? AND is_read=0 ORDER BY created_at DESC` | `(receiver_id, is_read, created_at)` |

**纪律**：
- 每个索引必须能对应到 §1 对照表里的**具体查询**，写不出来就别建；
- 避免冗余：`(a,b)` 已经覆盖了 `(a)`；
- 验证手段：`EXPLAIN`（或 `EXPLAIN ANALYZE`），目标是 `type` 不为 `ALL`、`rows` 合理。

---

## 7. Flyway 约定

- 目录：`blog-infrastructure/src/main/resources/db/migration`（当前阶段可先在 `blog-service`）；
- 版本化脚本：`V1__init_schema.sql`、`V2__add_xxx.sql` —— **已执行过的 V 脚本绝不能改**；
- 种子/字典数据用**可重复脚本**：`R__seed_rbac.sql`（每次校验和变化才重跑，方便同步权限）；
- **必须加依赖**：`org.flywaydb:flyway-mysql`（Flyway 10 起 MySQL 支持被拆成独立模块，只引 `flyway-core` 会报"不支持"）；
- 配置：`spring.flyway.enabled=true`、`locations=classpath:db/migration`；
- 测试库 `blog_test` 由测试启动时自动 migrate（阶段 2 方案 C）；
- 社区版**没有 undo**：回滚 = 再写一个 V 脚本。

---

## 8. 你要产出的东西 & 验收标准

**产出**：
1. `docs/db/schema-design.md`（§5 的表清单 + 索引—查询对照）
2. `db/migration/V1__init_schema.sql`
3. `db/migration/R__seed_rbac.sql`

**验收（我会逐条对）**：

- [ ] 空库跑 `V1` + `R__seed` 成功，表清单与 §5 一致
- [ ] **没有**那 5 张死表
- [ ] 所有关系表都是**联合主键**（没有多余自增 `id`）
- [ ] 每个外键列都有对应索引
- [ ] 每个状态/类型列的 `COMMENT` 里**列全了枚举值**
- [ ] §5 的索引清单全都有落点（或说明为什么不需要）
- [ ] 关键查询 `EXPLAIN` 无 `ALL`
- [ ] `permissions` 用 `code` 而非 `url`
- [ ] 全文没有 `mediumtext` 存 URL、没有魔法数字状态

---

## 9. 我会怎么审

按 §2 的 12 条规矩逐表过一遍 —— 重点看：**命名一致性**、**索引与查询的对应关系**、
**是否还有"URL 即权限"的残留**、**关系表主键设计**、**枚举注释是否完整**。

> 建议你先只设计**身份与权限域（10 张）** —— 它是阶段 1/6 的直接依据，其余域可以在阶段 4 前后再补。
