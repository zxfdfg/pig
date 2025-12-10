# Design Document - 商品管理系统

## Overview

商品管理系统是 PIG 微服务平台的核心业务模块，负责管理平台上的所有商品信息。系统采用微服务架构，与分销系统深度集成，支持实物商品和虚拟商品（CDKEY）的全生命周期管理。

### 核心功能
- 商品信息管理（CRUD）
- 虚拟商品 CDKEY 管理
- 商品分类管理
- SKU 多规格管理
- 库存管理
- 商品上下架
- 分销佣金配置
- 商品统计分析

### 技术栈
- **后端**: Spring Boot 3.5, MyBatis Plus 3.5, MySQL 8.0
- **前端**: Vue 3.5, Element Plus 2.8, TypeScript 5.6
- **缓存**: Redis 7.0
- **文件存储**: 本地存储 / 阿里云 OSS

## Architecture

### 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                         前端层 (Vue 3)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │商品管理  │  │分类管理  │  │库存管理  │  │统计分析  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ↓ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                      网关层 (Gateway)                         │
│                    路由、鉴权、限流                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   商品服务 (Product Service)                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Controller   │  │   Service    │  │    Mapper    │     │
│  │  - Product   │  │  - Product   │  │  - Product   │     │
│  │  - Category  │  │  - Category  │  │  - Category  │     │
│  │  - SKU       │  │  - SKU       │  │  - SKU       │     │
│  │  - CDKEY     │  │  - CDKEY     │  │  - CDKEY     │     │
│  │  - Stock     │  │  - Stock     │  │  - Stock     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      数据层 (MySQL + Redis)                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  商品表      │  │  分类表      │  │  SKU表       │     │
│  │  CDKEY表     │  │  库存表      │  │  佣金配置表  │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘


### 模块划分

#### 1. 商品管理模块 (Product Module)
- 商品基本信息管理
- 商品图片管理
- 商品状态管理（草稿、上架、下架、售罄）
- 商品搜索和筛选

#### 2. 分类管理模块 (Category Module)
- 多级分类管理
- 分类树形结构
- 分类商品关联

#### 3. SKU 管理模块 (SKU Module)
- 多规格商品管理
- SKU 属性管理（颜色、尺寸等）
- SKU 独立定价和库存

#### 4. 虚拟商品模块 (Virtual Product Module)
- CDKEY 批量导入
- CDKEY 自动分配
- CDKEY 使用状态追踪
- CDKEY 分销来源记录

#### 5. 库存管理模块 (Stock Module)
- 库存增减
- 库存预警
- 订单扣库存
- 库存日志

#### 6. 分销佣金模块 (Commission Module)
- 商品佣金配置
- 分类默认佣金
- 佣金计算规则
- 佣金成本分析

#### 7. 统计分析模块 (Statistics Module)
- 商品统计
- 分类统计
- 库存统计
- 分销数据统计

## Components and Interfaces

### 核心组件

#### 1. ProductController
商品管理控制器，提供商品 CRUD 接口

**接口列表**:
- `POST /product` - 创建商品
- `PUT /product` - 更新商品
- `DELETE /product/{id}` - 删除商品
- `GET /product/{id}` - 查询商品详情
- `GET /product/page` - 分页查询商品
- `PUT /product/status/{id}` - 更新商品状态（上下架）
- `POST /product/batch/status` - 批量更新状态
- `POST /product/batch/category` - 批量修改分类
- `DELETE /product/batch` - 批量删除

#### 2. CategoryController
分类管理控制器

**接口列表**:
- `POST /category` - 创建分类
- `PUT /category` - 更新分类
- `DELETE /category/{id}` - 删除分类
- `GET /category/{id}` - 查询分类详情
- `GET /category/tree` - 查询分类树
- `GET /category/{id}/products` - 查询分类下的商品

#### 3. SKUController
SKU 管理控制器

**接口列表**:
- `POST /sku` - 创建 SKU
- `PUT /sku` - 更新 SKU
- `DELETE /sku/{id}` - 删除 SKU
- `GET /sku/{id}` - 查询 SKU 详情
- `GET /sku/product/{productId}` - 查询商品的所有 SKU

#### 4. CDKEYController
CDKEY 管理控制器

**接口列表**:
- `POST /cdkey/import` - 批量导入 CDKEY
- `GET /cdkey/page` - 分页查询 CDKEY
- `GET /cdkey/{id}` - 查询 CDKEY 详情
- `PUT /cdkey/{id}/status` - 更新 CDKEY 状态
- `GET /cdkey/product/{productId}` - 查询商品的 CDKEY 列表

#### 5. StockController
库存管理控制器

**接口列表**:
- `PUT /stock/increase` - 增加库存
- `PUT /stock/decrease` - 减少库存
- `GET /stock/{productId}` - 查询商品库存
- `GET /stock/low` - 查询低库存商品
- `GET /stock/log/{productId}` - 查询库存日志

#### 6. CommissionConfigController
佣金配置控制器

**接口列表**:
- `POST /commission/config` - 创建佣金配置
- `PUT /commission/config` - 更新佣金配置
- `GET /commission/config/{productId}` - 查询商品佣金配置
- `GET /commission/config/category/{categoryId}` - 查询分类默认佣金
- `POST /commission/config/batch` - 批量设置佣金

#### 7. StatisticsController
统计分析控制器

**接口列表**:
- `GET /statistics/overview` - 商品总览统计
- `GET /statistics/category` - 分类统计
- `GET /statistics/stock` - 库存统计
- `GET /statistics/distribution/{productId}` - 商品分销统计
- `GET /statistics/profit/{productId}` - 商品利润分析
- `POST /statistics/export` - 导出商品数据


## Data Models

### 数据库表设计

#### 1. 商品表 (product)

```sql
CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `type` TINYINT NOT NULL DEFAULT 0 COMMENT '商品类型：0-实物商品，1-虚拟商品',
  `cover_image` VARCHAR(500) COMMENT '封面图片',
  `images` TEXT COMMENT '商品图片（JSON数组）',
  `description` TEXT COMMENT '商品描述',
  `detail` LONGTEXT COMMENT '商品详情（富文本）',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  `cost_price` DECIMAL(10,2) COMMENT '成本价',
  `market_price` DECIMAL(10,2) COMMENT '市场价',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `stock_warning` INT DEFAULT 10 COMMENT '库存预警值',
  `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-草稿，1-上架，2-下架，3-售罄',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(64) COMMENT '创建人',
  `update_by` VARCHAR(64) COMMENT '更新人',
  `del_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-正常，1-删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

#### 2. 商品分类表 (product_category)

```sql
CREATE TABLE `product_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(500) COMMENT '分类图标',
  `description` VARCHAR(500) COMMENT '分类描述',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `level` TINYINT NOT NULL DEFAULT 1 COMMENT '层级：1-一级，2-二级，3-三级',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-正常，1-删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';
```

#### 3. SKU 表 (product_sku)

```sql
CREATE TABLE `product_sku` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `sku_code` VARCHAR(100) NOT NULL COMMENT 'SKU编码',
  `sku_name` VARCHAR(200) COMMENT 'SKU名称',
  `attributes` JSON COMMENT 'SKU属性（JSON）',
  `price` DECIMAL(10,2) NOT NULL COMMENT 'SKU价格',
  `cost_price` DECIMAL(10,2) COMMENT 'SKU成本价',
  `stock` INT NOT NULL DEFAULT 0 COMMENT 'SKU库存',
  `image` VARCHAR(500) COMMENT 'SKU图片',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-正常，1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';
```

#### 4. CDKEY 表 (product_cdkey)

```sql
CREATE TABLE `product_cdkey` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'CDKEY ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `cdkey` VARCHAR(200) NOT NULL COMMENT 'CDKEY码',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期',
  `distributor_id` BIGINT COMMENT '推广分销商ID',
  `order_id` BIGINT COMMENT '订单ID',
  `user_id` BIGINT COMMENT '使用用户ID',
  `used_time` DATETIME COMMENT '使用时间',
  `expire_time` DATETIME COMMENT '过期时间',
  `commission_level1` DECIMAL(5,2) COMMENT '一级佣金比例',
  `commission_level2` DECIMAL(5,2) COMMENT '二级佣金比例',
  `commission_level3` DECIMAL(5,2) COMMENT '三级佣金比例',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cdkey` (`cdkey`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_distributor_id` (`distributor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品CDKEY表';
```

#### 5. 库存日志表 (product_stock_log)

```sql
CREATE TABLE `product_stock_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `sku_id` BIGINT COMMENT 'SKU ID',
  `type` TINYINT NOT NULL COMMENT '类型：1-入库，2-出库，3-订单扣减，4-订单退回',
  `quantity` INT NOT NULL COMMENT '数量（正数为增加，负数为减少）',
  `before_stock` INT NOT NULL COMMENT '操作前库存',
  `after_stock` INT NOT NULL COMMENT '操作后库存',
  `order_id` BIGINT COMMENT '关联订单ID',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` VARCHAR(64) COMMENT '操作人',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存日志表';
```

#### 6. 商品佣金配置表 (product_commission_config)

```sql
CREATE TABLE `product_commission_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `product_id` BIGINT COMMENT '商品ID（NULL表示分类默认配置）',
  `category_id` BIGINT COMMENT '分类ID（NULL表示商品独立配置）',
  `level1_rate` DECIMAL(5,2) NOT NULL COMMENT '一级佣金比例（%）',
  `level2_rate` DECIMAL(5,2) NOT NULL COMMENT '二级佣金比例（%）',
  `level3_rate` DECIMAL(5,2) NOT NULL COMMENT '三级佣金比例（%）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(64) COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品佣金配置表';
```

#### 7. 价格变更历史表 (product_price_history)

```sql
CREATE TABLE `product_price_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '历史ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `sku_id` BIGINT COMMENT 'SKU ID',
  `old_price` DECIMAL(10,2) NOT NULL COMMENT '原价格',
  `new_price` DECIMAL(10,2) NOT NULL COMMENT '新价格',
  `change_reason` VARCHAR(500) COMMENT '变更原因',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  `create_by` VARCHAR(64) COMMENT '操作人',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格变更历史表';
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: 商品创建返回有效ID
*For any* 有效的商品信息，创建商品后应返回一个大于0的商品ID，且该商品在数据库中可查询到
**Validates: Requirements 1.1**

### Property 2: 图片格式验证
*For any* 上传的图片文件，系统应验证其格式（jpg/png/gif）和大小（<5MB），只接受有效图片
**Validates: Requirements 1.2**

### Property 3: 商品分类关联
*For any* 商品和分类，创建商品时指定分类ID后，该商品的category_id应等于指定的分类ID
**Validates: Requirements 1.3**

### Property 4: 价格正数验证
*For any* 商品价格，系统应拒绝负数和零，只接受正数价格
**Validates: Requirements 1.4**

### Property 5: 必填字段验证
*For any* 缺少必填字段的商品数据，系统应拒绝创建并返回缺失字段列表
**Validates: Requirements 1.5**

### Property 6: CDKEY批量导入解析
*For any* 包含N个有效CDKEY的导入文件，系统应成功存储N个CDKEY记录
**Validates: Requirements 2.2**

### Property 7: CDKEY格式错误检测
*For any* 包含格式错误的CDKEY导入文件，系统应拒绝导入并返回错误行号
**Validates: Requirements 2.3**

### Property 8: CDKEY自动分配唯一性
*For any* 虚拟商品订单，系统应分配一个状态为"未使用"的CDKEY，且该CDKEY不会被重复分配
**Validates: Requirements 2.4**

### Property 9: CDKEY库存为零自动售罄
*For any* 虚拟商品，当其未使用的CDKEY数量为0时，商品状态应自动更新为售罄
**Validates: Requirements 2.5**

### Property 10: 商品更新时间记录
*For any* 商品修改操作，update_time字段应更新为当前时间
**Validates: Requirements 3.1**

### Property 11: 价格变更历史记录
*For any* 已上架商品的价格修改，系统应在price_history表中创建一条记录
**Validates: Requirements 3.2**

### Property 12: 有订单商品禁止删除
*For any* 存在未完成订单的商品，删除操作应被拒绝
**Validates: Requirements 3.5**

### Property 13: 库存增加正确性
*For any* 商品和增加数量N，执行增加库存后，库存应等于原库存+N
**Validates: Requirements 4.1**

### Property 14: 库存减少验证
*For any* 商品和减少数量N，当N大于当前库存时，操作应被拒绝
**Validates: Requirements 4.2**

### Property 15: 库存预警标记
*For any* 商品，当库存小于stock_warning值时，应被标记为低库存状态
**Validates: Requirements 4.3**

### Property 16: 库存为零自动售罄
*For any* 商品，当库存变为0时，状态应自动更新为售罄
**Validates: Requirements 4.4**

### Property 17: 订单扣库存正确性
*For any* 订单，支付成功后，商品库存应减少订单数量
**Validates: Requirements 4.5**

### Property 18: 分类删除限制
*For any* 分类，当其下存在商品时，删除操作应被拒绝
**Validates: Requirements 5.3**

### Property 19: 分类查询包含子分类
*For any* 分类，查询该分类下的商品应返回该分类及其所有子分类下的商品
**Validates: Requirements 5.5**

### Property 20: 草稿商品禁止上架
*For any* 状态为草稿的商品，上架操作应被拒绝
**Validates: Requirements 6.3**

### Property 21: 零库存商品禁止上架
*For any* 库存为0的商品，上架操作应被拒绝
**Validates: Requirements 6.4**

### Property 22: 商品搜索名称匹配
*For any* 搜索关键词，返回的商品列表中所有商品名称应包含该关键词
**Validates: Requirements 7.1**

### Property 23: 分类筛选正确性
*For any* 分类ID，筛选结果中所有商品的category_id应等于该分类ID或其子分类ID
**Validates: Requirements 7.2**

### Property 24: 价格区间筛选
*For any* 价格区间[min, max]，筛选结果中所有商品价格应在该区间内
**Validates: Requirements 7.4**

### Property 25: SKU删除限制
*For any* SKU，当其存在未完成订单时，删除操作应被拒绝
**Validates: Requirements 8.5**

### Property 26: 佣金比例范围验证
*For any* 一级佣金比例，系统应验证其在0-50%范围内
**Validates: Requirements 11.2**

### Property 27: 佣金递减规则
*For any* 商品佣金配置，二级佣金应小于一级佣金，三级佣金应小于二级佣金
**Validates: Requirements 11.3, 11.4**

### Property 28: 默认佣金继承
*For any* 未设置佣金的商品，其佣金配置应等于所属分类的默认佣金配置
**Validates: Requirements 11.5**

### Property 29: 分销商商品筛选
*For any* 分销商查询商品列表，返回的商品应都是已上架且库存大于0的商品
**Validates: Requirements 14.1**

### Property 30: 推广链接包含ID
*For any* 分销商和商品，生成的推广链接应包含商品ID和分销商ID
**Validates: Requirements 14.5**

### Property 31: 订单佣金计算触发
*For any* 订单，当状态变为已完成时，系统应查询订单商品的佣金配置
**Validates: Requirements 15.1**

### Property 32: 分销关系链查询
*For any* 有推荐人的买家，系统应查询其分销关系链，最多3级
**Validates: Requirements 15.2**

### Property 33: CDKEY分销来源记录
*For any* 通过分销商推广的虚拟商品订单，分配的CDKEY应记录分销商ID
**Validates: Requirements 16.1**

### Property 34: CDKEY使用触发佣金
*For any* CDKEY，当其被使用时，应触发对应分销商的佣金结算
**Validates: Requirements 16.2**

### Property 35: CDKEY过期取消佣金
*For any* 过期的CDKEY，其待结算佣金应被自动取消
**Validates: Requirements 16.5**


## Error Handling

### 错误码设计

```java
public enum ProductErrorCode {
    // 商品相关 (10000-10099)
    PRODUCT_NOT_FOUND(10001, "商品不存在"),
    PRODUCT_NAME_REQUIRED(10002, "商品名称不能为空"),
    PRODUCT_PRICE_INVALID(10003, "商品价格必须大于0"),
    PRODUCT_HAS_ORDERS(10004, "商品存在未完成订单，无法删除"),
    PRODUCT_ALREADY_ONLINE(10005, "商品已上架"),
    PRODUCT_DRAFT_CANNOT_ONLINE(10006, "草稿状态商品无法上架"),
    PRODUCT_NO_STOCK_CANNOT_ONLINE(10007, "库存为0的商品无法上架"),
    
    // 分类相关 (10100-10199)
    CATEGORY_NOT_FOUND(10101, "分类不存在"),
    CATEGORY_HAS_PRODUCTS(10102, "分类下存在商品，无法删除"),
    CATEGORY_HAS_CHILDREN(10103, "分类下存在子分类，无法删除"),
    CATEGORY_LEVEL_EXCEED(10104, "分类层级不能超过3级"),
    
    // SKU相关 (10200-10299)
    SKU_NOT_FOUND(10201, "SKU不存在"),
    SKU_CODE_DUPLICATE(10202, "SKU编码已存在"),
    SKU_HAS_ORDERS(10203, "SKU存在未完成订单，无法删除"),
    
    // CDKEY相关 (10300-10399)
    CDKEY_NOT_FOUND(10301, "CDKEY不存在"),
    CDKEY_ALREADY_USED(10302, "CDKEY已被使用"),
    CDKEY_EXPIRED(10303, "CDKEY已过期"),
    CDKEY_FORMAT_ERROR(10304, "CDKEY格式错误"),
    CDKEY_DUPLICATE(10305, "CDKEY已存在"),
    CDKEY_NO_AVAILABLE(10306, "没有可用的CDKEY"),
    
    // 库存相关 (10400-10499)
    STOCK_NOT_ENOUGH(10401, "库存不足"),
    STOCK_QUANTITY_INVALID(10402, "库存数量必须大于0"),
    
    // 佣金相关 (10500-10599)
    COMMISSION_RATE_INVALID(10501, "佣金比例必须在0-50%之间"),
    COMMISSION_RATE_NOT_DECREASE(10502, "佣金比例必须递减"),
    COMMISSION_CONFIG_NOT_FOUND(10503, "佣金配置不存在"),
    
    // 图片相关 (10600-10699)
    IMAGE_FORMAT_INVALID(10601, "图片格式不支持，仅支持jpg/png/gif"),
    IMAGE_SIZE_EXCEED(10602, "图片大小超过限制（5MB）"),
    IMAGE_UPLOAD_FAILED(10603, "图片上传失败");
    
    private final int code;
    private final String message;
}
```

### 异常处理策略

1. **业务异常**: 使用自定义 `ProductException` 抛出业务错误
2. **参数验证**: 使用 `@Valid` 注解 + 全局异常处理
3. **数据库异常**: 捕获并转换为友好的错误信息
4. **第三方服务异常**: 使用熔断降级机制

### 事务管理

- 商品创建/更新/删除操作使用 `@Transactional`
- 库存扣减使用分布式锁防止超卖
- CDKEY 分配使用乐观锁防止重复分配
- 批量操作失败时回滚所有操作

## Testing Strategy

### 单元测试

**测试框架**: JUnit 5 + Mockito

**测试覆盖**:
- Service 层业务逻辑测试
- 边界条件测试（如库存为0、价格为负数）
- 异常情况测试（如商品不存在、权限不足）

**示例**:
```java
@Test
void testCreateProduct_WithValidData_ShouldSuccess() {
    // Given
    Product product = new Product();
    product.setName("测试商品");
    product.setPrice(new BigDecimal("99.99"));
    
    // When
    Long productId = productService.createProduct(product);
    
    // Then
    assertNotNull(productId);
    assertTrue(productId > 0);
}
```

### 属性测试 (Property-Based Testing)

**测试框架**: jqwik (Java Property-Based Testing)

**配置**: 每个属性测试运行 100 次迭代

**测试示例**:

```java
@Property
@Label("Feature: product-management, Property 1: 商品创建返回有效ID")
void productCreation_ShouldReturnValidId(@ForAll("validProduct") Product product) {
    // When
    Long productId = productService.createProduct(product);
    
    // Then
    assertThat(productId).isGreaterThan(0L);
    Product saved = productService.getById(productId);
    assertThat(saved).isNotNull();
}

@Property
@Label("Feature: product-management, Property 4: 价格正数验证")
void productPrice_ShouldBePositive(@ForAll @Negative BigDecimal price) {
    // Given
    Product product = new Product();
    product.setPrice(price);
    
    // Then
    assertThrows(ProductException.class, () -> {
        productService.createProduct(product);
    });
}

@Property
@Label("Feature: product-management, Property 13: 库存增加正确性")
void stockIncrease_ShouldBeCorrect(
    @ForAll @Positive int initialStock,
    @ForAll @Positive int increaseAmount) {
    
    // Given
    Product product = createProductWithStock(initialStock);
    
    // When
    stockService.increaseStock(product.getId(), increaseAmount);
    
    // Then
    int finalStock = stockService.getStock(product.getId());
    assertThat(finalStock).isEqualTo(initialStock + increaseAmount);
}
```

### 集成测试

**测试范围**:
- Controller 层接口测试
- 数据库操作测试
- 与分销系统的集成测试

**工具**: Spring Boot Test + TestContainers (MySQL)

### 性能测试

**测试指标**:
- 商品列表查询响应时间 < 500ms
- 商品详情查询响应时间 < 200ms
- 库存扣减操作响应时间 < 100ms
- 批量导入1000个CDKEY < 5s

## Integration with Distribution System

### 集成点

#### 1. 佣金计算触发
**时机**: 订单状态变为"已完成"时

**流程**:
```
订单服务 -> 商品服务(查询佣金配置) -> 分销服务(创建佣金记录)
```

**接口**:
```java
// 商品服务提供
CommissionConfig getCommissionConfig(Long productId);

// 调用分销服务
distributionClient.calculateCommission(orderId, commissionConfig);
```

#### 2. 分销商商品查询
**场景**: 分销商查看可推广商品

**权限**: 只能查看已上架且有库存的商品

**接口**:
```java
@GetMapping("/product/distribution/list")
Page<ProductVO> getDistributionProductList(
    @RequestParam Long distributorId,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) BigDecimal minCommission
);
```

#### 3. 推广链接生成
**场景**: 分销商生成商品推广链接

**链接格式**: `https://domain.com/product/{productId}?distributor={distributorId}`

**接口**:
```java
@PostMapping("/product/{productId}/promotion-link")
String generatePromotionLink(
    @PathVariable Long productId,
    @RequestParam Long distributorId
);
```

#### 4. CDKEY 分销追踪
**场景**: 虚拟商品通过分销商推广

**数据流**:
```
1. 买家通过推广链接访问商品
2. 下单时记录分销商ID
3. 支付成功后分配CDKEY
4. CDKEY记录分销商ID
5. CDKEY使用时触发佣金结算
```

### 数据同步

#### 商品数据同步到分销系统
**场景**: 商品上下架、价格变更时通知分销系统

**方式**: 消息队列（RabbitMQ / Kafka）

**消息格式**:
```json
{
  "eventType": "PRODUCT_STATUS_CHANGED",
  "productId": 123,
  "oldStatus": 1,
  "newStatus": 2,
  "timestamp": "2025-12-09T10:00:00"
}
```

#### 分销数据回写商品系统
**场景**: 更新商品的分销统计数据

**数据**: 分销订单数、分销销售额、推广分销商数

**频率**: 每小时同步一次

## Deployment

### 服务配置

**端口**: 4300

**数据库**: pig (与其他服务共享)

**Redis**: 用于缓存商品信息、库存信息

### 网关路由配置

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: pig-product-biz
          uri: lb://pig-product-biz
          predicates:
            - Path=/product/**
          filters:
            - name: RequestRateLimiter
              args:
                key-resolver: '#{@remoteAddrKeyResolver}'
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
```

### 缓存策略

**商品信息缓存**:
- Key: `product:{productId}`
- TTL: 1小时
- 更新策略: 商品修改时删除缓存

**分类树缓存**:
- Key: `category:tree`
- TTL: 24小时
- 更新策略: 分类修改时删除缓存

**库存缓存**:
- Key: `stock:{productId}`
- TTL: 5分钟
- 更新策略: 库存变更时更新缓存

## Security Considerations

### 权限控制

**角色定义**:
- `ROLE_ADMIN`: 管理员，可以管理所有商品
- `ROLE_DISTRIBUTOR`: 分销商，只能查看可推广商品
- `ROLE_USER`: 普通用户，只能查看已上架商品

**接口权限**:
```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/product")
public R<Long> createProduct(@RequestBody Product product) {
    // ...
}

@PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR')")
@GetMapping("/product/distribution/list")
public R<Page<ProductVO>> getDistributionProductList() {
    // ...
}
```

### 数据安全

- 敏感字段（成本价）只对管理员可见
- 商品删除使用逻辑删除
- 操作日志记录（谁在什么时间做了什么）

### 防刷机制

- 接口限流（网关层）
- 批量操作限制数量（单次最多100条）
- CDKEY导入限制文件大小（最大10MB）

## Performance Optimization

### 数据库优化

- 为常用查询字段添加索引
- 使用分页查询避免全表扫描
- 商品列表查询使用覆盖索引

### 缓存优化

- 热门商品信息缓存
- 分类树缓存
- 库存信息缓存

### 查询优化

- 商品列表查询避免关联查询
- 使用 DTO 只返回必要字段
- 图片URL使用CDN加速

### 并发优化

- 库存扣减使用 Redis 分布式锁
- CDKEY 分配使用数据库乐观锁
- 批量操作使用异步处理

## Monitoring and Logging

### 监控指标

- 商品创建/更新/删除 QPS
- 商品查询响应时间
- 库存扣减成功率
- CDKEY 分配成功率
- 缓存命中率

### 日志记录

**业务日志**:
- 商品创建/更新/删除
- 库存变更
- CDKEY 分配
- 佣金配置变更

**错误日志**:
- 库存不足
- CDKEY 不足
- 佣金计算失败
- 第三方服务调用失败

### 告警规则

- 库存不足告警（低于预警值）
- CDKEY 不足告警（少于100个）
- 接口响应时间超过阈值
- 错误率超过5%

---

**文档版本**: v1.0  
**创建时间**: 2025-12-09  
**最后更新**: 2025-12-09
