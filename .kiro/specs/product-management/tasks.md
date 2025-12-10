# Implementation Plan - 商品管理系统

## 任务概述

本实施计划将商品管理系统的开发分解为可执行的任务。任务按照依赖关系排序，确保每个任务都可以基于前面的任务进行开发。

**预计工作量**: 约 120 小时  
**建议团队规模**: 2-3 人  
**预计完成时间**: 3-4 周

---

## 任务列表

- [x] 1. 项目结构搭建


  - 创建 pig-product 模块目录结构
  - 配置 Maven 依赖
  - 配置 Nacos 注册和配置中心
  - 配置数据库连接
  - _Requirements: 所有_
  - _预计时间: 2小时_

- [x] 2. 数据库表创建





  - 创建商品表 (product)
  - 创建分类表 (product_category)
  - 创建 SKU 表 (product_sku)
  - 创建 CDKEY 表 (product_cdkey)
  - 创建库存日志表 (product_stock_log)
  - 创建佣金配置表 (product_commission_config)
  - 创建价格历史表 (product_price_history)
  - _Requirements: 所有_
  - _预计时间: 2小时_

- [x] 3. 实体类和 Mapper 创建




- [x] 3.1 创建实体类


  - Product, ProductCategory, ProductSku, ProductCdkey
  - ProductStockLog, ProductCommissionConfig, ProductPriceHistory
  - _Requirements: 所有_
  - _预计时间: 2小时_

- [x] 3.2 创建 Mapper 接口


  - ProductMapper, CategoryMapper, SkuMapper, CdkeyMapper
  - StockLogMapper, CommissionConfigMapper, PriceHistoryMapper
  - _Requirements: 所有_
  - _预计时间: 1小时_

- [x] 3.3 创建 Mapper XML 文件


  - 编写基础 CRUD SQL
  - 编写复杂查询 SQL（分类树、库存统计等）
  - _Requirements: 所有_
  - _预计时间: 3小时_

- [x] 4. 商品分类管理






- [x] 4.1 实现 CategoryService

  - 创建分类
  - 更新分类
  - 删除分类（验证无商品）
  - 查询分类树
  - 查询分类下的商品
  - _Requirements: 5.1, 5.2, 5.3, 5.5_
  - _预计时间: 4小时_

- [ ]* 4.2 编写分类管理属性测试
  - **Property 18: 分类删除限制**
  - **Validates: Requirements 5.3**
  - **Property 19: 分类查询包含子分类**
  - **Validates: Requirements 5.5**
  - _预计时间: 2小时_


- [x] 4.3 实现 CategoryController

  - POST /category - 创建分类
  - PUT /category - 更新分类
  - DELETE /category/{id} - 删除分类
  - GET /category/tree - 查询分类树
  - GET /category/{id}/products - 查询分类商品
  - _Requirements: 5.1, 5.2, 5.3, 5.5_
  - _预计时间: 2小时_

- [-] 5. 商品基础管理


- [ ] 5.1 实现 ProductService 核心功能
  - 创建商品（验证必填字段、价格）
  - 更新商品（记录修改时间）
  - 删除商品（验证无订单）
  - 查询商品详情
  - 分页查询商品
  - _Requirements: 1.1, 1.4, 1.5, 3.1, 3.5, 7.5_
  - _预计时间: 6小时_

- [ ]* 5.2 编写商品管理属性测试
  - **Property 1: 商品创建返回有效ID**
  - **Validates: Requirements 1.1**
  - **Property 4: 价格正数验证**
  - **Validates: Requirements 1.4**
  - **Property 5: 必填字段验证**
  - **Validates: Requirements 1.5**
  - **Property 10: 商品更新时间记录**
  - **Validates: Requirements 3.1**
  - **Property 12: 有订单商品禁止删除**
  - **Validates: Requirements 3.5**
  - _预计时间: 3小时_

- [ ] 5.3 实现商品图片管理
  - 图片上传（验证格式和大小）
  - 图片删除（从存储移除）
  - 图片列表管理
  - _Requirements: 1.2, 3.4_
  - _预计时间: 3小时_

- [ ]* 5.4 编写图片管理属性测试
  - **Property 2: 图片格式验证**
  - **Validates: Requirements 1.2**
  - _预计时间: 1小时_

- [ ] 5.5 实现 ProductController 基础接口
  - POST /product - 创建商品
  - PUT /product - 更新商品
  - DELETE /product/{id} - 删除商品
  - GET /product/{id} - 查询详情
  - GET /product/page - 分页查询
  - _Requirements: 1.1, 1.4, 1.5, 3.1, 3.5, 7.5_
  - _预计时间: 3小时_

- [ ] 6. 商品搜索和筛选
- [ ] 6.1 实现商品搜索功能
  - 按名称搜索
  - 按分类筛选
  - 按状态筛选
  - 按价格区间筛选
  - _Requirements: 7.1, 7.2, 7.3, 7.4_
  - _预计时间: 4小时_

- [ ]* 6.2 编写搜索筛选属性测试
  - **Property 22: 商品搜索名称匹配**
  - **Validates: Requirements 7.1**
  - **Property 23: 分类筛选正确性**
  - **Validates: Requirements 7.2**
  - **Property 24: 价格区间筛选**
  - **Validates: Requirements 7.4**
  - _预计时间: 2小时_

- [ ] 7. SKU 多规格管理
- [ ] 7.1 实现 SkuService
  - 创建 SKU
  - 更新 SKU
  - 删除 SKU（验证无订单）
  - 查询商品的所有 SKU
  - SKU 属性管理
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_
  - _预计时间: 5小时_

- [ ]* 7.2 编写 SKU 管理属性测试
  - **Property 25: SKU删除限制**
  - **Validates: Requirements 8.5**
  - _预计时间: 1小时_

- [ ] 7.3 实现 SkuController
  - POST /sku - 创建 SKU
  - PUT /sku - 更新 SKU
  - DELETE /sku/{id} - 删除 SKU
  - GET /sku/product/{productId} - 查询商品SKU
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_
  - _预计时间: 2小时_

- [ ] 8. 库存管理
- [ ] 8.1 实现 StockService
  - 增加库存（记录日志）
  - 减少库存（验证充足）
  - 查询库存
  - 库存预警检测
  - 自动售罄处理
  - _Requirements: 4.1, 4.2, 4.3, 4.4_
  - _预计时间: 5小时_

- [ ]* 8.2 编写库存管理属性测试
  - **Property 13: 库存增加正确性**
  - **Validates: Requirements 4.1**
  - **Property 14: 库存减少验证**
  - **Validates: Requirements 4.2**
  - **Property 15: 库存预警标记**
  - **Validates: Requirements 4.3**
  - **Property 16: 库存为零自动售罄**
  - **Validates: Requirements 4.4**
  - _预计时间: 2小时_

- [ ] 8.2 实现订单扣库存逻辑
  - 监听订单支付成功事件
  - 扣减商品库存
  - 使用分布式锁防止超卖
  - _Requirements: 4.5_
  - _预计时间: 3小时_

- [ ]* 8.3 编写订单扣库存属性测试
  - **Property 17: 订单扣库存正确性**
  - **Validates: Requirements 4.5**
  - _预计时间: 1小时_

- [ ] 8.4 实现 StockController
  - PUT /stock/increase - 增加库存
  - PUT /stock/decrease - 减少库存
  - GET /stock/{productId} - 查询库存
  - GET /stock/low - 查询低库存商品
  - GET /stock/log/{productId} - 查询库存日志
  - _Requirements: 4.1, 4.2, 4.3_
  - _预计时间: 2小时_

- [ ] 9. 商品上下架管理
- [ ] 9.1 实现商品状态管理
  - 上架商品（验证信息完整、库存充足）
  - 下架商品
  - 草稿商品禁止上架
  - 零库存商品禁止上架
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_
  - _预计时间: 3小时_

- [ ]* 9.2 编写状态管理属性测试
  - **Property 20: 草稿商品禁止上架**
  - **Validates: Requirements 6.3**
  - **Property 21: 零库存商品禁止上架**
  - **Validates: Requirements 6.4**
  - _预计时间: 1小时_

- [ ] 9.3 实现状态管理接口
  - PUT /product/status/{id} - 更新商品状态
  - _Requirements: 6.1, 6.2_
  - _预计时间: 1小时_

- [ ] 10. 虚拟商品 CDKEY 管理
- [ ] 10.1 实现 CdkeyService 核心功能
  - 批量导入 CDKEY（解析文件、验证格式）
  - CDKEY 自动分配（使用乐观锁）
  - CDKEY 使用状态更新
  - CDKEY 库存为零自动售罄
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  - _预计时间: 6小时_

- [ ]* 10.2 编写 CDKEY 管理属性测试
  - **Property 6: CDKEY批量导入解析**
  - **Validates: Requirements 2.2**
  - **Property 7: CDKEY格式错误检测**
  - **Validates: Requirements 2.3**
  - **Property 8: CDKEY自动分配唯一性**
  - **Validates: Requirements 2.4**
  - **Property 9: CDKEY库存为零自动售罄**
  - **Validates: Requirements 2.5**
  - _预计时间: 2小时_

- [ ] 10.3 实现 CdkeyController
  - POST /cdkey/import - 批量导入
  - GET /cdkey/page - 分页查询
  - GET /cdkey/{id} - 查询详情
  - GET /cdkey/product/{productId} - 查询商品CDKEY
  - _Requirements: 2.2, 2.3_
  - _预计时间: 2小时_

- [ ] 11. 价格管理
- [ ] 11.1 实现价格变更历史记录
  - 监听商品价格变更
  - 记录价格历史
  - 查询价格历史
  - _Requirements: 3.2_
  - _预计时间: 2小时_

- [ ]* 11.2 编写价格历史属性测试
  - **Property 11: 价格变更历史记录**
  - **Validates: Requirements 3.2**
  - _预计时间: 1小时_

- [ ] 12. 分销佣金配置
- [ ] 12.1 实现 CommissionConfigService
  - 创建商品佣金配置
  - 创建分类默认佣金配置
  - 更新佣金配置
  - 查询商品佣金配置（含继承）
  - 验证佣金比例（范围、递减）
  - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 12.1, 12.3_
  - _预计时间: 5小时_

- [ ]* 12.2 编写佣金配置属性测试
  - **Property 26: 佣金比例范围验证**
  - **Validates: Requirements 11.2**
  - **Property 27: 佣金递减规则**
  - **Validates: Requirements 11.3, 11.4**
  - **Property 28: 默认佣金继承**
  - **Validates: Requirements 11.5**
  - _预计时间: 2小时_

- [ ] 12.3 实现 CommissionConfigController
  - POST /commission/config - 创建配置
  - PUT /commission/config - 更新配置
  - GET /commission/config/{productId} - 查询商品佣金
  - GET /commission/config/category/{categoryId} - 查询分类佣金
  - POST /commission/config/batch - 批量设置
  - _Requirements: 11.1, 11.2, 11.3, 11.4, 12.1_
  - _预计时间: 2小时_

- [ ] 13. 分销商商品查询
- [ ] 13.1 实现分销商商品列表
  - 查询已上架且有库存的商品
  - 显示佣金比例和预期收益
  - 按佣金率排序
  - 筛选高佣金商品
  - _Requirements: 14.1, 14.2, 14.3, 14.4_
  - _预计时间: 4小时_

- [ ]* 13.2 编写分销商查询属性测试
  - **Property 29: 分销商商品筛选**
  - **Validates: Requirements 14.1**
  - _预计时间: 1小时_

- [ ] 13.3 实现推广链接生成
  - 生成包含商品ID和分销商ID的链接
  - _Requirements: 14.5_
  - _预计时间: 1小时_

- [ ]* 13.4 编写推广链接属性测试
  - **Property 30: 推广链接包含ID**
  - **Validates: Requirements 14.5**
  - _预计时间: 1小时_

- [ ] 14. 订单佣金计算集成
- [ ] 14.1 实现订单完成监听
  - 监听订单完成事件
  - 查询订单商品佣金配置
  - 查询买家分销关系链
  - _Requirements: 15.1, 15.2_
  - _预计时间: 3小时_

- [ ]* 14.2 编写订单佣金属性测试
  - **Property 31: 订单佣金计算触发**
  - **Validates: Requirements 15.1**
  - **Property 32: 分销关系链查询**
  - **Validates: Requirements 15.2**
  - _预计时间: 2小时_

- [ ] 14.3 实现佣金计算逻辑
  - 根据商品佣金配置计算佣金
  - 根据分销商等级调整佣金
  - 调用分销系统接口创建佣金记录
  - 商品未配置佣金时跳过
  - _Requirements: 15.3, 15.4, 15.5_
  - _预计时间: 4小时_

- [ ]* 14.4 编写佣金计算属性测试
  - **Property 33: CDKEY分销来源记录**
  - **Validates: Requirements 16.1**
  - **Property 34: CDKEY使用触发佣金**
  - **Validates: Requirements 16.2**
  - **Property 35: CDKEY过期取消佣金**
  - **Validates: Requirements 16.5**
  - _预计时间: 2小时_

- [ ] 15. CDKEY 分销追踪
- [ ] 15.1 实现 CDKEY 分销来源记录
  - 订单下单时记录分销商ID
  - CDKEY 分配时关联分销商
  - CDKEY 使用时触发佣金结算
  - CDKEY 过期时取消待结算佣金
  - _Requirements: 16.1, 16.2, 16.5_
  - _预计时间: 4小时_

- [ ] 15.2 实现 CDKEY 独立佣金配置
  - 批量导入时支持设置佣金
  - 查询 CDKEY 详情显示佣金配置
  - _Requirements: 16.4_
  - _预计时间: 2小时_

- [ ] 16. 统计分析功能
- [ ] 16.1 实现商品统计
  - 商品总览（总数、上架数、下架数、售罄数）
  - 分类统计（每个分类商品数量）
  - 库存统计（低库存、售罄商品）
  - 销量统计（销售数量、销售额）
  - _Requirements: 9.1, 9.2, 9.3, 9.4_
  - _预计时间: 4小时_

- [ ] 16.2 实现分销数据统计
  - 商品分销订单数和销售额
  - 商品总佣金和佣金率
  - 推广排行榜
  - 利润分析（成本、售价、佣金、利润）
  - 佣金成本预警
  - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5_
  - _预计时间: 5小时_

- [ ] 16.3 实现数据导出
  - 导出商品数据为 Excel
  - _Requirements: 9.5_
  - _预计时间: 2小时_

- [ ] 16.4 实现 StatisticsController
  - GET /statistics/overview - 商品总览
  - GET /statistics/category - 分类统计
  - GET /statistics/stock - 库存统计
  - GET /statistics/distribution/{productId} - 分销统计
  - GET /statistics/profit/{productId} - 利润分析
  - POST /statistics/export - 导出数据
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 13.1, 13.2, 13.3, 13.4, 13.5_
  - _预计时间: 3小时_

- [ ] 17. 批量操作功能
- [ ] 17.1 实现批量操作
  - 批量上架商品
  - 批量下架商品
  - 批量修改分类
  - 批量删除商品
  - 批量操作错误处理
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_
  - _预计时间: 4小时_

- [ ]* 17.2 编写批量操作属性测试
  - 测试批量操作的正确性
  - 测试部分失败的错误处理
  - _预计时间: 2小时_

- [ ] 17.3 实现批量操作接口
  - POST /product/batch/status - 批量更新状态
  - POST /product/batch/category - 批量修改分类
  - DELETE /product/batch - 批量删除
  - _Requirements: 10.1, 10.2, 10.3, 10.4_
  - _预计时间: 2小时_

- [ ] 18. 缓存实现
- [ ] 18.1 实现商品信息缓存
  - 商品详情缓存
  - 分类树缓存
  - 库存缓存
  - 缓存更新策略
  - _预计时间: 3小时_

- [x] 19. 前端页面开发
- [x] 19.1 商品管理页面
  - 商品列表（搜索、筛选、分页）
  - 商品创建表单
  - 商品编辑表单
  - 商品详情展示
  - 图片上传组件
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 3.1, 3.2, 3.3, 3.4, 7.1, 7.2, 7.3, 7.4, 7.5_
  - _预计时间: 8小时_

- [x] 19.2 分类管理页面
  - 分类树展示
  - 分类创建/编辑对话框
  - 分类删除确认
  - _Requirements: 5.1, 5.2, 5.3_
  - _预计时间: 4小时_

- [x] 19.3 SKU 管理页面
  - SKU 列表
  - SKU 创建/编辑表单
  - SKU 属性配置
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_
  - _预计时间: 5小时_

- [x] 19.4 CDKEY 管理页面
  - CDKEY 列表
  - CDKEY 批量导入
  - CDKEY 详情查看
  - _Requirements: 2.2, 2.3_
  - _预计时间: 4小时_

- [x] 19.5 库存管理页面
  - 库存列表
  - 库存增减操作
  - 库存日志查看
  - 低库存预警展示
  - _Requirements: 4.1, 4.2, 4.3_
  - _预计时间: 4小时_

- [x] 19.6 佣金配置页面
  - 商品佣金配置表单
  - 分类默认佣金配置
  - 佣金配置列表
  - _Requirements: 11.1, 11.2, 11.3, 11.4, 12.1_
  - _预计时间: 4小时_

- [x] 19.7 统计分析页面
  - 商品总览仪表盘
  - 分类统计图表
  - 库存统计图表
  - 分销数据统计
  - 利润分析图表
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 13.1, 13.2, 13.3, 13.4, 13.5_
  - _预计时间: 6小时_

- [x] 19.8 分销商商品页面
  - 可推广商品列表
  - 商品详情（含佣金信息）
  - 推广链接生成
  - 按佣金排序和筛选
  - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5_
  - _预计时间: 5小时_

- [ ] 20. API 接口文档
- [ ] 20.1 编写 Swagger 注解
  - 为所有 Controller 添加 @Tag 注解
  - 为所有接口添加 @Operation 注解
  - 为所有参数添加 @Parameter 注解
  - _预计时间: 3小时_

- [ ] 21. 权限控制
- [ ] 21.1 添加后端权限注解
  - 为管理接口添加 @PreAuthorize 注解
  - 为分销商接口添加权限控制
  - _预计时间: 2小时_

- [ ] 21.2 添加前端按钮权限
  - 使用 v-auth 指令控制按钮显示
  - _预计时间: 2小时_

- [x] 22. 菜单配置
- [x] 22.1 创建菜单 SQL
  - 一级菜单：商品管理
  - 二级菜单：商品列表、分类管理、库存管理、统计分析
  - 按钮权限配置
  - _预计时间: 1小时_

- [x] 22.2 配置角色菜单关联
  - 为管理员角色分配商品管理权限
  - _预计时间: 0.5小时_

- [x] 23. 网关路由配置
- [x] 23.1 添加商品服务路由
  - 在 Nacos 配置中心添加路由配置
  - 配置限流规则
  - _预计时间: 0.5小时_

- [ ] 24. 最终测试
- [ ] 24.1 集成测试
  - 测试完整的商品创建流程
  - 测试 CDKEY 导入和分配流程
  - 测试订单佣金计算流程
  - 测试批量操作
  - _预计时间: 4小时_

- [ ] 24.2 性能测试
  - 测试商品列表查询性能
  - 测试库存扣减并发性能
  - 测试 CDKEY 分配并发性能
  - _预计时间: 3小时_

- [ ] 24.3 用户验收测试
  - 管理员功能测试
  - 分销商功能测试
  - 前后端联调测试
  - _预计时间: 4小时_

---

## 任务依赖关系

```
1 → 2 → 3 → 4 → 5 → 6
              ↓   ↓
              7   8 → 9
              ↓   ↓
             10  11
              ↓
             12 → 13 → 14 → 15
              ↓
             16 → 17
              ↓
             18 → 19 → 20 → 21 → 22 → 23 → 24
```

## 里程碑

**里程碑 1**: 基础功能完成（任务 1-11）
- 商品、分类、SKU、库存基础管理
- 预计完成时间: 第 1-2 周

**里程碑 2**: 分销集成完成（任务 12-17）
- 佣金配置、分销商查询、订单佣金计算
- 预计完成时间: 第 2-3 周

**里程碑 3**: 前端和测试完成（任务 18-24）
- 前端页面、权限控制、集成测试
- 预计完成时间: 第 3-4 周

---

**文档版本**: v1.0  
**创建时间**: 2025-12-09  
**最后更新**: 2025-12-09
