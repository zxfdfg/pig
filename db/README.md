# 数据库脚本使用说明

> ⚠️ **重要**: 请先阅读 [DATABASE_STANDARDS.md](./DATABASE_STANDARDS.md) 了解数据库开发规范

## 📁 文件说明

- `pig.sql` - 主数据库初始化脚本（包含所有表结构和初始数据）
- `pig_config.sql` - 配置数据库初始化脚本
- `test-data/test_data.sql` - 测试数据脚本（仅用于开发环境）
- `DATABASE_STANDARDS.md` - 数据库开发规范（必读）
- `migrations/` - ~~数据库版本迁移脚本目录~~（已废弃，不再使用）

## 🚀 使用场景

### 场景1：开发环境完整初始化

**适用于**：第一次安装、本地开发、测试环境

```bash
# 1. 初始化表结构和配置
mysql -u root -p < pig.sql
mysql -u root -p < pig_config.sql

# 2. 加载测试数据（可选）
mysql -u root -p < test-data/test_data.sql
```

⚠️ **警告**：这会删除所有现有数据！

---

### 场景2：只重置测试数据

**适用于**：开发过程中需要重置测试数据

```bash
# 只重新加载测试数据，不影响表结构
mysql -u root -p pig < test-data/test_data.sql
```

---

### 场景3：生产环境初始化

**适用于**：生产环境首次部署

```bash
# 只执行表结构和配置，不执行测试数据
mysql -u root -p < pig.sql
mysql -u root -p < pig_config.sql
```

---

### 场景4：生产环境表结构更新

**适用于**：生产环境、已有数据、只需要更新表结构

#### 方法1：手动执行 ALTER 语句（推荐）

如果只是修改某个表的字段，可以单独执行 ALTER 语句：

```sql
-- 连接到数据库
USE pig;

-- 添加字段
ALTER TABLE dist_distributor ADD COLUMN new_field VARCHAR(50) DEFAULT NULL COMMENT '新字段';

-- 修改字段
ALTER TABLE dist_distributor MODIFY COLUMN phone VARCHAR(30) DEFAULT NULL COMMENT '联系电话';

-- 删除字段
ALTER TABLE dist_distributor DROP COLUMN old_field;
```

#### 方法2：重建测试环境（开发环境）

如果是开发/测试环境，可以直接重建：

```bash
mysql -u root -p < pig.sql
mysql -u root -p < pig_config.sql
mysql -u root -p < test-data/test_data.sql
```

---

## ⚠️ 注意事项

### 开发环境

- ✅ 可以随时删除重建数据库
- ✅ 使用 `pig.sql` 完整重建
- ✅ 不需要担心数据丢失

### 生产环境

- ❌ **绝对不要**执行 `DROP DATABASE`
- ❌ **绝对不要**执行完整的 `pig.sql`
- ✅ 只执行增量迁移脚本
- ✅ 执行前务必备份数据库
- ✅ 在测试环境验证后再上生产

---

## 📊 数据库模块

| 模块 | 表数量 | 描述 | 状态 |
|------|--------|------|------|
| 系统核心 | 15+ | 用户、角色、菜单、部门等 | ✅ 已完成 |
| 分销系统 | 6 | 分销商、佣金、提现等 | ✅ 已完成 |
| 商品管理 | 7 | 商品、分类、SKU、CDKEY等 | ✅ 已完成 |

**当前版本**: v1.0.2  
**最后更新**: 2025-12-09

---

## 🛠️ 常用操作

### 备份数据库

```bash
# 备份整个数据库
mysqldump -u root -p pig > backup_$(date +%Y%m%d_%H%M%S).sql

# 只备份表结构
mysqldump -u root -p --no-data pig > schema_backup.sql

# 只备份数据
mysqldump -u root -p --no-create-info pig > data_backup.sql
```

### 恢复数据库

```bash
# 恢复整个数据库
mysql -u root -p pig < backup_20251207_120000.sql
```

### 查看表结构

```sql
-- 查看表结构
DESC dist_distributor;

-- 查看建表语句
SHOW CREATE TABLE dist_distributor;

-- 查看所有表
SHOW TABLES;
```

---

## 📞 问题排查

### 问题1：表已存在

**错误**：`Table 'xxx' already exists`

**原因**：表已经创建过了

**解决**：
1. 如果是开发环境，删除整个数据库重建
2. 如果是生产环境，检查是否需要更新表结构，使用 ALTER 语句

### 问题2：字段已存在

**错误**：`Duplicate column name 'xxx'`

**原因**：字段已经添加过了

**解决**：
1. 检查迁移脚本是否已经执行过
2. 使用 `IF NOT EXISTS` 或先检查字段是否存在

### 问题3：数据丢失

**原因**：执行了 `DROP DATABASE` 或 `DROP TABLE`

**解决**：
1. 从备份恢复
2. 如果没有备份，数据无法恢复
3. **预防**：生产环境定期备份，执行前先备份

---

**最后更新**: 2025-12-07  
**维护人**: 开发团队
