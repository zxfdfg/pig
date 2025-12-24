# 部署方案技术调研总结

## 📋 调研概述

本文档总结了将 **PIG 后台管理系统**、**PIG-UI 前端** 和 **ZNHAAS-DOCS 文档系统** 部署到一台服务器的技术方案调研结果。

---

## 🎯 推荐方案：Docker Compose 统一编排

### 为什么选择这个方案？

1. **简单高效**: 一个配置文件管理所有服务
2. **成本最优**: 单服务器即可运行，月成本 ¥100-300
3. **易于维护**: 统一的日志、监控、备份策略
4. **开发友好**: 本地可以完全复制生产环境
5. **平滑迁移**: 后期可轻松迁移到 K8s 或云服务

### 核心优势

✅ **统一入口**: Nginx 反向代理，统一域名访问
✅ **资源共享**: MySQL、Redis、Nacos 等基础服务共享
✅ **服务隔离**: Docker 网络隔离，安全可靠
✅ **数据持久化**: 数据卷挂载，数据不丢失
✅ **一键部署**: 提供自动化脚本，5分钟完成部署

---

## 🏗️ 架构设计

### 整体架构

```
                    Internet
                       │
                       ▼
            ┌──────────────────────┐
            │   Nginx (80/443)     │
            │   - SSL 终止          │
            │   - 反向代理          │
            │   - 静态文件服务      │
            └──────────┬───────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
┌──────────────┐ ┌──────────┐ ┌──────────────┐
│   PIG-UI     │ │   API    │ │ ZNHAAS-DOCS  │
│  /admin/*    │ │  /api/*  │ │   /docs/*    │
│  (静态文件)   │ │ Gateway  │ │  (静态文件)   │
└──────────────┘ └────┬─────┘ └──────────────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
        ▼             ▼             ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│  Auth    │   │   UPMS   │   │ Codegen  │
│  (3000)  │   │  (4000)  │   │  (5002)  │
└──────────┘   └────┬─────┘   └──────────┘
                    │
        ┌───────────┼───────────┐
        │           │           │
        ▼           ▼           ▼
┌──────────┐  ┌─────────┐  ┌─────────┐
│  MySQL   │  │  Redis  │  │  Nacos  │
│  (3306)  │  │ (6379)  │  │ (8848)  │
└──────────┘  └─────────┘  └─────────┘
```

### 路径规划

| 服务 | 访问路径 | 说明 |
|------|---------|------|
| 管理后台 | `https://domain.com/admin/` | PIG-UI 前端界面 |
| 后端 API | `https://domain.com/api/` | 微服务网关入口 |
| 文档系统 | `https://domain.com/docs/` | Docusaurus 文档 |
| 监控面板 | `https://domain.com/monitor/` | Spring Boot Admin |
| Nacos 控制台 | `http://domain.com:8848/nacos` | 服务注册中心 |

---

## 💻 技术栈

### 容器化
- **Docker**: 20.10+
- **Docker Compose**: 2.0+

### 反向代理
- **Nginx**: Alpine 版本
  - HTTP/HTTPS 支持
  - Gzip 压缩
  - 静态文件缓存
  - WebSocket 支持

### 后端服务
- **Spring Boot**: 3.5.8
- **Spring Cloud**: 2025.0.0
- **Spring Cloud Alibaba**: 2025.0.0.0
- **MySQL**: 8.0
- **Redis**: 7.0
- **Nacos**: 2.x

### 前端应用
- **PIG-UI**: Vue 3 + Element Plus + Vite
- **ZNHAAS-DOCS**: Docusaurus 3 + React 19

---

## 📊 资源规划

### 服务器配置

#### 最低配置（开发/测试）
- CPU: 4核
- 内存: 8GB
- 硬盘: 50GB SSD
- 带宽: 5Mbps
- **月成本**: ¥100-200

#### 推荐配置（生产环境）
- CPU: 8核
- 内存: 16GB
- 硬盘: 100GB SSD
- 带宽: 10Mbps+
- **月成本**: ¥200-500

### 容器资源分配

| 服务 | CPU | 内存 | 说明 |
|------|-----|------|------|
| Nginx | 0.5核 | 128MB | 反向代理 |
| MySQL | 2核 | 2GB | 数据库 |
| Redis | 0.5核 | 512MB | 缓存 |
| Nacos | 1核 | 1GB | 注册中心 |
| Gateway | 1核 | 512MB | 网关 |
| Auth | 0.5核 | 512MB | 认证服务 |
| UPMS | 1核 | 1GB | 用户权限 |
| Codegen | 0.5核 | 512MB | 代码生成 |
| Monitor | 0.5核 | 512MB | 监控 |
| **总计** | **8核** | **7GB** | - |

---

## 🚀 部署流程

### 快速部署（5分钟）

```bash
# 1. 构建前端（本地）
cd pig-ui && npm run build
cd ../znhaas-docs && npm run build

# 2. 上传文件到服务器
scp -r deployment-guide root@server:/opt/pig-platform
scp -r pig-ui/dist root@server:/opt/pig-platform/pig-ui/
scp -r znhaas-docs/build root@server:/opt/pig-platform/docs/

# 3. 启动服务（服务器）
ssh root@server
cd /opt/pig-platform
docker-compose up -d
```

### 自动化部署

提供了两个自动化脚本：

1. **deploy.sh** - Linux 服务器脚本
   - 自动检查环境
   - 自动安装依赖
   - 自动备份数据
   - 自动启动服务

2. **deploy-windows.bat** - Windows 本地脚本
   - 自动构建前端
   - 自动上传文件
   - 自动启动服务
   - 一键完成部署

---

## 🔐 安全方案

### 网络安全

1. **防火墙配置**
   ```bash
   # 只开放必要端口
   ufw allow 80/tcp    # HTTP
   ufw allow 443/tcp   # HTTPS
   ufw allow 22/tcp    # SSH
   ufw enable
   ```

2. **Docker 网络隔离**
   - 所有服务在独立网络中
   - 外部只能访问 Nginx
   - 服务间通过容器名通信

3. **敏感路径屏蔽**
   ```nginx
   # 屏蔽 Actuator、Swagger 等
   location ~ ^/api/(actuator|swagger-ui|doc\.html) {
       return 403;
   }
   ```

### 数据安全

1. **SSL/TLS 加密**
   - 支持 Let's Encrypt 免费证书
   - 自动续期
   - 强制 HTTPS

2. **密码加密**
   - 数据库密码使用环境变量
   - 应用密码使用 BCrypt
   - 敏感配置使用 Jasypt 加密

3. **数据备份**
   - 自动备份脚本
   - 定时任务执行
   - 保留 7 天备份

### 访问控制

1. **权限管理**
   - RBAC 权限模型
   - 菜单级权限控制
   - 按钮级权限控制

2. **SSH 安全**
   - 密钥认证
   - 禁用 root 登录
   - 修改默认端口

---

## 📈 监控方案

### 服务监控

1. **Spring Boot Admin**
   - 服务健康检查
   - JVM 监控
   - 日志查看
   - 访问地址: `/monitor/`

2. **Docker 监控**
   ```bash
   # 查看容器状态
   docker-compose ps
   
   # 查看资源使用
   docker stats
   ```

### 日志管理

1. **集中式日志**
   - 所有容器日志统一管理
   - 支持实时查看
   - 支持日志搜索

2. **日志轮转**
   ```yaml
   logging:
     driver: "json-file"
     options:
       max-size: "10m"
       max-file: "3"
   ```

### 告警机制

1. **健康检查**
   - 容器自动重启
   - 服务异常告警
   - 资源使用告警

2. **备份验证**
   - 备份成功通知
   - 备份失败告警

---

## 🔄 运维方案

### 日常运维

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f [service]

# 重启服务
docker-compose restart [service]

# 更新服务
docker-compose pull
docker-compose up -d
```

### 数据备份

```bash
# 手动备份
docker exec pig-mysql mysqldump -uroot -p pig > backup.sql

# 自动备份（每天凌晨2点）
0 2 * * * /opt/backup.sh
```

### 故障恢复

```bash
# 恢复数据库
docker exec -i pig-mysql mysql -uroot -p pig < backup.sql

# 回滚服务
docker-compose down
docker-compose up -d
```

---

## 💰 成本分析

### 服务器成本

| 配置 | 云服务商 | 月成本 | 适用场景 |
|------|---------|--------|---------|
| 4核8GB | 阿里云 | ¥150-200 | 开发/测试 |
| 8核16GB | 阿里云 | ¥300-500 | 生产环境 |
| 4核8GB | 腾讯云 | ¥120-180 | 开发/测试 |
| 8核16GB | 腾讯云 | ¥250-450 | 生产环境 |

### 其他成本

| 项目 | 成本 | 说明 |
|------|------|------|
| 域名 | ¥50-100/年 | .com/.cn 域名 |
| SSL 证书 | ¥0 | Let's Encrypt 免费 |
| 备份存储 | ¥10-50/月 | 对象存储 |
| CDN | ¥50-200/月 | 可选 |

### 总成本估算

- **最低配置**: ¥100-200/月
- **推荐配置**: ¥300-500/月
- **企业配置**: ¥1000-2000/月

---

## 📚 最佳实践

### 1. 环境分离

```
开发环境: 本地 Docker Compose
测试环境: 独立服务器 Docker Compose
生产环境: 高配服务器 Docker Compose + 云数据库
```

### 2. 配置管理

```
开发: .env.dev
测试: .env.test
生产: .env.prod
```

### 3. 版本控制

```
代码: Git
配置: Git
镜像: Docker Registry
```

### 4. 持续集成

```
代码提交 → 自动构建 → 自动测试 → 自动部署
```

### 5. 灰度发布

```
1. 部署新版本到测试环境
2. 验证功能正常
3. 部署到生产环境（单实例）
4. 观察运行情况
5. 全量部署
```

---

## 🎓 学习资源

### Docker
- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 文档](https://docs.docker.com/compose/)
- [Docker 从入门到实践](https://yeasy.gitbook.io/docker_practice/)

### Nginx
- [Nginx 官方文档](https://nginx.org/en/docs/)
- [Nginx 配置指南](https://www.nginx.com/resources/wiki/)

### Spring Cloud
- [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
- [Spring Cloud Alibaba 文档](https://spring-cloud-alibaba-group.github.io/)

---

## 🔮 未来规划

### 短期（1-3个月）
- [ ] 完善监控告警
- [ ] 优化性能
- [ ] 增加自动化测试

### 中期（3-6个月）
- [ ] 支持多实例部署
- [ ] 实现灰度发布
- [ ] 集成 CI/CD

### 长期（6-12个月）
- [ ] 迁移到 Kubernetes
- [ ] 实现自动扩缩容
- [ ] 多区域部署

---

## ✅ 总结

### 核心优势

1. **简单**: 5分钟快速部署，一键启动所有服务
2. **经济**: 单服务器运行，月成本 ¥100-500
3. **可靠**: Docker 容器化，服务隔离，数据持久化
4. **安全**: HTTPS 加密，权限控制，数据备份
5. **易维护**: 统一管理，自动化脚本，完善文档

### 适用场景

✅ 中小型项目（用户量 < 10000）
✅ 初创公司快速上线
✅ 个人项目/学习项目
✅ 开发测试环境
✅ 预算有限的项目

### 不适用场景

❌ 大规模集群（用户量 > 100000）
❌ 需要自动扩缩容
❌ 多区域部署
❌ 极高可用性要求（99.99%+）

---

## 📞 获取支持

### 文档
- [快速开始](QUICK-START.md)
- [完整指南](README.md)
- [方案对比](DEPLOYMENT-OPTIONS.md)

### 社区
- PIG 官方文档: https://wiki.pig4cloud.com
- Docker 社区: https://forums.docker.com
- Spring Cloud 社区: https://spring.io/community

---

**调研完成时间**: 2024-12-24  
**文档版本**: v1.0  
**调研人员**: Kiro AI Assistant

---

**结论**: Docker Compose 统一编排方案是当前最适合的部署方案，能够满足项目需求，成本可控，易于维护。建议采用此方案进行部署。
