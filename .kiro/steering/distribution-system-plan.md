---
inclusion: manual
---

# 分销系统设计方案

## 一、系统概述

基于 PIG 微服务平台开发的多级分销系统，支持逐级分享、利润分配、提成结算等核心功能。

### 核心特性

- 多级分销体系（支持 N 级分销）
- 灵活的佣金配置
- 实时订单追踪
- 自动化结算
- 分销商等级管理
- 数据统计分析

## 二、业务模型设计

### 2.1 分销层级关系

```
平台
  └── 一级分销商（直接推广）
        └── 二级分销商（间接推广）
              └── 三级分销商（...）
                    └── 最终消费者
```

### 2.2 佣金分配规则

**示例：商品售价 1000 元**

| 角色 | 佣金比例 | 佣金金额 | 说明 |
|------|---------|---------|------|
| 一级分销商 | 10% | 100 元 | 直接推荐人 |
| 二级分销商 | 5% | 50 元 | 间接推荐人 |
| 三级分销商 | 2% | 20 元 | 二级推荐人 |
| 平台 | 83% | 830 元 | 剩余利润 |

### 2.3 分销商等级体系

| 等级 | 名称 | 条件 | 佣金加成 |
|------|------|------|---------|
| V1 | 普通会员 | 注册即可 | 0% |
| V2 | 铜牌分销商 | 累计销售 1万 | +5% |
| V3 | 银牌分销商 | 累计销售 5万 | +10% |
| V4 | 金牌分销商 | 累计销售 10万 | +15% |
| V5 | 钻石分销商 | 累计销售 50万 | +20% |

## 三、数据库设计

### 3.1 核心表结构

#### 分销商表 (dist_distributor)

```sql
CREATE TABLE `dist_distributor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '上级分销商ID',
  `level` INT DEFAULT 1 COMMENT '分销商等级',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `total_sales` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计销售额',
  `total_commission` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计佣金',
  `available_commission` DECIMAL(10,2) DEFAULT 0.00 COMMENT '可提现佣金',
  `frozen_commission` DECIMAL(10,2) DEFAULT 0.00 COMMENT '冻结佣金',
  `direct_count` INT DEFAULT 0 COMMENT '直推人数',
  `team_count` INT DEFAULT 0 COMMENT '团队人数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销商表';
```

#### 分销关系表 (dist_relation)

```sql
CREATE TABLE `dist_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `ancestor_id` BIGINT NOT NULL COMMENT '上级分销商ID',
  `level` INT NOT NULL COMMENT '层级：1-直接上级 2-二级上级',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_ancestor_id` (`ancestor_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销关系表';
```

#### 佣金配置表 (dist_commission_config)

```sql
CREATE TABLE `dist_commission_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(100) NOT NULL COMMENT '配置名称',
  `level` INT NOT NULL COMMENT '分销层级',
  `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '佣金比例(%)',
  `distributor_level` INT DEFAULT 0 COMMENT '分销商等级：0-全部',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金配置表';
```

#### 订单佣金表 (dist_order_commission)

```sql
CREATE TABLE `dist_order_commission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `buyer_id` BIGINT NOT NULL COMMENT '购买者ID',
  `level` INT NOT NULL COMMENT '分销层级',
  `order_amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '佣金比例',
  `commission_amount` DECIMAL(10,2) NOT NULL COMMENT '佣金金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待结算 1-已结算 2-已取消',
  `settle_time` DATETIME DEFAULT NULL COMMENT '结算时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单佣金表';
```

#### 提现记录表 (dist_withdraw)

```sql
CREATE TABLE `dist_withdraw` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `withdraw_no` VARCHAR(50) NOT NULL COMMENT '提现单号',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
  `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '手续费',
  `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际到账金额',
  `account_type` TINYINT NOT NULL COMMENT '账户类型：1-支付宝 2-微信 3-银行卡',
  `account_no` VARCHAR(100) NOT NULL COMMENT '账户号',
  `account_name` VARCHAR(50) NOT NULL COMMENT '账户名',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-审核通过 2-已打款 3-已拒绝',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `pay_time` DATETIME DEFAULT NULL COMMENT '打款时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_withdraw_no` (`withdraw_no`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现记录表';
```

## 四、微服务模块设计

### 4.1 模块划分

```
pig-distribution/
├── pig-distribution-api      # API 模块
│   ├── dto/                  # 数据传输对象
│   ├── vo/                   # 视图对象
│   └── feign/                # Feign 接口
└── pig-distribution-biz      # 业务模块
    ├── controller/           # 控制器
    ├── service/              # 服务层
    ├── mapper/               # 数据访问层
    └── entity/               # 实体类
```

### 4.2 核心接口设计

#### 分销商管理 (DistributorController)

```java
@RestController
@RequestMapping("/distributor")
@Tag(name = "分销商管理")
public class DistributorController {
    
    // 申请成为分销商
    @PostMapping("/apply")
    @Operation(summary = "申请成为分销商")
    R<Boolean> apply(@RequestBody DistributorApplyDTO dto);
    
    // 获取分销商信息
    @GetMapping("/info")
    @Operation(summary = "获取分销商信息")
    R<DistributorVO> getInfo();
    
    // 获取下级分销商列表
    @GetMapping("/children")
    @Operation(summary = "获取下级分销商列表")
    R<IPage<DistributorVO>> getChildren(@RequestParam Integer level);
    
    // 获取团队统计
    @GetMapping("/team/stats")
    @Operation(summary = "获取团队统计")
    R<TeamStatsVO> getTeamStats();
}
```

#### 佣金管理 (CommissionController)

```java
@RestController
@RequestMapping("/commission")
@Tag(name = "佣金管理")
public class CommissionController {
    
    // 获取佣金列表
    @GetMapping("/list")
    @Operation(summary = "获取佣金列表")
    R<IPage<CommissionVO>> getCommissionList(@RequestParam Integer status);
    
    // 获取佣金统计
    @GetMapping("/stats")
    @Operation(summary = "获取佣金统计")
    R<CommissionStatsVO> getCommissionStats();
    
    // 佣金明细
    @GetMapping("/detail/{id}")
    @Operation(summary = "佣金明细")
    R<CommissionDetailVO> getCommissionDetail(@PathVariable Long id);
}
```

#### 提现管理 (WithdrawController)

```java
@RestController
@RequestMapping("/withdraw")
@Tag(name = "提现管理")
public class WithdrawController {
    
    // 申请提现
    @PostMapping("/apply")
    @Operation(summary = "申请提现")
    R<Boolean> applyWithdraw(@RequestBody WithdrawApplyDTO dto);
    
    // 提现记录
    @GetMapping("/list")
    @Operation(summary = "提现记录")
    R<IPage<WithdrawVO>> getWithdrawList();
    
    // 提现详情
    @GetMapping("/detail/{id}")
    @Operation(summary = "提现详情")
    R<WithdrawDetailVO> getWithdrawDetail(@PathVariable Long id);
}
```

#### 后台管理 (AdminDistributionController)

```java
@RestController
@RequestMapping("/admin/distribution")
@Tag(name = "分销后台管理")
public class AdminDistributionController {
    
    // 分销商列表
    @GetMapping("/distributor/list")
    @Operation(summary = "分销商列表")
    @PreAuthorize("@pms.hasPermission('distribution:distributor:list')")
    R<IPage<DistributorVO>> getDistributorList(@RequestParam Map<String, Object> params);
    
    // 审核分销商
    @PutMapping("/distributor/audit/{id}")
    @Operation(summary = "审核分销商")
    @PreAuthorize("@pms.hasPermission('distribution:distributor:audit')")
    R<Boolean> auditDistributor(@PathVariable Long id, @RequestParam Integer status);
    
    // 佣金配置
    @PostMapping("/commission/config")
    @Operation(summary = "佣金配置")
    @PreAuthorize("@pms.hasPermission('distribution:commission:config')")
    R<Boolean> configCommission(@RequestBody CommissionConfigDTO dto);
    
    // 提现审核
    @PutMapping("/withdraw/audit/{id}")
    @Operation(summary = "提现审核")
    @PreAuthorize("@pms.hasPermission('distribution:withdraw:audit')")
    R<Boolean> auditWithdraw(@PathVariable Long id, @RequestBody WithdrawAuditDTO dto);
}
```

## 五、核心业务逻辑

### 5.1 分销关系建立

```java
@Service
public class DistributorServiceImpl implements DistributorService {
    
    /**
     * 建立分销关系
     */
    @Transactional(rollbackFor = Exception.class)
    public void buildRelation(Long userId, Long parentId) {
        // 1. 创建分销商记录
        Distributor distributor = new Distributor();
        distributor.setUserId(userId);
        distributor.setParentId(parentId);
        distributor.setLevel(1);
        save(distributor);
        
        // 2. 建立多级关系
        if (parentId != null) {
            // 查询上级的所有上级关系
            List<DistRelation> parentRelations = relationMapper.selectList(
                new LambdaQueryWrapper<DistRelation>()
                    .eq(DistRelation::getDistributorId, parentId)
            );
            
            // 建立直接关系
            DistRelation directRelation = new DistRelation();
            directRelation.setDistributorId(distributor.getId());
            directRelation.setAncestorId(parentId);
            directRelation.setLevel(1);
            relationMapper.insert(directRelation);
            
            // 建立间接关系
            for (DistRelation parentRelation : parentRelations) {
                DistRelation indirectRelation = new DistRelation();
                indirectRelation.setDistributorId(distributor.getId());
                indirectRelation.setAncestorId(parentRelation.getAncestorId());
                indirectRelation.setLevel(parentRelation.getLevel() + 1);
                relationMapper.insert(indirectRelation);
            }
            
            // 更新上级的直推人数和团队人数
            updateParentCount(parentId);
        }
    }
}
```

### 5.2 订单佣金计算

```java
@Service
public class CommissionServiceImpl implements CommissionService {
    
    /**
     * 计算订单佣金
     */
    @Transactional(rollbackFor = Exception.class)
    public void calculateCommission(Order order) {
        // 1. 获取购买者的推荐人
        Distributor buyer = distributorMapper.selectOne(
            new LambdaQueryWrapper<Distributor>()
                .eq(Distributor::getUserId, order.getUserId())
        );
        
        if (buyer == null || buyer.getParentId() == null) {
            return; // 无推荐人，不计算佣金
        }
        
        // 2. 获取所有上级关系
        List<DistRelation> relations = relationMapper.selectList(
            new LambdaQueryWrapper<DistRelation>()
                .eq(DistRelation::getDistributorId, buyer.getId())
                .orderByAsc(DistRelation::getLevel)
        );
        
        // 3. 计算每一级佣金
        for (DistRelation relation : relations) {
            // 获取佣金配置
            CommissionConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<CommissionConfig>()
                    .eq(CommissionConfig::getLevel, relation.getLevel())
                    .eq(CommissionConfig::getStatus, 1)
            );
            
            if (config == null) {
                continue;
            }
            
            // 获取分销商信息
            Distributor distributor = distributorMapper.selectById(relation.getAncestorId());
            
            // 计算佣金（考虑分销商等级加成）
            BigDecimal rate = config.getCommissionRate();
            BigDecimal levelBonus = getLevelBonus(distributor.getLevel());
            BigDecimal finalRate = rate.add(levelBonus);
            BigDecimal commission = order.getAmount()
                .multiply(finalRate)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            
            // 创建佣金记录
            OrderCommission orderCommission = new OrderCommission();
            orderCommission.setOrderId(order.getId());
            orderCommission.setOrderNo(order.getOrderNo());
            orderCommission.setDistributorId(distributor.getId());
            orderCommission.setBuyerId(order.getUserId());
            orderCommission.setLevel(relation.getLevel());
            orderCommission.setOrderAmount(order.getAmount());
            orderCommission.setCommissionRate(finalRate);
            orderCommission.setCommissionAmount(commission);
            orderCommission.setStatus(0); // 待结算
            commissionMapper.insert(orderCommission);
            
            // 更新分销商冻结佣金
            distributor.setFrozenCommission(
                distributor.getFrozenCommission().add(commission)
            );
            distributorMapper.updateById(distributor);
        }
    }
    
    /**
     * 获取等级加成
     */
    private BigDecimal getLevelBonus(Integer level) {
        switch (level) {
            case 2: return new BigDecimal("5");
            case 3: return new BigDecimal("10");
            case 4: return new BigDecimal("15");
            case 5: return new BigDecimal("20");
            default: return BigDecimal.ZERO;
        }
    }
}
```

### 5.3 佣金结算

```java
@Service
public class SettlementServiceImpl implements SettlementService {
    
    /**
     * 订单确认收货后结算佣金
     */
    @Transactional(rollbackFor = Exception.class)
    public void settleCommission(Long orderId) {
        // 1. 查询订单的所有佣金记录
        List<OrderCommission> commissions = commissionMapper.selectList(
            new LambdaQueryWrapper<OrderCommission>()
                .eq(OrderCommission::getOrderId, orderId)
                .eq(OrderCommission::getStatus, 0)
        );
        
        // 2. 结算每条佣金
        for (OrderCommission commission : commissions) {
            // 更新佣金状态
            commission.setStatus(1); // 已结算
            commission.setSettleTime(LocalDateTime.now());
            commissionMapper.updateById(commission);
            
            // 更新分销商佣金
            Distributor distributor = distributorMapper.selectById(
                commission.getDistributorId()
            );
            
            // 冻结佣金转为可提现佣金
            distributor.setFrozenCommission(
                distributor.getFrozenCommission().subtract(commission.getCommissionAmount())
            );
            distributor.setAvailableCommission(
                distributor.getAvailableCommission().add(commission.getCommissionAmount())
            );
            distributor.setTotalCommission(
                distributor.getTotalCommission().add(commission.getCommissionAmount())
            );
            distributor.setTotalSales(
                distributor.getTotalSales().add(commission.getOrderAmount())
            );
            
            // 检查是否升级
            checkAndUpgradeLevel(distributor);
            
            distributorMapper.updateById(distributor);
        }
    }
    
    /**
     * 检查并升级分销商等级
     */
    private void checkAndUpgradeLevel(Distributor distributor) {
        BigDecimal totalSales = distributor.getTotalSales();
        int currentLevel = distributor.getLevel();
        int newLevel = currentLevel;
        
        if (totalSales.compareTo(new BigDecimal("500000")) >= 0) {
            newLevel = 5; // 钻石
        } else if (totalSales.compareTo(new BigDecimal("100000")) >= 0) {
            newLevel = 4; // 金牌
        } else if (totalSales.compareTo(new BigDecimal("50000")) >= 0) {
            newLevel = 3; // 银牌
        } else if (totalSales.compareTo(new BigDecimal("10000")) >= 0) {
            newLevel = 2; // 铜牌
        }
        
        if (newLevel > currentLevel) {
            distributor.setLevel(newLevel);
            // 发送升级通知
            sendUpgradeNotification(distributor);
        }
    }
}
```

## 六、前端页面设计

### 6.1 页面结构

```
pig-ui/src/views/distribution/
├── index.vue                 # 分销中心首页
├── team/
│   ├── index.vue            # 我的团队
│   └── detail.vue           # 团队成员详情
├── commission/
│   ├── index.vue            # 佣金列表
│   └── detail.vue           # 佣金明细
├── withdraw/
│   ├── index.vue            # 提现记录
│   └── apply.vue            # 申请提现
└── admin/
    ├── distributor.vue      # 分销商管理
    ├── commission.vue       # 佣金管理
    ├── withdraw.vue         # 提现审核
    └── config.vue           # 系统配置
```

### 6.2 核心页面功能

#### 分销中心首页

- 分销商信息卡片（等级、团队人数、累计收益）
- 今日数据统计（订单数、销售额、佣金）
- 推广二维码/链接
- 快捷入口（我的团队、佣金明细、申请提现）

#### 我的团队

- 团队结构树形展示
- 一级/二级/三级分销商列表
- 成员销售数据统计
- 搜索和筛选功能

#### 佣金列表

- 佣金记录列表（订单号、金额、状态、时间）
- 佣金统计（总佣金、可提现、已提现、冻结中）
- 按状态筛选
- 佣金明细查看

#### 提现管理

- 提现申请表单
- 提现记录列表
- 提现状态跟踪
- 账户管理

## 七、技术实现要点

### 7.1 性能优化

1. **关系查询优化**
   - 使用闭包表存储分销关系
   - 建立合适的索引
   - 使用 Redis 缓存热点数据

2. **佣金计算优化**
   - 异步计算佣金（使用消息队列）
   - 批量处理订单佣金
   - 定时任务结算

3. **数据统计优化**
   - 使用 Redis 存储实时统计数据
   - 定时同步到数据库
   - 使用 ElasticSearch 做数据分析

### 7.2 安全控制

1. **权限控制**
   - 分销商只能查看自己的数据
   - 后台管理需要特定权限
   - 敏感操作需要二次验证

2. **防刷控制**
   - 限制提现频率
   - 订单防刷检测
   - 异常行为监控

3. **数据安全**
   - 敏感信息加密存储
   - 操作日志记录
   - 定期数据备份

### 7.3 消息通知

1. **站内消息**
   - 佣金到账通知
   - 提现审核通知
   - 等级升级通知

2. **推送通知**
   - 微信模板消息
   - 短信通知
   - 邮件通知

## 八、开发计划

### 第一阶段：基础功能（2周）

- [ ] 数据库表设计和创建
- [ ] 分销商管理模块
- [ ] 分销关系建立
- [ ] 基础 API 接口

### 第二阶段：核心功能（3周）

- [ ] 佣金计算逻辑
- [ ] 订单佣金记录
- [ ] 佣金结算功能
- [ ] 提现申请和审核

### 第三阶段：前端开发（2周）

- [ ] 分销中心首页
- [ ] 团队管理页面
- [ ] 佣金管理页面
- [ ] 提现管理页面

### 第四阶段：后台管理（1周）

- [ ] 分销商管理
- [ ] 佣金配置
- [ ] 提现审核
- [ ] 数据统计

### 第五阶段：优化和测试（1周）

- [ ] 性能优化
- [ ] 安全测试
- [ ] 功能测试
- [ ] 上线部署

## 九、注意事项

### 9.1 法律合规

- 确保分销层级符合当地法律法规（建议不超过3级）
- 明确分销协议和用户协议
- 做好资金监管和税务处理

### 9.2 业务风险

- 防止恶意刷单
- 防止虚假交易
- 设置提现门槛和审核机制
- 建立风控预警系统

### 9.3 用户体验

- 简化分销商申请流程
- 清晰展示佣金规则
- 及时的消息通知
- 便捷的提现操作

## 十、扩展功能

### 10.1 营销工具

- 分销海报生成
- 优惠券分发
- 拼团活动
- 限时促销

### 10.2 数据分析

- 销售数据看板
- 团队业绩排行
- 商品销售分析
- 用户行为分析

### 10.3 社交功能

- 分销商社区
- 经验分享
- 培训课程
- 等级勋章

---

**文档版本**: v1.0  
**创建时间**: 2025-12-07  
**维护人**: 开发团队
