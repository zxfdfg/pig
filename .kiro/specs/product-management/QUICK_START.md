# 商品管理系统 - 快速启动指南

## 🚀 5分钟快速启动

### 前置条件

确保已安装：
- ✅ JDK 17
- ✅ Node.js 18+
- ✅ MySQL 8.0
- ✅ Redis 6.0
- ✅ Maven 3.8+

---

## 📝 启动步骤

### 1. 初始化数据库（2分钟）

**⚠️ 重要**: 必须按顺序执行以下命令，确保Nacos配置正确加载！

```bash
# 进入数据库目录
cd pig/db

# 初始化数据库（会删除并重建pig数据库）
mysql -u root -p < pig.sql

# 导入配置数据（包含Nacos配置，必须执行！）
mysql -u root -p < pig_config.sql

# 导入测试数据
mysql -u root -p < test-data/test_data.sql
```

**验证数据库**:
```sql
USE pig;
SHOW TABLES LIKE 'product%';
-- 应该看到7张product相关的表
```

**验证Nacos配置**:
```sql
USE pig_config;
SELECT id, data_id FROM config_info WHERE data_id LIKE 'pig-product%';
-- 应该看到: pig-product-biz-dev.yml (id=9)
```

---

### 2. 启动后端服务（3分钟）

**打开5个终端窗口，分别执行：**

#### 终端1: Nacos注册中心
```bash
cd pig/pig-register
mvn spring-boot:run
```
等待启动完成，访问 http://localhost:8848/nacos （用户名/密码: nacos/nacos）

#### 终端2: 网关服务
```bash
cd pig/pig-gateway
mvn spring-boot:run
```
等待启动完成，看到 "Started PigGatewayApplication"

#### 终端3: 认证服务
```bash
cd pig/pig-auth
mvn spring-boot:run
```
等待启动完成

#### 终端4: 用户权限服务
```bash
cd pig/pig-upms/pig-upms-biz
mvn spring-boot:run
```
等待启动完成

#### 终端5: 商品管理服务
```bash
cd pig/pig-product/pig-product-biz
mvn spring-boot:run
```
等待启动完成，看到 "Started ProductApplication"

**验证**: 访问 Nacos控制台，检查服务列表中是否有5个服务都已注册

---

### 3. 启动前端（1分钟）

```bash
cd pig-ui
npm run dev
```

等待启动完成，访问: http://localhost:8888

---

## 🔐 登录系统

1. 打开浏览器访问: http://localhost:8888
2. 输入账号: `admin`
3. 输入密码: `admin`
4. 点击登录

---

## 🎯 验证功能

### 1. 查看菜单

登录后，左侧菜单应该看到：
- 商品管理
  - 商品列表
  - 分类管理
  - SKU管理
  - 库存管理
  - CDKEY管理
  - 佣金配置
  - 统计分析
  - 分销商品

**如果看不到菜单**，执行以下SQL：
```sql
-- 为管理员角色分配商品管理权限
INSERT INTO sys_role_menu VALUES (1, 4000);
INSERT INTO sys_role_menu VALUES (1, 4100);
-- ... 其他菜单权限（参考pig.sql末尾）
```

### 2. 测试分类管理

1. 点击 "商品管理 → 分类管理"
2. 应该看到测试数据中的分类树
3. 点击 "新增根分类"，创建一个新分类
4. 验证创建成功

### 3. 测试商品管理

1. 点击 "商品管理 → 商品列表"
2. 应该看到测试数据中的商品
3. 点击 "新增商品"，创建一个新商品
4. 验证创建成功
5. 点击 "上架"，验证商品状态变更

### 4. 测试库存管理

1. 点击 "商品管理 → 库存管理"
2. 点击某个商品的 "增加库存"
3. 输入数量和备注
4. 验证库存增加成功
5. 点击 "库存日志"，查看操作记录

### 5. 测试统计分析

1. 点击 "商品管理 → 统计分析"
2. 查看商品总览卡片
3. 查看分类统计图表
4. 查看库存统计图表
5. 查看低库存预警列表

---

## 🐛 常见问题

### 问题1: 服务启动失败 - DataSource配置错误

**症状**: 
```
WARN: config[dataId=pig-product-biz-dev.yml, group=DEFAULT_GROUP] is empty
Failed to configure a DataSource: 'url' attribute is not specified
```

**原因**: Nacos配置中心没有加载到 `pig-product-biz-dev.yml` 配置

**解决方案**:
1. **重新导入pig_config.sql**（推荐）:
   ```bash
   mysql -u root -p < pig/db/pig_config.sql
   ```
   然后重启Nacos服务

2. **或手动在Nacos控制台添加配置**:
   - 访问 http://localhost:8848/nacos
   - 登录 (nacos/nacos)
   - 进入 配置管理 → 配置列表
   - 点击 "+" 新建配置
   - Data ID: `pig-product-biz-dev.yml`
   - Group: `DEFAULT_GROUP`
   - 配置格式: YAML
   - 配置内容:
   ```yaml
   spring:
     datasource:
       type: com.zaxxer.hikari.HikariDataSource
       driver-class-name: com.mysql.cj.jdbc.Driver
       username: ${MYSQL_USERNAME:root}
       password: ${MYSQL_PASSWORD:root}
       url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/${MYSQL_DB:pig}?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true
   ```

### 问题2: 端口被占用

**症状**: 服务启动报端口占用错误

**解决方案**:
1. 检查端口是否被占用
   - Nacos: 8848
   - Gateway: 9999
   - Auth: 3000
   - UPMS: 4000
   - Product: 4200
2. 检查MySQL和Redis是否正常运行
3. 检查Nacos是否已启动

### 问题3: 菜单不显示

**症状**: 登录后看不到"商品管理"菜单

**解决方案**:
```sql
-- 检查菜单是否存在
SELECT * FROM sys_menu WHERE menu_id = 4000;

-- 检查角色菜单关联
SELECT * FROM sys_role_menu WHERE menu_id = 4000;

-- 如果没有，重新导入pig.sql
```

### 问题4: 接口404

**症状**: 前端调用接口返回404

**解决方案**:
1. 检查网关服务是否正常运行
2. 检查商品服务是否已注册到Nacos
3. 访问 http://localhost:8848/nacos 查看服务列表
4. 检查浏览器控制台的Network标签，查看实际请求URL

### 问题5: 前端页面报错

**症状**: 前端页面显示错误

**解决方案**:
1. 打开浏览器开发者工具（F12）
2. 查看Console标签的错误信息
3. 查看Network标签的API请求
4. 检查API返回的错误信息

---

## 📊 服务端口一览

| 服务 | 端口 | 访问地址 |
|------|------|----------|
| Nacos | 8848 | http://localhost:8848/nacos |
| Gateway | 9999 | http://localhost:9999 |
| Auth | 3000 | - |
| UPMS | 4000 | - |
| Product | 4200 | - |
| Frontend | 8888 | http://localhost:8888 |

---

## 🔍 API测试

### 获取Token

```bash
curl -X POST "http://localhost:9999/auth/oauth2/token" \
  -H "Authorization: Basic cGlnOnBpZw==" \
  -d "grant_type=password&username=admin&password=admin"
```

### 测试商品接口

```bash
# 查询商品列表
curl -X GET "http://localhost:9999/product/product/page?current=1&size=10" \
  -H "Authorization: Bearer {替换为实际token}"

# 查询分类树
curl -X GET "http://localhost:9999/product/category/tree" \
  -H "Authorization: Bearer {替换为实际token}"
```

---

## 📚 下一步

完成快速启动后，请参考：

1. **完整测试**: `TESTING_GUIDE.md` - 详细的功能测试清单
2. **交付文档**: `DELIVERY.md` - 完整的交付内容和说明
3. **设计文档**: `design.md` - 系统架构和设计细节
4. **任务清单**: `tasks.md` - 开发任务和进度

---

## ✅ 启动成功标志

- [ ] 5个后端服务全部启动成功
- [ ] Nacos控制台显示5个服务已注册
- [ ] 前端成功启动并可访问
- [ ] 成功登录系统
- [ ] 可以看到"商品管理"菜单
- [ ] 可以正常访问各个管理页面
- [ ] API接口调用正常

---

**如果所有步骤都成功，恭喜！系统已经可以正常使用了！** 🎉
