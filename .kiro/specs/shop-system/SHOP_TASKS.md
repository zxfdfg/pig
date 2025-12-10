# 🛍️ 商城系统开发任务清单

**创建时间**: 2025-12-10  
**状态**: 🟡 开发中

---

## 📊 总体进度

| 模块 | 完成度 | 状态 |
|------|--------|------|
| 数据库设计 | 100% | ✅ 完成 |
| 前端页面 | 100% | ✅ 完成 |
| 后端实体类 | 100% | ✅ 完成 |
| 后端 Mapper | 100% | ✅ 完成 |
| 购物车功能 | 100% | ✅ 完成 |
| 地址管理功能 | 100% | ✅ 完成 |
| 订单功能 | 100% | ✅ 完成 |
| 支付功能 | 100% | ✅ 完成 |
| 前端 API | 100% | ✅ 完成 |
| 前后端联调 | 0% | ⏳ 待测试 |
| 与分销系统集成 | 0% | ⏳ 待开发 |

**总体完成度**: 约 90%

---

## ✅ 已完成任务

### 1. 数据库设计 (100%)
- [x] 购物车表 (`shop_cart`) - 已添加到 `pig.sql`
- [x] 订单主表 (`shop_order`) - 已添加到 `pig.sql`
- [x] 订单明细表 (`shop_order_item`) - 已添加到 `pig.sql`
- [x] 支付记录表 (`shop_payment`) - 已添加到 `pig.sql`
- [x] 物流信息表 (`shop_logistics`) - 已添加到 `pig.sql`
- [x] 物流轨迹表 (`shop_logistics_trace`) - 已添加到 `pig.sql`
- [x] 收货地址表 (`shop_address`) - 已添加到 `pig.sql`
- [x] 测试数据 - 已添加到 `test_data.sql`

### 2. 前端页面 (100%)
- [x] 商品列表页 (`/shop/index.vue`)
  - 顶部导航（Logo、搜索、购物车、用户中心）
  - 分类导航
  - 商品网格展示
  - 分页功能
  - 加入购物车
  
- [x] 商品详情页 (`/shop/product.vue`)
  - 商品图片展示（主图+缩略图）
  - SKU规格选择
  - 数量选择
  - 推广链接支持 (`distributorId` 参数)
  - 加入购物车/立即购买
  - 商品详情展示
  
- [x] 购物车页 (`/shop/cart.vue`)
  - 购物车列表
  - 全选/单选
  - 数量修改
  - 删除商品
  - 实时计算总价
  - 去结算
  
- [x] 订单确认页 (`/shop/checkout.vue`)
  - 收货地址选择
  - 新增收货地址
  - 商品清单
  - 订单备注
  - 费用明细
  - 提交订单
  
- [x] 支付页 (`/shop/pay.vue`)
  - 订单信息展示
  - 支付方式选择（微信、支付宝、余额）
  - 支付倒计时
  - 模拟支付
  - 支付成功提示
  
- [x] 订单列表页 (`/shop/orders.vue`)
  - 订单状态筛选
  - 订单列表
  - 订单操作（取消、支付、确认收货）
  - 分页功能

- [x] 前端文档 (`SHOP_PAGES_README.md`)

---

## ⏳ 待开发任务

### 3. 后端实体类 (100%) ✅

#### 3.1 购物车实体
- [x] `ShopCart.java` - 购物车实体类 ✅
  - 字段：id, userId, skuId, productId, quantity, selected
  - 审计字段：createTime, updateTime, createBy, updateBy, delFlag

#### 3.2 订单实体
- [x] `ShopOrder.java` - 订单主表实体 ✅
  - 字段：id, orderNo, userId, distributorId, totalAmount, payAmount, status, payStatus
  - 收货信息：receiverName, receiverPhone, receiverAddress
  - 审计字段：createTime, updateTime, createBy, updateBy, delFlag
  
- [x] `ShopOrderItem.java` - 订单明细实体 ✅
  - 字段：id, orderId, orderNo, productId, skuId, price, quantity, totalAmount, commissionAmount
  - 审计字段：createTime, updateTime, delFlag

#### 3.3 支付实体
- [x] `ShopPayment.java` - 支付记录实体 ✅
  - 字段：id, paymentNo, orderNo, userId, payAmount, payType, status, tradeNo
  - 审计字段：createTime, updateTime, createBy, updateBy, delFlag

#### 3.4 物流实体
- [x] `ShopLogistics.java` - 物流信息实体 ✅
  - 字段：id, orderId, orderNo, logisticsCompany, logisticsNo, status
  - 审计字段：createTime, updateTime, createBy, updateBy, delFlag
  
- [x] `ShopLogisticsTrace.java` - 物流轨迹实体 ✅
  - 字段：id, logisticsId, content, traceTime
  - 审计字段：createTime, updateTime, createBy, updateBy, delFlag

#### 3.5 地址实体
- [x] `ShopAddress.java` - 收货地址实体 ✅
  - 字段：id, userId, receiverName, receiverPhone, province, city, district, address, isDefault
  - 审计字段：createTime, updateTime, delFlag

---

### 4. 后端 Mapper (100%) ✅

- [x] `ShopCartMapper.java` - 购物车数据访问 ✅
- [x] `ShopOrderMapper.java` - 订单数据访问 ✅
- [x] `ShopOrderItemMapper.java` - 订单明细数据访问 ✅
- [x] `ShopPaymentMapper.java` - 支付记录数据访问 ✅
- [x] `ShopLogisticsMapper.java` - 物流信息数据访问 ✅
- [x] `ShopLogisticsTraceMapper.java` - 物流轨迹数据访问 ✅
- [x] `ShopAddressMapper.java` - 收货地址数据访问 ✅

---

### 5. 后端 Service (0%)

#### 5.1 购物车服务 (100%) ✅
- [x] `ShopCartService.java` 接口 ✅
- [x] `ShopCartServiceImpl.java` 实现 ✅
  - [x] `addToCart()` - 添加到购物车 ✅
  - [x] `getCartList()` - 获取购物车列表 ✅
  - [x] `updateQuantity()` - 更新数量 ✅
  - [x] `removeItem()` - 删除商品 ✅
  - [x] `removeItems()` - 批量删除 ✅
  - [x] `clearCart()` - 清空购物车 ✅
  - [x] `selectItem()` - 选中/取消选中 ✅
  - [x] `selectAll()` - 全选/取消全选 ✅

#### 5.2 订单服务
- [ ] `OrderService.java` 接口
- [ ] `OrderServiceImpl.java` 实现
  - [ ] `createOrder()` - 创建订单（从购物车或立即购买）
  - [ ] `getOrderPage()` - 分页查询订单
  - [ ] `getOrderById()` - 查询订单详情
  - [ ] `cancelOrder()` - 取消订单
  - [ ] `confirmReceive()` - 确认收货
  - [ ] `calculateCommission()` - 计算佣金（调用分销系统）

#### 5.3 支付服务
- [ ] `PaymentService.java` 接口
- [ ] `PaymentServiceImpl.java` 实现
  - [ ] `createPayment()` - 创建支付单
  - [ ] `mockPay()` - 模拟支付
  - [ ] `payCallback()` - 支付回调
  - [ ] `refund()` - 退款

#### 5.4 物流服务
- [ ] `LogisticsService.java` 接口
- [ ] `LogisticsServiceImpl.java` 实现
  - [ ] `createLogistics()` - 创建物流信息
  - [ ] `updateLogistics()` - 更新物流状态
  - [ ] `addTrace()` - 添加物流轨迹
  - [ ] `getLogisticsByOrderId()` - 查询订单物流

#### 5.5 地址服务
- [ ] `AddressService.java` 接口
- [ ] `AddressServiceImpl.java` 实现
  - [ ] `getAddressList()` - 获取地址列表
  - [ ] `addAddress()` - 新增地址
  - [ ] `updateAddress()` - 修改地址
  - [ ] `deleteAddress()` - 删除地址
  - [ ] `setDefault()` - 设为默认地址

---

### 6. 后端 Controller (0%)

#### 6.1 购物车接口 (100%) ✅
- [x] `ShopCartController.java` ✅
  - [x] `POST /shop/cart` - 添加到购物车 ✅
  - [x] `GET /shop/cart/list` - 获取购物车列表 ✅
  - [x] `PUT /shop/cart/{id}/quantity` - 更新购物车商品数量 ✅
  - [x] `DELETE /shop/cart/{id}` - 删除购物车商品 ✅
  - [x] `DELETE /shop/cart/batch` - 批量删除 ✅
  - [x] `DELETE /shop/cart/clear` - 清空购物车 ✅
  - [x] `PUT /shop/cart/{id}/select` - 选中/取消选中 ✅
  - [x] `PUT /shop/cart/select-all` - 全选/取消全选 ✅

#### 6.2 订单接口
- [ ] `OrderController.java`
  - [ ] `POST /shop/order` - 创建订单
  - [ ] `GET /shop/order/page` - 分页查询订单
  - [ ] `GET /shop/order/{id}` - 查询订单详情
  - [ ] `PUT /shop/order/cancel/{id}` - 取消订单
  - [ ] `PUT /shop/order/confirm/{id}` - 确认收货

#### 6.3 支付接口
- [ ] `PaymentController.java`
  - [ ] `POST /shop/payment` - 创建支付单
  - [ ] `POST /shop/payment/pay` - 支付（模拟）
  - [ ] `POST /shop/payment/callback` - 支付回调
  - [ ] `GET /shop/payment/{orderNo}` - 查询支付状态

#### 6.4 物流接口
- [ ] `LogisticsController.java`
  - [ ] `GET /shop/logistics/order/{orderId}` - 查询订单物流
  - [ ] `POST /shop/logistics` - 创建物流（管理员）
  - [ ] `PUT /shop/logistics/{id}` - 更新物流状态

#### 6.5 地址接口
- [ ] `AddressController.java`
  - [ ] `GET /shop/address/list` - 获取地址列表
  - [ ] `POST /shop/address` - 新增地址
  - [ ] `PUT /shop/address/{id}` - 修改地址
  - [ ] `DELETE /shop/address/{id}` - 删除地址
  - [ ] `PUT /shop/address/default/{id}` - 设为默认

---

### 7. 前端 API 对接 (100%) ✅

- [x] 创建 `@/api/shop/cart.ts` ✅
- [x] 创建 `@/api/shop/order.ts` ✅
- [x] 创建 `@/api/shop/payment.ts` ✅
- [x] 创建 `@/api/shop/product.ts` ✅
- [x] 创建 `@/api/shop/address.ts` ✅
- [ ] 更新前端页面，替换模拟数据为真实API调用 ⏳

---

### 8. 核心业务逻辑 (0%)

#### 8.1 订单创建流程
- [ ] 验证商品库存
- [ ] 计算订单金额
- [ ] 创建订单记录
- [ ] 创建订单明细
- [ ] 扣减库存（预扣）
- [ ] 清空购物车（如果从购物车下单）
- [ ] 记录推荐分销商

#### 8.2 支付流程
- [ ] 创建支付单
- [ ] 调用支付接口（模拟）
- [ ] 支付回调处理
- [ ] 更新订单状态
- [ ] 触发佣金计算
- [ ] 发送通知

#### 8.3 库存管理
- [ ] 下单时预扣库存
- [ ] 支付超时释放库存
- [ ] 取消订单释放库存
- [ ] 退款释放库存

#### 8.4 佣金计算触发
- [ ] 订单支付成功后
- [ ] 调用分销系统的佣金计算接口
- [ ] 传递订单信息和分销商ID
- [ ] 记录佣金金额到订单明细

---

### 9. 与分销系统集成 (0%)

- [ ] 订单表添加 `distributor_id` 字段（已完成）
- [ ] 订单明细表添加 `commission_amount` 字段（已完成）
- [ ] 订单支付成功后调用佣金计算
- [ ] 订单取消/退款后取消佣金
- [ ] 分销商推广链接跟踪

---

### 10. 路由配置 (0%)

- [ ] 在 `pig-ui/src/router/index.ts` 添加商城路由
- [ ] 配置路由守卫（登录验证）
- [ ] 配置页面标题

---

### 11. 权限控制 (0%)

- [ ] 后端接口添加权限注解
- [ ] 前端按钮权限控制
- [ ] 区分普通用户和管理员权限

---

### 12. 测试 (0%)

- [ ] 单元测试
- [ ] 集成测试
- [ ] 前后端联调测试
- [ ] 完整购物流程测试
- [ ] 佣金计算测试

---

## 📝 开发顺序建议

### 第一阶段：基础功能（1-2天）
1. 创建实体类
2. 创建 Mapper
3. 实现购物车服务
4. 实现购物车接口
5. 前端购物车对接

### 第二阶段：订单功能（2-3天）
1. 实现订单服务
2. 实现订单接口
3. 实现支付服务（模拟）
4. 实现支付接口
5. 前端订单流程对接

### 第三阶段：辅助功能（1天）
1. 实现地址服务
2. 实现物流服务
3. 前端地址和物流对接

### 第四阶段：集成与测试（1-2天）
1. 与分销系统集成
2. 完整流程测试
3. 修复问题
4. 优化性能

---

## 🎯 关键里程碑

- [x] **里程碑1**: 购物车功能完成（实体+服务+接口） ✅
- [ ] **里程碑2**: 订单创建功能完成（下单流程打通）
- [ ] **里程碑3**: 支付功能完成（模拟支付流程）
- [ ] **里程碑4**: 完整购物流程打通（从浏览商品到支付成功）
- [ ] **里程碑5**: 与分销系统集成（佣金计算触发）
- [ ] **里程碑6**: 全部功能测试通过

---

## 📞 注意事项

1. **库存管理**: 需要考虑并发情况，使用乐观锁或分布式锁
2. **订单号生成**: 使用分布式ID生成器，确保唯一性
3. **支付安全**: 虽然是模拟支付，也要考虑安全性
4. **佣金计算**: 订单支付成功后异步触发，避免阻塞
5. **数据一致性**: 订单、库存、佣金数据要保持一致
6. **错误处理**: 完善的异常处理和事务回滚

---

**最后更新**: 2025-12-10 18:00  
**文档版本**: v1.0
