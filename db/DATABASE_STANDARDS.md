# 数据库开发规范

## 📋 核心原则

### 原则1：统一使用 pig.sql 管理表结构

**✅ 正确做法**：
- 所有新表结构直接添加到 `pig.sql` 文件末尾
- 表结构变更直接修改 `pig.sql` 中的对应表定义
- 保持 `pig.sql` 作为唯一的数据库结构真实来源

**❌ 错误做法**：
- ~~创建迁移脚本（migrations/V1.0.x__xxx.sql）~~
- ~~创建单独的模块SQL文件~~
- ~~使用Flyway/Liquibase等迁移工具~~

**原因**：
- 简化数据库管理，避免版本混乱
- 开发环境可以随时删除重建，无需迁移
- 生产环境通过完整的 `pig.sql` 初始化新实例

### 原则2：统一使用 test_data.sql 管理测试数据

**✅ 正确做法**：
- 所有测试数据添加到 `test-data/test_data.sql` 文件
- 按模块组织测试数据（用注释分隔）
- 测试数据包含清空和重置逻辑

**❌ 错误做法**：
- ~~创建单独的测试数据文件（test_product.sql）~~
- ~~在 pig.sql 中混入大量测试数据~~
- ~~创建多个测试数据脚本~~

**原因**：
- 统一管理所有测试数据
- 方便开发环境快速重置
- 测试数据与生产数据分离

---

## 📁 文件结构

```
pig/db/
├── pig.sql                    # ✅ 唯一的表结构定义文件
├── pig_config.sql             # ✅ 配置数据（Nacos配置等）
├── test-data/
│   └── test_data.sql          # ✅ 唯一的测试数据文件
├── migrations/                # ❌ 不再使用（保留用于历史参考）
├── Dockerfile                 # Docker镜像构建
└── README.md                  # 使用说明
```

---

## 🔧 开发流程

### 场景1：新增数据库表

**步骤**：

1. **编辑 pig.sql**
   ```sql
   -- 在文件末尾添加新表
   -- ----------------------------
   -- Table structure for your_new_table
   -- ----------------------------
   DROP TABLE IF EXISTS `your_new_table`;
   CREATE TABLE `your_new_table` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
     `name` VARCHAR(100) NOT NULL COMMENT '名称',
     `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
     `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
     `del_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-正常，1-删除',
     PRIMARY KEY (`id`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='你的表注释';
   
   -- 如果有初始数据
   BEGIN;
   INSERT INTO `your_new_table` (`name`, `create_by`) VALUES ('示例数据', 'admin');
   COMMIT;
   ```

2. **添加测试数据到 test_data.sql**
   ```sql
   -- ========================================
   -- 你的模块测试数据
   -- ========================================
   
   DELETE FROM your_new_table;
   ALTER TABLE your_new_table AUTO_INCREMENT = 1;
   
   INSERT INTO `your_new_table` (`name`, `create_by`) VALUES 
   ('测试数据1', 'admin'),
   ('测试数据2', 'admin');
   ```

3. **重建开发数据库**
   ```bash
   mysql -u root -p < pig/db/pig.sql
   mysql -u root -p < pig/db/pig_config.sql
   mysql -u root -p < pig/db/test-data/test_data.sql
   ```

### 场景2：修改现有表结构

**步骤**：

1. **直接修改 pig.sql 中的表定义**
   ```sql
   -- 找到对应的表定义，直接修改
   CREATE TABLE `your_table` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
     `name` VARCHAR(100) NOT NULL COMMENT '名称',
     `new_field` VARCHAR(50) DEFAULT NULL COMMENT '新增字段', -- ✅ 新增
     -- ...
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表注释';
   ```

2. **重建开发数据库**
   ```bash
   mysql -u root -p < pig/db/pig.sql
   mysql -u root -p < pig/db/pig_config.sql
   mysql -u root -p < pig/db/test-data/test_data.sql
   ```

### 场景3：删除表

**步骤**：

1. **从 pig.sql 中删除表定义**
   - 删除整个表的 `DROP TABLE` 和 `CREATE TABLE` 语句
   - 删除相关的 `INSERT` 初始数据

2. **从 test_data.sql 中删除测试数据**
   - 删除该表的所有测试数据

3. **重建开发数据库**

---

## 📝 表结构规范

### 必备字段

每个表必须包含以下审计字段：

```sql
`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
`update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
`del_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-正常，1-删除'
```

### 命名规范

- **表名**：小写下划线分隔，如 `product_category`
- **字段名**：小写下划线分隔，如 `user_name`
- **主键**：统一使用 `id`
- **外键**：使用 `{表名}_id`，如 `user_id`

### 字段类型

- **主键**：`BIGINT NOT NULL AUTO_INCREMENT`
- **字符串**：`VARCHAR(长度)` 或 `TEXT`
- **数字**：`INT`, `BIGINT`, `DECIMAL(10,2)`
- **日期时间**：`DATETIME`
- **布尔**：`TINYINT`（0/1）
- **JSON**：`JSON`（MySQL 5.7+）

### 索引规范

- **主键索引**：`PRIMARY KEY (id)`
- **唯一索引**：`UNIQUE KEY uk_{字段名} ({字段名})`
- **普通索引**：`KEY idx_{字段名} ({字段名})`
- **组合索引**：`KEY idx_{字段1}_{字段2} ({字段1}, {字段2})`

### 表引擎和字符集

```sql
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表注释'
```

---

## 🧪 测试数据规范

### test_data.sql 结构

```sql
-- ========================================
-- 模块名称测试数据
-- 用于开发环境测试
-- ========================================

-- 清空现有测试数据
DELETE FROM table1;
DELETE FROM table2;

-- 重置自增ID
ALTER TABLE table1 AUTO_INCREMENT = 1;
ALTER TABLE table2 AUTO_INCREMENT = 1;

-- 插入测试数据
INSERT INTO `table1` (...) VALUES (...);
INSERT INTO `table2` (...) VALUES (...);

-- 数据统计（可选）
SELECT '✅ 模块测试数据创建完成！' AS message;
SELECT CONCAT('表1数量: ', COUNT(*)) AS info FROM table1;
SELECT CONCAT('表2数量: ', COUNT(*)) AS info FROM table2;
```

### 测试数据原则

1. **完整性**：覆盖各种状态和场景
2. **关联性**：保持数据之间的关联关系
3. **可重复**：每次执行结果一致
4. **可清理**：包含清空逻辑

---

## 🚫 禁止的做法

### ❌ 不要创建迁移脚本

```
❌ pig/db/migrations/V1.0.2__add_product_tables.sql
❌ pig/db/migrations/V1.0.3__modify_product_fields.sql
```

**原因**：
- 增加维护复杂度
- 容易导致版本混乱
- 开发环境不需要迁移

### ❌ 不要创建多个测试数据文件

```
❌ pig/db/test-data/test_product.sql
❌ pig/db/test-data/test_order.sql
❌ pig/db/test-data/test_user.sql
```

**原因**：
- 难以管理和维护
- 执行顺序难以控制
- 数据关联容易出错

### ❌ 不要在 pig.sql 中混入大量测试数据

```sql
-- ❌ 错误：在 pig.sql 中插入大量测试数据
INSERT INTO product VALUES (1, '测试商品1', ...);
INSERT INTO product VALUES (2, '测试商品2', ...);
-- ... 100条测试数据
```

**原因**：
- pig.sql 应该只包含表结构和必要的初始数据
- 测试数据应该在 test_data.sql 中

---

## 📚 最佳实践

### 1. 开发环境数据库初始化

```bash
# 完整初始化（删除并重建）
mysql -u root -p < pig/db/pig.sql
mysql -u root -p < pig/db/pig_config.sql
mysql -u root -p < pig/db/test-data/test_data.sql
```

### 2. 只重置测试数据

```bash
# 只重新加载测试数据
mysql -u root -p pig < pig/db/test-data/test_data.sql
```

### 3. 生产环境初始化

```bash
# 只执行表结构和配置，不执行测试数据
mysql -u root -p < pig/db/pig.sql
mysql -u root -p < pig/db/pig_config.sql
```

### 4. 备份数据库

```bash
# 备份整个数据库
mysqldump -u root -p pig > backup_$(date +%Y%m%d_%H%M%S).sql

# 只备份表结构
mysqldump -u root -p --no-data pig > schema_backup.sql
```

---

## 🔍 常见问题

### Q1: 生产环境如何更新表结构？

**A**: 生产环境应该通过以下方式更新：

1. **小改动**：使用 ALTER 语句
   ```sql
   ALTER TABLE product ADD COLUMN new_field VARCHAR(50) DEFAULT NULL COMMENT '新字段';
   ```

2. **大改动**：
   - 先在测试环境验证完整的 pig.sql
   - 导出生产数据
   - 重建生产数据库
   - 导入生产数据

### Q2: 如何处理多人协作时的表结构冲突？

**A**: 
1. 使用 Git 管理 pig.sql
2. 提交前先拉取最新代码
3. 解决冲突后再提交
4. 团队成员及时同步最新的 pig.sql

### Q3: 为什么不使用 Flyway/Liquibase？

**A**: 
- 本项目是微服务架构，开发环境可以随时重建
- 迁移工具增加了复杂度，不适合快速迭代
- pig.sql 作为单一真实来源更简单直接
- 生产环境更新频率低，手动管理更可控

---

## ✅ 检查清单

在提交代码前，请确认：

- [ ] 新表已添加到 `pig.sql` 末尾
- [ ] 表结构包含所有必备审计字段
- [ ] 表名、字段名符合命名规范
- [ ] 索引设置合理
- [ ] 测试数据已添加到 `test_data.sql`
- [ ] 测试数据包含清空和重置逻辑
- [ ] 本地已验证数据库初始化成功
- [ ] 没有创建迁移脚本文件
- [ ] 没有创建单独的测试数据文件

---

## 📞 技术支持

如有疑问，请参考：
- 项目文档：`pig/db/README.md`
- 后端规范：`pig/.kiro/steering/project-standards.md`
- 前端规范：`pig-ui/.kiro/steering/project-standards.md`

---

**文档版本**: v1.0  
**创建时间**: 2025-12-09  
**最后更新**: 2025-12-09  
**维护人**: 开发团队
