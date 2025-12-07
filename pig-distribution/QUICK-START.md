# 分销系统快速启动

## 🚀 5 分钟快速启动

### 1️⃣ 数据库初始化（1分钟）

```bash
mysql -u root -p
CREATE DATABASE IF NOT EXISTS pig DEFAULT CHARACTER SET utf8mb4;
USE pig;
SOURCE ../db/distribution-schema.sql;
```

### 2️⃣ 配置 Nacos（2分钟）

**Data ID**: `pig-distribution-biz-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pig
    username: root
    password: your_password
```

### 3️⃣ 启动服务（1分钟）

```bash
cd pig-distribution-biz
mvn spring-boot:run
```

### 4️⃣ 验证（1分钟）

访问: http://localhost:4200/doc.html

## 📡 API 快速测试

### 申请成为分销商

```bash
curl -X POST http://localhost:4200/distributor/apply \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "realName": "张三",
    "phone": "13800138000",
    "idCard": "110101199001011234",
    "parentId": null
  }'
```

### 获取分销商信息

```bash
curl -X GET http://localhost:4200/distributor/info \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🎯 核心配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| 服务端口 | 4200 | 分销服务端口 |
| 服务名 | pig-distribution-biz | 注册到 Nacos |
| 数据库 | pig | 数据库名称 |
| 一级佣金 | 10% | 默认配置 |
| 二级佣金 | 5% | 默认配置 |
| 三级佣金 | 2% | 默认配置 |

## 📊 数据库表

```
dist_distributor          # 分销商表
dist_relation            # 分销关系表
dist_commission_config   # 佣金配置表
dist_order_commission    # 订单佣金表
dist_withdraw            # 提现记录表
dist_commission_log      # 佣金流水表
```

## 🔑 关键代码位置

```
后端:
  实体类: pig-distribution-api/src/.../entity/
  服务层: pig-distribution-biz/src/.../service/
  控制器: pig-distribution-biz/src/.../controller/

前端:
  页面: pig-ui/src/views/distribution/
  API: pig-ui/src/api/distribution/
```

## ⚡ 常用命令

```bash
# 格式化代码
mvn spring-javaformat:apply

# 编译打包
mvn clean package

# 启动服务
mvn spring-boot:run

# Docker 构建
mvn clean package docker:build
```

## 🐛 常见问题

**Q: 服务启动失败？**
A: 检查 Nacos 是否启动，配置是否正确

**Q: 数据库连接失败？**
A: 检查数据库地址、用户名、密码

**Q: 接口 401 错误？**
A: 检查 Token 是否有效

## 📚 更多文档

- 详细设计: `distribution-system-plan.md`
- 实施总结: `distribution-implementation-summary.md`
- 完整 README: `README.md`
