# 🎯 下一步行动清单

**当前状态**: ✅ 代码集成100%完成  
**下一步**: 启动服务进行测试

---

## 📋 快速启动步骤

### 1. 编译项目 (5分钟)

```bash
cd pig
mvn clean install -DskipTests
```

### 2. 初始化数据库 (2分钟)

```bash
# 进入数据库目录
cd pig/db

# 初始化数据库
mysql -u root -p < pig.sql
mysql -u root -p < pig_config.sql
mysql -u root -p < test-data/test_data.sql
```

### 3. 启动服务 (10分钟)

**按顺序启动以下服务**：

```bash
# 1. Nacos (8848)
cd pig/pig-register
mvn spring-boot:run

# 2. Gateway (9999) - 新终端
cd pig/pig-gateway
mvn spring-boot:run

# 3. UPMS (4000) - 新终端
cd pig/pig-upms/pig-upms-biz
mvn spring-boot:run

# 4. Auth (3000) - 新终端
cd pig/pig-auth
mvn spring-boot:run

# 5. Distribution (4200) - 新终端
cd pig/pig-distribution/pig-distribution-biz
mvn spring-boot:run

# 6. Product (4300) - 新终端
cd pig/pig-product/pig-product-biz
mvn spring-boot:run

# 7. 前端 (8888) - 新终端
cd pig-ui
npm run dev
```

### 4. 测试验证 (15分钟)

#### 测试场景 1: 普通订单
1. 访问 http://localhost:8888
2. 登录 (admin/admin)
3. 访问商城 http://localhost:8888/#/shop
4. 购买商品 → 支付
5. ✅ 验证订单正常

#### 测试场景 2: 推广订单 ⭐ 重点
1. 访问推广链接: http://localhost:8888/#/shop/product/1?distributorId=1
2. 立即购买 → 支付
3. ✅ 验证佣金计算触发
4. ✅ 查看数据库佣金记录

```sql
-- 查询佣金记录
SELECT * FROM order_commission ORDER BY create_time DESC LIMIT 5;

-- 查询分销商余额
SELECT id, user_id, total_commission, available_balance FROM distributor;
```

---

## 📊 验证清单

- [ ] 所有服务启动成功
- [ ] Nacos 中看到所有服务注册
- [ ] 前端可以正常访问
- [ ] 普通订单流程正常
- [ ] 推广订单触发佣金计算
- [ ] 佣金记录正确生成
- [ ] 分销商余额正确更新

---

## 🔍 关键日志

**Product 服务日志**:
```
订单支付成功，开始计算佣金，订单号：ORDER123，分销商ID：1
订单 ORDER123 佣金计算成功
```

**Distribution 服务日志**:
```
开始计算佣金: orderId=1, buyerId=1, amount=999.00
佣金计算完成，共生成 1 条佣金记录
```

---

## 📚 详细文档

- **部署测试指南**: `pig/.kiro/specs/shop-system/DEPLOYMENT_AND_TEST_GUIDE.md`
- **集成完成总结**: `pig/.kiro/specs/shop-system/INTEGRATION_COMPLETE_SUMMARY.md`
- **佣金集成指南**: `pig/.kiro/specs/shop-system/COMMISSION_INTEGRATION_GUIDE.md`

---

## ⚠️ 常见问题

### 问题 1: 服务启动失败
- 确认 Nacos 已启动
- 检查端口是否被占用
- 查看服务日志

### 问题 2: 佣金计算不触发
- 确认订单有 distributorId
- 确认 Distribution 服务已启动
- 查看 Product 服务日志

### 问题 3: 编译失败
- 先编译父项目: `mvn clean install -DskipTests`
- 再编译子模块

---

**准备好了吗？开始测试吧！** 🚀
