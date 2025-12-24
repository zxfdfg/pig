# 2核2GB 服务器部署指南

## 🎯 目标

在 2核2GB 服务器上运行 PIG 平台，通过极限优化将内存占用降到最低。

---

## ⚠️ 重要说明

### 适用场景

- ✅ 测试和演示环境
- ✅ 个人学习和开发
- ✅ 低并发场景（10-20 用户）

### 不适用场景

- ❌ 生产环境
- ❌ 高并发场景
- ❌ 需要高可用性的场景

### 性能预期

| 指标 | 预期值 |
|------|--------|
| 启动时间 | 5-8 分钟 |
| 响应时间 | 2-5 秒 |
| 并发用户 | 10-20 |
| 内存使用 | ~1.85GB + Swap |
| CPU 使用 | 60-80% |

---

## 📋 前提条件

### 服务器要求

- CPU: 2核
- 内存: 2GB
- 硬盘: 30GB SSD
- 操作系统: Ubuntu 20.04+ / CentOS 7+

### 必须配置

- ✅ Docker 20.10+
- ✅ Docker Compose 2.0+
- ✅ Swap 分区（至少 1GB）

---

## 🚀 部署步骤

### 步骤 1: 配置 Swap 分区（必须）

```bash
# 登录服务器
ssh root@your-server

# 创建 1GB Swap 文件
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 永久生效
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# 优化 Swap 使用策略
sudo sysctl vm.swappiness=10
echo 'vm.swappiness=10' | sudo tee -a /etc/sysctl.conf

# 验证
free -h
swapon --show
```

---

### 步骤 2: 本地构建镜像（必须）

**2核2GB 服务器不能在服务器上构建！**

#### 使用 IntelliJ IDEA 构建（推荐）

1. **打开项目**
   - 使用 IntelliJ IDEA 打开 `pig` 项目
   - 等待 Maven 依赖下载完成

2. **安装 Docker 插件**
   - `Settings/Preferences` → `Plugins` → 搜索 `Docker`
   - 安装并重启 IDEA

3. **构建前端**
   - 打开 IDEA 内置终端（`View` → `Tool Windows` → `Terminal`）
   - 执行：
     ```bash
     cd pig-ui
     npm install
     npm run build
     
     cd ../znhaas-docs
     npm install
     npm run build
     ```

4. **构建后端镜像**
   
   **方式 1：使用 Maven 面板 + Docker**
   - 打开右侧 `Maven` 面板
   - 展开 `pig (root)` → `Lifecycle`
   - 双击 `clean` 和 `package`（跳过测试）
   - 构建完成后，为每个服务构建 Docker 镜像：
     - 找到 `pig-gateway/Dockerfile`
     - 右键 → `Run 'Dockerfile'`
     - Image tag: `pig-gateway:latest`
     - 点击 `Run`
   - 重复为其他服务构建镜像

   **方式 2：使用 Services 面板**
   - 打开底部 `Services` 面板
   - 展开 `Docker` → `Images`
   - 右键 → `Build Image...`
   - 选择 Dockerfile 并构建

5. **导出镜像**
   - 在 `Services` 面板 → `Docker` → `Images`
   - 右键镜像 → `Save Image...`
   - 保存为 `.tar.gz` 文件
   - 或使用终端：
     ```bash
     docker save pig-gateway:latest | gzip > pig-gateway.tar.gz
     ```

#### 使用命令行构建（可选）

```bash
# 在本地机器上执行

# 1. 构建前端
cd ~/projects/pig-ui
npm install
npm run build

cd ~/projects/znhaas-docs
npm install
npm run build

# 2. 构建后端 Docker 镜像
cd ~/projects/pig

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

# 注意：不构建 codegen 和 monitor（节省资源）

# 3. 导出镜像
mkdir -p ~/pig-images
cd ~/pig-images

docker save pig-register:latest | gzip > pig-register.tar.gz
docker save pig-gateway:latest | gzip > pig-gateway.tar.gz
docker save pig-auth:latest | gzip > pig-auth.tar.gz
docker save pig-upms:latest | gzip > pig-upms.tar.gz
```

---

### 步骤 3: 准备部署文件

```bash
# 创建临时部署目录
mkdir -p /tmp/pig-deploy-2c2g
cd /tmp/pig-deploy-2c2g

# 复制优化的配置文件
cp ~/projects/deployment-guide/docker-compose-2c2g.yml docker-compose.yml
cp ~/projects/deployment-guide/.env .
cp -r ~/projects/deployment-guide/nginx .
cp ~/projects/deployment-guide/mysql/my-2c2g.cnf mysql/my-2c2g.cnf

# 复制前端文件
mkdir -p pig-ui/dist docs/build
cp -r ~/projects/pig-ui/dist/* pig-ui/dist/
cp -r ~/projects/znhaas-docs/build/* docs/build/

# 复制数据库脚本
mkdir -p mysql/init
cp ~/projects/pig/db/pig.sql mysql/init/
cp ~/projects/pig/db/pig_config.sql mysql/init/

# 打包
tar -czf pig-deploy-2c2g.tar.gz *
```

---

### 步骤 4: 上传到服务器

```bash
# 上传部署文件
scp pig-deploy-2c2g.tar.gz root@your-server:/opt/

# 上传 Docker 镜像
cd ~/pig-images
scp *.tar.gz root@your-server:/opt/pig-images/
```

---

### 步骤 5: 服务器上部署

```bash
# 登录服务器
ssh root@your-server

# 1. 导入 Docker 镜像
mkdir -p /opt/pig-images
cd /opt/pig-images

docker load < pig-register.tar.gz
docker load < pig-gateway.tar.gz
docker load < pig-auth.tar.gz
docker load < pig-upms.tar.gz

# 验证镜像
docker images | grep pig

# 2. 解压部署文件
cd /opt
mkdir -p pig-platform
tar -xzf pig-deploy-2c2g.tar.gz -C pig-platform
cd pig-platform

# 3. 配置环境变量
nano .env
# 修改 MYSQL_ROOT_PASSWORD

# 4. 启动服务
docker-compose up -d

# 5. 查看启动进度（需要 5-8 分钟）
docker-compose logs -f
```

---

## 📊 资源使用监控

### 实时监控

```bash
# 查看容器资源使用
docker stats

# 查看系统内存
free -h

# 查看 Swap 使用
swapon --show

# 查看 CPU 使用
top
```

### 预期资源使用

```
NAME         CPU %    MEM USAGE / LIMIT    MEM %
pig-mysql    5-10%    300-400MB / 400MB    75-100%
pig-redis    1-3%     80-100MB / 100MB     80-100%
pig-nacos    10-15%   300-400MB / 400MB    75-100%
pig-gateway  5-10%    200-300MB / 300MB    67-100%
pig-auth     3-5%     200-300MB / 300MB    67-100%
pig-upms     5-10%    200-300MB / 300MB    67-100%
pig-nginx    1-2%     30-50MB / 50MB       60-100%
─────────────────────────────────────────────────
总计         30-55%   ~1.5-1.85GB          75-93%
```

---

## 🔧 优化说明

### 1. JVM 参数优化

所有 Java 服务使用以下参数：

```bash
JAVA_OPTS: >-
  -Xms128m          # 初始堆内存 128MB
  -Xmx200m          # 最大堆内存 200MB
  -Xmn64m           # 新生代 64MB
  -XX:+UseG1GC      # 使用 G1 垃圾回收器
  -XX:MaxGCPauseMillis=500  # 最大 GC 暂停时间
  -XX:+DisableExplicitGC    # 禁用显式 GC
  -XX:MetaspaceSize=64m     # 元空间初始大小
  -XX:MaxMetaspaceSize=128m # 元空间最大大小
```

### 2. MySQL 优化

- InnoDB 缓冲池: 100MB（默认 128MB）
- 最大连接数: 30（默认 151）
- 禁用性能模式（performance_schema）
- 禁用二进制日志（如果不需要主从复制）
- 使用 `innodb_flush_log_at_trx_commit=2`

### 3. Redis 优化

- 最大内存: 80MB
- 禁用持久化（save "" 和 appendonly no）
- 使用 allkeys-lru 淘汰策略

### 4. 服务裁剪

- ❌ 关闭 codegen（代码生成服务）
- ❌ 关闭 monitor（监控服务）
- ✅ 保留核心服务（Gateway, Auth, UPMS）

---

## 🚨 常见问题

### Q1: 服务启动失败

```bash
# 查看日志
docker-compose logs [service_name]

# 常见原因：
# 1. 内存不足 - 检查 Swap 是否配置
# 2. 端口占用 - 检查端口是否被占用
# 3. 镜像未导入 - 检查镜像是否存在
```

### Q2: 系统响应慢

```bash
# 检查 Swap 使用率
swapon --show

# 如果 Swap 使用率 > 50%，说明内存严重不足
# 解决方案：
# 1. 增加 Swap 大小到 2GB
# 2. 进一步减少服务内存限制
# 3. 升级服务器配置
```

### Q3: 服务频繁重启

```bash
# 查看容器状态
docker-compose ps

# 查看重启原因
docker inspect [container_name] | grep -A 10 State

# 常见原因：OOM（内存不足）
# 解决方案：
# 1. 增加该服务的内存限制
# 2. 减少其他服务的内存使用
# 3. 配置更大的 Swap
```

### Q4: 数据库连接失败

```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql

# 常见原因：
# 1. MySQL 启动慢（需要等待 2-3 分钟）
# 2. 连接数超限（最大 30 个连接）
```

---

## 📈 性能测试

### 基准测试

```bash
# 1. 测试响应时间
curl -w "@curl-format.txt" -o /dev/null -s http://your-server/api/health

# 2. 测试并发（使用 ab 工具）
ab -n 100 -c 10 http://your-server/api/health

# 3. 监控资源使用
watch -n 1 'docker stats --no-stream'
```

### 预期结果

| 测试项 | 预期值 |
|--------|--------|
| 首页加载 | 2-5 秒 |
| API 响应 | 500ms-2s |
| 并发 10 用户 | 正常 |
| 并发 20 用户 | 较慢 |
| 并发 50 用户 | 超时 |

---

## 🔄 日常维护

### 重启服务

```bash
cd /opt/pig-platform

# 重启所有服务
docker-compose restart

# 重启单个服务
docker-compose restart gateway
```

### 清理资源

```bash
# 清理未使用的镜像
docker image prune -a

# 清理未使用的容器
docker container prune

# 清理未使用的卷
docker volume prune
```

### 备份数据

```bash
# 备份数据库
docker exec pig-mysql mysqldump -uroot -p pig | gzip > backup_$(date +%Y%m%d).sql.gz

# 备份到远程
scp backup_*.sql.gz user@backup-server:/backups/
```

---

## 📊 升级建议

### 何时升级？

如果出现以下情况，建议升级服务器：

- ❌ Swap 使用率持续 > 50%
- ❌ 响应时间 > 5 秒
- ❌ 服务频繁重启
- ❌ 用户反馈系统卡顿
- ❌ 需要支持更多并发用户

### 升级路径

```
2核2GB → 2核4GB → 4核8GB → 8核16GB
```

### 升级后的改进

| 配置 | 响应时间 | 并发用户 | 稳定性 |
|------|---------|---------|--------|
| 2核2GB | 2-5秒 | 10-20 | 一般 |
| 2核4GB | 1-3秒 | 20-30 | 较好 |
| 4核8GB | <1秒 | 50-100 | 良好 |
| 8核16GB | <500ms | 200+ | 优秀 |

---

## ✅ 检查清单

部署完成后，请检查：

- [ ] Swap 分区已配置（至少 1GB）
- [ ] 所有容器都在运行（docker-compose ps）
- [ ] 内存使用率 < 90%（free -h）
- [ ] Swap 使用率 < 50%（swapon --show）
- [ ] 可以访问管理后台
- [ ] 可以登录系统
- [ ] API 接口正常工作
- [ ] 已修改默认密码

---

## 📞 获取帮助

如遇到问题：

1. 查看日志：`docker-compose logs -f`
2. 检查资源：`docker stats` 和 `free -h`
3. 查看文档：`QUICK-START.md` 和 `FAQ.md`

---

**2核2GB 服务器可以运行 PIG 平台，但需要极限优化配置。** ⚠️

**建议仅用于测试和演示环境，生产环境请使用 4核8GB 或更高配置。** 🎯
