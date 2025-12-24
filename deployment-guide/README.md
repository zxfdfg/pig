# PIG 平台统一部署方案

## 📋 目录

- [方案概述](#方案概述)
- [架构设计](#架构设计)
- [服务器要求](#服务器要求)
- [部署方式](#部署方式)
- [SSL 证书配置](#ssl-证书配置)
- [监控与维护](#监控与维护)
- [常见问题](#常见问题)

---

## 方案概述

本方案将 **PIG 后台管理系统**、**PIG-UI 前端** 和 **ZNHAAS-DOCS 文档系统** 统一部署到一台服务器上，使用 Docker Compose 进行容器编排，Nginx 作为统一入口进行反向代理。

### 核心特点

- ✅ **统一管理**: 所有服务通过 Docker Compose 统一编排
- ✅ **资源优化**: 共享基础服务（MySQL、Redis、Nacos）
- ✅ **易于维护**: 一键部署、一键更新、一键回滚
- ✅ **安全可靠**: 支持 HTTPS、服务隔离、数据持久化
- ✅ **可扩展**: 易于添加新服务或扩展现有服务

---

## 架构设计

### 服务架构图

```
Internet
    │
    ▼
┌─────────────────────────────────────────┐
│  Nginx (80/443)                         │
│  - SSL 终止                              │
│  - 反向代理                              │
│  - 静态文件服务                          │
└────────┬────────────────────────────────┘
         │
    ┌────┴────┬────────┬────────┐
    │         │        │        │
    ▼         ▼        ▼        ▼
┌────────┐ ┌──────┐ ┌──────┐ ┌──────┐
│PIG-UI  │ │ API  │ │ Docs │ │Monitor│
│静态文件│ │Gateway│ │静态  │ │ 5001 │
└────────┘ └──┬───┘ └──────┘ └──────┘
              │
    ┌─────────┼─────────┐
    │         │         │
    ▼         ▼         ▼
┌────────┐ ┌──────┐ ┌──────┐
│ Auth   │ │ UPMS │ │Codegen│
│ 3000   │ │ 4000 │ │ 5002 │
└────────┘ └──────┘ └──────┘
              │
    ┌─────────┼─────────┐
    │         │         │
    ▼         ▼         ▼
┌────────┐ ┌──────┐ ┌──────┐
│ MySQL  │ │Redis │ │Nacos │
│ 3306   │ │ 6379 │ │ 8848 │
└────────┘ └──────┘ └──────┘
```

### 路径规划

| 服务 | 访问路径 | 后端服务 | 说明 |
|------|---------|---------|------|
| 管理后台 | `/admin/` | Nginx 静态文件 | PIG-UI 前端 |
| 后端 API | `/api/` | Gateway:9999 | 微服务网关 |
| 文档系统 | `/docs/` | Nginx 静态文件 | Docusaurus 文档 |
| 监控面板 | `/monitor/` | Monitor:5001 | Spring Boot Admin |
| Nacos 控制台 | `:8848/nacos` | Nacos:8848 | 注册中心 |

### 端口分配

| 服务 | 容器端口 | 宿主机端口 | 说明 |
|------|---------|-----------|------|
| Nginx | 80, 443 | 80, 443 | HTTP/HTTPS |
| MySQL | 3306 | 3306 | 数据库 |
| Redis | 6379 | 6379 | 缓存 |
| Nacos | 8848, 9848 | 8848, 9848 | 注册中心 |
| Gateway | 9999 | 9999 | 网关 |
| Monitor | 5001 | 5001 | 监控 |

---

## 服务器要求

### 最低配置

- **CPU**: 4 核
- **内存**: 8GB
- **硬盘**: 50GB SSD
- **带宽**: 5Mbps
- **操作系统**: Ubuntu 20.04+ / CentOS 7+ / Debian 10+

### 推荐配置

- **CPU**: 8 核
- **内存**: 16GB
- **硬盘**: 100GB SSD
- **带宽**: 10Mbps+
- **操作系统**: Ubuntu 22.04 LTS

### 软件要求

- Docker 20.10+
- Docker Compose 2.0+
- Git（可选）

---

## 部署方式

### 方式一：Linux 服务器直接部署（推荐）

#### 1. 准备服务器

```bash
# 登录服务器
ssh root@your-server-ip

# 安装 Docker
curl -fsSL https://get.docker.com | bash
systemctl start docker
systemctl enable docker

# 安装 Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

#### 2. 上传部署文件

```bash
# 在本地机器上
scp -r deployment-guide root@your-server-ip:/opt/pig-platform
```

#### 3. 构建并上传前端文件

```bash
# 构建 PIG-UI
cd pig-ui
npm install
npm run build

# 上传到服务器
scp -r dist/* root@your-server-ip:/opt/pig-platform/pig-ui/dist/

# 构建文档
cd ../znhaas-docs
npm install
npm run build

# 上传到服务器
scp -r build/* root@your-server-ip:/opt/pig-platform/docs/build/
```

#### 4. 上传数据库脚本

```bash
cd ../pig
scp db/pig.sql root@your-server-ip:/opt/pig-platform/mysql/init/
scp db/pig_config.sql root@your-server-ip:/opt/pig-platform/mysql/init/
```

#### 5. 启动服务

```bash
# 登录服务器
ssh root@your-server-ip

# 进入部署目录
cd /opt/pig-platform

# 修改配置（重要！）
nano .env
# 修改 MYSQL_ROOT_PASSWORD 为强密码

# 启动所有服务
docker-compose up -d

# 查看启动状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 方式二：Windows 一键部署

#### 1. 准备工作

- 安装 Node.js 18+
- 安装 Git for Windows（包含 SSH）
- 确保服务器已安装 Docker 和 Docker Compose

#### 2. 运行部署脚本

```cmd
# 双击运行
deploy-windows.bat

# 或在命令行运行
cd deployment-guide
deploy-windows.bat
```

#### 3. 按提示输入信息

- 服务器地址
- SSH 密码（如果未配置密钥）

脚本会自动完成：
- ✅ 构建前端项目
- ✅ 构建文档项目
- ✅ 上传所有文件
- ✅ 启动服务

---

## SSL 证书配置

### 方式一：Let's Encrypt 免费证书（推荐）

```bash
# 登录服务器
ssh root@your-server-ip

# 安装 Certbot
apt install certbot -y  # Ubuntu/Debian
# 或
yum install certbot -y  # CentOS

# 停止 Nginx 容器
cd /opt/pig-platform
docker-compose stop nginx

# 生成证书
certbot certonly --standalone -d yourdomain.com

# 复制证书到部署目录
cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem nginx/ssl/
cp /etc/letsencrypt/live/yourdomain.com/privkey.pem nginx/ssl/

# 重启 Nginx
docker-compose start nginx
```

### 方式二：自签名证书（测试环境）

```bash
cd /opt/pig-platform/nginx/ssl

# 生成自签名证书
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout privkey.pem \
  -out fullchain.pem \
  -subj "/C=CN/ST=Beijing/L=Beijing/O=Company/CN=yourdomain.com"
```

### 自动续期（Let's Encrypt）

```bash
# 添加定时任务
crontab -e

# 每月1日凌晨3点自动续期
0 3 1 * * certbot renew --quiet && cd /opt/pig-platform && docker-compose restart nginx
```

---

## 监控与维护

### 日常运维命令

```bash
# 进入部署目录
cd /opt/pig-platform

# 查看所有服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f [service_name]

# 重启服务
docker-compose restart [service_name]

# 停止所有服务
docker-compose down

# 启动所有服务
docker-compose up -d

# 更新服务
docker-compose pull
docker-compose up -d
```

### 数据备份

```bash
# 备份 MySQL 数据
docker exec pig-mysql mysqldump -uroot -p pig > backup_$(date +%Y%m%d).sql

# 备份整个数据目录
tar -czf pig-platform-backup-$(date +%Y%m%d).tar.gz /opt/pig-platform/mysql/data
```

### 性能监控

访问监控面板：`https://yourdomain.com/monitor/`

- 查看服务健康状态
- 监控 JVM 内存使用
- 查看接口响应时间
- 查看日志输出

### 资源监控

```bash
# 查看容器资源使用
docker stats

# 查看磁盘使用
df -h

# 查看内存使用
free -h

# 清理 Docker 资源
docker system prune -a
```

---

## 常见问题

### 1. 服务启动失败

**问题**: 某个服务一直重启

**排查步骤**:
```bash
# 查看服务日志
docker-compose logs [service_name]

# 检查端口占用
netstat -tulpn | grep [port]

# 检查配置文件
docker-compose config
```

### 2. 前端页面 404

**问题**: 访问 `/admin/` 返回 404

**解决方案**:
```bash
# 检查文件是否上传
ls -la /opt/pig-platform/pig-ui/dist/

# 检查 Nginx 配置
docker exec pig-nginx nginx -t

# 重启 Nginx
docker-compose restart nginx
```

### 3. API 请求失败

**问题**: 前端调用 API 返回 502

**排查步骤**:
```bash
# 检查网关是否启动
docker-compose ps gateway

# 检查网关日志
docker-compose logs gateway

# 检查 Nacos 注册
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=pig-gateway
```

### 4. 数据库连接失败

**问题**: 服务无法连接数据库

**解决方案**:
```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 检查 MySQL 日志
docker-compose logs mysql

# 进入 MySQL 容器
docker exec -it pig-mysql mysql -uroot -p

# 检查数据库
SHOW DATABASES;
```

### 5. 内存不足

**问题**: 服务频繁 OOM

**解决方案**:
```yaml
# 在 docker-compose.yml 中限制内存
services:
  gateway:
    mem_limit: 512m
    environment:
      JAVA_OPTS: "-Xmx256m -Xms256m"
```

### 6. 磁盘空间不足

**问题**: 磁盘使用率过高

**解决方案**:
```bash
# 清理 Docker 日志
truncate -s 0 /var/lib/docker/containers/*/*-json.log

# 清理未使用的镜像
docker image prune -a

# 清理未使用的卷
docker volume prune
```

---

## 最佳实践

### 1. 安全加固

- ✅ 修改默认密码（数据库、管理员账号）
- ✅ 配置防火墙，只开放必要端口
- ✅ 使用 HTTPS
- ✅ 定期更新系统和 Docker
- ✅ 限制 SSH 登录（密钥认证、禁用 root 登录）

### 2. 性能优化

- ✅ 使用 SSD 硬盘
- ✅ 配置 Redis 持久化
- ✅ 启用 Nginx Gzip 压缩
- ✅ 配置 CDN 加速静态资源
- ✅ 调整 JVM 参数

### 3. 高可用方案

- ✅ 数据库主从复制
- ✅ Redis 哨兵模式
- ✅ Nginx 负载均衡
- ✅ 定期备份数据
- ✅ 监控告警

### 4. 日志管理

```yaml
# 在 docker-compose.yml 中配置日志
services:
  gateway:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

---

## 更新日志

- **2024-12-24**: 初始版本，支持 PIG + PIG-UI + ZNHAAS-DOCS 统一部署

---

## 技术支持

如遇到问题，请检查：
1. 本文档的常见问题部分
2. Docker 和 Docker Compose 日志
3. 各服务的日志输出
4. 网络连接和防火墙设置

---

**祝部署顺利！** 🎉
