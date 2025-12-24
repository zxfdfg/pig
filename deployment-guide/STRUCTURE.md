# 部署文件结构说明

## 📁 完整目录结构

```
deployment-guide/                    # 部署指南根目录
│
├── 📄 INDEX.md                      # 文档索引（从这里开始）
├── 📄 SUMMARY.md                    # 技术调研总结
├── 📄 README.md                     # 完整部署文档
├── 📄 QUICK-START.md                # 快速开始指南
├── 📄 DEPLOYMENT-OPTIONS.md         # 方案对比文档
├── 📄 STRUCTURE.md                  # 本文件
│
├── 🔧 docker-compose.yml            # Docker 编排配置
├── 🔧 .env                          # 环境变量配置
│
├── 📜 deploy.sh                     # Linux 部署脚本
├── 📜 deploy-windows.bat            # Windows 部署脚本
│
└── 📂 nginx/                        # Nginx 配置目录
    ├── nginx.conf                   # Nginx 主配置
    └── conf.d/                      # 站点配置目录
        └── default.conf             # 默认站点配置
```

---

## 📖 文档说明

### 核心文档

#### 1. INDEX.md - 文档索引 ⭐
**用途**: 文档导航，帮助快速找到需要的文档

**适合人群**: 所有用户

**内容**:
- 文档导航
- 快速链接
- 架构图
- 访问地址

---

#### 2. SUMMARY.md - 技术调研总结 ⭐⭐⭐
**用途**: 完整的技术调研报告

**适合人群**: 技术决策者、架构师

**内容**:
- 方案选择理由
- 架构设计
- 技术栈
- 资源规划
- 成本分析
- 最佳实践

---

#### 3. README.md - 完整部署文档 ⭐⭐⭐⭐
**用途**: 详细的部署指南

**适合人群**: 运维工程师、开发人员

**内容**:
- 方案概述
- 架构设计
- 服务器要求
- 详细部署步骤
- SSL 配置
- 监控维护
- 常见问题

---

#### 4. QUICK-START.md - 快速开始 ⭐⭐⭐⭐⭐
**用途**: 5分钟快速部署

**适合人群**: 想要快速上线的用户

**内容**:
- 简化的部署步骤
- 常用命令
- 故障排查
- 安全加固

---

#### 5. DEPLOYMENT-OPTIONS.md - 方案对比 ⭐⭐⭐
**用途**: 帮助选择合适的部署方案

**适合人群**: 不确定选择哪种方案的用户

**内容**:
- 4种方案对比
- 成本分析
- 选择建议
- 迁移路径

---

## 🔧 配置文件说明

### docker-compose.yml
**用途**: Docker 容器编排配置

**包含服务**:
- MySQL (数据库)
- Redis (缓存)
- Nacos (注册中心)
- Gateway (网关)
- Auth (认证服务)
- UPMS (用户权限)
- Codegen (代码生成)
- Monitor (监控)
- Nginx (反向代理)

**关键配置**:
```yaml
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    volumes:
      - ./mysql/data:/var/lib/mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
```

---

### .env
**用途**: 环境变量配置

**包含变量**:
- `MYSQL_ROOT_PASSWORD`: 数据库密码
- `TZ`: 时区设置
- `DOMAIN`: 域名配置

**示例**:
```env
MYSQL_ROOT_PASSWORD=your_secure_password
TZ=Asia/Shanghai
DOMAIN=yourdomain.com
```

---

### nginx/nginx.conf
**用途**: Nginx 主配置文件

**功能**:
- 工作进程配置
- 日志配置
- Gzip 压缩
- 包含站点配置

---

### nginx/conf.d/default.conf
**用途**: 站点配置文件

**功能**:
- HTTP 到 HTTPS 重定向
- SSL 配置
- 反向代理配置
- 静态文件服务
- 路径路由

**路由规则**:
```nginx
/admin/   → PIG-UI 静态文件
/api/     → Gateway 服务
/docs/    → 文档静态文件
/monitor/ → Monitor 服务
```

---

## 📜 脚本说明

### deploy.sh (Linux)
**用途**: Linux 服务器自动化部署脚本

**功能**:
1. 检查 Docker 环境
2. 创建目录结构
3. 备份现有数据
4. 停止现有服务
5. 复制配置文件
6. 初始化数据库
7. 启动服务
8. 验证部署

**使用方法**:
```bash
chmod +x deploy.sh
./deploy.sh
```

---

### deploy-windows.bat (Windows)
**用途**: Windows 本地一键部署脚本

**功能**:
1. 检查本地环境
2. 检查服务器环境
3. 构建 PIG-UI
4. 构建文档
5. 创建服务器目录
6. 上传配置文件
7. 上传前端文件
8. 上传文档文件
9. 上传数据库脚本
10. 启动服务

**使用方法**:
```cmd
双击运行 deploy-windows.bat
或
deploy-windows.bat
```

---

## 🚀 使用流程

### 第一次部署

```
1. 阅读 INDEX.md
   ↓
2. 选择阅读 QUICK-START.md 或 README.md
   ↓
3. 准备服务器和本地环境
   ↓
4. 修改 .env 配置
   ↓
5. 运行部署脚本
   ↓
6. 验证部署结果
```

### 日常更新

```
1. 构建新版本
   ↓
2. 上传文件
   ↓
3. 重启服务
```

### 故障排查

```
1. 查看 QUICK-START.md 或 README.md 的故障排查章节
   ↓
2. 查看日志
   ↓
3. 检查配置
   ↓
4. 重启服务
```

---

## 📋 文件清单

### 必需文件 ✅

| 文件 | 说明 | 必需 |
|------|------|------|
| docker-compose.yml | Docker 编排 | ✅ |
| .env | 环境变量 | ✅ |
| nginx/nginx.conf | Nginx 主配置 | ✅ |
| nginx/conf.d/default.conf | 站点配置 | ✅ |

### 可选文件 ⭐

| 文件 | 说明 | 推荐 |
|------|------|------|
| deploy.sh | Linux 部署脚本 | ⭐⭐⭐ |
| deploy-windows.bat | Windows 部署脚本 | ⭐⭐⭐ |

### 文档文件 📖

| 文件 | 说明 | 优先级 |
|------|------|--------|
| INDEX.md | 文档索引 | ⭐⭐⭐⭐⭐ |
| QUICK-START.md | 快速开始 | ⭐⭐⭐⭐⭐ |
| README.md | 完整文档 | ⭐⭐⭐⭐ |
| SUMMARY.md | 技术总结 | ⭐⭐⭐ |
| DEPLOYMENT-OPTIONS.md | 方案对比 | ⭐⭐⭐ |
| STRUCTURE.md | 本文件 | ⭐⭐ |

---

## 🎯 推荐阅读顺序

### 新手用户

```
1. INDEX.md (了解整体)
   ↓
2. QUICK-START.md (快速上手)
   ↓
3. README.md 的常见问题部分 (遇到问题时)
```

### 进阶用户

```
1. INDEX.md (了解整体)
   ↓
2. README.md (详细了解)
   ↓
3. DEPLOYMENT-OPTIONS.md (了解其他方案)
```

### 架构师/决策者

```
1. SUMMARY.md (技术调研)
   ↓
2. DEPLOYMENT-OPTIONS.md (方案对比)
   ↓
3. README.md (实施细节)
```

---

## 💡 使用建议

### 1. 首次部署
- 先阅读 QUICK-START.md
- 跟着步骤操作
- 遇到问题查看 README.md

### 2. 生产部署
- 完整阅读 README.md
- 了解架构设计
- 做好安全加固
- 配置监控告警

### 3. 方案选择
- 阅读 DEPLOYMENT-OPTIONS.md
- 评估自己的需求
- 选择合适的方案

### 4. 技术调研
- 阅读 SUMMARY.md
- 了解技术细节
- 评估成本和风险

---

## 📞 获取帮助

### 找不到需要的信息？

1. 查看 INDEX.md 的文档导航
2. 使用文档内搜索功能
3. 查看各文档的目录

### 遇到部署问题？

1. 查看 QUICK-START.md 的故障排查
2. 查看 README.md 的常见问题
3. 查看日志输出

### 不确定选择哪个方案？

1. 阅读 DEPLOYMENT-OPTIONS.md
2. 评估自己的需求
3. 参考决策清单

---

## 🔄 文档更新

### 版本历史

- **v1.0** (2024-12-24)
  - 初始版本
  - 完整的部署方案
  - 详细的文档说明

### 后续计划

- [ ] 添加视频教程
- [ ] 添加更多示例
- [ ] 添加性能优化指南
- [ ] 添加高可用方案

---

## 📝 贡献指南

如果你想改进这些文档：

1. 提出改进建议
2. 补充缺失内容
3. 修正错误信息
4. 添加最佳实践

---

**文档结构说明完成！**

希望这个结构说明能帮助你快速找到需要的文档和配置文件。

如有疑问，请从 INDEX.md 开始阅读。
