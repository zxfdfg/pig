# PIG 分销系统

基于 PIG 微服务平台的多级分销系统，支持逐级分享、利润分配、提成结算等核心功能。

## 功能特性

- ✅ 多级分销体系（支持 N 级分销，当前配置 3 级）
- ✅ 灵活的佣金配置
- ✅ 分销商等级管理（5 级会员体系）
- ✅ 实时订单追踪
- ✅ 自动化结算
- ✅ 提现管理
- ✅ 数据统计分析

## 技术栈

- Spring Boot 3.5.8
- Spring Cloud 2025.0.0
- MyBatis Plus 3.5.15
- MySQL 8.0+
- Redis（推荐）
- Nacos

## 快速开始

### 1. 数据库初始化

```bash
# 执行 SQL 脚本
mysql -u root -p your_database < ../db/distribution-schema.sql
```

### 2. 配置 Nacos

在 Nacos 配置中心添加配置文件 `pig-distribution-biz-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pig?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  mapper-locations: classpath:/mapper/*Mapper.xml
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: delFlag
      logic-delete-value: 1
      logic-not-delete-value: 0
  configuration:
    map-underscore-to-camel-case: true
```

### 3. 启动服务

```bash
# 方式一：Maven 启动
cd pig-distribution-biz
mvn spring-boot:run

# 方式二：IDEA 启动
# 直接运行 DistributionApplication.java

# 方式三：Docker 启动
mvn clean package docker:build
docker run -d -p 4200:4200 pig-distribution-biz
```

### 4. 验证服务

访问 Swagger 文档：http://localhost:4100/doc.html

## 模块说明

### pig-distribution-api

API 模块，包含实体类、DTO、VO 等公共类。

**主要类**:
- `Distributor` - 分销商实体
- `CommissionConfig` - 佣金配置实体
- `OrderCommission` - 订单佣金实体
- `Withdraw` - 提现记录实体
- `DistRelation` - 分销关系实体

### pig-distribution-biz

业务模块，包含服务实现、控制器等。

**主要功能**:
- 分销商管理
- 佣金计算
- 提现管理
- 数据统计

## API 接口

### 分销商管理

```
POST   /distributor/apply          申请成为分销商
GET    /distributor/info           获取分销商信息
GET    /distributor/children       获取下级分销商列表
GET    /distributor/team/stats     获取团队统计
```

### 佣金管理

```
GET    /commission/list            获取佣金列表
GET    /commission/stats           获取佣金统计
GET    /commission/detail/{id}     获取佣金明细
```

### 提现管理

```
POST   /withdraw/apply             申请提现
GET    /withdraw/list              获取提现记录
GET    /withdraw/detail/{id}       获取提现详情
```

## 数据库表

| 表名 | 说明 |
|------|------|
| dist_distributor | 分销商表 |
| dist_relation | 分销关系表 |
| dist_commission_config | 佣金配置表 |
| dist_order_commission | 订单佣金表 |
| dist_withdraw | 提现记录表 |
| dist_commission_log | 佣金流水表 |

## 配置说明

### 佣金配置

默认佣金比例：
- 一级分销：10%
- 二级分销：5%
- 三级分销：2%

可在 `dist_commission_config` 表中修改配置。

### 分销商等级

| 等级 | 名称 | 条件 | 佣金加成 |
|------|------|------|---------|
| V1 | 普通会员 | 注册即可 | 0% |
| V2 | 铜牌分销商 | 累计销售 1万 | +5% |
| V3 | 银牌分销商 | 累计销售 5万 | +10% |
| V4 | 金牌分销商 | 累计销售 10万 | +15% |
| V5 | 钻石分销商 | 累计销售 50万 | +20% |

## 开发指南

### 添加新功能

1. 在 `api` 模块添加 DTO/VO
2. 在 `biz` 模块添加 Service 接口和实现
3. 在 `biz` 模块添加 Controller
4. 添加权限配置

### 代码规范

遵循 PIG 项目的编码规范：
- 使用 Spring Java Format 格式化代码
- 提交前运行 `mvn spring-javaformat:apply`
- 使用 Lombok 简化代码
- 使用 Swagger 注解生成 API 文档

## 常见问题

### Q: 如何修改分销层级？

A: 修改 `DistributorServiceImpl.buildRelation()` 方法中的层级限制。

### Q: 如何自定义佣金计算规则？

A: 实现 `CommissionService` 接口，自定义佣金计算逻辑。

### Q: 如何集成到现有订单系统？

A: 在订单确认收货后调用 `CommissionService.calculateCommission()` 方法。

## 注意事项

⚠️ **法律合规**: 请确保分销层级符合当地法律法规（建议不超过 3 级）

⚠️ **安全性**: 
- 所有接口需要添加权限控制
- 敏感信息需要加密存储
- 提现操作需要二次验证

⚠️ **性能优化**:
- 建议使用 Redis 缓存热点数据
- 异步处理佣金计算
- 定时任务批量结算

## 更新日志

### v1.0.0 (2025-12-07)

- ✅ 初始版本发布
- ✅ 分销商管理功能
- ✅ 分销关系建立
- ✅ 基础 API 接口

## 许可证

Apache License 2.0

## 联系方式

- 官网: https://www.pig4cloud.com
- 文档: https://wiki.pig4cloud.com
- 问题反馈: https://gitee.com/log4j/pig/issues
