#!/bin/bash

# 部署脚本 - 一键部署 PIG 平台到服务器
# 使用方法: ./deploy.sh

set -e

echo "=========================================="
echo "  PIG 平台一键部署脚本"
echo "=========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 配置变量
DEPLOY_DIR="/opt/pig-platform"
BACKUP_DIR="/opt/pig-platform-backup"

# 检查是否为 root 用户
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}请使用 root 用户运行此脚本${NC}"
    exit 1
fi

# 步骤 1: 检查 Docker 环境
echo -e "${YELLOW}[1/8] 检查 Docker 环境...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker 未安装，正在安装...${NC}"
    curl -fsSL https://get.docker.com | bash
    systemctl start docker
    systemctl enable docker
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}Docker Compose 未安装，正在安装...${NC}"
    curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
fi

echo -e "${GREEN}✓ Docker 环境检查完成${NC}"

# 步骤 2: 创建目录结构
echo -e "${YELLOW}[2/8] 创建目录结构...${NC}"
mkdir -p ${DEPLOY_DIR}/{nginx/{conf.d,ssl},mysql/{data,init},redis/data,pig-ui/dist,docs/build}
echo -e "${GREEN}✓ 目录创建完成${NC}"

# 步骤 3: 备份现有数据（如果存在）
if [ -d "${DEPLOY_DIR}/mysql/data" ] && [ "$(ls -A ${DEPLOY_DIR}/mysql/data)" ]; then
    echo -e "${YELLOW}[3/8] 备份现有数据...${NC}"
    BACKUP_TIME=$(date +%Y%m%d_%H%M%S)
    mkdir -p ${BACKUP_DIR}/${BACKUP_TIME}
    cp -r ${DEPLOY_DIR}/mysql/data ${BACKUP_DIR}/${BACKUP_TIME}/
    echo -e "${GREEN}✓ 数据已备份到 ${BACKUP_DIR}/${BACKUP_TIME}${NC}"
else
    echo -e "${YELLOW}[3/8] 跳过备份（无现有数据）${NC}"
fi

# 步骤 4: 停止现有服务
echo -e "${YELLOW}[4/8] 停止现有服务...${NC}"
if [ -f "${DEPLOY_DIR}/docker-compose.yml" ]; then
    cd ${DEPLOY_DIR}
    docker-compose down || true
fi
echo -e "${GREEN}✓ 服务已停止${NC}"

# 步骤 5: 复制配置文件
echo -e "${YELLOW}[5/8] 复制配置文件...${NC}"
# 这里假设配置文件已经在当前目录
if [ -f "docker-compose.yml" ]; then
    cp docker-compose.yml ${DEPLOY_DIR}/
    cp .env ${DEPLOY_DIR}/
    cp -r nginx/* ${DEPLOY_DIR}/nginx/
    echo -e "${GREEN}✓ 配置文件复制完成${NC}"
else
    echo -e "${RED}错误: 找不到配置文件，请确保在正确的目录运行脚本${NC}"
    exit 1
fi

# 步骤 6: 初始化数据库（仅首次）
if [ ! -d "${DEPLOY_DIR}/mysql/data" ] || [ ! "$(ls -A ${DEPLOY_DIR}/mysql/data)" ]; then
    echo -e "${YELLOW}[6/8] 初始化数据库...${NC}"
    # 复制数据库初始化脚本
    if [ -f "../../pig/db/pig.sql" ]; then
        cp ../../pig/db/pig.sql ${DEPLOY_DIR}/mysql/init/
        cp ../../pig/db/pig_config.sql ${DEPLOY_DIR}/mysql/init/
        echo -e "${GREEN}✓ 数据库脚本已复制${NC}"
    else
        echo -e "${YELLOW}警告: 未找到数据库初始化脚本${NC}"
    fi
else
    echo -e "${YELLOW}[6/8] 跳过数据库初始化（已存在）${NC}"
fi

# 步骤 7: 启动服务
echo -e "${YELLOW}[7/8] 启动服务...${NC}"
cd ${DEPLOY_DIR}
docker-compose up -d

# 等待服务启动
echo "等待服务启动..."
sleep 10

echo -e "${GREEN}✓ 服务启动完成${NC}"

# 步骤 8: 验证部署
echo -e "${YELLOW}[8/8] 验证部署...${NC}"
docker-compose ps

echo ""
echo "=========================================="
echo -e "${GREEN}部署完成！${NC}"
echo "=========================================="
echo ""
echo "访问地址:"
echo "  - 管理后台: http://your-server-ip/admin/"
echo "  - 文档系统: http://your-server-ip/docs/"
echo "  - 监控面板: http://your-server-ip/monitor/"
echo ""
echo "常用命令:"
echo "  - 查看日志: cd ${DEPLOY_DIR} && docker-compose logs -f"
echo "  - 重启服务: cd ${DEPLOY_DIR} && docker-compose restart"
echo "  - 停止服务: cd ${DEPLOY_DIR} && docker-compose down"
echo ""
echo "注意事项:"
echo "  1. 首次部署需要等待 2-3 分钟让所有服务完全启动"
echo "  2. 默认管理员账号: admin / admin"
echo "  3. 请及时修改 .env 文件中的数据库密码"
echo "  4. 生产环境请配置 SSL 证书"
echo ""
