# 快速开始指南

## 🚀 5 分钟快速部署

> **重要说明**：本方案使用 Docker Compose 自动构建和部署
> 
> - ✅ **前端**：本地构建后上传静态文件
> - ✅ **后端**：上传源码，Docker Compose 自动构建镜像并运行
> - ✅ **数据库**：自动初始化并导入数据
> 
> 你只需要执行 `docker-compose up -d`，它会自动完成：
> 1. 读取每个服务的 Dockerfile
> 2. 使用 Maven 编译 Java 代码
> 3. 构建 Docker 镜像
> 4. 启动容器运行服务
>
> 💡 **不理解构建流程？** 查看 [FAQ.md](FAQ.md) 和 [BUILD-PROCESS.md](BUILD-PROCESS.md)  
> 💡 **不理解目录结构？** 查看 [DIRECTORY-STRUCTURE.md](DIRECTORY-STRUCTURE.md)

### 前提条件

#### 服务器配置要求

| 配置 | 服务器构建 | 本地构建 | 说明 |
|------|-----------|---------|------|
| **最低配置** | 4核8GB | 2核2GB | 服务器构建需要编译 Java 代码 |
| **推荐配置** | 8核16GB | 2核4GB | 更快的构建速度 |
| **硬盘** | 50GB SSD | 30GB SSD | 需要存储镜像和数据 |

> ⚠️ **重要提示**：
> - **2核2GB 服务器**：必须使用本地构建方式（方式 B）
> - **4核8GB 服务器**：可以服务器构建，但首次需要 20-30 分钟
> - **8核16GB 服务器**：推荐服务器构建，快速且稳定

#### 软件要求

- ✅ 服务器已安装 Docker 20.10+ 和 Docker Compose 2.0+
- ✅ 本地已安装 Node.js 18+
- ✅ 本地已安装 Maven 3.8+ 和 JDK 17（如果选择本地构建）

---

## 步骤 1: 构建前端项目（本地）

```bash
# 构建 PIG-UI
cd pig-ui
npm install
npm run build

# 构建文档
cd ../znhaas-docs
npm install
npm run build
```

---

## 步骤 2: 选择后端构建方式

### 🔀 两种构建方式对比

| 方式 | 优点 | 缺点 | 适用场景 |
|------|------|------|---------|
| **方式 A：服务器构建** | 简单、环境一致 | 首次慢（15-20分钟）、需要 4核8GB+ | 服务器配置充足 |
| **方式 B：本地构建** | 本地快、减轻服务器压力 | 需要配置环境、可能有差异 | **2核2GB 服务器必选** |

> 💡 **如何选择？**
> - **服务器 ≥ 4核8GB**：推荐方式 A（服务器构建）
> - **服务器 = 2核2GB**：必须方式 B（本地构建）
> - **服务器 = 2核4GB**：建议方式 B（本地构建）

---

### 方式 A：服务器上构建（需要 4核8GB+）⭐

**不需要在本地构建后端！** 直接跳到步骤 3，上传源码即可。

Docker Compose 会在服务器上自动：
1. 读取 Dockerfile
2. 使用 Maven 编译 Java 代码
3. 构建 Docker 镜像
4. 启动容器

**优点**：
- ✅ 简单，只需上传源码
- ✅ 环境一致，避免本地环境差异
- ✅ 不需要本地配置 Maven 和 JDK

**缺点**：
- ❌ 首次构建需要 15-20 分钟
- ❌ 需要服务器有足够的资源（**最低 4核8GB**）
- ❌ 构建过程会占用大量 CPU 和内存

> ⚠️ **2核2GB 服务器不要使用此方式！** 会导致：
> - 构建过程非常慢（可能需要 1-2 小时）
> - 可能因为内存不足而失败
> - 服务器可能卡死或无响应

**操作**：直接跳到步骤 3

---

### 方式 B：本地构建（2核2GB 服务器必选）

如果你的服务器配置较低（2核2GB 或 2核4GB），**必须**选择本地构建。

**优点**：
- ✅ 本地构建更快（可以利用本地缓存）
- ✅ 大幅减轻服务器压力
- ✅ 可以在本地测试
- ✅ **2核2GB 服务器也能运行**

**缺点**：
- ❌ 需要本地安装 Maven 和 JDK 17
- ❌ 可能存在环境差异问题
- ❌ 需要上传镜像（约 3GB）

> 💡 **2核2GB 服务器用户必读**：
> - 不要尝试在服务器上构建，会失败或非常慢
> - 使用本地构建方式 B2（构建 Docker 镜像）
> - 运行时内存优化：限制每个服务的内存使用

#### B1. 本地构建 JAR 包（传统方式）

**使用 IntelliJ IDEA 构建**（推荐）：

1. **打开项目**
   - 使用 IntelliJ IDEA 打开 `pig` 项目
   - 等待 Maven 依赖下载完成

2. **配置 Maven**
   - 打开 `Settings/Preferences` → `Build, Execution, Deployment` → `Build Tools` → `Maven`
   - 确保 Maven home directory 配置正确
   - 勾选 `Skip tests` （跳过测试以加快构建）

3. **构建所有模块**
   
   **方式 1：使用 Maven 面板**
   - 打开右侧 `Maven` 面板
   - 展开 `pig (root)` → `Lifecycle`
   - 双击 `clean`，等待完成
   - 双击 `package`，等待构建完成（约 5-10 分钟）
   
   **方式 2：使用 Run Configuration**
   - 点击顶部 `Run` → `Edit Configurations...`
   - 点击 `+` → `Maven`
   - Name: `Build All`
   - Command line: `clean package -DskipTests`
   - 点击 `OK`
   - 点击运行按钮执行

4. **查看构建结果**
   - 构建完成后，在 `Project` 面板中查看：
     ```
     pig-register/target/pig-register-3.9.0.jar
     pig-gateway/target/pig-gateway-3.9.0.jar
     pig-auth/target/pig-auth-3.9.0.jar
     pig-upms/pig-upms-biz/target/pig-upms-biz-3.9.0.jar
     pig-visual/pig-codegen/target/pig-codegen-3.9.0.jar
     pig-visual/pig-monitor/target/pig-monitor-3.9.0.jar
     ```

**使用命令行构建**（可选）：

```bash
cd pig

# 构建所有模块
mvn clean package -DskipTests

# 构建完成后，每个服务的 target/ 目录下会有 JAR 包
```

**然后需要修改部署方式**：

1. 为每个服务创建简化的 Dockerfile（只运行 JAR，不编译）：

```dockerfile
# pig-gateway/Dockerfile.prebuilt
FROM openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

2. 修改 docker-compose.yml 使用预构建的 JAR：

```yaml
services:
  gateway:
    build:
      context: ./pig-services/pig-gateway
      dockerfile: Dockerfile.prebuilt  # 使用简化的 Dockerfile
```

3. 复制 JAR 包到部署目录：

```bash
# 在步骤 3 中，额外复制 JAR 包
cp pig-gateway/target/*.jar /tmp/pig-deploy/pig-services/pig-gateway/target/
```

#### B2. 本地构建 Docker 镜像（推荐）

**使用 IntelliJ IDEA 构建**（推荐）：

1. **安装 Docker 插件**
   - 打开 `Settings/Preferences` → `Plugins`
   - 搜索并安装 `Docker` 插件
   - 重启 IDEA

2. **配置 Docker**
   - 打开 `Settings/Preferences` → `Build, Execution, Deployment` → `Docker`
   - 点击 `+` 添加 Docker 连接
   - 选择 `Docker for Windows/Mac` 或 `TCP socket`
   - 测试连接成功

3. **为每个服务构建镜像**

   **方式 1：使用 Dockerfile 右键菜单**
   - 在 `Project` 面板中找到 `pig-gateway/Dockerfile`
   - 右键点击 Dockerfile → `Run 'Dockerfile'`
   - 在弹出的配置中：
     - Image tag: `pig-gateway:latest`
     - 点击 `Run`
   - 重复此步骤为其他服务构建镜像

   **方式 2：使用 Services 面板**
   - 打开底部 `Services` 面板
   - 展开 `Docker` → `Images`
   - 右键 → `Build Image...`
   - 选择 Dockerfile 路径
   - 输入 Image tag
   - 点击 `Build`

4. **批量构建脚本**（可选）
   
   创建 `build-images.sh`（Mac/Linux）或 `build-images.bat`（Windows）：
   
   ```bash
   # build-images.sh
   #!/bin/bash
   cd pig
   
   services=("pig-register" "pig-gateway" "pig-auth" "pig-upms/pig-upms-biz")
   
   for service in "${services[@]}"; do
       echo "Building $service..."
       cd $service
       docker build -t ${service##*/}:latest .
       cd ..
   done
   ```
   
   在 IDEA 中：
   - 右键点击脚本 → `Run 'build-images.sh'`

5. **验证镜像**
   - 在 `Services` 面板 → `Docker` → `Images` 中查看
   - 或在终端运行：`docker images | grep pig`

**使用命令行构建**（可选）：

```bash
cd pig

# 为每个服务构建 Docker 镜像
cd pig-register
docker build -t pig-register:latest .
cd ..

cd pig-gateway
docker build -t pig-gateway:latest .
cd ..

cd pig-auth
docker build -t pig-auth:latest .
cd ..

cd pig-upms/pig-upms-biz
docker build -t pig-upms:latest .
cd ../..
```

**然后有两种部署方式**：

**选项 1：导出镜像并上传**

```bash
# 导出镜像为 tar 文件
docker save pig-gateway:latest | gzip > pig-gateway.tar.gz
docker save pig-auth:latest | gzip > pig-auth.tar.gz
docker save pig-upms:latest | gzip > pig-upms.tar.gz
# ... 其他服务

# 上传到服务器
scp *.tar.gz root@server:/opt/pig-images/

# 在服务器上导入镜像
ssh root@server
cd /opt/pig-images
docker load < pig-gateway.tar.gz
docker load < pig-auth.tar.gz
docker load < pig-upms.tar.gz
# ... 其他服务
```

**选项 2：推送到 Docker Registry**

```bash
# 标记镜像
docker tag pig-gateway:latest your-registry/pig-gateway:latest
docker tag pig-auth:latest your-registry/pig-auth:latest
# ... 其他服务

# 推送到私有仓库
docker push your-registry/pig-gateway:latest
docker push your-registry/pig-auth:latest
# ... 其他服务
```

**修改 docker-compose.yml**：

```yaml
services:
  gateway:
    image: pig-gateway:latest  # 使用本地镜像
    # 或
    image: your-registry/pig-gateway:latest  # 使用远程镜像
    # 删除 build 配置
    environment:
      NACOS_HOST: nacos
      REDIS_HOST: redis
```

---

## 步骤 3: 准备部署文件

### 重要：理解目录结构

我们需要在一个临时目录中组织所有文件，然后一起打包：

```
/tmp/pig-deploy/                    # 临时部署目录（所有文件都在这里）
├── docker-compose.yml              # 从 deployment-guide 复制
├── .env                            # 从 deployment-guide 复制
├── nginx/                          # 从 deployment-guide 复制
│   ├── nginx.conf
│   └── conf.d/
│       └── default.conf
├── pig-services/                   # 后端源码（新建目录）
│   ├── pig-register/               # 从 pig/ 复制
│   ├── pig-gateway/                # 从 pig/ 复制
│   ├── pig-auth/                   # 从 pig/ 复制
│   ├── pig-upms/                   # 从 pig/ 复制
│   └── pig-visual/                 # 从 pig/ 复制
├── pig-ui/                         # 前端文件（新建目录）
│   └── dist/                       # 从 pig-ui/dist 复制
│       ├── index.html
│       └── assets/
├── docs/                           # 文档文件（新建目录）
│   └── build/                      # 从 znhaas-docs/build 复制
│       ├── index.html
│       └── assets/
└── mysql/                          # 数据库脚本（新建目录）
    └── init/
        ├── pig.sql                 # 从 pig/db 复制
        └── pig_config.sql          # 从 pig/db 复制
```

### 执行准备命令

```bash
# 假设你的项目结构是：
# ~/projects/
#   ├── pig/                    # 后端项目
#   ├── pig-ui/                 # 前端项目
#   ├── znhaas-docs/            # 文档项目
#   └── deployment-guide/       # 部署配置

# 1. 创建临时部署目录
mkdir -p /tmp/pig-deploy
cd /tmp/pig-deploy

# 2. 复制部署配置文件（docker-compose.yml, .env, nginx/ 等）
cp -r ~/projects/deployment-guide/* .

# 3. 创建并复制后端源码目录
mkdir -p pig-services
cp -r ~/projects/pig/pig-register pig-services/
cp -r ~/projects/pig/pig-gateway pig-services/
cp -r ~/projects/pig/pig-auth pig-services/
cp -r ~/projects/pig/pig-upms pig-services/
cp -r ~/projects/pig/pig-visual pig-services/

# 4. 创建并复制前端构建文件
mkdir -p pig-ui/dist
cp -r ~/projects/pig-ui/dist/* pig-ui/dist/

# 5. 创建并复制文档构建文件
mkdir -p docs/build
cp -r ~/projects/znhaas-docs/build/* docs/build/

# 6. 创建并复制数据库脚本
mkdir -p mysql/init
cp ~/projects/pig/db/pig.sql mysql/init/
cp ~/projects/pig/db/pig_config.sql mysql/init/

# 7. 验证目录结构
tree -L 2 /tmp/pig-deploy
# 或者
ls -la /tmp/pig-deploy
```

---

## 步骤 4: 打包并上传到服务器

```bash
# 确保在部署目录中
cd /tmp/pig-deploy

# 打包所有文件（当前目录下的所有内容）
tar -czf pig-deploy.tar.gz *

# 查看压缩包内容（可选，验证打包是否正确）
tar -tzf pig-deploy.tar.gz | head -20

# 上传到服务器
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

## 步骤 5: 配置环境变量

```bash
# 编辑 .env 文件
nano .env
```

修改以下内容：
```env
MYSQL_ROOT_PASSWORD=your_strong_password_here
DOMAIN=yourdomain.com
```

---

## 步骤 6: 启动服务

### 重要说明：Docker Compose 会自动构建和运行

```bash
# 🔥 关键步骤：docker-compose 会自动完成以下工作：
# 1. 根据 Dockerfile 构建每个服务的镜像
# 2. 创建容器并启动服务
# 3. 配置网络和数据卷
# 4. 按依赖顺序启动服务

# 启动所有服务（首次会自动构建镜像，需要 10-20 分钟）
docker-compose up -d

# 查看构建和启动进度
docker-compose logs -f

# 等待所有服务启动完成（约 2-3 分钟）
# 你会看到类似这样的日志：
# pig-nacos    | Nacos started successfully
# pig-gateway  | Started PigGatewayApplication
# pig-auth     | Started PigAuthApplication
```

### 构建过程说明

Docker Compose 会为每个服务执行：

1. **读取 Dockerfile** - 每个服务目录下的 Dockerfile
2. **构建 Java 应用** - 使用 Maven 编译打包
3. **创建 Docker 镜像** - 将 JAR 包打包成镜像
4. **启动容器** - 运行镜像创建容器

例如 Gateway 服务的构建过程：
```dockerfile
# pig-gateway/Dockerfile
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-slim
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 验证构建状态

```bash
# 查看所有服务状态（应该都是 Up）
docker-compose ps

# 查看镜像列表（应该看到所有服务的镜像）
docker images | grep pig

# 输出示例：
# pig-gateway    latest    abc123    10 minutes ago    500MB
# pig-auth       latest    def456    10 minutes ago    480MB
# pig-upms       latest    ghi789    10 minutes ago    520MB
```

---

## 步骤 7: 验证部署

### 检查服务状态

```bash
# 所有服务应该是 Up 状态
docker-compose ps

# 预期输出：
# NAME           IMAGE          STATUS         PORTS
# pig-mysql      mysql:8.0      Up 5 minutes   0.0.0.0:3306->3306/tcp
# pig-redis      redis:7        Up 5 minutes   0.0.0.0:6379->6379/tcp
# pig-nacos      pig-nacos      Up 4 minutes   0.0.0.0:8848->8848/tcp
# pig-gateway    pig-gateway    Up 3 minutes   0.0.0.0:9999->9999/tcp
# pig-auth       pig-auth       Up 3 minutes
# pig-upms       pig-upms       Up 3 minutes
# pig-nginx      nginx:alpine   Up 2 minutes   0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp
```

### 检查服务日志

```bash
# 查看网关日志（确认服务注册成功）
docker-compose logs gateway | grep "Started"

# 查看 Nacos 日志（确认服务注册）
docker-compose logs nacos | grep "register"

# 如果看到类似输出，说明服务启动成功：
# gateway    | Started PigGatewayApplication in 45.123 seconds
# nacos      | Service registered: pig-gateway
```

### 访问系统

- **管理后台**: http://your-server-ip/admin/
- **文档系统**: http://your-server-ip/docs/
- **Nacos 控制台**: http://your-server-ip:8848/nacos

### 默认账号

- **管理员**: admin / admin
- **Nacos**: nacos / nacos

---

## 📖 工作原理详解

### Docker Compose 自动构建流程

当你执行 `docker-compose up -d` 时，会发生以下过程：

#### 1. 读取配置文件

```yaml
# docker-compose.yml
services:
  gateway:
    build:
      context: ./pig-gateway  # 指向源码目录
    # Docker Compose 会在这个目录找 Dockerfile
```

#### 2. 执行 Dockerfile 构建

每个服务目录下都有 Dockerfile，例如：

```dockerfile
# pig-gateway/Dockerfile
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests  # 🔥 在这里编译 Java 代码

FROM openjdk:17-slim
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

这个过程：
- 使用 Maven 镜像编译代码
- 生成 JAR 包
- 将 JAR 包打包到运行镜像
- 设置启动命令

#### 3. 构建镜像

```bash
# Docker Compose 自动执行类似这样的命令：
docker build -t pig-gateway ./pig-gateway
docker build -t pig-auth ./pig-auth
docker build -t pig-upms ./pig-upms
# ... 其他服务
```

#### 4. 启动容器

```bash
# 按依赖顺序启动：
# 1. MySQL, Redis (基础服务)
# 2. Nacos (注册中心)
# 3. Gateway, Auth, UPMS (业务服务)
# 4. Nginx (反向代理)
```

### 为什么不需要手动构建？

传统方式需要：
```bash
# ❌ 传统方式（繁琐）
cd pig-gateway
mvn clean package
docker build -t pig-gateway .
docker run -d pig-gateway

cd ../pig-auth
mvn clean package
docker build -t pig-auth .
docker run -d pig-auth
# ... 重复 N 次
```

Docker Compose 方式：
```bash
# ✅ Docker Compose（简单）
docker-compose up -d
# 一条命令完成所有构建和启动！
```

### 构建时间说明

- **首次构建**：10-20 分钟（需要下载依赖、编译代码）
- **后续构建**：2-5 分钟（利用 Docker 缓存）
- **仅重启**：30 秒（如果镜像已存在）

### 如何查看构建进度？

```bash
# 实时查看构建日志
docker-compose up

# 或者后台启动后查看日志
docker-compose up -d
docker-compose logs -f
```

---

## 🎯 常用命令

```bash
# 查看所有服务
docker-compose ps

# 查看日志
docker-compose logs -f [service_name]

# 重启服务
docker-compose restart [service_name]

# 停止所有服务
docker-compose down

# 启动所有服务
docker-compose up -d

# 进入容器
docker exec -it [container_name] bash
```

---

## 🔧 故障排查

### 服务无法启动

```bash
# 查看日志
docker-compose logs [service_name]

# 检查端口占用
netstat -tulpn | grep [port]

# 重启服务
docker-compose restart [service_name]
```

### 前端页面 404

```bash
# 检查文件是否存在
ls -la pig-ui/dist/
ls -la docs/build/

# 重启 Nginx
docker-compose restart nginx
```

### 数据库连接失败

```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql

# 进入 MySQL
docker exec -it pig-mysql mysql -uroot -p
```

---

## 📋 检查清单

部署完成后，请检查：

- [ ] 所有容器都在运行（docker-compose ps）
- [ ] 可以访问管理后台
- [ ] 可以访问文档系统
- [ ] 可以登录系统
- [ ] API 接口正常工作
- [ ] 已修改默认密码

---

## 🔐 安全加固（重要！）

```bash
# 1. 修改数据库密码
docker exec -it pig-mysql mysql -uroot -p
ALTER USER 'root'@'%' IDENTIFIED BY 'new_strong_password';
FLUSH PRIVILEGES;

# 2. 修改管理员密码
# 登录系统后在个人中心修改

# 3. 配置防火墙
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 22/tcp
ufw enable

# 4. 配置 SSL（推荐）
# 参考 README.md 中的 SSL 配置章节
```

---

## 📊 性能优化

### 调整 JVM 参数

编辑 `docker-compose.yml`：

```yaml
services:
  gateway:
    environment:
      JAVA_OPTS: "-Xmx512m -Xms512m -XX:+UseG1GC"
```

### 配置 Nginx 缓存

编辑 `nginx/conf.d/default.conf`：

```nginx
# 静态资源缓存
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

---

## 🔄 更新部署

### 更新前端

```bash
# 本地构建
cd pig-ui
npm run build

# 上传到服务器
scp -r dist/* root@your-server:/opt/pig-platform/pig-ui/dist/

# 重启 Nginx
ssh root@your-server "cd /opt/pig-platform && docker-compose restart nginx"
```

### 更新后端

```bash
# 重新构建镜像
cd /opt/pig-platform
docker-compose build [service_name]

# 重启服务
docker-compose up -d [service_name]
```

---

## 💾 数据备份

### 备份数据库

```bash
# 备份所有数据库
docker exec pig-mysql mysqldump -uroot -p --all-databases > backup_$(date +%Y%m%d).sql

# 备份单个数据库
docker exec pig-mysql mysqldump -uroot -p pig > pig_backup_$(date +%Y%m%d).sql
```

### 恢复数据库

```bash
# 恢复数据库
docker exec -i pig-mysql mysql -uroot -p pig < pig_backup_20241224.sql
```

### 自动备份脚本

```bash
# 创建备份脚本
cat > /opt/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

# 备份数据库
docker exec pig-mysql mysqldump -uroot -pYOUR_PASSWORD --all-databases | gzip > $BACKUP_DIR/mysql_$DATE.sql.gz

# 删除 7 天前的备份
find $BACKUP_DIR -name "mysql_*.sql.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/mysql_$DATE.sql.gz"
EOF

chmod +x /opt/backup.sh

# 添加定时任务（每天凌晨 2 点）
crontab -e
0 2 * * * /opt/backup.sh
```

---

## 📈 监控告警

### 查看资源使用

```bash
# 查看容器资源使用
docker stats

# 查看磁盘使用
df -h

# 查看内存使用
free -h
```

### 配置告警（可选）

使用 Prometheus + Grafana 或云监控服务

---

## 🆘 获取帮助

如遇到问题：

1. 查看日志：`docker-compose logs -f`
2. 检查配置：`docker-compose config`
3. 查看文档：`README.md`
4. 搜索错误信息

---

## 📚 下一步

- [ ] 配置 SSL 证书
- [ ] 设置自动备份
- [ ] 配置监控告警
- [ ] 性能调优
- [ ] 安全加固

---

**恭喜！你已经成功部署了 PIG 平台！** 🎉

如需更详细的说明，请查看 `README.md` 和 `DEPLOYMENT-OPTIONS.md`。

---

## 🏗️ 附录 A：本地构建完整示例

### 场景：服务器资源有限（2核2GB 或 2核4GB）

如果你的服务器配置较低，**必须**选择在本地构建 Docker 镜像，然后上传到服务器。

---

### 完整流程

#### 步骤 1: 本地构建所有组件

```bash
# 1. 构建前端
cd ~/projects/pig-ui
npm install
npm run build

cd ~/projects/znhaas-docs
npm install
npm run build

# 2. 构建后端 Docker 镜像
cd ~/projects/pig

# 为每个服务构建镜像
cd pig-register
docker build -t pig-register:latest .
cd ..

cd pig-gateway
docker build -t pig-gateway:latest .
cd ..

cd pig-auth
docker build -t pig-auth:latest .
cd ..

cd pig-upms/pig-upms-biz
docker build -t pig-upms:latest .
cd ../..

cd pig-visual/pig-codegen
docker build -t pig-codegen:latest .
cd ../..

cd pig-visual/pig-monitor
docker build -t pig-monitor:latest .
cd ../..

# 3. 验证镜像
docker images | grep pig
```

---

#### 步骤 2: 导出镜像

```bash
# 创建导出目录
mkdir -p ~/pig-images
cd ~/pig-images

# 导出所有镜像
docker save pig-register:latest | gzip > pig-register.tar.gz
docker save pig-gateway:latest | gzip > pig-gateway.tar.gz
docker save pig-auth:latest | gzip > pig-auth.tar.gz
docker save pig-upms:latest | gzip > pig-upms.tar.gz
docker save pig-codegen:latest | gzip > pig-codegen.tar.gz
docker save pig-monitor:latest | gzip > pig-monitor.tar.gz

# 查看文件大小
ls -lh
```

---

#### 步骤 3: 准备部署文件

```bash
# 创建临时部署目录
mkdir -p /tmp/pig-deploy
cd /tmp/pig-deploy

# 复制部署配置
cp -r ~/projects/deployment-guide/* .

# 复制前端构建文件
mkdir -p pig-ui/dist
cp -r ~/projects/pig-ui/dist/* pig-ui/dist/

# 复制文档构建文件
mkdir -p docs/build
cp -r ~/projects/znhaas-docs/build/* docs/build/

# 复制数据库脚本
mkdir -p mysql/init
cp ~/projects/pig/db/pig.sql mysql/init/
cp ~/projects/pig/db/pig_config.sql mysql/init/

# 注意：不需要复制后端源码了！
```

---

#### 步骤 4: 修改 docker-compose.yml

```bash
cd /tmp/pig-deploy

# 编辑 docker-compose.yml
nano docker-compose.yml
```

修改所有后端服务，将 `build` 改为 `image`：

```yaml
services:
  # 基础服务保持不变
  mysql:
    image: mysql:8.0
    # ...

  redis:
    image: redis:7-alpine
    # ...

  # 后端服务使用本地镜像
  nacos:
    image: pig-register:latest  # ← 改为使用镜像
    # 删除 build 配置
    container_name: pig-nacos
    restart: always
    environment:
      MODE: standalone
      MYSQL_HOST: mysql
      # ...

  gateway:
    image: pig-gateway:latest  # ← 改为使用镜像
    # 删除 build 配置
    container_name: pig-gateway
    restart: always
    environment:
      NACOS_HOST: nacos
      # ...

  auth:
    image: pig-auth:latest  # ← 改为使用镜像
    # 删除 build 配置
    # ...

  upms:
    image: pig-upms:latest  # ← 改为使用镜像
    # 删除 build 配置
    # ...

  codegen:
    image: pig-codegen:latest  # ← 改为使用镜像
    # 删除 build 配置
    # ...

  monitor:
    image: pig-monitor:latest  # ← 改为使用镜像
    # 删除 build 配置
    # ...

  # Nginx 保持不变
  nginx:
    image: nginx:alpine
    # ...
```

---

#### 步骤 5: 打包并上传

```bash
# 打包部署文件
cd /tmp/pig-deploy
tar -czf pig-deploy.tar.gz *

# 上传部署文件
scp pig-deploy.tar.gz root@your-server:/opt/

# 上传 Docker 镜像
cd ~/pig-images
scp *.tar.gz root@your-server:/opt/pig-images/
```

---

#### 步骤 6: 服务器上部署

```bash
# 登录服务器
ssh root@your-server

# 1. 导入 Docker 镜像
cd /opt/pig-images
docker load < pig-register.tar.gz
docker load < pig-gateway.tar.gz
docker load < pig-auth.tar.gz
docker load < pig-upms.tar.gz
docker load < pig-codegen.tar.gz
docker load < pig-monitor.tar.gz

# 验证镜像
docker images | grep pig

# 2. 解压部署文件
cd /opt
mkdir -p pig-platform
tar -xzf pig-deploy.tar.gz -C pig-platform
cd pig-platform

# 3. 配置环境变量
nano .env
# 修改 MYSQL_ROOT_PASSWORD

# 4. 启动服务（不需要构建，直接启动）
docker-compose up -d

# 5. 查看状态
docker-compose ps

# 6. 查看日志
docker-compose logs -f
```

---

### 优势对比

| 项目 | 服务器构建 | 本地构建 |
|------|-----------|---------|
| 首次部署时间 | 20-30 分钟 | 5-10 分钟 |
| 服务器资源需求 | 4核8GB+ | 2核4GB 即可 |
| 网络要求 | 需要下载 Maven 依赖 | 只需上传镜像 |
| 环境一致性 | 高 | 中（可能有差异） |
| 操作复杂度 | 低 | 中 |

---

### 注意事项

1. **镜像大小**：每个服务镜像约 500MB，总共约 3GB
2. **上传时间**：取决于网络速度，可能需要 10-30 分钟
3. **镜像版本**：确保本地和服务器的镜像版本一致
4. **更新流程**：每次更新代码都需要重新构建和上传镜像

---

### 使用 Docker Registry（推荐）

如果频繁更新，建议使用 Docker Registry：

```bash
# 1. 搭建私有 Registry（服务器上）
docker run -d -p 5000:5000 --name registry registry:2

# 2. 本地推送镜像
docker tag pig-gateway:latest your-server:5000/pig-gateway:latest
docker push your-server:5000/pig-gateway:latest

# 3. 修改 docker-compose.yml
services:
  gateway:
    image: your-server:5000/pig-gateway:latest
```

---

### 快速更新脚本

创建一个自动化脚本 `update-local-build.sh`：

```bash
#!/bin/bash

# 本地构建并上传脚本
SERVER="root@your-server"
SERVICE=$1

if [ -z "$SERVICE" ]; then
    echo "用法: ./update-local-build.sh <service-name>"
    echo "例如: ./update-local-build.sh gateway"
    exit 1
fi

echo "1. 构建镜像..."
cd ~/projects/pig/pig-$SERVICE
docker build -t pig-$SERVICE:latest .

echo "2. 导出镜像..."
docker save pig-$SERVICE:latest | gzip > /tmp/pig-$SERVICE.tar.gz

echo "3. 上传镜像..."
scp /tmp/pig-$SERVICE.tar.gz $SERVER:/tmp/

echo "4. 服务器上更新..."
ssh $SERVER << EOF
cd /tmp
docker load < pig-$SERVICE.tar.gz
cd /opt/pig-platform
docker-compose up -d $SERVICE
docker-compose logs -f $SERVICE
EOF

echo "完成！"
```

使用方法：

```bash
chmod +x update-local-build.sh
./update-local-build.sh gateway
```

---

**本地构建方案适合服务器资源有限的场景，但需要更多的手动操作。** 🎉


---

## 🏗️ 附录 B：2核2GB 服务器优化配置

> 💡 **完整的 2核2GB 部署指南**：查看 [DEPLOY-2C2G.md](DEPLOY-2C2G.md)
> 
> 包含：
> - ✅ 完整的部署步骤
> - ✅ 优化的 docker-compose.yml
> - ✅ 优化的 MySQL 配置
> - ✅ Swap 配置指南
> - ✅ 性能测试和监控

### 问题：2核2GB 服务器能运行 PIG 平台吗？

**答案**：可以，但需要优化配置。

---

### 优化策略

#### 1. 使用本地构建（必须）

2核2GB 服务器**不能**在服务器上构建，必须使用附录 A 的本地构建方式。

---

#### 2. 限制服务内存使用

修改 `docker-compose.yml`，为每个服务添加内存限制：

```yaml
services:
  mysql:
    image: mysql:8.0
    mem_limit: 512m  # ← 添加内存限制
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    command: --max_connections=50 --innodb_buffer_pool_size=128M

  redis:
    image: redis:7-alpine
    mem_limit: 128m  # ← 添加内存限制
    command: redis-server --maxmemory 100mb --maxmemory-policy allkeys-lru

  nacos:
    image: pig-register:latest
    mem_limit: 512m  # ← 添加内存限制
    environment:
      JAVA_OPTS: "-Xms256m -Xmx256m -XX:+UseG1GC"

  gateway:
    image: pig-gateway:latest
    mem_limit: 384m  # ← 添加内存限制
    environment:
      JAVA_OPTS: "-Xms192m -Xmx192m -XX:+UseG1GC"

  auth:
    image: pig-auth:latest
    mem_limit: 384m
    environment:
      JAVA_OPTS: "-Xms192m -Xmx192m -XX:+UseG1GC"

  upms:
    image: pig-upms:latest
    mem_limit: 384m
    environment:
      JAVA_OPTS: "-Xms192m -Xmx192m -XX:+UseG1GC"

  nginx:
    image: nginx:alpine
    mem_limit: 64m  # ← 添加内存限制
```

---

#### 3. 减少运行的服务

如果内存仍然不足，可以暂时不启动某些服务：

```yaml
# 注释掉不必要的服务
services:
  # codegen:  # 代码生成服务（可选）
  #   image: pig-codegen:latest
  #   ...

  # monitor:  # 监控服务（可选）
  #   image: pig-monitor:latest
  #   ...
```

---

#### 4. 使用 Swap 分区

```bash
# 创建 2GB Swap（临时缓解内存压力）
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 永久生效
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# 验证
free -h
```

---

#### 5. 优化 MySQL 配置

创建 `mysql/my.cnf`：

```ini
[mysqld]
# 内存优化
innodb_buffer_pool_size = 128M
innodb_log_buffer_size = 8M
max_connections = 50
table_open_cache = 64
query_cache_size = 0
query_cache_type = 0

# 性能优化
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT
```

在 docker-compose.yml 中挂载：

```yaml
services:
  mysql:
    volumes:
      - ./mysql/my.cnf:/etc/mysql/conf.d/my.cnf:ro
```

---

### 内存分配建议（2核2GB）

| 服务 | 内存限制 | 说明 |
|------|---------|------|
| MySQL | 512MB | 数据库 |
| Redis | 128MB | 缓存 |
| Nacos | 512MB | 注册中心 |
| Gateway | 384MB | 网关 |
| Auth | 384MB | 认证 |
| UPMS | 384MB | 用户权限 |
| Nginx | 64MB | 反向代理 |
| **总计** | **~2.3GB** | 需要 Swap 支持 |

> ⚠️ **注意**：
> - 总内存需求约 2.3GB，超过物理内存
> - 必须配置 Swap 分区
> - 性能会比高配服务器差
> - 不建议用于生产环境

---

### 性能预期

| 指标 | 2核2GB | 4核8GB | 8核16GB |
|------|--------|--------|---------|
| 启动时间 | 5-8 分钟 | 2-3 分钟 | 1-2 分钟 |
| 响应时间 | 较慢 | 正常 | 快 |
| 并发用户 | 10-20 | 50-100 | 200+ |
| 适用场景 | 测试/演示 | 小型项目 | 生产环境 |

---

### 监控内存使用

```bash
# 实时监控
docker stats

# 查看系统内存
free -h

# 查看 Swap 使用
swapon --show
```

---

### 故障排查

#### 问题 1：服务启动失败

```bash
# 查看日志
docker-compose logs [service_name]

# 常见错误：OutOfMemoryError
# 解决：减少 JVM 内存或增加 Swap
```

#### 问题 2：系统响应慢

```bash
# 检查内存使用
free -h

# 检查 Swap 使用（如果 Swap 使用过高，说明内存不足）
swapon --show

# 解决：升级服务器配置
```

#### 问题 3：服务频繁重启

```bash
# 查看容器状态
docker-compose ps

# 查看重启原因
docker inspect [container_name] | grep -A 10 State

# 常见原因：内存不足被 OOM Killer 杀死
# 解决：增加内存限制或减少服务数量
```

---

### 升级建议

如果遇到以下情况，建议升级服务器：

- ❌ 服务频繁重启或崩溃
- ❌ 响应时间超过 5 秒
- ❌ Swap 使用率持续 > 50%
- ❌ 需要支持更多并发用户

**推荐升级到**：4核8GB 或更高配置

---

**2核2GB 服务器可以运行 PIG 平台，但需要优化配置，且仅适合测试和演示环境。** ⚠️
