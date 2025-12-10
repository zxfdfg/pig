# 🔗 商城系统与分销系统佣金集成指南

**创建时间**: 2025-12-10 20:30  
**状态**: 🟡 待完成

---

## 📋 概述

本文档说明如何将商城系统的订单支付与分销系统的佣金计算进行集成。

---

## ✅ 已完成的准备工作

### 1. Feign 客户端接口 ✅

**文件**: `pig/pig-distribution/pig-distribution-api/src/main/java/com/pig4cloud/pig/distribution/api/feign/RemoteCommissionService.java`

```java
@FeignClient(contextId = "remoteCommissionService", value = ServiceNameConstants.DISTRIBUTION_SERVICE)
public interface RemoteCommissionService {
    
    @PostMapping("/commission/calculate")
    R<Boolean> calculateCommission(
        @RequestParam("orderId") Long orderId,
        @RequestParam("orderNo") String orderNo,
        @RequestParam("buyerId") Long buyerId,
        @RequestParam("orderAmount") BigDecimal orderAmount
    );
    
    @PostMapping("/commission/cancel")
    R<Boolean> cancelCommission(@RequestParam("orderId") Long orderId);
}
```

### 2. 服务名称常量 ✅

**文件**: `pig-common/pig-common-core/src/main/java/com/pig4cloud/pig/common/core/constant/ServiceNameConstants.java`

已添加：
- `DISTRIBUTION_SERVICE = "pig-distribution-biz"`
- `PRODUCT_SERVICE = "pig-product-biz"`

### 3. 订单服务预留接口 ✅

**文件**: `pig/pig-product/pig-product-biz/src/main/java/com/pig4cloud/pig/product/service/impl/ShopOrderServiceImpl.java`

在 `paySuccess` 方法中已添加佣金计算调用框架。

---

## 🔧 待完成的集成步骤

### 步骤 1: 在 Product 模块添加 Distribution API 依赖

**文件**: `pig/pig-product/pig-product-biz/pom.xml`

在 `<dependencies>` 中添加：

```xml
<!-- 分销系统 API -->
<dependency>
    <groupId>com.pig4cloud</groupId>
    <artifactId>pig-distribution-api</artifactId>
</dependency>
```

### 步骤 2: 在 ShopOrderServiceImpl 中注入 Feign 客户端

**文件**: `pig/pig-product/pig-product-biz/src/main/java/com/pig4cloud/pig/product/service/impl/ShopOrderServiceImpl.java`

在类的顶部添加：

```java
import com.pig4cloud.pig.distribution.api.feign.RemoteCommissionService;
import com.pig4cloud.pig.common.core.util.R;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopOrderServiceImpl extends ServiceImpl<ShopOrderMapper, ShopOrder> implements ShopOrderService {

    // ... 其他依赖注入
    
    private final RemoteCommissionService remoteCommissionService;
    
    // ... 其他代码
}
```

### 步骤 3: 实现 calculateCommission 方法

在 `ShopOrderServiceImpl` 中找到 `calculateCommission` 方法，替换为：

```java
/**
 * 计算订单佣金
 */
private void calculateCommission(ShopOrder order) {
    log.info("触发佣金计算：订单ID={}，订单号={}，购买者ID={}，订单金额={}", 
        order.getId(), order.getOrderNo(), order.getUserId(), order.getPayAmount());

    try {
        R<Boolean> result = remoteCommissionService.calculateCommission(
            order.getId(),
            order.getOrderNo(),
            order.getUserId(),
            order.getPayAmount()
        );

        if (result.getCode() == 0 && Boolean.TRUE.equals(result.getData())) {
            log.info("订单 {} 佣金计算成功", order.getOrderNo());
        } else {
            log.error("订单 {} 佣金计算失败: {}", order.getOrderNo(), result.getMsg());
        }
    } catch (Exception e) {
        log.error("订单 {} 佣金计算异常", order.getOrderNo(), e);
        throw e;
    }
}
```

### 步骤 4: 在取消订单时取消佣金

在 `cancelOrder` 方法中添加佣金取消逻辑：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public boolean cancelOrder(Long id, String reason) {
    Assert.notNull(id, "订单ID不能为空");

    ShopOrder order = this.getById(id);
    Assert.notNull(order, "订单不存在");
    Assert.isTrue(order.getStatus() == 0, "只能取消待支付订单");

    // 释放库存
    releaseStock(order.getOrderNo());

    // 取消佣金（如果已计算）
    if (order.getDistributorId() != null && order.getPayStatus() == 1) {
        try {
            log.info("取消订单，同时取消佣金，订单ID：{}", id);
            R<Boolean> result = remoteCommissionService.cancelCommission(id);
            if (result.getCode() != 0) {
                log.error("取消佣金失败: {}", result.getMsg());
            }
        } catch (Exception e) {
            log.error("取消佣金异常", e);
            // 不影响订单取消流程
        }
    }

    // 更新订单状态
    order.setStatus(4); // 已取消
    order.setCancelReason(reason);
    order.setCancelTime(LocalDateTime.now());

    boolean result = this.updateById(order);
    log.info("取消订单成功，订单号：{}", order.getOrderNo());
    return result;
}
```

### 步骤 5: 在 Distribution 模块添加 Controller 接口

**文件**: `pig/pig-distribution/pig-distribution-biz/src/main/java/com/pig4cloud/pig/distribution/controller/CommissionController.java`

确保已有以下接口（应该已存在）：

```java
/**
 * 计算订单佣金（供其他服务调用）
 */
@PostMapping("/calculate")
public R<Boolean> calculateCommission(
    @RequestParam("orderId") Long orderId,
    @RequestParam("orderNo") String orderNo,
    @RequestParam("buyerId") Long buyerId,
    @RequestParam("orderAmount") BigDecimal orderAmount) {
    
    Boolean result = commissionService.calculateCommission(orderId, orderNo, buyerId, orderAmount);
    return R.ok(result);
}

/**
 * 取消订单佣金（供其他服务调用）
 */
@PostMapping("/cancel")
public R<Boolean> cancelCommission(@RequestParam("orderId") Long orderId) {
    Boolean result = commissionService.cancelCommission(orderId);
    return R.ok(result);
}
```

---

## 🧪 测试步骤

### 1. 启动服务

按顺序启动：
1. Nacos (8848)
2. Gateway (9999)
3. Distribution 服务 (4200)
4. Product 服务 (4300)
5. 前端项目 (8888)

### 2. 测试场景

#### 场景 1: 普通订单（无推广链接）

1. 浏览商品列表
2. 加入购物车
3. 创建订单
4. 模拟支付
5. **预期结果**: 订单支付成功，不触发佣金计算

#### 场景 2: 推广订单（带 distributorId）

1. 访问推广链接: `/shop/product/1?distributorId=1`
2. 立即购买或加入购物车
3. 创建订单
4. 模拟支付
5. **预期结果**: 
   - 订单支付成功
   - 触发佣金计算
   - 分销商获得佣金记录

#### 场景 3: 取消已支付订单

1. 创建并支付推广订单
2. 取消订单
3. **预期结果**:
   - 订单状态变为已取消
   - 库存释放
   - 佣金记录被取消

### 3. 日志验证

查看 Product 服务日志：
```
订单支付成功，开始计算佣金，订单号：ORDER123，分销商ID：1
触发佣金计算：订单ID=1，订单号=ORDER123，购买者ID=1，订单金额=999.00
订单 ORDER123 佣金计算成功
```

查看 Distribution 服务日志：
```
开始计算佣金: orderId=1, buyerId=1, amount=999.00
查询购买者的推荐分销商
计算一级佣金: 分销商ID=1, 佣金=99.90
佣金计算完成，共生成 1 条佣金记录
```

---

## 📊 数据流程图

```
用户购买商品（带 distributorId）
    ↓
创建订单（记录 distributor_id）
    ↓
模拟支付
    ↓
ShopOrderServiceImpl.paySuccess()
    ↓
更新订单状态为已支付
    ↓
检查 distributor_id 是否存在
    ↓
调用 RemoteCommissionService.calculateCommission()
    ↓
CommissionController.calculateCommission()
    ↓
CommissionService.calculateCommission()
    ↓
查询分销关系（闭包表）
    ↓
计算各级佣金（最多3级）
    ↓
创建佣金记录
    ↓
更新分销商累计佣金
    ↓
返回成功
```

---

## ⚠️ 注意事项

### 1. 异常处理

- 佣金计算失败不应影响订单支付流程
- 使用 try-catch 捕获异常
- 记录详细的错误日志

### 2. 事务管理

- 订单支付和佣金计算应该在不同的事务中
- 避免分布式事务问题
- 考虑使用消息队列实现最终一致性

### 3. 性能优化

- 佣金计算可以异步执行
- 使用消息队列（RabbitMQ/Kafka）
- 避免阻塞订单支付流程

### 4. 幂等性

- 佣金计算接口应该支持幂等
- 使用订单号作为幂等键
- 避免重复计算佣金

---

## 🚀 优化建议

### 1. 使用消息队列（推荐）

**优点**:
- 解耦服务
- 异步处理
- 提高性能
- 支持重试

**实现**:
```java
// 在 paySuccess 方法中
if (order.getDistributorId() != null) {
    // 发送消息到 MQ
    rabbitTemplate.convertAndSend(
        "order.exchange",
        "order.paid",
        new OrderPaidEvent(order.getId(), order.getOrderNo(), order.getUserId(), order.getPayAmount())
    );
}

// 在 Distribution 服务中监听消息
@RabbitListener(queues = "commission.calculate.queue")
public void handleOrderPaid(OrderPaidEvent event) {
    commissionService.calculateCommission(
        event.getOrderId(),
        event.getOrderNo(),
        event.getBuyerId(),
        event.getOrderAmount()
    );
}
```

### 2. 使用异步线程池

```java
@Async("commissionExecutor")
public void calculateCommissionAsync(ShopOrder order) {
    calculateCommission(order);
}
```

### 3. 添加重试机制

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

---

## ✅ 验收标准

- [ ] Product 模块成功引入 Distribution API 依赖
- [ ] Feign 客户端正常注入
- [ ] 订单支付成功后触发佣金计算
- [ ] 佣金计算成功，生成佣金记录
- [ ] 分销商余额正确更新
- [ ] 取消订单时正确取消佣金
- [ ] 异常情况不影响订单流程
- [ ] 日志记录完整清晰

---

## 📚 相关文档

- [分销系统设计文档](../distribution-system/design.md)
- [商城系统设计文档](./design.md)
- [佣金计算算法](../distribution-system/design.md#佣金计算算法)
- [Feign 使用指南](https://spring.io/projects/spring-cloud-openfeign)

---

**文档版本**: v1.0  
**最后更新**: 2025-12-10 20:30
