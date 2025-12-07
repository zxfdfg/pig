# 数据库脚本使用说明

## 📁 文件说明

- `pig.sql` - 主数据库初始化脚本（包含所有表结构和初始数据）
- `pig_config.sql` - 配置数据库初始化脚本
- `migrations/` - 数据库版本迁移脚本目录（增量更新）

## 🚀 使用场景

### 场景1：全新安装（开发环境/测试环境）

**适用于**：第一次安装、本地开发、测试环境

```bash
# 会删除并重建整个数据库
mysql -u root -p < pig.sql
mysql -u root -p < pig_config.sql
```

⚠️ **警告**：这会删除所有现有数据！

---

### 场景2：更新表结构（生产环境/有数据的环境）

**适用于**：生产环境、已有数据、只需要更新表结构

#### 方法1：使用迁移脚本（推荐）

```bash
# 执行特定版本的迁移脚本
mysql -u root -p pig < migrations/V1.0.1__add_distribution_tables.sql
```

#### 方法2：手动执行 ALTER 语句

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

#### 方法3：只更新特定表（谨慎使用）

如果确定某个表可以重建（没有重要数据），可以单独执行该表的 DDL：

```sql
-- 连接到数据库
USE pig;

-- 删除并重建特定表
DROP TABLE IF EXISTS dist_commission_config;
CREATE TABLE `dist_commission_config` (
  -- ... 完整的表结构
);
```

---

## 📝 数据库迁移脚本规范

### 命名规范

迁移脚本统一放在 `migrations/` 目录，命名格式：

```
V{版本号}__{描述}.sql
```

示例：
- `V1.0.0__init_database.sql` - 初始化数据库
- `V1.0.1__add_distribution_tables.sql` - 添加分销系统表
- `V1.0.2__add_distributor_level_field.sql` - 添加分销商等级字段
- `V1.1.0__modify_commission_rate_precision.sql` - 修改佣金比例精度

### 脚本内容规范

每个迁移脚本应该：

1. **幂等性**：可以重复执行不会出错
2. **向下兼容**：不破坏现有数据
3. **包含回滚**：提供回滚方案（注释形式）

示例：

```sql
-- ========================================
-- 版本: V1.0.2
-- 描述: 添加分销商等级字段
-- 日期: 2025-12-07
-- 作者: 开发团队
-- ========================================

-- 升级脚本
ALTER TABLE dist_distributor 
  ADD COLUMN vip_level INT DEFAULT 0 COMMENT 'VIP等级：0-普通 1-VIP1 2-VIP2';

-- 回滚脚本（注释形式，需要时手动执行）
-- ALTER TABLE dist_distributor DROP COLUMN vip_level;
```

---

## 🔄 版本管理工具（可选）

对于大型项目，建议使用专业的数据库版本管理工具：

### Flyway（推荐）

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### Liquibase

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
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

## 📊 当前数据库版本

| 版本 | 日期 | 描述 | 脚本 |
|------|------|------|------|
| 1.0.0 | 2025-12-07 | 初始化数据库 | pig.sql |
| 1.0.1 | 2025-12-07 | 添加分销系统表 | 已集成到 pig.sql |

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
