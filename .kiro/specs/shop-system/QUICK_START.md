# 🚀 商城系统快速启动指南

**版本**: v1.0  
**更新时间**: 2025-12-10

---

## 📋 前置条件

- ✅ Java 17
- ✅ Maven 3.6+
- ✅ MySQL 8.0+
- ✅ Node.js 16+
- ✅ Redis（可选）

---

## 🗄️ 步骤1：数据库初始化

### 方式1：全新安装（推荐）

```bash
# 进入数据库目录
cd pig/db

# 初始化数据库表结构
mysql -u root -p < pig.sql

# 导入配置数据
mysql -u root -p < pig_config.sql

# 导入测试数据
mysql -u root -p < test-data/test_data.sql
```

### 方式2：只添加商城表（如果已有数据库）

```sql
-- 手动执行 pig.sql 末尾的商城表结构
-- 从 "商城系统表结构" 注释开始的部分

-- 然后执行测试数据中的商城部分
-- test-data/test_data.sql 中 "商城系统测试数据" 部分
```

---

## 🔧 步骤2：启动后端服务

### 2.1 启动 Nacos 注册中心

```bash
cd pig/pig-register
mvn spring-boot:run
```

访问: http://localhost:8848/nacos  
账号/密码: nacos/nacos

### 2.2 启动网关服务

```bash
cd pig/pig-gateway
mvn spring-boot:run
```

端口: 9999

### 2.3 启动 Product 服务（商城服务）

```bash
cd pig/pig-product/pig-product-biz
mvn spring-boot:run
```

端口: 4300

### 2.4 启动 Auth 服务（可选，如需登录）

```bash
cd pig/pig-auth
mvn spring-boot:run
```

端口: 3000

### 2.5 启动 UPMS 服务（可选，如需用户管理）

```bash
cd pig/pig-upms/pig-upms-biz
mvn spring-boot:run
```

端口: 4000

---

## 💻 步骤3：启动前端

```bash
cd pig-ui

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

访问: http://localhost:8888

---

## 🧪 步骤4：测试商城功能

### 4.1 登录系统

- 访问: http://localhost:8888
- 账号: admin
- 密码: admin

### 4.2 访问商城页面

直接在浏览器访问以下地址：

1. **商品列表**: http://localhost:8888/shop
2. **商品详情**: http://localhost:8888/shop/product/1
3. **购物车**: http://localhost:8888/shop/cart
4. **订单列表**: http://localhost:8888/shop/orders

### 4.3 测试完整购物流程

1. 浏览商品列表
2. 点击商品查看详情
3. 加入购物车
4. 查看购物车
5. 选择商品，去结算
6. 选择收货地址
7. 提交订单
8. 模拟支付
9. 查看订单状态

---

## 🔍 步骤5：验证功能

### 5.1 验证购物车功能

```bash
# 使用 Postman 或 curl 测试

# 添加到购物车
curl -X POST http://localhost:9999/product/shop/cart \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "productId": 1,
    "skuId": 0,
    "quantity": 1
  }'

# 获取购物车列表
curl -X GET http://localhost:9999/product/shop/cart/list \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5.2 验证订单功能

```bash
# 创建订单（立即购买）
curl -X POST http://localhost:9999/product/shop/order/create-direct \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "productId": 1,
    "skuId": 0,
    "quantity": 1,
    "addressId": 1,
    "remark": "测试订单"
  }'

# 查询订单列表
curl -X GET "http://localhost:9999/product/shop/order/page?current=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5.3 验证支付功能

```bash
# 创建支付单
curl -X POST http://localhost:9999/product/shop/payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "orderNo": "ORDER1234567890",
    "payType": 1
  }'

# 模拟支付
curl -X POST http://localhost:9999/product/shop/payment/mock-pay \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "paymentNo": "PAY1234567890"
  }'
```

---

## 📊 步骤6：查看 API 文档

访问 Swagger 文档：

- Product 服务: http://localhost:4300/swagger-ui.html
- 网关代理: http://localhost:9999/product/swagger-ui.html

查看商城相关接口：
- 购物车管理
- 订单管理
- 支付管理
- 收货地址管理

---

## 🐛 常见问题

### 问题1：服务启动失败

**原因**: 端口被占用或数据库连接失败

**解决**:
```bash
# 检查端口占用
netstat -ano | findstr "8848"  # Nacos
netstat -ano | findstr "9999"  # Gateway
netstat -ano | findstr "4300"  # Product

# 检查数据库连接
mysql -u root -p -e "SHOW DATABASES;"
```

### 问题2：前端无法访问后端

**原因**: 网关未启动或跨域配置问题

**解决**:
1. 确保网关服务已启动（9999端口）
2. 检查 `pig-ui/.env.development` 中的 API 地址
3. 检查浏览器控制台是否有跨域错误

### 问题3：登录后无法访问商城

**原因**: 路由未配置或权限不足

**解决**:
1. 检查路由配置 `pig-ui/src/router/index.ts`
2. 确保用户有访问权限
3. 检查浏览器控制台错误信息

### 问题4：订单创建失败

**原因**: 库存不足或地址不存在

**解决**:
1. 检查商品库存是否充足
2. 确保收货地址已创建
3. 查看后端日志获取详细错误信息

---

## 📝 测试数据说明

系统已预置以下测试数据：

### 用户数据
- 用户: admin (ID: 1)
- 密码: admin

### 商品数据
- 11个测试商品（包含虚拟商品和实物商品）
- 商品ID: 1-11
- 部分商品有SKU（如罗技键盘、保温杯）

### 购物车数据
- admin 用户已有3个购物车商品

### 收货地址数据
- admin 用户已有3个收货地址
- 默认地址：深圳市南山区

### 订单数据
- admin 用户已有5个订单
- 状态：待支付、待发货、待收货、已完成、已取消

---

## 🎯 下一步

1. **前后端联调**: 更新前端页面，替换模拟数据为真实API
2. **路由配置**: 在路由文件中添加商城路由
3. **权限控制**: 添加权限注解和按钮权限
4. **分销集成**: 订单支付后触发佣金计算
5. **性能优化**: 添加缓存、优化查询

---

## 📞 技术支持

如遇问题，请检查：

1. ✅ 所有服务是否正常启动
2. ✅ 数据库是否正确初始化
3. ✅ 网关路由是否配置正确
4. ✅ 前端 API 地址是否正确
5. ✅ 浏览器控制台是否有错误

查看日志：
- 后端日志: `pig/logs/`
- 前端控制台: F12 打开开发者工具

---

**祝你使用愉快！** 🎉
