# 常见问题解答 (FAQ)

## 🔥 核心问题

### Q: 只用在服务器上执行 docker-compose 就行了？

**A: 是的！但前提是你已经准备好了所有文件。**

完整流程：

```bash
# 1️⃣ 本地准备（一次性）
- 构建前端项目（npm run build）
- 复制后端源码（不需要编译）
- 复制配置文件
- 打包上传到服务器

# 2️⃣ 服务器执行（核心步骤）
cd /opt/pig-platform
docker-compose up -d  # ← 就这一条命令！

# 3️⃣ Docker Compose 自动完成
- 读取 docker-compose.yml
- 为每个服务构建 Docker 镜像（自动编译 Java 代码）
- 启动所有容器
- 配置网络和数据卷
```

**关键点**：
- ✅ 前端需要本地构建（生成静态文件）
- ✅ 后端只需上传源码（Docker 会自动编译）
- ✅ 一条 `docker-compose up -d` 完成所有后端构建和部署

---

## 📚 详细问题

### Q1: 后端代码是怎么构建的？

**A**: Docker Compose 通过 Dockerfile 自动构建

#### 流程图

```
docker-compose.yml 指定构建路径
         ↓
找到每个服务的 Dockerfile
         ↓
执行 Dockerfile 中的构建步骤
         ↓
使用 Maven 编译 Java 代码
         ↓
生成 Docker 镜像
         ↓
启动容器运行服务
```

#### 示例

```yaml
# docker-compose.yml
services:
  gateway:
    build:
      context: ./pig-services/pig-gateway  # ← 源码目录
    # Docker Compose 会在这个目录找 Dockerfile
```

```dockerfile
# pig-services/pig-gateway/Dockerfile
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests  # ← 在这里编译！

FROM openjdk:17-slim
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**结论**：你不需要手动编译，Docker 会自动完成！

---

### Q2: 需要在本地编译 Java 代码吗？

**A: 不需要！**

#### 传统方式（不推荐）

```bash
# ❌ 传统方式：本地编译
cd pig-gateway
mvn clean package
# 生成 target/pig-gateway.jar

# 然后上传 JAR 包
scp target/pig-gateway.jar root@server:/opt/
```

#### Docker Compose 方式（推荐）

```bash
# ✅ Docker Compose 方式：上传源码
cd pig-gateway
# 不需要编译！直接上传整个目录

# Docker Compose 会在服务器上自动编译
docker-compose up -d
```

**优势**：
- 环境一致性（避免本地环境差异）
- 自动化程度高
- 不需要本地配置 Maven

---

### Q3: 前端代码怎么处理？

**A: 前端必须在本地构建**

#### 为什么？

- 前端是静态文件（HTML/CSS/JS）
- 不需要在服务器上运行时编译
- 本地构建更快，可以利用缓存

#### 流程

```bash
# 1. 本地构建
cd pig-ui
npm install
npm run build
# 生成 dist/ 目录

# 2. 上传到服务器
scp -r dist/* root@server:/opt/pig-platform/pig-ui/dist/

# 3. Nginx 直接提供静态文件服务
# 不需要任何编译或构建
```

---

### Q4: 数据库怎么初始化？

**A: 自动初始化**

#### 流程

```bash
# 1. 准备 SQL 脚本
mysql/init/
  ├── pig.sql         # 表结构
  └── pig_config.sql  # 配置数据

# 2. Docker Compose 配置
services:
  mysql:
    volumes:
      - ./mysql/init:/docker-entrypoint-initdb.d
    # MySQL 容器启动时会自动执行这个目录下的 SQL 脚本

# 3. 自动执行
docker-compose up -d
# MySQL 容器启动后自动导入数据
```

---

### Q5: 完整的部署步骤是什么？

**A: 分为本地准备和服务器部署两个阶段**

#### 阶段 1: 本地准备

```bash
# 假设你的项目结构：
# ~/projects/
#   ├── pig/                    # 后端项目
#   ├── pig-ui/                 # 前端项目
#   ├── znhaas-docs/            # 文档项目
#   └── deployment-guide/       # 部署配置（本仓库）

# 1. 构建前端
cd ~/projects/pig-ui
npm install
npm run build  # 生成 dist/ 目录

cd ~/projects/znhaas-docs
npm install
npm run build  # 生成 build/ 目录

# 2. 创建临时部署目录（关键：所有文件都放在这里）
mkdir -p /tmp/pig-deploy
cd /tmp/pig-deploy

# 3. 复制部署配置
cp -r ~/projects/deployment-guide/* .

# 4. 复制后端源码到 pig-services/ 目录
mkdir -p pig-services
cp -r ~/projects/pig/pig-register pig-services/
cp -r ~/projects/pig/pig-gateway pig-services/
cp -r ~/projects/pig/pig-auth pig-services/
cp -r ~/projects/pig/pig-upms pig-services/
cp -r ~/projects/pig/pig-visual pig-services/

# 5. 复制前端构建文件到 pig-ui/dist/ 目录
mkdir -p pig-ui/dist
cp -r ~/projects/pig-ui/dist/* pig-ui/dist/

# 6. 复制文档构建文件到 docs/build/ 目录
mkdir -p docs/build
cp -r ~/projects/znhaas-docs/build/* docs/build/

# 7. 复制数据库脚本到 mysql/init/ 目录
mkdir -p mysql/init
cp ~/projects/pig/db/pig.sql mysql/init/
cp ~/projects/pig/db/pig_config.sql mysql/init/

# 8. 验证目录结构（重要！）
ls -la /tmp/pig-deploy
# 应该看到：
# docker-compose.yml
# .env
# nginx/
# pig-services/
# pig-ui/
# docs/
# mysql/

# 9. 打包（在 /tmp/pig-deploy 目录中执行）
cd /tmp/pig-deploy
tar -czf pig-deploy.tar.gz *
# 这会把当前目录下的所有文件打包到一个压缩包

# 10. 上传
scp pig-deploy.tar.gz root@server:/opt/
```

**关键点**：
- ✅ 所有文件都在 `/tmp/pig-deploy` 目录中
- ✅ 使用 `tar -czf pig-deploy.tar.gz *` 打包当前目录的所有内容
- ✅ 解压后会保持相同的目录结构

#### 阶段 2: 服务器部署

```bash
# 1. 登录服务器
ssh root@server

# 2. 解压
cd /opt
mkdir -p pig-platform
tar -xzf pig-deploy.tar.gz -C pig-platform
cd pig-platform

# 3. 配置环境变量
nano .env
# 修改 MYSQL_ROOT_PASSWORD

# 4. 启动（自动构建和部署）
docker-compose up -d

# 5. 查看进度
docker-compose logs -f

# 6. 验证
docker-compose ps
```

---

### Q6: 构建需要多长时间？

**A: 首次 15-20 分钟，后续 2-5 分钟**

#### 时间分解

```
首次构建（15-20 分钟）：
├─ MySQL/Redis/Nginx: 2 分钟（拉取镜像）
├─ Nacos: 3-5 分钟（编译 Java）
├─ Gateway: 2-3 分钟（编译 Java）
├─ Auth: 2-3 分钟（编译 Java）
├─ UPMS: 2-3 分钟（编译 Java）
├─ Codegen: 2-3 分钟（编译 Java）
└─ Monitor: 2-3 分钟（编译 Java）

后续构建（2-5 分钟）：
└─ 利用 Docker 缓存，只编译变化的代码

仅重启（30 秒）：
└─ 镜像已存在，直接启动容器
```

---

### Q7: 如何查看构建进度？

**A: 使用 docker-compose logs**

```bash
# 方式 1: 实时查看所有日志
docker-compose logs -f

# 方式 2: 查看特定服务
docker-compose logs -f gateway

# 方式 3: 查看最近 100 行
docker-compose logs --tail=100

# 方式 4: 查看容器状态
docker-compose ps

# 方式 5: 查看镜像列表
docker images | grep pig
```

---

### Q8: 构建失败怎么办？

**A: 查看日志，清理后重试**

```bash
# 1. 查看失败的服务日志
docker-compose logs gateway

# 2. 清理后重新构建
docker-compose down
docker-compose build --no-cache
docker-compose up -d

# 3. 单独重建某个服务
docker-compose build gateway
docker-compose up -d gateway
```

---

### Q9: 如何更新代码？

**A: 重新构建镜像**

```bash
# 方式 1: 更新所有服务
docker-compose down
docker-compose build
docker-compose up -d

# 方式 2: 更新单个服务
docker-compose up -d --build gateway

# 方式 3: 强制重建（不使用缓存）
docker-compose build --no-cache gateway
docker-compose up -d gateway
```

---

### Q10: 需要安装 Maven 吗？

**A: 服务器不需要，Docker 镜像自带**

#### 本地

```bash
# 可选：如果想本地测试编译
mvn clean package

# 但部署时不需要本地编译
```

#### 服务器

```bash
# 不需要安装 Maven
# Docker 镜像 maven:3.8-openjdk-17 自带 Maven

# Dockerfile 中使用
FROM maven:3.8-openjdk-17 AS build
RUN mvn clean package  # ← 使用镜像自带的 Maven
```

---

## 🎯 快速参考

### 最简部署流程

```bash
# 本地
cd pig-ui && npm run build
cd ../znhaas-docs && npm run build
# 准备文件并上传...

# 服务器
cd /opt/pig-platform
docker-compose up -d  # ← 就这一条！
```

### 核心概念

| 组件 | 构建位置 | 构建方式 |
|------|---------|---------|
| 前端 | 本地 | npm run build |
| 后端 | 服务器 | Docker 自动构建 |
| 数据库 | 服务器 | 自动初始化 |

### 关键文件

| 文件 | 作用 |
|------|------|
| `docker-compose.yml` | 定义所有服务和构建配置 |
| `Dockerfile` | 定义每个服务的构建步骤 |
| `.env` | 环境变量配置 |
| `nginx/conf.d/default.conf` | 路由配置 |

---

## 📖 延伸阅读

- [QUICK-START.md](QUICK-START.md) - 快速开始指南
- [BUILD-PROCESS.md](BUILD-PROCESS.md) - 构建流程详解
- [README.md](README.md) - 完整部署文档

---

**还有疑问？查看 [BUILD-PROCESS.md](BUILD-PROCESS.md) 了解详细的构建流程！** 🎉


---

### Q11: 2核2GB 服务器能运行 PIG 平台吗？

**A: 可以，但有限制**

#### 能做什么

- ✅ 可以运行（需要优化配置）
- ✅ 适合测试和演示
- ✅ 适合学习和开发

#### 不能做什么

- ❌ 不适合生产环境
- ❌ 不能在服务器上构建（必须本地构建）
- ❌ 不能支持高并发

#### 必须的优化

1. **使用本地构建**（不能服务器构建）
2. **限制内存使用**（每个服务设置 mem_limit）
3. **配置 Swap 分区**（至少 2GB）
4. **减少运行的服务**（关闭 codegen、monitor 等可选服务）

#### 性能预期

| 指标 | 2核2GB | 4核8GB |
|------|--------|--------|
| 启动时间 | 5-8 分钟 | 2-3 分钟 |
| 响应时间 | 较慢（2-5秒） | 正常（<1秒） |
| 并发用户 | 10-20 | 50-100 |

#### 详细配置

查看 QUICK-START.md 的"附录 B：2核2GB 服务器优化配置"

---

### Q12: 服务器配置怎么选？

**A: 根据使用场景选择**

#### 测试/演示环境

```
配置: 2核2GB
成本: ¥50-100/月
说明: 需要优化配置，性能有限
```

#### 开发环境

```
配置: 2核4GB 或 4核8GB
成本: ¥100-200/月
说明: 可以正常使用，响应速度尚可
```

#### 小型生产环境

```
配置: 4核8GB
成本: ¥200-300/月
说明: 推荐配置，支持 50-100 并发用户
```

#### 中型生产环境

```
配置: 8核16GB
成本: ¥400-600/月
说明: 性能优秀，支持 200+ 并发用户
```

#### 大型生产环境

```
配置: 16核32GB 或更高
成本: ¥1000+/月
说明: 高性能，支持 1000+ 并发用户
建议: 使用 Kubernetes 集群
```

---

### Q13: 如何判断服务器配置是否足够？

**A: 监控关键指标**

#### 内存使用率

```bash
free -h

# 正常: < 80%
# 警告: 80-90%
# 危险: > 90%
```

#### CPU 使用率

```bash
top

# 正常: < 70%
# 警告: 70-85%
# 危险: > 85%
```

#### Swap 使用率

```bash
swapon --show

# 正常: < 20%
# 警告: 20-50%
# 危险: > 50%（说明内存严重不足）
```

#### 响应时间

```bash
# 测试 API 响应时间
curl -w "@curl-format.txt" -o /dev/null -s http://your-server/api/health

# 正常: < 1 秒
# 警告: 1-3 秒
# 危险: > 3 秒
```

#### 升级信号

如果出现以下情况，建议升级服务器：

- ❌ 内存使用率持续 > 85%
- ❌ Swap 使用率 > 50%
- ❌ 服务频繁重启
- ❌ 响应时间 > 3 秒
- ❌ 用户反馈系统卡顿

---
