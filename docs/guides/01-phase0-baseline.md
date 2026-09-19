# 阶段 0 · 基线与可运行

> 目标：让**命令行**打出的产物能跑起来，把配置与密钥归位，建立 Git 基线。
> 本阶段**不写任何业务代码**。做完之后，你才有资格进入阶段 1。
> 预计：1–2 天。前置：[总索引](./00-index.md)。

## 0. 验收标准（必须全部通过）

1. `mvn -B clean package -DskipTests` 后，`jar tf` 能看到 `application.yml` 与 `ip2region*.xdb`；
2. `java -jar blog-service/target/blog-service-0.0.1-SNAPSHOT.jar` 能启动并连上 MySQL/Redis；
3. 仓库里**没有任何明文密钥**，`.gitignore` 不再一刀切忽略 `*.yml`；
4. `git status` 干净、分支为 `main`、提交信息为英文 Conventional Commits；
5. Maven 不再有 `malformed project` 警告。

---

## 1. 原理：为什么现在 `java -jar` 跑不起来

三个概念必须先分清：

| 概念 | 含义 |
|------|------|
| `src/main/resources` | **Maven 约定的资源目录**。打包时会被复制到 `target/classes`，最终进入 jar 的 `BOOT-INF/classes` |
| `target/classes` | 编译输出目录，Maven 每次 `clean` 会删掉重建 |
| `.iml` / IDEA 配置 | **只影响 IDEA**，Maven 完全不看。你的 `blog-service.iml` 把 `src/resources` 标成了 `java-resource`，所以 IDEA 能跑，Maven 不认 |

你亲自验证一下这个差异（这就是 P0-1 的根因）：

```powershell
# 在 IDEA 里跑过 -> target/classes 有 yml；Maven clean 后就没了
mvn -B clean package -DskipTests
jar tf blog-service/target/blog-service-0.0.1-SNAPSHOT.jar | Select-String 'application.*yml|xdb'
# 输出为空 = 资源没打进去
```

---

## Step 0.1 先清理 pom 与误配置（让 Maven 干净）

**1) 删掉 `blog-service/pom.xml` 里重复的 pagehelper 声明。** 现在同一个依赖写了两次（一次带 version、一次不带），Maven 会报 `malformed project`。删掉其中一条。

**2) 删掉 `blog-security/src/main/resources/application.properties`。**
原因：里面是 `spring.application.name=blog-security`。同一个 classpath 下 `.properties` 优先级**高于** `.yml`，所以你的应用名其实被改成了 `blog-security`。这是残留文件，直接删除。

**验收**：

```powershell
mvn -B clean package -DskipTests
# 观察输出不再出现 "malformed project" 与 "thousands" 类警告
```

---

## Step 0.2 把资源搬回 Maven 约定的位置（本阶段最关键）

当前 `blog-service/src/resources/` 下有：`application*.yml`（未跟踪）和 `ip2region_v4.xdb`/`ip2region_v6.xdb`（已跟踪）。

```powershell
# 1) 建标准目录
New-Item -ItemType Directory -Force blog-service/src/main/resources | Out-Null

# 2) yml 是未跟踪文件 -> 普通移动即可
Move-Item blog-service/src/resources/*.yml blog-service/src/main/resources/

# 3) xdb 是已跟踪文件 -> 用 git mv 保留历史
git mv blog-service/src/resources/ip2region_v4.xdb blog-service/src/main/resources/ip2region_v4.xdb
git mv blog-service/src/resources/ip2region_v6.xdb blog-service/src/main/resources/ip2region_v6.xdb

# 4) 删掉空目录
Remove-Item blog-service/src/resources -Force
```

**同时清理 IDEA 的 hack**：删除 `blog-service/blog-service.iml`（它是 gitignore 的，IDEA 会重新生成），或在 IDEA 里
`File → Project Structure → Modules → blog-service` 里移除 `src/resources` 这个 java-resource 标记。**不要**再把 `.iml` 当成构建配置。

**验收**：

```powershell
mvn -B clean package -DskipTests
jar tf blog-service/target/blog-service-0.0.1-SNAPSHOT.jar | Select-String 'application|ip2region'
# 这次必须能看到 application.yml / application-local.yml / ip2region*.xdb
```

> 到这里，P0-1（打包缺配置）就算修完了。

---

## Step 0.3 配置分层：占位符进仓库，真值出仓库

### 目标结构（建议只保留两个文件）

| 文件 | 是否提交 | 内容 |
|------|----------|------|
| `application.yml` | ✅ 提交 | 所有**非敏感**公共配置 + 敏感项的 `${ENV_VAR}` 占位符 |
| `application-local.yml` | ❌ 加入 .gitignore | 你本机的真实密码 |
| `application-dev/test/pro.yml` | 建议删除 | 用环境变量区分环境，不再每个环境一份文件 |

### `application.yml` 改造要点

1. **profile 改成环境变量驱动**（现在硬编码 `active: test`）：
   ```yaml
   spring:
     profiles:
       active: ${SPRING_PROFILES_ACTIVE:local}
   ```
2. **敏感项全部换占位符**：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:blog}?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true
       username: ${DB_USERNAME:root}
       password: ${DB_PASSWORD}
     data:
       redis:
         host: ${REDIS_HOST:localhost}
         port: ${REDIS_PORT:6379}
         password: ${REDIS_PASSWORD}
         database: ${REDIS_DATABASE:0}
   jwt:
     secret: ${JWT_SECRET}
   oss:
     access-key-id: ${OSS_ACCESS_KEY_ID}
     access-key-secret: ${OSS_ACCESS_KEY_SECRET}
   email:
     password: ${MAIL_PASSWORD}
   ```
3. **删掉死配置** `jwt.refreshExpiration`：没有任何 Java 代码读它。
   ⚠️ **千万别手滑删掉 `jwt.accessExpiration`** —— 它被 `JwtTokenUtil` 用 `@Value` 读取，删了启动即失败。
4. 顺手修一个小 typo：URL 里 `connectTimeout=100008` → `10000`（若已删除该参数则忽略）。

### ⚠️ 自引用陷阱（`application.yml` 最容易踩的坑）

**`key: ${key}` 是自引用，不是"继承别处"。** 例如：

```yaml
jwt:
  tokenHeader: ${jwt.tokenHeader}     # 指向的正是它自己
```

它只在一种情况下"看起来能跑"：某个 **profile 文件**（如 `application-test.yml`）里也用**字面值**定义了同一个 key。
profile 文件优先级更高，字面值把这条自引用覆盖掉了；否则 Spring 会抛
`Circular placeholder reference` 或 `Could not resolve placeholder`。

所以把默认 profile 从 `test` 改成 `local` 之后，**原先靠 `application-test.yml` 兜住的那些非敏感 key 就全断了**。
结论：**非敏感值不要用占位符，直接写在 `application.yml` 里。**

### 启动强依赖的属性清单（`@Value` 扫出来的硬依赖）

| 属性 | 读取方 | 性质 | 建议 |
|------|--------|------|------|
| `jwt.secret` | `JwtTokenUtil` | 敏感 | `${JWT_SECRET}` + 本地真值 |
| `jwt.accessExpiration` | `JwtTokenUtil` | 非敏感 | 直接写 `604800000` |
| `jwt.tokenHeader` | `JwtAuthenticationFilter` | 非敏感 | 直接写 `Authorization` |
| `jwt.tokenHead` | `JwtAuthenticationFilter`、`TokenController` | 非敏感 | `"Bearer "`（**尾随空格，必须加引号**） |
| `email.send-address` / `reply-address` / `send-name` | `SampleMailUtil` | 非敏感 | 直接写 |
| `email.password` | `SampleMailUtil` | 敏感 | `${MAIL_PASSWORD}` |
| `verification-code.length` / `ttl` / `min-interval-time` / `minute-ip-limit` | `VerificationCodeUtil` | 非敏感 | 直接写默认值 |
| `wanwei.api-key` | `HttpUtil` | 敏感 | `${WANWEI_API_KEY}` + 本地真值 |
| `spring.datasource.password` | Spring | 敏感 | `${DB_PASSWORD}` |
| `spring.data.redis.password` | Spring | 敏感 | `${REDIS_PASSWORD:}` |
| `oss.access-key-id` / `access-key-secret` | `OssProperties` | 敏感 | `${OSS_ACCESS_KEY_ID}` / `${OSS_ACCESS_KEY_SECRET}` |
| `oss.endpoint` / `bucket-name` | `OssProperties` | 非敏感 | 直接写 |

**一条自检命令**（把含占位符的行全列出来，逐个确认：要么是你设的环境变量，要么由 `application-local.yml` 覆盖）：

```powershell
Select-String -Path blog-service/src/main/resources/application.yml -SimpleMatch '${' |
  ForEach-Object { $_.Line.Trim() } | Sort-Object -Unique
```

> 经验法则：**非敏感值不要用占位符**；占位符只留给"每个环境/每个人不同"或"不能进仓库"的值。
> 每多一层间接引用，就多一个启动时会炸的点。

> 语法提示：`${NAME:default}` 表示"取属性/环境变量 NAME，没有就用 default"。**有默认值的不会启动失败，没有默认值的缺失会启动失败**。

### `application-local.yml` 示例

```yaml
spring:
  datasource:
    password: 你本机的真实密码
  data:
    redis:
      password: 你本机的真实 redis 密码
jwt:
  secret: 你生成的 32+ 字节随机串
oss:
  access-key-id: 轮换后的新 Key
  access-key-secret: 轮换后的新 Secret
```

---

## Step 0.4 轮换凭据（本阶段唯一需要你去控制台操作的事）

完整清单与原理见 [`../REFACTORING_PLAN.md` §6](../REFACTORING_PLAN.md)。执行顺序：

- [ ] 阿里云 RAM：新建 AccessKey → 替换到本地配置 → **删除旧 Key**（旧的是 `LTAI5t7…` 开头）
- [ ] MySQL：`ALTER USER 'root'@'%' IDENTIFIED BY '<新密码>'; FLUSH PRIVILEGES;`
- [ ] Redis：改 `requirepass` 后重载
- [ ] 邮箱：后台重新生成授权码
- [ ] `wanwei.api-key`：对应平台重置
- [ ] JWT secret：`openssl rand -base64 48` 生成新值（会让所有旧 token 立即失效；你没有生产用户，零成本）

### 本地怎么把环境变量喂给程序

**方式 A（推荐，IDEA）**：`Run → Edit Configurations → 你的启动类 → Environment variables` 填入 `DB_PASSWORD=...;JWT_SECRET=...`。改完需要重启 Run Configuration。

**方式 B（更省事）**：把真值写在 `application-local.yml`（已忽略），不设任何环境变量。Spring 会自动加载 `application-local.yml`（因为默认 profile 是 local）。

> 目标只有一个：**`git status` 里永远看不到密钥**。

---

## Step 0.5 `.gitignore` 重写 + Maven Wrapper 归一

### `.gitignore`（替换掉现在的 `*.yml` 一刀切）

```gitignore
### Build ###
target/
!.mvn/wrapper/maven-wrapper.jar

### IDE ###
.idea/
*.iml
*.iws
*.ipr
.vscode/

### Local / secrets ###
application-local.yml
application-*.local.yml
.env
.env.local
secrets/
config/secret.properties

### OS ###
Thumbs.db
.DS_Store
```

> 注意顺序：`application-local.yml` 必须在**后面**覆盖前面的规则；不要再用 `*.yml`。

### Wrapper 归一

现在根目录和 4 个子模块（blog-common / blog-pojo / blog-security / blog-service）各有一份 `.mvn/wrapper/maven-wrapper.properties`，而且**没有 `mvnw` 脚本**，所以 wrapper 实际是坏的。

```powershell
# 1) 删掉子模块里重复的 wrapper
Remove-Item -Recurse -Force -ErrorAction SilentlyContinue blog-common/.mvn, blog-pojo/.mvn, blog-security/.mvn, blog-service/.mvn

# 2) 在根目录生成可用的 wrapper（会生成 mvnw / mvnw.cmd / .mvn/wrapper）
#    ⚠️ 必须加引号！见下方「PowerShell 的 -D 陷阱」
mvn -N wrapper:wrapper '-Dmaven=3.9.16'

# 3) 验证
.\mvnw -v
```

> ⚠️ **PowerShell 的 `-D` 陷阱（我第一版指南写错就在这里）**
>
> PowerShell 会把 `-Dmaven=3.9.16` 当作**参数名**解析，而参数名遇到 `.` 就结束，
> 于是它被**拆成两个参数**：`-Dmaven=3` 和 `.9.16`。Maven 拿到 `.9.16` 就报：
> `Unknown lifecycle phase ".9.16"`。
>
> 更坑的是：`-Dmaven=3` 仍然生效，于是生成出
> `distributionUrl=.../apache-maven/3/apache-maven-3-bin.zip` —— 一个**不存在的版本**，wrapper 直接是坏的。
>
> **规则：在 PowerShell 里，凡 `-D` 的值含 `.` 或 `:` 的，一律加引号。**
> 单引号、双引号都行；`--%`（停止解析）也可以，但用了 `--%` 之后**不能再接管道**。
>
> ```powershell
> mvn -N wrapper:wrapper '-Dmaven=3.9.16'      # ✅
> mvn test '-Dtest=MyTest#a.b'                 # ✅ 值含点号
> mvn -DskipTests package                      # ✅ 没有值，不受影响
> ```

之后你就能用 `./mvnw`（或 `mvnw.cmd`）而不依赖本机 Maven 版本。

---

## Step 0.6 恢复 docs、规范目录

工作区现在有 5 个 `docs/` 文件处于"已删除未提交"状态，它们是**这次重构的原始依据**，建议恢复：

```powershell
git checkout -- docs/
```

建议的目录结构（已就位的一部分）：

```
docs/
├── REFACTORING_PLAN.md      # 总方案（审计 + 方向 + 流程）
├── guides/                  # 本套实施指南
│   ├── 00-index.md
│   ├── 01-phase0-baseline.md
│   └── 02-phase1-security.md
├── db/                      # 把 init.sql / blog.sql 归档到这里（阶段 5 会被 Flyway 取代）
├── security-design.md       # 旧的安全设计（历史的，保留参考）
└── token-and-security-flow.md
```

```powershell
New-Item -ItemType Directory -Force docs/db | Out-Null
git mv docs/init.sql docs/db/init.sql
git mv docs/blog.sql docs/db/blog.sql
git mv docs/admin_rbac_init.sql docs/db/admin_rbac_init.sql
```

---

## Step 0.7 Git 基线

```powershell
# 1) 统一身份（现在仓库里有两个不同的 email）
git config user.name  "你的名字"
git config user.email "你的邮箱"

# 2) 分支改名 master -> main
git branch -m master main

# 3) 从 main 切出本次工作分支
git switch -c feature/phase0-baseline
```

推送并切换远端默认分支：

```powershell
git push -u origin main
git push origin --delete master      # 如果远端还需要 master，先别删
# GitHub → Settings → Branches → 默认分支改为 main
```

**保护 main**（GitHub → Settings → Branches → Add branch protection rule）：勾选
`Require a pull request before merging`、`Require status checks to pass`（阶段 2 有了 CI 再选具体检查）。

**本阶段提交（英文示例）**：

```
chore(build): move resources to src/main/resources
build(deps): remove duplicate pagehelper declaration
chore(config): externalize secrets via env placeholders
chore(git): stop ignoring all yml files
docs(db): archive legacy schema scripts
```

---

## Step 0.8（推荐）升级 Spring Boot 3.4.13 → 3.5.16

理由：3.4 已过 OSS 支持窗口；**趁没有测试和业务代码时升级最便宜**，一次只变一件事。

```powershell
# 只改根 pom 的 parent version
# <version>3.4.13</version>  ->  <version>3.5.16</version>
mvn -B clean verify
```

**注意**：本步**不要**同时加新依赖。若报错，把完整错误发我。常见变化点是测试相关注解（`@MockBean` → `@MockitoBean`）与少量废弃 API。

---

## 2. 验收命令清单（复制即用）

```powershell
# ① 打包产物包含配置与数据文件
mvn -B clean package -DskipTests
jar tf blog-service/target/blog-service-0.0.1-SNAPSHOT.jar | Select-String 'application|ip2region'

# ② 能启动（先在 IDEA 或本地设好 DB_PASSWORD 等环境变量）
java -jar blog-service/target/blog-service-0.0.1-SNAPSHOT.jar
# 期望：控制台无 "Could not resolve placeholder" 报错，端口 8080 起来

# ③ 仓库无密钥、无多余的 yml 忽略规则
git status --short
Select-String -Path .gitignore -SimpleMatch '*.yml'

# ④ 无 malformed 警告
mvn -B -q validate 2>&1 | Select-String 'malformed'

# ⑤ 分支与提交
git branch --show-current
git log --oneline -5
```

---

## 3. 常见坑

| 现象 | 原因 / 处理 |
|------|-------------|
| `Could not resolve placeholder 'DB_PASSWORD'` | 环境变量没设，且占位符没写默认值。这是**预期行为**，去设环境变量或写 `application-local.yml` |
| `mvn -o` 直接失败 | `-o` 是纯离线，缺依赖就失败。首次加依赖时去掉 `-o` |
| 改了 IDEA 环境变量但仍连旧库 | Run Configuration 改了要重启；或 `application-local.yml` 与 profile 不一致 |
| `git mv` 报找不到文件 | 该文件未被 Git 跟踪（如 yml）。未跟踪文件用 `Move-Item` |
| IDEA 重新导入后 `src/resources` 又出现 | 确认删除了旧的 `.iml` 与 `.idea/workspace.xml` 里的模块缓存（重开项目） |
| 打包后仍看不到 yml | 确认文件在 `blog-service/src/main/resources/`，且 pom 没有自定义 `<resources>` 覆盖默认 |
| MySQL 连不上 | 检查 `allowPublicKeyRetrieval=true`、用户名/密码、以及测试库地址是否可达 |

---

## 4. 完成后告诉我

把这三样发我即可进入阶段 1：

1. `jar tf` 的输出（确认资源已进包）；
2. `java -jar` 的启动日志最后 20 行（确认无 placeholder 报错）；
3. `git log --oneline -5` 的输出。

> 下一份：[阶段 1 · 安全止血](./02-phase1-security.md)
