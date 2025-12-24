# 后端服务构建与部署流程详解

## 🎯 核心概念

### Docker Compose 自动构建原理

**关键点**：你只需要上传源码和配置文件，执行 `docker-compose up -d`，剩下的全部自动完成！

```
源码 + docker-compose.yml → Docker Compose → 自动构建 → 自动运行
```

---

## 📊 完整流程图

```
┌─────────────────────────────────────────────────────────────┐
│                    本地准备阶段                              │
├─────────────────────────────────────────────────────────────┤
│ 1. 构建前端项目                                              │
│    cd pig-ui && npm run build                               │
│    cd znhaas-docs && npm run build                          │
│                                                              │
│ 2. 准备后端源码（不需要编译！）                              │
│    复制 pig-register, pig-gateway, pig-auth 等源码目录       │
│                                                              │
│ 3. 打包上传                                                  │
│    tar -czf pig-deploy.tar.gz *                             │
│    scp pig-deploy.tar.gz root@server:/opt/                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    服务器部署阶段                            │
├─────────────────────────────────────────────────────────────┤
│ 1. 解压文件                                                  │
│    tar -xzf pig-deploy.tar.gz -C pig-platform              │
│                                                              │
│ 2. 配置环境变量                                              │
│    编辑 .env 文件（数据库密码等）                            │
│                                                              │
│ 3. 执行 docker-compose up -d                                │
│    ↓                                                         │
│    Docker Compose 自动执行以下步骤：                         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Docker Compose 自动构建流程                     │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  对每个服务（gateway, auth, upms 等）：                      │
│                                                              │
│  ┌────────────────────────────────────────────┐            │
│  │ 步骤 1: 读取 docker-compose.yml            │            │
│  │                                             │            │
│  │ services:                                   │            │
│  │   gateway:                                  │            │
│  │     build:                                  │            │
│  │       context: ./pig-services/pig-gateway  │            │
│  └────────────────────────────────────────────┘            │
│                    ↓                                         │
│  ┌────────────────────────────────────────────┐            │
│  │ 步骤 2: 查找 Dockerfile                     │            │
│  │                                             │            │
│  │ 在 ./pig-services/pig-gateway/ 目录下      │            │
│  │ 找到 Dockerfile                             │            │
│  └────────────────────────────────────────────┘            │
│                    ↓                                         │
│  ┌────────────────────────────────────────────┐            │
│  │ 步骤 3: 执行 Dockerfile 构建                │            │
│  │                                             │            │
│  │ FROM maven:3.8-openjdk-17 AS build         │            │
│  │ COPY pom.xml .                              │            │
│  │ COPY src ./src                              │            │
│  │ RUN mvn clean package -DskipTests  ← 编译！ │            │
│  │                                             │            │
│  │ FROM openjdk:17-slim                        │            │
│  │ COPY --from=build /app/target/*.jar app.jar│            │
│  │ ENTRYPOINT ["java", "-jar", "app.jar"]     │            │
│  └────────────────────────────────────────────┘            │
│                    ↓                                         │
│  ┌────────────────────────────────────────────┐            │
│  │ 步骤 4: 生成 Docker 镜像                    │            │
│  │                                             │            │
│  │ 镜像名称: pig-gateway                       │            │
│  │ 镜像大小: ~500MB                            │            │
│  │ 包含内容: JRE + JAR 包                      │            │
│  └────────────────────────────────────────────┘            │
│                    ↓                                         │
│  ┌────────────────────────────────────────────┐            │
│  │ 步骤 5: 创建并启动容器                      │            │
│  │                                             │            │
│  │ docker run -d \                             │            │
│  │   --name pig-gateway \                      │            │
│  │   --network pig-network \                   │            │
│  │   -e NACOS_HOST=nacos \                     │            │
│  │   pig-gateway                               │            │
│  └────────────────────────────────────────────┘            │
│                                                              │
│  重复以上步骤，构建所有服务...                               │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    服务启动顺序                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. MySQL, Redis (基础服务)                                 │
│     ↓                                                        │
│  2. Nacos (注册中心) - 等待 MySQL 就绪                       │
│     ↓                                                        │
│  3. Gateway, Auth, UPMS (业务服务) - 等待 Nacos 就绪        │
│     ↓                                                        │
│  4. Nginx (反向代理) - 等待 Gateway 就绪                     │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    部署完成                                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ✅ 所有服务运行中                                           │
│  ✅ 可以通过 Nginx 访问                                      │
│  ✅ 服务已注册到 Nacos                                       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔍 详细步骤说明

### 步骤 1: 本地准备

#### 1.1 构建前端（必须在本地完成）

```bash
# PIG-UI
cd pig-ui
npm install
npm run build
# 生成 dist/ 目录

# ZNHAAS-DOCS
cd ../znhaas-docs
npm install
npm run build
# 生成 build/ 目录
```

**为什么前端要本地构建？**
- 前端是静态文件，不需要在服务器上运行时编译
- 本地构建更快，可以利用本地缓存
- 减轻服务器压力

#### 1.2 准备后端源码（不需要编译）

```bash
# 创建部署目录
mkdir -p /tmp/pig-deploy/pig-services

# 复制源码（注意：是源码，不是编译后的 JAR）
cp -r pig/pig-register /tmp/pig-deploy/pig-services/
cp -r pig/pig-gateway /tmp/pig-deploy/pig-services/
cp -r pig/pig-auth /tmp/pig-deploy/pig-services/
cp -r pig/pig-upms /tmp/pig-deploy/pig-services/
cp -r pig/pig-visual /tmp/pig-deploy/pig-services/
```

**为什么不在本地编译后端？**
- Docker 多阶段构建会在容器内编译
- 保证编译环境一致性
- 避免本地环境差异导致的问题

#### 1.3 准备配置文件

```bash
# 复制部署配置
cp -r deployment-guide/* /tmp/pig-deploy/

# 复制前端构建文件
cp -r pig-ui/dist/* /tmp/pig-deploy/pig-ui/dist/
cp -r znhaas-docs/build/* /tmp/pig-deploy/docs/build/

# 复制数据库脚本
cp pig/db/pig.sql /tmp/pig-deploy/mysql/init/
cp pig/db/pig_config.sql /tmp/pig-deploy/mysql/init/
```

---

### 步骤 2: 上传到服务器

```bash
cd /tmp/pig-deploy

# 打包（包含源码、配置、前端文件）
tar -czf pig-deploy.tar.gz *

# 上传
scp pig-deploy.tar.gz root@your-server:/opt/

# 登录服务器
ssh root@your-server

# 解压
cd /opt
mkdir -p pig-platform
tar -xzf pig-deploy.tar.gz -C pig-platform
cd pig-platform
```

---

### 步骤 3: 服务器上执行构建

#### 3.1 配置环境变量

```bash
# 编辑 .env
nano .env

# 修改数据库密码
MYSQL_ROOT_PASSWORD=your_secure_password
```

#### 3.2 执行 Docker Compose

```bash
# 🔥 关键命令：一条命令完成所有构建和部署
docker-compose up -d

# 这条命令会：
# 1. 读取 docker-compose.yml
# 2. 为每个服务查找 Dockerfile
# 3. 执行 Dockerfile 中的构建步骤
# 4. 创建 Docker 镜像
# 5. 启动容器
# 6. 配置网络和数据卷
```

#### 3.3 查看构建进度

```bash
# 方式 1: 实时查看所有日志
docker-compose logs -f

# 方式 2: 查看特定服务
docker-compose logs -f gateway

# 方式 3: 查看构建状态
docker-compose ps
```

---

## 🏗️ Dockerfile 构建详解

### 典型的 Dockerfile 结构

```dockerfile
# ==================== 第一阶段：构建 ====================
FROM maven:3.8-openjdk-17 AS build

# 设置工作目录
WORKDIR /app

# 1️⃣ 复制 pom.xml（利用 Docker 缓存）
COPY pom.xml .
COPY ../pom.xml ../pom.xml

# 2️⃣ 下载依赖（这一步会被缓存）
RUN mvn dependency:go-offline -B

# 3️⃣ 复制源码
COPY src ./src

# 4️⃣ 编译打包
RUN mvn clean package -DskipTests -B
# 生成 target/pig-gateway-3.9.0.jar

# ==================== 第二阶段：运行 ====================
FROM openjdk:17-slim

WORKDIR /app

# 5️⃣ 从构建阶段复制 JAR 包
COPY --from=build /app/target/*.jar app.jar

# 6️⃣ 设置环境变量
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 7️⃣ 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 为什么使用多阶段构建？

```
单阶段构建:
┌─────────────────────────────┐
│ Maven + JDK + 源码 + JAR    │  ← 镜像大小: ~1GB
└─────────────────────────────┘

多阶段构建:
┌─────────────────────────────┐
│ 构建阶段: Maven + JDK + 源码 │  ← 临时，不保留
└─────────────────────────────┘
              ↓ 只复制 JAR
┌─────────────────────────────┐
│ 运行阶段: JRE + JAR         │  ← 镜像大小: ~500MB
└─────────────────────────────┘
```

---

## ⏱️ 构建时间说明

### 首次构建（10-20 分钟）

```
服务           构建时间    说明
─────────────────────────────────────
MySQL          1 分钟     拉取官方镜像
Redis          30 秒      拉取官方镜像
Nacos          3-5 分钟   编译 Java 代码
Gateway        2-3 分钟   编译 Java 代码
Auth           2-3 分钟   编译 Java 代码
UPMS           2-3 分钟   编译 Java 代码
Codegen        2-3 分钟   编译 Java 代码
Monitor        2-3 分钟   编译 Java 代码
Nginx          30 秒      拉取官方镜像
─────────────────────────────────────
总计           15-20 分钟
```

### 后续构建（2-5 分钟）

```
利用 Docker 缓存:
- 依赖层缓存（pom.xml 未变化）
- 基础镜像缓存
- 只重新编译变化的代码

实际构建时间: 2-5 分钟
```

### 仅重启（30 秒）

```bash
# 如果镜像已存在，只是重启容器
docker-compose restart

# 或者停止后重新启动
docker-compose down
docker-compose up -d

# 时间: 30 秒左右
```

---

## 🔧 常见问题

### Q1: 为什么不在本地编译 JAR 包？

**A**: Docker 多阶段构建的优势：
- ✅ 环境一致性（避免"在我机器上能跑"问题）
- ✅ 自动化程度高（一条命令完成）
- ✅ 易于维护（不需要本地配置 Maven）
- ✅ 镜像体积小（多阶段构建）

### Q2: 构建太慢怎么办？

**方案 1**: 使用国内 Maven 镜像

```dockerfile
# 在 Dockerfile 中添加
RUN mkdir -p /root/.m2 && \
    echo '<settings><mirrors><mirror><id>aliyun</id><url>https://maven.aliyun.com/repository/public</url><mirrorOf>central</mirrorOf></mirror></mirrors></settings>' > /root/.m2/settings.xml
```

**方案 2**: 本地构建镜像后推送

```bash
# 本地构建
cd pig-gateway
docker build -t pig-gateway .

# 推送到私有仓库
docker tag pig-gateway your-registry/pig-gateway
docker push your-registry/pig-gateway

# 服务器上拉取
docker pull your-registry/pig-gateway
```

### Q3: 如何查看构建日志？

```bash
# 实时查看所有服务构建日志
docker-compose up

# 后台启动后查看日志
docker-compose up -d
docker-compose logs -f

# 查看特定服务
docker-compose logs -f gateway

# 查看最近 100 行
docker-compose logs --tail=100 gateway
```

### Q4: 构建失败怎么办？

```bash
# 1. 查看失败的服务日志
docker-compose logs gateway

# 2. 清理后重新构建
docker-compose down
docker-compose build --no-cache gateway
docker-compose up -d

# 3. 手动构建调试
cd pig-services/pig-gateway
docker build -t pig-gateway .
```

### Q5: 如何更新单个服务？

```bash
# 方式 1: 重新构建并重启
docker-compose up -d --build gateway

# 方式 2: 手动构建
docker-compose build gateway
docker-compose up -d gateway

# 方式 3: 完全重建
docker-compose stop gateway
docker-compose rm gateway
docker-compose up -d gateway
```

---

## 📋 检查清单

### 部署前检查

- [ ] 前端已在本地构建完成
- [ ] 后端源码已准备好（不需要编译）
- [ ] 配置文件已准备好
- [ ] 数据库脚本已准备好
- [ ] 服务器已安装 Docker 和 Docker Compose

### 部署中检查

- [ ] 文件已成功上传到服务器
- [ ] .env 文件已正确配置
- [ ] docker-compose up -d 执行成功
- [ ] 所有服务正在构建（查看日志）

### 部署后检查

- [ ] 所有容器状态为 Up（docker-compose ps）
- [ ] 所有镜像已创建（docker images | grep pig）
- [ ] 服务已注册到 Nacos
- [ ] 可以访问前端页面
- [ ] API 接口正常工作

---

## 🎯 总结

### 核心要点

1. **前端**：本地构建 → 上传静态文件
2. **后端**：上传源码 → Docker Compose 自动构建
3. **数据库**：上传 SQL 脚本 → 自动初始化
4. **一条命令**：`docker-compose up -d` 完成所有工作

### 工作流程

```
本地: 构建前端 + 准备源码
  ↓
上传: 打包上传到服务器
  ↓
服务器: docker-compose up -d
  ↓
自动: 构建镜像 + 启动服务
  ↓
完成: 访问系统
```

### 时间估算

- 首次部署: 20-30 分钟（包括构建）
- 日常更新: 5-10 分钟
- 仅重启: 1 分钟

---

**现在你应该完全理解后端服务是如何构建和部署的了！** 🎉
