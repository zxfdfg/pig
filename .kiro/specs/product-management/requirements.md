# Requirements Document - 商品管理系统

## Introduction

商品管理系统是 PIG 微服务平台的核心业务模块，用于管理平台上的所有商品信息。系统需要支持多种商品类型，包括实物商品和虚拟商品（如 CDKEY），提供完整的商品生命周期管理功能，包括商品的创建、编辑、上下架、库存管理等。

## Glossary

- **System**: 商品管理系统（Product Management System）
- **Product**: 商品，平台上可销售的物品或服务
- **Physical Product**: 实物商品，需要物流配送的商品
- **Virtual Product**: 虚拟商品，无需物流配送的数字化商品
- **CDKEY**: 虚拟商品的一种，由字符串组成的激活码或兑换码
- **SKU**: Stock Keeping Unit，库存量单位，商品的最小销售单元
- **SPU**: Standard Product Unit，标准产品单元，商品信息聚合的最小单位
- **Category**: 商品分类，用于组织和管理商品
- **Inventory**: 库存，商品的可售数量
- **Status**: 商品状态，包括草稿、上架、下架、售罄等
- **Administrator**: 管理员，有权限管理商品的用户

## Requirements

### Requirement 1

**User Story:** 作为管理员，我想要创建新商品，以便在平台上销售

#### Acceptance Criteria

1. WHEN 管理员填写商品基本信息并提交 THEN System SHALL 创建新商品记录并返回商品ID
2. WHEN 管理员上传商品图片 THEN System SHALL 验证图片格式和大小并存储图片
3. WHEN 管理员选择商品分类 THEN System SHALL 关联商品与分类
4. WHEN 管理员设置商品价格 THEN System SHALL 验证价格为正数并保存
5. WHEN 管理员创建商品时未填写必填字段 THEN System SHALL 拒绝创建并提示缺失字段

### Requirement 2

**User Story:** 作为管理员，我想要管理虚拟商品（CDKEY），以便销售数字化产品

#### Acceptance Criteria

1. WHEN 管理员创建虚拟商品 THEN System SHALL 允许选择商品类型为虚拟商品
2. WHEN 管理员批量导入 CDKEY THEN System SHALL 解析文件并存储所有有效的 CDKEY
3. WHEN 管理员导入的 CDKEY 格式错误 THEN System SHALL 拒绝导入并提示错误行号
4. WHEN 虚拟商品被购买 THEN System SHALL 自动分配一个未使用的 CDKEY 给买家
5. WHEN 虚拟商品的 CDKEY 库存为零 THEN System SHALL 将商品状态更新为售罄

### Requirement 3

**User Story:** 作为管理员，我想要编辑商品信息，以便更新商品详情

#### Acceptance Criteria

1. WHEN 管理员修改商品信息并保存 THEN System SHALL 更新商品记录并记录修改时间
2. WHEN 管理员修改已上架商品的价格 THEN System SHALL 记录价格变更历史
3. WHEN 管理员修改商品的 SKU 信息 THEN System SHALL 更新对应的 SKU 记录
4. WHEN 管理员删除商品图片 THEN System SHALL 从存储中移除图片文件
5. WHILE 商品有未完成的订单 THEN System SHALL 禁止删除该商品

### Requirement 4

**User Story:** 作为管理员，我想要管理商品库存，以便控制商品的可售数量

#### Acceptance Criteria

1. WHEN 管理员增加商品库存 THEN System SHALL 更新库存数量并记录操作日志
2. WHEN 管理员减少商品库存 THEN System SHALL 验证库存充足并更新数量
3. WHEN 商品库存低于预警值 THEN System SHALL 标记商品为低库存状态
4. WHEN 商品库存为零 THEN System SHALL 自动将商品状态更新为售罄
5. WHEN 订单支付成功 THEN System SHALL 扣减对应商品的库存数量

### Requirement 5

**User Story:** 作为管理员，我想要管理商品分类，以便组织和展示商品

#### Acceptance Criteria

1. WHEN 管理员创建商品分类 THEN System SHALL 创建分类记录并支持多级分类
2. WHEN 管理员修改分类名称 THEN System SHALL 更新分类信息
3. WHEN 管理员删除分类 THEN System SHALL 验证分类下无商品后允许删除
4. WHEN 管理员移动商品到其他分类 THEN System SHALL 更新商品的分类关联
5. WHEN 管理员查询分类下的商品 THEN System SHALL 返回该分类及其子分类下的所有商品

### Requirement 6

**User Story:** 作为管理员，我想要上下架商品，以便控制商品的销售状态

#### Acceptance Criteria

1. WHEN 管理员上架商品 THEN System SHALL 验证商品信息完整并更新状态为上架
2. WHEN 管理员下架商品 THEN System SHALL 更新商品状态为下架并停止销售
3. WHEN 商品状态为草稿 THEN System SHALL 禁止上架该商品
4. WHEN 商品库存为零 THEN System SHALL 禁止上架该商品
5. WHEN 商品被下架 THEN System SHALL 在前台隐藏该商品

### Requirement 7

**User Story:** 作为管理员，我想要查询和搜索商品，以便快速找到目标商品

#### Acceptance Criteria

1. WHEN 管理员输入商品名称搜索 THEN System SHALL 返回名称匹配的商品列表
2. WHEN 管理员按分类筛选商品 THEN System SHALL 返回该分类下的所有商品
3. WHEN 管理员按状态筛选商品 THEN System SHALL 返回对应状态的商品
4. WHEN 管理员按价格区间筛选商品 THEN System SHALL 返回价格在区间内的商品
5. WHEN 管理员查询商品详情 THEN System SHALL 返回商品的完整信息包括 SKU 和库存

### Requirement 8

**User Story:** 作为管理员，我想要管理商品 SKU，以便支持多规格商品

#### Acceptance Criteria

1. WHEN 管理员为商品添加 SKU THEN System SHALL 创建 SKU 记录并关联到商品
2. WHEN 管理员设置 SKU 属性（如颜色、尺寸）THEN System SHALL 保存 SKU 的属性信息
3. WHEN 管理员设置 SKU 价格 THEN System SHALL 允许不同 SKU 有不同价格
4. WHEN 管理员设置 SKU 库存 THEN System SHALL 独立管理每个 SKU 的库存
5. WHEN 管理员删除 SKU THEN System SHALL 验证该 SKU 无未完成订单后允许删除

### Requirement 9

**User Story:** 作为管理员，我想要查看商品统计数据，以便了解商品运营情况

#### Acceptance Criteria

1. WHEN 管理员查看商品总览 THEN System SHALL 显示商品总数、上架数、下架数、售罄数
2. WHEN 管理员查看分类统计 THEN System SHALL 显示每个分类下的商品数量
3. WHEN 管理员查看库存统计 THEN System SHALL 显示低库存商品和售罄商品数量
4. WHEN 管理员查看商品销量 THEN System SHALL 显示商品的销售数量和销售额
5. WHEN 管理员导出商品数据 THEN System SHALL 生成 Excel 文件包含所有商品信息

### Requirement 10

**User Story:** 作为管理员，我想要批量操作商品，以便提高管理效率

#### Acceptance Criteria

1. WHEN 管理员批量上架商品 THEN System SHALL 验证每个商品并批量更新状态
2. WHEN 管理员批量下架商品 THEN System SHALL 批量更新商品状态为下架
3. WHEN 管理员批量修改商品分类 THEN System SHALL 批量更新商品的分类关联
4. WHEN 管理员批量删除商品 THEN System SHALL 验证商品可删除后批量删除
5. WHEN 批量操作中部分商品失败 THEN System SHALL 返回失败商品列表和失败原因

### Requirement 11

**User Story:** 作为管理员，我想要为商品配置分销佣金，以便商品可以通过分销系统销售

#### Acceptance Criteria

1. WHEN 管理员为商品设置佣金配置 THEN System SHALL 保存商品的分销佣金比例
2. WHEN 管理员设置商品的一级佣金比例 THEN System SHALL 验证比例在合理范围内（0-50%）
3. WHEN 管理员设置商品的二级佣金比例 THEN System SHALL 验证比例小于一级佣金比例
4. WHEN 管理员设置商品的三级佣金比例 THEN System SHALL 验证比例小于二级佣金比例
5. WHEN 商品未设置佣金配置 THEN System SHALL 使用商品分类的默认佣金配置

### Requirement 12

**User Story:** 作为管理员，我想要为商品分类设置默认佣金，以便批量管理商品佣金

#### Acceptance Criteria

1. WHEN 管理员为分类设置默认佣金 THEN System SHALL 保存分类的佣金配置
2. WHEN 管理员修改分类佣金配置 THEN System SHALL 询问是否同步更新该分类下的所有商品
3. WHEN 新商品选择分类时 THEN System SHALL 自动继承该分类的佣金配置
4. WHEN 管理员查看分类佣金统计 THEN System SHALL 显示该分类商品的平均佣金率和利润率
5. WHEN 分类佣金率超过预警阈值 THEN System SHALL 显示预警标识

### Requirement 13

**User Story:** 作为管理员，我想要查看商品的分销数据，以便了解商品的推广效果

#### Acceptance Criteria

1. WHEN 管理员查看商品详情 THEN System SHALL 显示该商品的分销订单数量和销售额
2. WHEN 管理员查看商品分销统计 THEN System SHALL 显示该商品产生的总佣金和佣金率
3. WHEN 管理员查看商品推广排行 THEN System SHALL 显示推广该商品最多的分销商列表
4. WHEN 管理员查看商品利润分析 THEN System SHALL 显示商品的成本、售价、佣金、利润
5. WHEN 商品佣金成本过高 THEN System SHALL 在商品列表中显示预警标识

### Requirement 14

**User Story:** 作为分销商，我想要查看可推广的商品列表，以便选择合适的商品进行推广

#### Acceptance Criteria

1. WHEN 分销商访问商品列表 THEN System SHALL 只显示已上架且有库存的商品
2. WHEN 分销商查看商品详情 THEN System SHALL 显示该商品的佣金比例和预期收益
3. WHEN 分销商按佣金率排序商品 THEN System SHALL 按佣金比例从高到低排序
4. WHEN 分销商筛选高佣金商品 THEN System SHALL 只显示佣金率高于指定值的商品
5. WHEN 分销商生成推广链接 THEN System SHALL 生成包含商品ID和分销商ID的专属链接

### Requirement 15

**User Story:** 作为系统，我需要在订单完成时触发佣金计算，以便分销商获得收益

#### Acceptance Criteria

1. WHEN 订单状态变为已完成 THEN System SHALL 查询订单商品的佣金配置
2. WHEN 订单买家有推荐人 THEN System SHALL 查询买家的分销关系链（最多3级）
3. WHEN 计算订单佣金 THEN System SHALL 根据商品佣金配置和分销商等级计算每级佣金
4. WHEN 商品未配置佣金 THEN System SHALL 跳过该商品的佣金计算
5. WHEN 佣金计算完成 THEN System SHALL 调用分销系统接口创建佣金记录

### Requirement 16

**User Story:** 作为管理员，我想要管理虚拟商品的 CDKEY 与分销的关系，以便控制虚拟商品的分销

#### Acceptance Criteria

1. WHEN 虚拟商品被分销商推广 THEN System SHALL 记录 CDKEY 的分销来源
2. WHEN CDKEY 被使用 THEN System SHALL 触发对应分销商的佣金结算
3. WHEN 管理员查看 CDKEY 详情 THEN System SHALL 显示该 CDKEY 的推广分销商和使用状态
4. WHEN 批量导入 CDKEY THEN System SHALL 支持为每个 CDKEY 设置独立的佣金配置
5. WHEN CDKEY 过期 THEN System SHALL 自动取消该 CDKEY 的待结算佣金

---

## 业务闭环支持模块

### Requirement 17

**User Story:** 作为用户，我想要浏览商品并查看详情，以便选择我想购买的商品

#### Acceptance Criteria

1. WHEN 用户访问商城首页 THEN System SHALL 显示推荐商品和热门商品列表
2. WHEN 用户点击商品 THEN System SHALL 显示商品详情页包括图片、价格、库存、描述
3. WHEN 用户通过分销链接访问商品 THEN System SHALL 记录推荐关系并在会话中保持
4. WHEN 用户搜索商品 THEN System SHALL 按商品名称、分类、关键词搜索并返回结果
5. WHEN 用户筛选商品 THEN System SHALL 支持按价格区间、分类、销量排序

### Requirement 18

**User Story:** 作为用户，我想要将商品加入购物车，以便一次性购买多个商品

#### Acceptance Criteria

1. WHEN 用户点击加入购物车 THEN System SHALL 将商品添加到用户的购物车
2. WHEN 用户修改购物车商品数量 THEN System SHALL 验证库存并更新购物车
3. WHEN 用户删除购物车商品 THEN System SHALL 从购物车中移除该商品
4. WHEN 用户查看购物车 THEN System SHALL 显示商品列表、总价、优惠信息
5. WHEN 商品库存不足 THEN System SHALL 在购物车中标记该商品并提示用户

### Requirement 19

**User Story:** 作为用户，我想要创建订单并支付，以便购买商品

#### Acceptance Criteria

1. WHEN 用户提交订单 THEN System SHALL 创建订单记录并锁定商品库存
2. WHEN 用户选择收货地址 THEN System SHALL 保存订单的收货信息
3. WHEN 用户选择支付方式 THEN System SHALL 记录支付方式并生成支付订单
4. WHEN 订单创建成功 THEN System SHALL 返回订单号并跳转到支付页面
5. WHEN 订单超时未支付 THEN System SHALL 自动取消订单并释放库存

### Requirement 20

**User Story:** 作为用户，我想要完成支付，以便获得商品

#### Acceptance Criteria

1. WHEN 用户选择支付宝支付 THEN System SHALL 调用支付宝接口生成支付二维码
2. WHEN 用户选择微信支付 THEN System SHALL 调用微信支付接口生成支付二维码
3. WHEN 支付成功 THEN System SHALL 接收支付回调并更新订单状态为已支付
4. WHEN 支付失败 THEN System SHALL 记录失败原因并允许用户重新支付
5. WHEN 订单已支付 THEN System SHALL 扣减商品库存并触发发货流程

### Requirement 21

**User Story:** 作为用户，我想要查看订单状态，以便了解订单进度

#### Acceptance Criteria

1. WHEN 用户查看订单列表 THEN System SHALL 显示所有订单及其状态
2. WHEN 用户查看订单详情 THEN System SHALL 显示订单商品、金额、收货地址、物流信息
3. WHEN 订单状态为待发货 THEN System SHALL 显示预计发货时间
4. WHEN 订单状态为已发货 THEN System SHALL 显示物流公司和物流单号
5. WHEN 用户点击确认收货 THEN System SHALL 更新订单状态为已完成

### Requirement 22

**User Story:** 作为管理员，我想要管理订单，以便处理用户的购买请求

#### Acceptance Criteria

1. WHEN 管理员查看订单列表 THEN System SHALL 显示所有订单并支持按状态筛选
2. WHEN 订单支付成功 THEN System SHALL 通知管理员有新订单待处理
3. WHEN 管理员标记订单为已发货 THEN System SHALL 更新订单状态并记录物流信息
4. WHEN 管理员取消订单 THEN System SHALL 退还库存并触发退款流程
5. WHEN 订单完成 THEN System SHALL 触发佣金计算并通知分销商

### Requirement 23

**User Story:** 作为用户，我想要申请售后服务，以便处理商品问题

#### Acceptance Criteria

1. WHEN 用户申请退款 THEN System SHALL 创建退款申请并通知管理员
2. WHEN 用户申请退货 THEN System SHALL 创建退货申请并生成退货地址
3. WHEN 管理员审核退款申请 THEN System SHALL 更新申请状态并触发退款
4. WHEN 退款成功 THEN System SHALL 恢复商品库存并取消相关佣金
5. WHEN 虚拟商品已使用 THEN System SHALL 禁止申请退款

### Requirement 24

**User Story:** 作为分销商，我想要生成商品推广素材，以便在社交媒体推广

#### Acceptance Criteria

1. WHEN 分销商选择商品生成推广海报 THEN System SHALL 生成包含商品信息和推广二维码的海报
2. WHEN 分销商复制推广文案 THEN System SHALL 提供包含商品卖点和推广链接的文案模板
3. WHEN 分销商下载推广素材 THEN System SHALL 提供多种尺寸的商品图片和海报
4. WHEN 分销商分享推广链接 THEN System SHALL 生成短链接并支持一键复制
5. WHEN 用户通过推广链接访问 THEN System SHALL 记录推广来源并在订单中关联分销商

### Requirement 25

**User Story:** 作为分销商，我想要查看推广效果，以便优化推广策略

#### Acceptance Criteria

1. WHEN 分销商查看推广数据 THEN System SHALL 显示推广链接的点击量和转化率
2. WHEN 分销商查看推广订单 THEN System SHALL 显示通过推广产生的订单列表和金额
3. WHEN 分销商查看商品推广排行 THEN System SHALL 显示推广效果最好的商品
4. WHEN 分销商查看推广趋势 THEN System SHALL 显示推广数据的时间趋势图
5. WHEN 分销商导出推广数据 THEN System SHALL 生成包含详细推广数据的 Excel 文件

### Requirement 26

**User Story:** 作为系统，我需要处理支付回调，以便更新订单状态和触发业务流程

#### Acceptance Criteria

1. WHEN 接收到支付宝回调 THEN System SHALL 验证签名并更新订单支付状态
2. WHEN 接收到微信支付回调 THEN System SHALL 验证签名并更新订单支付状态
3. WHEN 支付回调验证失败 THEN System SHALL 记录错误日志并拒绝处理
4. WHEN 订单状态更新为已支付 THEN System SHALL 扣减库存并发送支付成功通知
5. WHEN 订单包含虚拟商品 THEN System SHALL 自动分配 CDKEY 并发送给用户

### Requirement 27

**User Story:** 作为用户，我想要收到订单通知，以便及时了解订单状态变化

#### Acceptance Criteria

1. WHEN 订单创建成功 THEN System SHALL 发送订单确认通知给用户
2. WHEN 订单支付成功 THEN System SHALL 发送支付成功通知给用户
3. WHEN 订单发货 THEN System SHALL 发送发货通知包含物流信息
4. WHEN 订单完成 THEN System SHALL 发送订单完成通知并邀请评价
5. WHEN 订单取消或退款 THEN System SHALL 发送相应通知给用户

### Requirement 28

**User Story:** 作为管理员，我想要配置支付方式，以便支持多种支付渠道

#### Acceptance Criteria

1. WHEN 管理员配置支付宝 THEN System SHALL 保存支付宝的 AppID、私钥、公钥
2. WHEN 管理员配置微信支付 THEN System SHALL 保存微信的商户号、API密钥、证书
3. WHEN 管理员启用支付方式 THEN System SHALL 在前台显示该支付选项
4. WHEN 管理员禁用支付方式 THEN System SHALL 在前台隐藏该支付选项
5. WHEN 管理员测试支付配置 THEN System SHALL 发起测试支付并返回结果

### Requirement 29

**User Story:** 作为用户，我想要管理收货地址，以便快速填写订单信息

#### Acceptance Criteria

1. WHEN 用户添加收货地址 THEN System SHALL 保存地址信息并支持设置默认地址
2. WHEN 用户修改收货地址 THEN System SHALL 更新地址信息
3. WHEN 用户删除收货地址 THEN System SHALL 验证该地址未被使用后允许删除
4. WHEN 用户创建订单 THEN System SHALL 自动选择默认收货地址
5. WHEN 用户选择收货地址 THEN System SHALL 显示所有可用地址供用户选择

### Requirement 30

**User Story:** 作为管理员，我想要配置物流公司，以便管理订单发货

#### Acceptance Criteria

1. WHEN 管理员添加物流公司 THEN System SHALL 保存物流公司名称和编码
2. WHEN 管理员配置物流接口 THEN System SHALL 保存物流查询接口的配置信息
3. WHEN 订单发货 THEN System SHALL 调用物流接口推送物流信息
4. WHEN 用户查询物流 THEN System SHALL 调用物流接口获取最新物流状态
5. WHEN 物流接口调用失败 THEN System SHALL 记录错误日志并使用备用方案
