# 目录结构详解

## 📁 完整目录结构图

### 本地项目结构（部署前）

```
~/projects/                          # 你的项目根目录
│
├── pig/                             # 后端项目（Spring Boot）
│   ├── pig-register/                # Nacos 注册中心
│   │   ├── src/
│   │   ├── pom.xml
│   │   └── Dockerfile               # ← 需要创建
│   ├── pig-gateway/                 # 网关服务
│   │   ├── src/
│   │   ├── pom.xml
│   │   └── Dockerfile               # ← 需要创建
│   ├── pig-auth/                    # 认证服务
│   │   ├── src/
│   │   ├── pom.xml
│   │   └── Dockerfile               # ← 需要创建
│   ├── pig-upms/                    # 用户权限服务
│   │   └── pig-upms-biz/
│   │       ├── src/
│   │       ├── pom.xml
│   │       └── Dockerfile           # ← 需要创建
│   ├── pig-visual/                  # 可视化模块
│   │   ├── pig-codegen/
│   │   │   ├── src/
│   │   │   ├── pom.xml
│   │   │   └── Dockerfile           # ← 需要创建
│   │   └── pig-monitor/
│   │       ├── src/
│   │       ├── pom.xml
│   │       └── Dockerfile           # ← 需要创建
│   └── db/                          # 数据库脚本
│       ├── pig.sql
│       └── pig_config.sql
│
├── pig-ui/                          # 前端项目（Vue 3）
│   ├── src/
│   ├── package.json
│   └── dist/                        # ← npm run build 生成
│       ├── index.html
│       └── assets/
│
├── znhaas-docs/                     # 文档项目（Docusaurus）
│   ├── docs/
│   ├── package.json
│   └── build/                       # ← npm run build 生成
│       ├── index.html
│       └── assets/
│
└── deployment-guide/                # 部署配置（本仓库）
    ├── docker-compose.yml
    ├── .env
    ├── nginx/
    │   ├── nginx.conf
    │   └── conf.d/
    │       └── default.conf
    └── README.md
```

---

## 🎯 临时部署目录（打包前）

### 关键步骤：将所有文件复制到一个目录

```
/tmp/pig-deploy/                     # 临时部署目录（所有文件都在这里）
│
├── docker-compose.yml               # 从 deployment-guide/ 复制
├── .env                             # 从 deployment-guide/ 复制
│
├── nginx/                           # 从 deployment-guide/ 复制
│   ├── nginx.conf
│   └── conf.d/
│       └── default.conf
│
├── pig-services/                    # 后端源码（新建目录）
│   ├── pig-register/                # 从 pig/ 复制
│   │   ├── src/
│   │   ├── pom.xml
│   │   └── Dockerfile
│   ├── pig-gateway/                 # 从 pig/ 复制
│   │   ├── src/
│   │   ├── pom.xml
│   │   └── Dockerfile
│   ├── pig-auth/                    # 从 pig/ 复制
│   │   ├── src/
│   │   ├── pom.xml
│   │   └── Dockerfile
│   ├── pig-upms/                    # 从 pig/ 复制
│   │   └── pig-upms-biz/
│   │       ├── src/
│   │       ├── pom.xml
│   │       └── Dockerfile
│   └── pig-visual/                  # 从 pig/ 复制
│       ├── pig-codegen/
│       │   ├── src/
│       │   ├── pom.xml
│       │   └── Dockerfile
│       └── pig-monitor/
│           ├── src/
│           ├── pom.xml
│           └── Dockerfile
│
├── pig-ui/                          # 前端文件（新建目录）
│   └── dist/                        # 从 pig-ui/dist/ 复制
│       ├── index.html
│       ├── assets/
│       └── ...
│
├── docs/                            # 文档文件（新建目录）
│   └── build/                       # 从 znhaas-docs/build/ 复制
│       ├── index.html
│       ├── assets/
│       └── ...
│
└── mysql/                           # 数据库脚本（新建目录）
    └── init/
        ├── pig.sql                  # 从 pig/db/ 复制
        └── pig_config.sql           # 从 pig/db/ 复制
```

---

## 📦 打包过程详解

### 步骤 1: 准备文件

```bash
# 创建临时目录
mkdir -p /tmp/pig-deploy
cd /tmp/pig-deploy

# 复制所有文件到这个目录
# （详细命令见下文）
```

### 步骤 2: 打包

```bash
cd /tmp/pig-deploy

# 打包当前目录的所有内容
tar -czf pig-deploy.tar.gz *

# 这会创建一个包含以下内容的压缩包：
# - docker-compose.yml
# - .env
# - nginx/
# - pig-services/
# - pig-ui/
# - docs/
# - mysql/
```

### 步骤 3: 验证压缩包

```bash
# 查看压缩包内容
tar -tzf pig-deploy.tar.gz | head -30

# 输出示例：
# docker-compose.yml
# .env
# nginx/
# nginx/nginx.conf
# nginx/conf.d/
# nginx/conf.d/default.conf
# pig-services/
# pig-services/pig-gateway/
# pig-services/pig-gateway/src/
# pig-services/pig-gateway/pom.xml
# pig-services/pig-gateway/Dockerfile
# ...
```

---

## 🚀 服务器目录结构（解压后）

```
/opt/pig-platform/                   # 服务器部署目录
│
├── docker-compose.yml               # Docker 编排配置
├── .env                             # 环境变量
│
├── nginx/                           # Nginx 配置
│   ├── nginx.conf
│   └── conf.d/
│       └── default.conf
│
├── pig-services/                    # 后端源码
│   ├── pig-register/
│   ├── pig-gateway/
│   ├── pig-auth/
│   ├── pig-upms/
│   └── pig-visual/
│
├── pig-ui/                          # 前端静态文件
│   └── dist/
│       ├── index.html
│       └── assets/
│
├── docs/                            # 文档静态文件
│   └── build/
│       ├── index.html
│       └── assets/
│
├── mysql/                           # 数据库
│   ├── init/                        # 初始化脚本
│   │   ├── pig.sql
│   │   └── pig_config.sql
│   └── data/                        # 数据持久化（自动创建）
│
└── redis/                           # Redis
    └── data/                        # 数据持久化（自动创建）
```

---

## 🔍 docker-compose.yml 中的路径映射

### 理解 context 路径

```yaml
services:
  gateway:
    build:
      context: ./pig-services/pig-gateway  # ← 相对于 docker-compose.yml
    # Docker Compose 会在这个目录找 Dockerfile
```

### 完整映射关系

```
docker-compose.yml 位置: /opt/pig-platform/docker-compose.yml

服务配置:
  gateway:
    context: ./pig-services/pig-gateway
    → 实际路径: /opt/pig-platform/pig-services/pig-gateway/
    → 查找文件: /opt/pig-platform/pig-services/pig-gateway/Dockerfile

  auth:
    context: ./pig-services/pig-auth
    → 实际路径: /opt/pig-platform/pig-services/pig-auth/
    → 查找文件: /opt/pig-platform/pig-services/pig-auth/Dockerfile

  nginx:
    volumes:
      - ./pig-ui/dist:/usr/share/nginx/html/admin
      → 宿主机: /opt/pig-platform/pig-ui/dist/
      → 容器内: /usr/share/nginx/html/admin/

      - ./docs/build:/usr/share/nginx/html/docs
      → 宿主机: /opt/pig-platform/docs/build/
      → 容器内: /usr/share/nginx/html/docs/
```

---

## 📝 完整复制命令

### 一次性复制所有文件

```bash
#!/bin/bash

# 设置项目路径（根据实际情况修改）
PIG_DIR=~/projects/pig
PIG_UI_DIR=~/projects/pig-ui
DOCS_DIR=~/projects/znhaas-docs
DEPLOY_CONFIG_DIR=~/projects/deployment-guide
TEMP_DIR=/tmp/pig-deploy

# 1. 创建临时目录
echo "创建临时目录..."
mkdir -p $TEMP_DIR
cd $TEMP_DIR

# 2. 复制部署配置
echo "复制部署配置..."
cp -r $DEPLOY_CONFIG_DIR/* .

# 3. 复制后端源码
echo "复制后端源码..."
mkdir -p pig-services
cp -r $PIG_DIR/pig-register pig-services/
cp -r $PIG_DIR/pig-gateway pig-services/
cp -r $PIG_DIR/pig-auth pig-services/
cp -r $PIG_DIR/pig-upms pig-services/
cp -r $PIG_DIR/pig-visual pig-services/

# 4. 复制前端构建文件
echo "复制前端文件..."
mkdir -p pig-ui/dist
cp -r $PIG_UI_DIR/dist/* pig-ui/dist/

# 5. 复制文档构建文件
echo "复制文档文件..."
mkdir -p docs/build
cp -r $DOCS_DIR/build/* docs/build/

# 6. 复制数据库脚本
echo "复制数据库脚本..."
mkdir -p mysql/init
cp $PIG_DIR/db/pig.sql mysql/init/
cp $PIG_DIR/db/pig_config.sql mysql/init/

# 7. 验证目录结构
echo "验证目录结构..."
ls -la

# 8. 打包
echo "打包文件..."
tar -czf pig-deploy.tar.gz *

echo "完成！压缩包位置: $TEMP_DIR/pig-deploy.tar.gz"
```

### 保存为脚本

```bash
# 保存上面的脚本为 prepare-deploy.sh
chmod +x prepare-deploy.sh

# 运行
./prepare-deploy.sh
```

---

## 🎯 关键要点

### 1. 为什么要创建临时目录？

```
❌ 错误做法：在不同目录分别打包
tar -czf pig.tar.gz pig/
tar -czf pig-ui.tar.gz pig-ui/
# 需要多次上传和解压

✅ 正确做法：统一到一个目录再打包
mkdir /tmp/pig-deploy
cp -r pig/ pig-ui/ docs/ /tmp/pig-deploy/
cd /tmp/pig-deploy
tar -czf pig-deploy.tar.gz *
# 一次上传，一次解压
```

### 2. 为什么用 `tar -czf pig-deploy.tar.gz *`？

```bash
# * 表示当前目录的所有文件和文件夹
# 打包后解压会保持相同的目录结构

# 例如：
cd /tmp/pig-deploy
tar -czf pig-deploy.tar.gz *

# 解压后：
tar -xzf pig-deploy.tar.gz -C /opt/pig-platform
# 会在 /opt/pig-platform/ 下创建：
# docker-compose.yml
# pig-services/
# pig-ui/
# 等等...
```

### 3. 目录结构必须匹配

```
docker-compose.yml 中的路径:
  context: ./pig-services/pig-gateway

必须对应实际目录:
  /opt/pig-platform/pig-services/pig-gateway/

如果目录不匹配，Docker Compose 会报错：
  ERROR: Cannot locate specified Dockerfile
```

---

## 📋 检查清单

### 打包前检查

```bash
cd /tmp/pig-deploy

# 检查必需文件
[ -f docker-compose.yml ] && echo "✓ docker-compose.yml" || echo "✗ docker-compose.yml"
[ -f .env ] && echo "✓ .env" || echo "✗ .env"
[ -d nginx ] && echo "✓ nginx/" || echo "✗ nginx/"
[ -d pig-services ] && echo "✓ pig-services/" || echo "✗ pig-services/"
[ -d pig-ui/dist ] && echo "✓ pig-ui/dist/" || echo "✗ pig-ui/dist/"
[ -d docs/build ] && echo "✓ docs/build/" || echo "✗ docs/build/"
[ -d mysql/init ] && echo "✓ mysql/init/" || echo "✗ mysql/init/"

# 检查后端源码
[ -d pig-services/pig-gateway ] && echo "✓ pig-gateway" || echo "✗ pig-gateway"
[ -d pig-services/pig-auth ] && echo "✓ pig-auth" || echo "✗ pig-auth"
[ -d pig-services/pig-upms ] && echo "✓ pig-upms" || echo "✗ pig-upms"
```

### 解压后检查

```bash
cd /opt/pig-platform

# 检查目录结构
tree -L 2

# 或者
ls -la
```

---

**现在应该完全清楚目录结构和打包过程了！** 🎉
