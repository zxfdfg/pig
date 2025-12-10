-- 检查数据库表是否存在
-- 在 MySQL 中执行这些命令

USE pig;

-- 1. 检查商城系统表
SHOW TABLES LIKE 'shop_%';

-- 2. 检查分销系统表
SHOW TABLES LIKE 'distributor%';
SHOW TABLES LIKE 'order_commission%';

-- 3. 检查测试数据
SELECT COUNT(*) as product_count FROM product;
SELECT COUNT(*) as distributor_count FROM distributor;
SELECT COUNT(*) as user_count FROM sys_user;

-- 4. 查看分销商信息（admin 用户应该是分销商）
SELECT d.id, d.user_id, u.username, d.level, d.status, d.total_commission, d.available_balance
FROM distributor d
LEFT JOIN sys_user u ON d.user_id = u.user_id;

-- 5. 查看商品信息
SELECT id, name, price, stock FROM product LIMIT 5;
