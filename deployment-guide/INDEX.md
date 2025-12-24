# PIG 平台部署文档索引

## 📚 文档导航

### 🚀 快速开始
- **[QUICK-START.md](QUICK-START.md)** - 5分钟快速部署指南
  - 适合：想要快速上线的用户
  - 内容：最简化的部署步骤

### 💻 低配服务器
- **[DEPLOY-2C2G.md](DEPLOY-2C2G.md)** - 2核2GB 服务器部署指南 ⭐⭐⭐
  - 适合：2核2GB 服务器用户
  - 内容：极限优化配置、Swap 配置、性能预期

### 📁 目录结构
- **[DIRECTORY-STRUCTURE.md](DIRECTORY-STRUCTURE.md)** - 目录结构详解 ⭐
  - 适合：不理解文件组织方式的用户
  - 内容：完整目录结构图、打包过程、路径映射

### 📖 完整指南
- **[README.md](README.md)** - 完整部署文档
  - 适合：需要详细了解的用户
  - 内容：架构设计、详细步骤、运维指南

### 🏗️ 构建流程
- **[BUILD-PROCESS.md](BUILD-PROCESS.md)** - 后端构建详解 ⭐
  - 适合：想了解构建原理的用户
  - 内容：Docker Compose 自动构建、Dockerfile 详解、流程图

### ❓ 常见问题
- **[FAQ.md](FAQ.md)** - 常见问题解答 ⭐⭐⭐
  - 适合：遇到问题的用户
  - 内容：10个核心问题、快速参考

### 🔍 方案对比
- **[DEPLOYMENT-OPTIONS.md](DEPLOYMENT-OPTIONS.md)** - 部署方案对比
  - 适合：不确定选择哪种方案的用户
  - 内容：4种方案对比、成本分析、选择建议

---

## 🎯 根据你的需求选择

### 我想快速上线
👉 阅读 [QUICK-START.md](QUICK-START.md)

### 我的服务器是 2核2GB
👉 阅读 [DEPLOY-2C2G.md](DEPLOY-2C2G.md)

### 我不理解目录结构和打包过程
👉 阅读 [DIRECTORY-STRUCTURE.md](DIRECTORY-STRUCTURE.md)

### 我想了解详细信息
👉 阅读 [README.md](README.md)

### 我想了解后端如何构建
👉 阅读 [BUILD-PROCESS.md](BUILD-PROCESS.md)

### 我遇到了问题
👉 阅读 [FAQ.md](FAQ.md)

### 我不确定用哪种方案
👉 阅读 [DEPLOYMENT-OPTIONS.md](DEPLOYMENT-OPTIONS.md)

---

## 📁 文件说明

### 配置文件

| 文件 | 说明 | 必需 |
|------|------|------|
| `docker-compose.yml` | Docker 编排配置 | ✅ |
| `.env` | 环境变量配置 | ✅ |
| `nginx/nginx.conf` | Nginx 主配置 | ✅ |
| `nginx/conf.d/default.conf` | 站点配置 | ✅ |

### 脚本文件

| 文件 | 说明 | 平台 |
|------|------|------|
| `deploy.sh` | Linux 部署脚本 | Linux |
| `deploy-windows.bat` | Windows 部署脚本 | Windows |

### 文档文件

| 文件 | 说明 |
|------|------|
| `README.md` | 完整部署文档 |
| `QUICK-START.md` | 快速开始指南 |
| `DIRECTORY-STRUCTURE.md` | 目录结构详解 |
| `BUILD-PROCESS.md` | 后端构建详解 |
| `FAQ.md` | 常见问题解答 |
| `DEPLOYMENT-OPTIONS.md` | 方案对比文档 |
| `STRUCTURE.md` | 文件结构说明 |
| `INDEX.md` | 本文档 |

---

## 🏗️ 部署架构

```
┌─────────────────────────────────────────┐
│           Nginx (80/443)                │
│     - 反向代理                           │
│     - SSL 终止                           │
│     - 静态文件服务                       │
└────────┬────────────────────────────────┘
         │
    ┌────┴────┬────────┬────────┐
    │         │        │        │
    ▼         ▼        ▼        ▼
┌────────┐ ┌──────┐ ┌──────┐ ┌──────┐
│PIG-UI  │ │ API  │ │ Docs │ │Monitor│
│/admin/ │ │/api/ │ │/docs/│ │/monitor/│
└────────┘ └──┬───┘ └──────┘ └──────┘
              │
    ┌─────────┼─────────┐
    │         │         │
    ▼         ▼         ▼
┌────────┐ ┌──────┐ ┌──────┐
│Gateway │ │ Auth │ │ UPMS │
│ 9999   │ │ 3000 │ │ 4000 │
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

---

## 🔗 访问地址

部署完成后，可以通过以下地址访问：

| 服务 | 地址 | 说明 |
|------|------|------|
| 管理后台 | `http://your-server/admin/` | PIG-UI 前端 |
| 后端 API | `http://your-server/api/` | 微服务网关 |
| 文档系统 | `http://your-server/docs/` | Docusaurus 文档 |
| 监控面板 | `http://your-server/monitor/` | Spring Boot Admin |
| Nacos 控制台 | `http://your-server:8848/nacos` | 注册中心 |

---

## 💻 服务器要求

### 最低配置
- CPU: 4核
- 内存: 8GB
- 硬盘: 50GB SSD
- 带宽: 5Mbps

### 推荐配置
- CPU: 8核
- 内存: 16GB
- 硬盘: 100GB SSD
- 带宽: 10Mbps+

---

## 📊 部署方案对比

| 方案 | 复杂度 | 成本 | 适用场景 |
|------|--------|------|---------|
| Docker Compose | ⭐⭐☆☆☆ | ¥100-300/月 | 中小型项目 |
| 分离部署 | ⭐⭐⭐⭐☆ | ¥500-1500/月 | 大型项目 |
| Kubernetes | ⭐⭐⭐⭐⭐ | ¥2000-10000/月 | 企业级应用 |
| 云服务托管 | ⭐⭐⭐☆☆ | ¥500-5000/月 | 快速上线 |

详细对比请查看 [DEPLOYMENT-OPTIONS.md](DEPLOYMENT-OPTIONS.md)

---

## 🛠️ 技术栈

### 后端
- Spring Boot 3.5.8
- Spring Cloud 2025.0.0
- Spring Cloud Alibaba 2025.0.0.0
- MySQL 8.0
- Redis 7.0
- Nacos 2.x

### 前端
- Vue 3.5.13
- Element Plus 2.8.7
- Vite 5.4.11
- TypeScript 5.6.3

### 文档
- Docusaurus 3.9.2
- React 19.0.0

### 基础设施
- Docker 20.10+
- Docker Compose 2.0+
- Nginx (Alpine)

---

## 📝 部署流程

### 简化流程（推荐）

```
1. 构建前端 → 2. 上传文件 → 3. 启动服务 → 4. 验证部署
   (5分钟)      (2分钟)       (3分钟)       (1分钟)
```

### 完整流程

```
1. 准备服务器
   ↓
2. 安装 Docker
   ↓
3. 构建前端项目
   ↓
4. 上传部署文件
   ↓
5. 配置环境变量
   ↓
6. 启动服务
   ↓
7. 配置 SSL（可选）
   ↓
8. 验证部署
   ↓
9. 安全加固
   ↓
10. 配置监控
```

---

## ✅ 部署检查清单

### 部署前
- [ ] 服务器已准备（满足最低配置）
- [ ] 已安装 Docker 和 Docker Compose
- [ ] 本地已安装 Node.js
- [ ] 已准备域名（可选）

### 部署中
- [ ] 前端项目构建成功
- [ ] 文档项目构建成功
- [ ] 文件上传成功
- [ ] 环境变量已配置
- [ ] 所有容器启动成功

### 部署后
- [ ] 可以访问管理后台
- [ ] 可以访问文档系统
- [ ] 可以登录系统
- [ ] API 接口正常
- [ ] 已修改默认密码
- [ ] 已配置 SSL（生产环境）
- [ ] 已配置备份
- [ ] 已配置监控

---

## 🔐 安全建议

1. ✅ 修改所有默认密码
2. ✅ 配置 HTTPS
3. ✅ 配置防火墙
4. ✅ 定期备份数据
5. ✅ 定期更新系统
6. ✅ 限制 SSH 访问
7. ✅ 配置监控告警

---

## 📞 获取帮助

### 常见问题
查看各文档的"常见问题"章节

### 日志查看
```bash
cd /opt/pig-platform
docker-compose logs -f [service_name]
```

### 社区支持
- PIG 官方文档: https://wiki.pig4cloud.com
- Docker 文档: https://docs.docker.com
- Nginx 文档: https://nginx.org/en/docs/

---

## 🎓 学习路径

### 初学者
1. 阅读 [QUICK-START.md](QUICK-START.md)
2. 跟着步骤操作
3. 遇到问题查看日志

### 进阶用户
1. 阅读 [README.md](README.md)
2. 了解架构设计
3. 学习运维技巧

### 架构师
1. 阅读 [DEPLOYMENT-OPTIONS.md](DEPLOYMENT-OPTIONS.md)
2. 评估不同方案
3. 选择最适合的方案

---

## 📈 后续优化

### 性能优化
- 调整 JVM 参数
- 配置 Redis 缓存
- 启用 CDN
- 数据库索引优化

### 高可用
- 数据库主从复制
- Redis 哨兵模式
- Nginx 负载均衡
- 服务多实例部署

### 监控告警
- Prometheus + Grafana
- ELK 日志分析
- 云监控服务
- 自定义告警规则

---

## 🔄 版本历史

- **v1.0** (2024-12-24)
  - 初始版本
  - 支持 Docker Compose 部署
  - 支持 PIG + PIG-UI + ZNHAAS-DOCS

---

## 📄 许可证

本部署方案遵循 Apache-2.0 许可证

---

**祝你部署顺利！** 🎉

如有问题，请先查看相关文档，大部分问题都能找到答案。
