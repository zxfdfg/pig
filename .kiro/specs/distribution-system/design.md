# 分销系统设计文档

## 1. 系统架构

### 1.1 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                     前端层 (Vue 3)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │分销中心  │  │分销商管理│  │佣金管理  │  │提现管理  │ │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘ │
└─────────────────────────────────────────────────────────┘
                          ↓ HTTP/REST
┌─────────────────────────────────────────────────────────┐
│                   网关层 (Gateway)                        │
│              路由、鉴权、限流、负载均衡                    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              分销服务 (Distribution Service)              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │Controller│  │ Service  │  │  Mapper  │  │  Entity  │ │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘ │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   数据层 (MySQL)                          │
│  分销商表、关系表、佣金表、提现表、配置表、流水表          │
└─────────────────────────────────────────────────────────┘
```

### 1.2 技术选型

| 层次 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 前端框架 | Vue | 3.5.13 | 渐进式框架 |
| UI 组件 | Element Plus | 2.8.7 | 组件库 |
| 状态管理 | Pinia | 2.3.0 | 状态管理 |
| 后端框架 | Spring Boot | 3.5.8 | 微服务框架 |
| 微服务 | Spring Cloud | 2025.0.0 | 微服务套件 |
| ORM | MyBatis Plus | 3.5.15 | 持久层框架 |
| 数据库 | MySQL | 8.0+ | 关系数据库 |
| 缓存 | Redis | 7.0+ | 缓存数据库 |

---

## 2. 数据库设计

### 2.1 分销商表 (dist_distributor)

```sql
CREATE TABLE `dist_distributor` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `parent_id` BIGINT COMMENT '上级分销商ID',
  `level` INT DEFAULT 1 COMMENT '等级：1-普通 2-铜牌 3-银牌 4-金牌 5-钻石',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用 2-待审核',
  `total_sales` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计销售额',
  `total_commission` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计佣金',
  `available_balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '可用余额',
  `frozen_balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '冻结余额',
  `direct_count` INT DEFAULT 0 COMMENT '直接下级数量',
  `team_count` INT DEFAULT 0 COMMENT '团队总人数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` TINYINT DEFAULT 0,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_status` (`status`)
);
```

### 2.2 分销关系表 (dist_relation) - 闭包表

```sql
CREATE TABLE `dist_relation` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `ancestor_id` BIGINT NOT NULL COMMENT '上级分销商ID',
  `level` INT NOT NULL COMMENT '层级：1-直接上级 2-二级上级 3-三级上级',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_distributor_id` (`distributor_id`),
  INDEX `idx_ancestor_id` (`ancestor_id`),
  INDEX `idx_level` (`level`)
);
```

### 2.3 佣金配置表 (dist_commission_config)

```sql
CREATE TABLE `dist_commission_config` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '配置名称',
  `level` INT NOT NULL COMMENT '分销层级：1-一级 2-二级 3-三级',
  `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '佣金比例(%)',
  `distributor_level` INT DEFAULT 0 COMMENT '分销商等级：0-全部',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME ON UPDATE CURRENT_TIMESTAMP
);
```

### 2.4 订单佣金表 (dist_order_commission)

```sql
CREATE TABLE `dist_order_commission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `buyer_id` BIGINT NOT NULL COMMENT '购买者ID',
  `level` INT NOT NULL COMMENT '分销层级',
  `order_amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '佣金比例',
  `commission_amount` DECIMAL(10,2) NOT NULL COMMENT '佣金金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待结算 1-已结算 2-已取消',
  `settle_time` DATETIME COMMENT '结算时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_distributor_id` (`distributor_id`),
  INDEX `idx_status` (`status`)
);
```

### 2.5 提现表 (dist_withdraw)

```sql
CREATE TABLE `dist_withdraw` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `withdraw_no` VARCHAR(50) NOT NULL COMMENT '提现单号',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
  `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '手续费',
  `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际到账',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-审核通过 2-审核拒绝 3-已打款',
  `bank_name` VARCHAR(100) COMMENT '银行名称',
  `bank_account` VARCHAR(50) COMMENT '银行账号',
  `account_name` VARCHAR(50) COMMENT '账户名',
  `audit_user` VARCHAR(50) COMMENT '审核人',
  `audit_time` DATETIME COMMENT '审核时间',
  `audit_remark` VARCHAR(500) COMMENT '审核备注',
  `pay_time` DATETIME COMMENT '打款时间',
  `pay_no` VARCHAR(100) COMMENT '支付流水号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_distributor_id` (`distributor_id`),
  INDEX `idx_status` (`status`)
);
```

### 2.6 佣金流水表 (dist_commission_log)

```sql
CREATE TABLE `dist_commission_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `type` TINYINT NOT NULL COMMENT '类型：1-佣金收入 2-提现支出 3-冻结 4-解冻 5-取消',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `before_balance` DECIMAL(10,2) NOT NULL COMMENT '变更前余额',
  `after_balance` DECIMAL(10,2) NOT NULL COMMENT '变更后余额',
  `order_no` VARCHAR(50) COMMENT '关联单号',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_distributor_id` (`distributor_id`),
  INDEX `idx_type` (`type`)
);
```

---

## 3. 核心业务流程

### 3.1 分销商申请流程

```
用户申请 → 创建待审核记录 → 管理员审核 → 审核通过 → 建立分销关系 → 完成
                                    ↓
                                审核拒绝 → 更新状态 → 结束
```

### 3.2 佣金计算流程

```
订单完成 → 查询买家推荐链 → 遍历上级（最多3级）→ 查询佣金配置 
    ↓
计算佣金金额 → 创建佣金记录 → 更新账户余额 → 创建流水记录 → 完成
```

### 3.3 提现流程

```
申请提现 → 验证余额 → 创建提现记录 → 管理员审核 → 审核通过 → 冻结余额
    ↓                                        ↓
验证失败 → 返回错误                      审核拒绝 → 更新状态
                                            ↓
                                    财务打款 → 扣除余额 → 创建流水 → 完成
```

---

## 4. 接口设计

### 4.1 分销商管理接口

#### 4.1.1 申请成为分销商
```
POST /distribution/distributor/apply
Request: { parentId: 123 }
Response: { code: 0, msg: "申请成功", data: { id: 456 } }
```

#### 4.1.2 分销商列表
```
GET /distribution/distributor/page?current=1&size=10&status=1
Response: { code: 0, data: { records: [...], total: 100 } }
```

#### 4.1.3 分销商详情
```
GET /distribution/distributor/{id}
Response: { code: 0, data: { id, userId, level, ... } }
```

#### 4.1.4 审核分销商
```
PUT /distribution/distributor/audit/{id}
Request: { status: 1, remark: "审核通过" }
Response: { code: 0, msg: "审核成功" }
```

### 4.2 佣金管理接口

#### 4.2.1 佣金列表
```
GET /distribution/commission/page?current=1&size=10
Response: { code: 0, data: { records: [...], total: 100 } }
```

#### 4.2.2 佣金统计
```
GET /distribution/commission/stats
Response: { 
  code: 0, 
  data: { 
    totalCommission: 10000, 
    todayCommission: 100,
    monthCommission: 1000 
  } 
}
```

#### 4.2.3 计算订单佣金
```
POST /distribution/commission/calculate
Request: { orderId: 123, orderAmount: 1000 }
Response: { code: 0, msg: "计算成功" }
```

### 4.3 提现管理接口

#### 4.3.1 申请提现
```
POST /distribution/withdraw/apply
Request: { 
  amount: 100, 
  bankName: "工商银行",
  bankAccount: "6222...",
  accountName: "张三"
}
Response: { code: 0, msg: "申请成功", data: { withdrawNo: "W..." } }
```

#### 4.3.2 提现列表
```
GET /distribution/withdraw/page?current=1&size=10&status=0
Response: { code: 0, data: { records: [...], total: 50 } }
```

#### 4.3.3 审核提现
```
PUT /distribution/withdraw/audit/{id}
Request: { status: 1, remark: "审核通过" }
Response: { code: 0, msg: "审核成功" }
```

#### 4.3.4 确认打款
```
PUT /distribution/withdraw/pay/{id}
Request: { payNo: "P123456" }
Response: { code: 0, msg: "打款成功" }
```

### 4.4 配置管理接口

#### 4.4.1 配置列表
```
GET /distribution/config/list
Response: { code: 0, data: [...] }
```

#### 4.4.2 新增配置
```
POST /distribution/config
Request: { name: "一级分销", level: 1, commissionRate: 10.00 }
Response: { code: 0, msg: "新增成功" }
```

#### 4.4.3 修改配置
```
PUT /distribution/config
Request: { id: 1, commissionRate: 12.00 }
Response: { code: 0, msg: "修改成功" }
```

---

## 5. 核心算法

### 5.1 佣金计算算法

```java
public void calculateCommission(Long orderId, BigDecimal orderAmount) {
    // 1. 查询订单买家
    Long buyerId = orderService.getBuyerId(orderId);
    
    // 2. 查询买家的推荐链（最多3级）
    List<DistRelation> relations = relationMapper.selectAncestors(buyerId, 3);
    
    // 3. 遍历每一级，计算佣金
    for (DistRelation relation : relations) {
        Long distributorId = relation.getAncestorId();
        Integer level = relation.getLevel();
        
        // 4. 查询分销商信息
        DistDistributor distributor = distributorMapper.selectById(distributorId);
        if (distributor.getStatus() != 1) continue; // 跳过禁用的分销商
        
        // 5. 查询佣金配置
        DistCommissionConfig config = configMapper.selectByLevelAndDistributorLevel(
            level, distributor.getLevel()
        );
        if (config == null || config.getStatus() != 1) continue;
        
        // 6. 计算佣金金额
        BigDecimal commissionAmount = orderAmount
            .multiply(config.getCommissionRate())
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // 7. 创建佣金记录
        DistOrderCommission commission = new DistOrderCommission();
        commission.setOrderId(orderId);
        commission.setDistributorId(distributorId);
        commission.setLevel(level);
        commission.setCommissionAmount(commissionAmount);
        commissionMapper.insert(commission);
        
        // 8. 更新分销商余额
        distributorMapper.updateBalance(distributorId, commissionAmount);
        
        // 9. 创建流水记录
        createCommissionLog(distributorId, commissionAmount, orderId);
    }
}
```

### 5.2 分销关系建立算法（闭包表）

```java
public void buildRelation(Long distributorId, Long parentId) {
    // 1. 创建到自己的关系（层级0）
    insertRelation(distributorId, distributorId, 0);
    
    // 2. 如果有上级，创建到上级的关系
    if (parentId != null) {
        // 查询上级的所有祖先
        List<DistRelation> parentRelations = relationMapper
            .selectAncestors(parentId, 3);
        
        // 创建到每个祖先的关系
        for (DistRelation parentRelation : parentRelations) {
            int newLevel = parentRelation.getLevel() + 1;
            if (newLevel <= 3) { // 最多3级
                insertRelation(
                    distributorId, 
                    parentRelation.getAncestorId(), 
                    newLevel
                );
            }
        }
    }
}
```

---

## 6. 安全设计

### 6.1 权限控制

```java
// Controller 层添加权限注解
@PreAuthorize("hasAuthority('dist_distributor_view')")
public R<IPage<DistDistributor>> page(Page page, DistDistributor distributor) {
    // ...
}

@PreAuthorize("hasAuthority('dist_distributor_audit')")
public R<Boolean> audit(@PathVariable Long id, @RequestBody AuditDTO dto) {
    // ...
}
```

### 6.2 数据隔离

```java
// 分销商只能查询自己的数据
public R<IPage<DistOrderCommission>> myCommissions(Page page) {
    Long distributorId = SecurityUtils.getCurrentDistributorId();
    return R.ok(commissionService.page(page, 
        Wrappers.<DistOrderCommission>lambdaQuery()
            .eq(DistOrderCommission::getDistributorId, distributorId)
    ));
}
```

### 6.3 敏感信息加密

```java
// 银行账号加密存储
@TableField(typeHandler = EncryptTypeHandler.class)
private String bankAccount;
```

---

## 7. 性能优化

### 7.1 缓存策略

```java
// 佣金配置缓存（1小时）
@Cacheable(value = "commission:config", key = "#level + ':' + #distributorLevel")
public DistCommissionConfig getConfig(Integer level, Integer distributorLevel) {
    // ...
}

// 分销商信息缓存（30分钟）
@Cacheable(value = "distributor", key = "#id")
public DistDistributor getById(Long id) {
    // ...
}
```

### 7.2 异步处理

```java
// 佣金计算异步处理
@Async
public void calculateCommissionAsync(Long orderId, BigDecimal orderAmount) {
    calculateCommission(orderId, orderAmount);
}
```

### 7.3 批量操作

```java
// 批量更新余额
public void batchUpdateBalance(List<BalanceUpdate> updates) {
    distributorMapper.batchUpdate(updates);
}
```

---

## 8. 监控与日志

### 8.1 关键操作日志

```java
@Slf4j
public class CommissionService {
    public void calculateCommission(Long orderId, BigDecimal orderAmount) {
        log.info("开始计算佣金, orderId={}, amount={}", orderId, orderAmount);
        try {
            // 业务逻辑
            log.info("佣金计算成功, orderId={}", orderId);
        } catch (Exception e) {
            log.error("佣金计算失败, orderId={}", orderId, e);
            throw e;
        }
    }
}
```

### 8.2 性能监控

```java
@Around("execution(* com.pig4cloud.pig.distribution.service.*.*(..))")
public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = pjp.proceed();
    long cost = System.currentTimeMillis() - start;
    if (cost > 3000) {
        log.warn("方法执行超时: {}, 耗时: {}ms", pjp.getSignature(), cost);
    }
    return result;
}
```

---

## 9. 测试策略

### 9.1 单元测试

- 佣金计算逻辑测试
- 分销关系建立测试
- 余额更新测试

### 9.2 集成测试

- 完整业务流程测试
- 接口联调测试
- 异常场景测试

### 9.3 性能测试

- 并发佣金计算测试
- 大数据量查询测试
- 缓存命中率测试

---

**文档版本**: v1.0  
**创建时间**: 2025-12-07  
**最后更新**: 2025-12-07
