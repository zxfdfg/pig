---
title: 数据库开发规范
category: development
priority: high
---

# 数据库开发规范

## 🎯 核心原则

### 1. 统一使用 pig.sql 管理表结构

**所有新表结构直接添加到 `pig/db/pig.sql` 文件末尾**

```sql
-- 在 pig.sql 末尾添加
-- ----------------------------
-- Table structure for your_new_table
-- ----------------------------
DROP TABLE IF EXISTS `your_new_table`;
CREATE TABLE `your_new_table` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  -- ... 其他字段
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `del_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-正常，1-删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表注释';
```

### 2. 统一使用 test_data.sql 管理测试数据

**所有测试数据添加到 `pig/db/test-data/test_data.sql` 文件**

```sql
-- ========================================
-- 你的模块测试数据
-- ========================================

-- 清空现有测试数据
DELETE FROM your_table;

-- 重置自增ID
ALTER TABLE your_table AUTO_INCREMENT = 1;

-- 插入测试数据
INSERT INTO `your_table` (...) VALUES (...);
```

## ❌ 禁止的做法

### 不要创建迁移脚本

```
❌ pig/db/migrations/V1.0.x__xxx.sql
```

### 不要创建单独的测试数据文件

```
❌ pig/db/test-data/test_product.sql
❌ pig/db/test-data/test_order.sql
```

### 不要在 pig.sql 中混入大量测试数据

```sql
-- ❌ 错误：在 pig.sql 中插入大量测试数据
INSERT INTO product VALUES (1, '测试商品1', ...);
-- ... 100条测试数据
```

## 📋 必备字段

每个表必须包含以下审计字段：

```sql
`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
`update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
`del_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-正常，1-删除'
```

## 🔧 开发流程

### 新增表

1. 编辑 `pig/db/pig.sql`，在末尾添加表定义
2. 编辑 `pig/db/test-data/test_data.sql`，添加测试数据
3. 重建开发数据库：
   ```bash
   mysql -u root -p < pig/db/pig.sql
   mysql -u root -p < pig/db/pig_config.sql
   mysql -u root -p < pig/db/test-data/test_data.sql
   ```

### 修改表

1. 直接修改 `pig/db/pig.sql` 中的表定义
2. 更新 `pig/db/test-data/test_data.sql` 中的测试数据（如需要）
3. 重建开发数据库

### 删除表

1. 从 `pig/db/pig.sql` 中删除表定义
2. 从 `pig/db/test-data/test_data.sql` 中删除测试数据
3. 重建开发数据库

## 📚 详细文档

完整的数据库开发规范请参考：`pig/db/DATABASE_STANDARDS.md`

## ✅ 提交前检查

- [ ] 新表已添加到 `pig.sql` 末尾
- [ ] 表结构包含所有必备审计字段
- [ ] 测试数据已添加到 `test_data.sql`
- [ ] 没有创建迁移脚本文件
- [ ] 没有创建单独的测试数据文件
- [ ] 本地已验证数据库初始化成功

---

**重要**: 这些规范适用于整个项目的数据库开发，所有开发人员必须遵守。
