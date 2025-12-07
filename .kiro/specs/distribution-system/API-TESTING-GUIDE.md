# 分销系统 API 测试指南

**版本**: v1.0  
**更新时间**: 2025-12-07

---

## 🚀 快速开始

### 1. 启动服务

```bash
# 1. 启动 Nacos (8848)
cd pig/pig-register
mvn spring-boot:run

# 2. 启动网关 (9999)
cd pig/pig-gateway
mvn spring-boot:run

# 3. 启动 UPMS (4000)
cd pig/pig-upms/pig-upms-biz
mvn spring-boot:run

# 4. 启动 Auth (3000)
cd pig/pig-auth
mvn spring-boot:run

# 5. 启动 Distribution (4201)
cd pig/pig-distribution/pig-distribution-biz
mvn spring-boot:run
```

### 2. 访问 Swagger 文档

- **直接访问**: http://localhost:4201/doc.html
- **通过网关**: http://localhost:9999/distribution/doc.html

---

## 📋 API 接口清单

### 分销商管理 (DistributorController)

#### 1. 申请成为分销商
```http
POST /distribution/distributor/apply
Authorization: Bearer {token}
Content-Type: application/json

{
  "parentId": 1,
  "realName": "张三",
  "phone": "13800138000",
  "idCard": "110101199001011234"
}
```

#### 2. 获取当前分销商信息
```http
GET /distribution/distributor/info
Authorization: Bearer {token}
```

#### 3. 分页查询分销商列表
```http
GET /distribution/distributor/page?current=1&size=10&status=1
Authorization: Bearer {token}
```

#### 4. 查询分销商详情
```http
GET /distribution/distributor/{id}
Authorization: Bearer {token}
```

#### 5. 审核分销商
```http
PUT /distribution/distributor/audit/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": 1,
  "remark": "审核通过"
}
```

#### 6. 更新分销商
```http
PUT /distribution/distributor
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "level": 2,
  "status": 1
}
```

---

### 佣金管理 (CommissionController)

#### 1. 分页查询佣金列表
```http
GET /distribution/commission/page?current=1&size=10
Authorization: Bearer {token}
```

#### 2. 获取佣金统计
```http
GET /distribution/commission/stats
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "totalCommission": 1000.00,
    "pendingCommission": 200.00,
    "settledCommission": 800.00
  }
}
```

#### 3. 计算订单佣金
```http
POST /distribution/commission/calculate
  ?orderId=1001
  &orderNo=O20251207001
  &buyerId=100
  &orderAmount=1000.00
Authorization: Bearer {token}
```

#### 4. 结算佣金
```http
PUT /distribution/commission/settle/{commissionId}
Authorization: Bearer {token}
```

---

### 提现管理 (WithdrawController)

#### 1. 分页查询提现列表
```http
GET /distribution/withdraw/page?current=1&size=10&status=0
Authorization: Bearer {token}
```

#### 2. 申请提现
```http
POST /distribution/withdraw/apply
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 100.00,
  "bankName": "工商银行",
  "bankAccount": "6222021234567890",
  "accountName": "张三"
}
```

**响应示例**:
```json
{
  "code": 0,
  "msg": "申请成功",
  "data": "W1234567890123456"
}
```

#### 3. 审核提现
```http
PUT /distribution/withdraw/audit/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": 1,
  "remark": "审核通过"
}
```

**状态说明**:
- `1` - 审核通过
- `3` - 审核拒绝

#### 4. 确认打款
```http
PUT /distribution/withdraw/pay/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "payNo": "P1234567890"
}
```

---

### 佣金配置 (ConfigController)

#### 1. 分页查询配置列表
```http
GET /distribution/config/page?current=1&size=10
Authorization: Bearer {token}
```

#### 2. 查询所有配置
```http
GET /distribution/config/list
Authorization: Bearer {token}
```

#### 3. 查询配置详情
```http
GET /distribution/config/{id}
Authorization: Bearer {token}
```

#### 4. 新增配置
```http
POST /distribution/config
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "一级分销-普通会员",
  "level": 1,
  "commissionRate": 10.00,
  "distributorLevel": 1,
  "status": 1,
  "remark": "一级分销商10%佣金"
}
```

#### 5. 修改配置
```http
PUT /distribution/config
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "commissionRate": 12.00
}
```

#### 6. 删除配置
```http
DELETE /distribution/config/{id}
Authorization: Bearer {token}
```

---

## 🧪 测试场景

### 场景 1: 完整的分销流程

#### 步骤 1: 用户A申请成为分销商
```http
POST /distribution/distributor/apply
{
  "parentId": null,
  "realName": "用户A",
  "phone": "13800138001"
}
```

#### 步骤 2: 管理员审核通过
```http
PUT /distribution/distributor/audit/1
{
  "status": 1,
  "remark": "审核通过"
}
```

#### 步骤 3: 用户B申请成为分销商（推荐人：用户A）
```http
POST /distribution/distributor/apply
{
  "parentId": 1,
  "realName": "用户B",
  "phone": "13800138002"
}
```

#### 步骤 4: 用户B下单，计算佣金
```http
POST /distribution/commission/calculate
?orderId=1001&orderNo=O001&buyerId=2&orderAmount=1000.00
```

**预期结果**:
- 用户A获得一级佣金：100元（10%）

#### 步骤 5: 结算佣金
```http
PUT /distribution/commission/settle/1
```

#### 步骤 6: 用户A申请提现
```http
POST /distribution/withdraw/apply
{
  "amount": 100.00,
  "bankName": "工商银行",
  "bankAccount": "6222021234567890",
  "accountName": "用户A"
}
```

#### 步骤 7: 管理员审核提现
```http
PUT /distribution/withdraw/audit/1
{
  "status": 1,
  "remark": "审核通过"
}
```

#### 步骤 8: 财务确认打款
```http
PUT /distribution/withdraw/pay/1
{
  "payNo": "P1234567890"
}
```

---

### 场景 2: 三级分销

#### 初始化数据
```
用户A (ID:1) - 一级分销商
  └─ 用户B (ID:2) - 二级分销商
      └─ 用户C (ID:3) - 三级分销商
```

#### 用户C下单
```http
POST /distribution/commission/calculate
?orderId=2001&orderNo=O002&buyerId=3&orderAmount=1000.00
```

**预期佣金分配**:
- 用户B（一级）：100元（10%）
- 用户A（二级）：50元（5%）

---

### 场景 3: 提现拒绝

#### 步骤 1: 申请提现
```http
POST /distribution/withdraw/apply
{
  "amount": 100.00,
  "bankName": "工商银行",
  "bankAccount": "6222021234567890",
  "accountName": "张三"
}
```

**余额变化**:
- 可用余额：1000 → 900
- 冻结余额：0 → 100

#### 步骤 2: 审核拒绝
```http
PUT /distribution/withdraw/audit/1
{
  "status": 3,
  "remark": "银行账号错误"
}
```

**余额变化**:
- 可用余额：900 → 1000
- 冻结余额：100 → 0

---

## 🔍 数据验证

### 1. 分销关系验证

查询分销关系表：
```sql
SELECT * FROM dist_relation WHERE distributor_id = 3;
```

**预期结果**:
```
| distributor_id | ancestor_id | level |
|----------------|-------------|-------|
| 3              | 2           | 1     |
| 3              | 1           | 2     |
```

### 2. 佣金记录验证

查询佣金记录：
```sql
SELECT * FROM dist_order_commission WHERE order_id = 1001;
```

**预期结果**:
```
| order_id | distributor_id | level | commission_amount | status |
|----------|----------------|-------|-------------------|--------|
| 1001     | 1              | 1     | 100.00            | 0      |
```

### 3. 余额验证

查询分销商余额：
```sql
SELECT 
  id,
  total_commission,
  available_commission,
  frozen_commission,
  withdrawn_commission
FROM dist_distributor WHERE id = 1;
```

---

## ⚠️ 常见问题

### 1. 401 Unauthorized

**原因**: Token 未传递或已过期

**解决**:
```bash
# 1. 登录获取 token
POST /auth/oauth2/token
grant_type=password&username=admin&password=admin

# 2. 在请求头中添加 token
Authorization: Bearer {access_token}
```

### 2. 404 Not Found

**原因**: 服务未启动或路由配置错误

**检查**:
```bash
# 1. 检查服务是否启动
curl http://localhost:4201/actuator/health

# 2. 检查 Nacos 注册
http://localhost:8848/nacos

# 3. 检查网关路由
curl http://localhost:9999/actuator/gateway/routes
```

### 3. 500 Internal Server Error

**原因**: 业务逻辑错误或数据库连接失败

**排查**:
```bash
# 1. 查看服务日志
tail -f pig/pig-distribution/pig-distribution-biz/logs/pig-distribution-biz.log

# 2. 检查数据库连接
mysql -u root -p -e "SELECT 1"

# 3. 检查 Nacos 配置
http://localhost:8848/nacos
```

---

## 📊 性能测试

### 1. 佣金计算性能

使用 JMeter 或 ab 工具：
```bash
ab -n 1000 -c 10 \
  -H "Authorization: Bearer {token}" \
  "http://localhost:9999/distribution/commission/calculate?orderId=1&orderNo=O001&buyerId=1&orderAmount=1000"
```

**预期指标**:
- 响应时间：< 500ms
- 吞吐量：> 100 req/s
- 错误率：< 1%

### 2. 并发提现测试

模拟多个用户同时提现：
```bash
# 使用 JMeter 并发测试
# 线程数：50
# 循环次数：10
# 预期：余额扣除正确，无超额提现
```

---

## 📝 测试清单

### 功能测试
- [ ] 分销商申请
- [ ] 分销商审核
- [ ] 分销关系建立
- [ ] 佣金计算
- [ ] 佣金结算
- [ ] 提现申请
- [ ] 提现审核
- [ ] 提现打款
- [ ] 配置管理

### 异常测试
- [ ] 重复申请分销商
- [ ] 余额不足提现
- [ ] 提现金额小于最小值
- [ ] 审核已审核的记录
- [ ] 打款已打款的记录

### 性能测试
- [ ] 佣金计算性能
- [ ] 并发提现测试
- [ ] 大数据量查询

### 安全测试
- [ ] 未登录访问
- [ ] 越权访问（查询他人数据）
- [ ] SQL 注入测试
- [ ] XSS 测试

---

**文档版本**: v1.0  
**创建时间**: 2025-12-07 15:10  
**维护人**: 开发团队
