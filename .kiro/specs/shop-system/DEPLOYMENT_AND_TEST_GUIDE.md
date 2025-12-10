# 🚀 商城系统部署与测试指南

**创建时间**: 2025-12-10 20:45  
**状态**: ✅ 代码集成完成，待测试

---

## ✅ 已完成的集成工作

### 1. 代码集成 (100%)

- ✅ 添加 Distribution API 依赖到 Product 模块
- ✅ 在 ShopOrderServiceImpl 中注入 RemoteCommissionService
- ✅ 实现 calculateCommission 方法（调用 Feign 接口）
- ✅ 在 cancelOrder 方法中添加佣金取消逻辑
- ✅ 在 CommissionController 中添加 cancel 接口
- ✅ 更新 ServiceNameConstants 添加服务名称常量

### 2. 核心功能

**订单支付成功后**:
```java
if (order.getDistributorId() != null) {
    // 调用分销系统计算佣金
    remoteCommissionService.calculateCommission(
        order.getId(),
        order.getOrderNo(),
        order.getUserId(),
        order.getPayAmount()
    );
}
```

**取消订单时**:
```java
if (order.getDistributorId() != null && order.getPayStatus() == 1) {
    // 取消已计算的佣金
    remoteCommissionService.cancelCommission(order.getId());
}
```

---

## 📋 部署步骤

### 步骤 1: 编译项目

在项目根目录执行：

```bash
# 清理并编译整个项目
mvn clean install -DskipTests

# 或者只编译需要的模块
mvn clean install -pl pig-common/pig-common-core,pig-distribution/pig-distribution-api,pig-product/pig-product-api,pig-product/pig-product-biz -am -DskipTests
```

### 步骤 2: 初始化数据库

```bash
# 进入数据库目录
cd pig/db

# 初始化数据库（会删除并重建）
mysql -u root -p < pig.sql
mysql -u root -p < pig_config.sql

# 导入测试数据
mysql -u root -p < test-data/test_data.sql
```

**重要**: 确保数据库中包含：
- 分销系统表（distributor, order_commission 等）
- 商城系统表（shop_cart, shop_order 等）
- 测试数据（admin 用户、商品、分销商等）

### 步骤 3: 启动服务

按以下顺序启动：

#### 1. Nacos 注册中心 (8848)
```bash
cd pig/pig-register
mvn spring-boot:run
```

访问: http://localhost:8848/nacos  
账号: nacos / nacos

#### 2. Gateway 网关 (9999)
```bash
cd pig/pig-gateway
mvn spring-boot:run
```

#### 3. UPMS 服务 (4000)
```bash
cd pig/pig-upms/pig-upms-biz
mvn spring-boot:run
```

#### 4. Auth 服务 (3000)
```bash
cd pig/pig-auth
mvn spring-boot:run
```

#### 5. Distribution 服务 (4200)
```bash
cd pig/pig-distribution/pig-distribution-biz
mvn spring-boot:run
```

#### 6. Product 服务 (4300)
```bash
cd pig/pig-product/pig-product-biz
mvn spring-boot:run
```

#### 7. 前端项目 (8888)
```bash
cd pig-ui
npm run dev
```

### 步骤 4: 验证服务启动

访问 Nacos 控制台，确认所有服务已注册：
- pig-auth
- pig-upms-biz
- pig-distribution-biz
- pig-product-biz
- pig-gateway

---

## 🧪 测试场景

### 场景 1: 普通订单（无推广）

**目的**: 验证基本购物流程

**步骤**:
1. 访问前端: http://localhost:8888
2. 登录系统 (admin/admin)
3. 访问商城: http://localhost:8888/#/shop
4. 浏览商品列表
5. 点击商品查看详情
6. 加入购物车
7. 进入购物车，选择商品
8. 点击"去结算"
9. 选择收货地址
10. 提交订单
11. 模拟支付

**预期结果**:
- ✅ 订单创建成功
- ✅ 支付成功
- ✅ 订单状态变为"待发货"
- ✅ 库存正确扣减
- ✅ **不触发佣金计算**（因为没有 distributorId）

**验证日志**:
```
订单支付成功，订单号：ORDER123
```

---

### 场景 2: 推广订单（带分销商）⭐ 重点

**目的**: 验证佣金计算集成

**步骤**:
1. 确保数据库中有分销商数据（test_data.sql 中的 admin 用户）
2. 访问推广链接: http://localhost:8888/#/shop/product/1?distributorId=1
3. 查看商品详情（应显示"由分销商推荐"）
4. 点击"立即购买"
5. 选择收货地址
6. 提交订单
7. 模拟支付

**预期结果**:
- ✅ 订单创建成功，`distributor_id` 字段为 1
- ✅ 支付成功
- ✅ **触发佣金计算**
- ✅ 在 `order_commission` 表中生成佣金记录
- ✅ 分销商余额增加

**验证日志 - Product 服务**:
```
订单支付成功，开始计算佣金，订单号：ORDER123，分销商ID：1
触发佣金计算：订单ID=1，订单号=ORDER123，购买者ID=1，订单金额=999.00
订单 ORDER123 佣金计算成功
```

**验证日志 - Distribution 服务**:
```
开始计算佣金: orderId=1, buyerId=1, amount=999.00
查询购买者的推荐分销商
计算一级佣金: 分销商ID=1, 佣金=99.90
佣金计算完成，共生成 1 条佣金记录
```

**数据库验证**:
```sql
-- 查询订单
SELECT id, order_no, distributor_id, pay_amount, status 
FROM shop_order 
WHERE order_no = 'ORDER123';

-- 查询佣金记录
SELECT * FROM order_commission 
WHERE order_id = 1;

-- 查询分销商余额
SELECT id, user_id, total_commission, available_balance 
FROM distributor 
WHERE id = 1;
```

---

### 场景 3: 取消推广订单

**目的**: 验证佣金取消功能

**步骤**:
1. 创建并支付一个推广订单（参考场景 2）
2. 等待佣金计算完成
3. 在订单列表中找到该订单
4. 点击"取消订单"
5. 输入取消原因

**预期结果**:
- ✅ 订单状态变为"已取消"
- ✅ 库存释放
- ✅ **佣金记录被取消**
- ✅ 分销商余额回滚

**验证日志 - Product 服务**:
```
取消订单，同时取消佣金，订单ID：1
订单 ORDER123 佣金取消成功
取消订单成功，订单号：ORDER123
```

**验证日志 - Distribution 服务**:
```
取消订单佣金: orderId=1
查询订单相关的佣金记录
取消佣金记录，数量：1
更新分销商余额
佣金取消完成
```

---

### 场景 4: 多级分销（高级）

**前提**: 需要在数据库中建立分销关系

**步骤**:
1. 创建分销关系：用户 A → 用户 B → 用户 C
2. 用户 C 通过用户 A 的推广链接购买商品
3. 支付订单

**预期结果**:
- ✅ 生成 3 条佣金记录（一级、二级、三级）
- ✅ 用户 A 获得一级佣金（最高）
- ✅ 用户 B 获得二级佣金
- ✅ 用户 C 的推荐人获得三级佣金

---

## 🔍 故障排查

### 问题 1: 服务启动失败

**症状**: 服务无法启动，报错 "Connection refused"

**解决方案**:
1. 确认 Nacos 已启动并正常运行
2. 检查 Nacos 配置中心是否有对应的配置文件
3. 检查服务端口是否被占用

### 问题 2: Feign 调用失败

**症状**: 日志显示 "FeignException: status 404"

**解决方案**:
1. 确认 Distribution 服务已启动
2. 检查 CommissionController 的 @RequestMapping 路径
3. 检查 RemoteCommissionService 的接口路径是否匹配
4. 在 Nacos 中确认服务名称是否正确

### 问题 3: 佣金计算不触发

**症状**: 订单支付成功，但没有佣金记录

**检查清单**:
- [ ] 订单的 `distributor_id` 字段是否有值
- [ ] Product 服务日志是否有"触发佣金计算"
- [ ] Distribution 服务是否正常运行
- [ ] Feign 调用是否成功（检查日志）

### 问题 4: 佣金计算失败

**症状**: 日志显示"佣金计算失败"

**检查清单**:
- [ ] 分销商是否存在且状态正常
- [ ] 佣金配置是否存在
- [ ] 数据库连接是否正常
- [ ] 查看 Distribution 服务的详细错误日志

---

## 📊 监控指标

### 关键日志关键词

**Product 服务**:
- "触发佣金计算"
- "佣金计算成功"
- "佣金计算失败"
- "取消佣金"

**Distribution 服务**:
- "开始计算佣金"
- "佣金计算完成"
- "取消订单佣金"

### 数据库监控

```sql
-- 今日订单统计
SELECT 
    COUNT(*) as total_orders,
    SUM(CASE WHEN distributor_id IS NOT NULL THEN 1 ELSE 0 END) as promotion_orders,
    SUM(pay_amount) as total_amount
FROM shop_order
WHERE DATE(create_time) = CURDATE();

-- 今日佣金统计
SELECT 
    COUNT(*) as total_commissions,
    SUM(commission_amount) as total_commission_amount,
    COUNT(DISTINCT distributor_id) as active_distributors
FROM order_commission
WHERE DATE(create_time) = CURDATE();

-- 分销商余额统计
SELECT 
    COUNT(*) as total_distributors,
    SUM(total_commission) as total_commission,
    SUM(available_balance) as total_available,
    SUM(frozen_balance) as total_frozen
FROM distributor
WHERE status = 1;
```

---

## ✅ 验收标准

### 功能验收
- [ ] 普通订单流程正常
- [ ] 推广订单正确记录 distributorId
- [ ] 订单支付后触发佣金计算
- [ ] 佣金记录正确生成
- [ ] 分销商余额正确更新
- [ ] 取消订单时正确取消佣金
- [ ] 库存管理正常
- [ ] 异常情况不影响订单流程

### 性能验收
- [ ] 订单支付响应时间 < 2秒
- [ ] 佣金计算不阻塞订单流程
- [ ] 并发订单处理正常

### 日志验收
- [ ] 关键操作有日志记录
- [ ] 错误日志包含详细信息
- [ ] 日志级别设置合理

---

## 🚀 优化建议

### 1. 使用消息队列（推荐）

**优点**:
- 解耦服务
- 异步处理
- 支持重试
- 提高性能

**实现**:
```java
// 发送消息
rabbitTemplate.convertAndSend("order.exchange", "order.paid", orderPaidEvent);

// 监听消息
@RabbitListener(queues = "commission.calculate.queue")
public void handleOrderPaid(OrderPaidEvent event) {
    commissionService.calculateCommission(...);
}
```

### 2. 添加重试机制

```java
@Retryable(
    value = {Exception.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000)
)
public void calculateCommission(ShopOrder order) {
    // 佣金计算逻辑
}
```

### 3. 添加熔断降级

```java
@FeignClient(
    value = ServiceNameConstants.DISTRIBUTION_SERVICE,
    fallback = RemoteCommissionServiceFallback.class
)
public interface RemoteCommissionService {
    // ...
}
```

---

## 📚 相关文档

- [佣金集成指南](./COMMISSION_INTEGRATION_GUIDE.md)
- [前端对接完成报告](./FRONTEND_INTEGRATION_COMPLETE.md)
- [快速启动指南](./QUICK_START.md)
- [项目总体状态](../../steering/distribution-current-status.md)

---

**文档版本**: v1.0  
**最后更新**: 2025-12-10 20:45
