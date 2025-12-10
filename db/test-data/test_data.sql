-- ========================================
-- 分销系统测试数据
-- 用于开发环境测试
-- 每次执行会清空并重新插入数据
-- ========================================

USE pig;

-- ========================================
-- 清空现有测试数据
-- ========================================
DELETE FROM dist_commission_log;
DELETE FROM dist_withdraw;
DELETE FROM dist_order_commission;
DELETE FROM dist_commission_config;
DELETE FROM dist_relation;
DELETE FROM dist_distributor;

-- 重置自增ID
ALTER TABLE dist_commission_log AUTO_INCREMENT = 1;
ALTER TABLE dist_withdraw AUTO_INCREMENT = 1;
ALTER TABLE dist_order_commission AUTO_INCREMENT = 1;
ALTER TABLE dist_commission_config AUTO_INCREMENT = 1;
ALTER TABLE dist_relation AUTO_INCREMENT = 1;
ALTER TABLE dist_distributor AUTO_INCREMENT = 1;

-- ========================================
-- 1. 创建测试分销商（3级分销结构）
-- ========================================

-- admin 作为顶级分销商（user_id=1）⭐ 重要：让 admin 能看到数据
INSERT INTO `dist_distributor` 
(`id`, `user_id`, `parent_id`, `level`, `status`, `total_sales`, `total_commission`, `available_commission`, `frozen_commission`, `withdrawn_commission`, `direct_count`, `team_count`, `real_name`, `phone`, `id_card`, `apply_time`, `audit_time`, `del_flag`, `create_by`, `create_time`) 
VALUES 
(1, 1, NULL, 5, 1, 80000.00, 8000.00, 5000.00, 1000.00, 2000.00, 3, 8, '陈明轩', '13800138000', '110101199001011234', '2025-01-01 10:00:00', '2025-01-01 11:00:00', 0, 'admin', NOW());

-- 二级分销商（user_id=2，上级是admin）
INSERT INTO `dist_distributor` 
(`id`, `user_id`, `parent_id`, `level`, `status`, `total_sales`, `total_commission`, `available_commission`, `frozen_commission`, `withdrawn_commission`, `direct_count`, `team_count`, `real_name`, `phone`, `id_card`, `apply_time`, `audit_time`, `del_flag`, `create_by`, `create_time`) 
VALUES 
(2, 2, 1, 4, 1, 50000.00, 5000.00, 3000.00, 500.00, 1500.00, 2, 5, '李雨婷', '13800138001', '110101199002021234', '2025-01-02 10:00:00', '2025-01-02 11:00:00', 0, 'admin', NOW());

-- 二级分销商（user_id=3，上级是admin）
INSERT INTO `dist_distributor` 
(`id`, `user_id`, `parent_id`, `level`, `status`, `total_sales`, `total_commission`, `available_commission`, `frozen_commission`, `withdrawn_commission`, `direct_count`, `team_count`, `real_name`, `phone`, `id_card`, `apply_time`, `audit_time`, `del_flag`, `create_by`, `create_time`) 
VALUES 
(3, 3, 1, 3, 1, 30000.00, 2500.00, 1800.00, 200.00, 500.00, 1, 2, '王浩然', '13800138002', '110101199003031234', '2025-01-03 10:00:00', '2025-01-03 11:00:00', 0, 'admin', NOW());

-- 三级分销商（user_id=4，上级是李雨婷）
INSERT INTO `dist_distributor` 
(`id`, `user_id`, `parent_id`, `level`, `status`, `total_sales`, `total_commission`, `available_commission`, `frozen_commission`, `withdrawn_commission`, `direct_count`, `team_count`, `real_name`, `phone`, `id_card`, `apply_time`, `audit_time`, `del_flag`, `create_by`, `create_time`) 
VALUES 
(4, 4, 2, 2, 1, 15000.00, 1200.00, 800.00, 100.00, 300.00, 1, 1, '张诗涵', '13800138003', '110101199004041234', '2025-01-04 10:00:00', '2025-01-04 11:00:00', 0, 'admin', NOW());

-- 三级分销商（user_id=5，上级是李雨婷）
INSERT INTO `dist_distributor` 
(`id`, `user_id`, `parent_id`, `level`, `status`, `total_sales`, `total_commission`, `available_commission`, `frozen_commission`, `withdrawn_commission`, `direct_count`, `team_count`, `real_name`, `phone`, `id_card`, `apply_time`, `audit_time`, `del_flag`, `create_by`, `create_time`) 
VALUES 
(5, 5, 2, 1, 1, 8000.00, 600.00, 400.00, 50.00, 150.00, 0, 0, '刘宇轩', '13800138004', '110101199005051234', '2025-01-05 10:00:00', '2025-01-05 11:00:00', 0, 'admin', NOW());

-- 四级分销商（user_id=6，上级是张诗涵）
INSERT INTO `dist_distributor` 
(`id`, `user_id`, `parent_id`, `level`, `status`, `total_sales`, `total_commission`, `available_commission`, `frozen_commission`, `withdrawn_commission`, `direct_count`, `team_count`, `real_name`, `phone`, `id_card`, `apply_time`, `audit_time`, `del_flag`, `create_by`, `create_time`) 
VALUES 
(6, 6, 4, 1, 1, 5000.00, 400.00, 250.00, 50.00, 100.00, 0, 0, '赵梓萱', '13800138005', '110101199006061234', '2025-01-06 10:00:00', '2025-01-06 11:00:00', 0, 'admin', NOW());

-- 待审核的分销商（user_id=7，上级是王浩然）
INSERT INTO `dist_distributor` 
(`id`, `user_id`, `parent_id`, `level`, `status`, `total_sales`, `total_commission`, `available_commission`, `frozen_commission`, `withdrawn_commission`, `direct_count`, `team_count`, `real_name`, `phone`, `id_card`, `apply_time`, `audit_time`, `del_flag`, `create_by`, `create_time`) 
VALUES 
(7, 7, 3, 1, 2, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 0, '孙佳怡', '13800138006', '110101199007071234', '2025-01-07 10:00:00', NULL, 0, 'admin', NOW());

-- 禁用的分销商（user_id=8，上级是陈明轩）
INSERT INTO `dist_distributor` 
(`id`, `user_id`, `parent_id`, `level`, `status`, `total_sales`, `total_commission`, `available_commission`, `frozen_commission`, `withdrawn_commission`, `direct_count`, `team_count`, `real_name`, `phone`, `id_card`, `apply_time`, `audit_time`, `del_flag`, `create_by`, `create_time`) 
VALUES 
(8, 8, 1, 1, 0, 2000.00, 150.00, 0.00, 0.00, 150.00, 0, 0, '周思琪', '13800138007', '110101199008081234', '2025-01-08 10:00:00', '2025-01-08 11:00:00', 0, 'admin', NOW());

-- ========================================
-- 2. 创建分销关系（闭包表）
-- ========================================

-- 李雨婷(2)的关系：上级是陈明轩(1)
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (2, 1, 1, 'admin', NOW());

-- 王浩然(3)的关系：上级是陈明轩(1)
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (3, 1, 1, 'admin', NOW());

-- 张诗涵(4)的关系：上级是李雨婷(2)，二级上级是陈明轩(1)
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (4, 2, 1, 'admin', NOW());
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (4, 1, 2, 'admin', NOW());

-- 刘宇轩(5)的关系：上级是李雨婷(2)，二级上级是陈明轩(1)
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (5, 2, 1, 'admin', NOW());
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (5, 1, 2, 'admin', NOW());

-- 赵梓萱(6)的关系：上级是张诗涵(4)，二级上级是李雨婷(2)，三级上级是陈明轩(1)
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (6, 4, 1, 'admin', NOW());
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (6, 2, 2, 'admin', NOW());
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (6, 1, 3, 'admin', NOW());

-- 孙佳怡(7)的关系：上级是王浩然(3)，二级上级是陈明轩(1)
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (7, 3, 1, 'admin', NOW());
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (7, 1, 2, 'admin', NOW());

-- 周思琪(8)的关系：上级是陈明轩(1)
INSERT INTO `dist_relation` (`distributor_id`, `ancestor_id`, `level`, `create_by`, `create_time`) VALUES (8, 1, 1, 'admin', NOW());

-- ========================================
-- 3. 创建佣金配置（3层级 × 5等级 = 15种配置）
-- ========================================

-- 一级佣金配置（直接推荐人）
INSERT INTO `dist_commission_config` 
(`name`, `level`, `commission_rate`, `distributor_level`, `status`, `remark`, `create_by`, `create_time`) 
VALUES 
('一级佣金-普通分销商', 1, 8.00, 1, 1, '普通分销商的一级佣金比例', 'admin', NOW()),
('一级佣金-铜牌分销商', 1, 10.00, 2, 1, '铜牌分销商的一级佣金比例', 'admin', NOW()),
('一级佣金-银牌分销商', 1, 12.00, 3, 1, '银牌分销商的一级佣金比例', 'admin', NOW()),
('一级佣金-金牌分销商', 1, 15.00, 4, 1, '金牌分销商的一级佣金比例', 'admin', NOW()),
('一级佣金-钻石分销商', 1, 18.00, 5, 1, '钻石分销商的一级佣金比例', 'admin', NOW());

-- 二级佣金配置（推荐人的推荐人）
INSERT INTO `dist_commission_config` 
(`name`, `level`, `commission_rate`, `distributor_level`, `status`, `remark`, `create_by`, `create_time`) 
VALUES 
('二级佣金-普通分销商', 2, 3.00, 1, 1, '普通分销商的二级佣金比例', 'admin', NOW()),
('二级佣金-铜牌分销商', 2, 4.00, 2, 1, '铜牌分销商的二级佣金比例', 'admin', NOW()),
('二级佣金-银牌分销商', 2, 5.00, 3, 1, '银牌分销商的二级佣金比例', 'admin', NOW()),
('二级佣金-金牌分销商', 2, 6.00, 4, 1, '金牌分销商的二级佣金比例', 'admin', NOW()),
('二级佣金-钻石分销商', 2, 8.00, 5, 1, '钻石分销商的二级佣金比例', 'admin', NOW());

-- 三级佣金配置（推荐人的推荐人的推荐人）
INSERT INTO `dist_commission_config` 
(`name`, `level`, `commission_rate`, `distributor_level`, `status`, `remark`, `create_by`, `create_time`) 
VALUES 
('三级佣金-普通分销商', 3, 1.00, 1, 1, '普通分销商的三级佣金比例', 'admin', NOW()),
('三级佣金-铜牌分销商', 3, 2.00, 2, 1, '铜牌分销商的三级佣金比例', 'admin', NOW()),
('三级佣金-银牌分销商', 3, 2.50, 3, 1, '银牌分销商的三级佣金比例', 'admin', NOW()),
('三级佣金-金牌分销商', 3, 3.00, 4, 1, '金牌分销商的三级佣金比例', 'admin', NOW()),
('三级佣金-钻石分销商', 3, 4.00, 5, 1, '钻石分销商的三级佣金比例', 'admin', NOW());

-- ========================================
-- 4. 创建订单佣金记录（admin 的佣金）
-- ========================================

-- admin 的已结算佣金
INSERT INTO `dist_order_commission` 
(`order_id`, `order_no`, `distributor_id`, `buyer_id`, `level`, `order_amount`, `commission_rate`, `commission_amount`, `status`, `settle_time`, `remark`, `create_by`, `create_time`) 
VALUES 
(1001, 'ORD202501010001', 1, 10, 1, 1000.00, 10.00, 100.00, 1, '2025-01-10 15:00:00', '一级佣金', 'system', '2025-01-10 10:00:00'),
(1002, 'ORD202501020001', 1, 11, 1, 2000.00, 10.00, 200.00, 1, '2025-01-11 15:00:00', '一级佣金', 'system', '2025-01-11 10:00:00'),
(1003, 'ORD202501030001', 1, 12, 1, 1500.00, 10.00, 150.00, 1, '2025-01-12 15:00:00', '一级佣金', 'system', '2025-01-12 10:00:00'),
(1004, 'ORD202501040001', 1, 13, 2, 3000.00, 5.00, 150.00, 1, '2025-01-13 15:00:00', '二级佣金', 'system', '2025-01-13 10:00:00'),
(1005, 'ORD202501050001', 1, 14, 1, 2500.00, 10.00, 250.00, 1, '2025-01-14 15:00:00', '一级佣金', 'system', '2025-01-14 10:00:00');

-- admin 的待结算佣金
INSERT INTO `dist_order_commission` 
(`order_id`, `order_no`, `distributor_id`, `buyer_id`, `level`, `order_amount`, `commission_rate`, `commission_amount`, `status`, `remark`, `create_by`, `create_time`) 
VALUES 
(1006, 'ORD202501150001', 1, 15, 1, 3000.00, 10.00, 300.00, 0, '一级佣金-待结算', 'system', '2025-01-15 10:00:00'),
(1007, 'ORD202501160001', 1, 16, 2, 1500.00, 5.00, 75.00, 0, '二级佣金-待结算', 'system', '2025-01-16 10:00:00'),
(1008, 'ORD202501170001', 1, 17, 1, 1200.00, 10.00, 120.00, 0, '一级佣金-待结算', 'system', '2025-01-17 10:00:00');

-- admin 的已取消佣金
INSERT INTO `dist_order_commission` 
(`order_id`, `order_no`, `distributor_id`, `buyer_id`, `level`, `order_amount`, `commission_rate`, `commission_amount`, `status`, `cancel_time`, `remark`, `create_by`, `create_time`) 
VALUES 
(1009, 'ORD202501180001', 1, 18, 1, 800.00, 10.00, 80.00, 2, '2025-01-18 15:00:00', '订单退款-佣金取消', 'system', '2025-01-18 10:00:00');

-- ========================================
-- 5. 创建提现记录（admin 的提现）
-- ========================================

-- admin 的待审核提现
INSERT INTO `dist_withdraw` 
(`withdraw_no`, `distributor_id`, `amount`, `fee`, `actual_amount`, `account_type`, `account_no`, `account_name`, `bank_name`, `status`, `remark`, `create_by`, `create_time`) 
VALUES 
('WD202501200001', 1, 1000.00, 10.00, 990.00, 1, 'admin@alipay.com', '管理员', NULL, 0, '申请提现1000元', 'admin', '2025-01-20 14:00:00'),
('WD202501210001', 1, 500.00, 5.00, 495.00, 2, 'admin_wx', '管理员', NULL, 0, '申请提现500元', 'admin', '2025-01-21 14:00:00');

-- admin 的审核通过待打款
INSERT INTO `dist_withdraw` 
(`withdraw_no`, `distributor_id`, `amount`, `fee`, `actual_amount`, `account_type`, `account_no`, `account_name`, `bank_name`, `status`, `remark`, `audit_user`, `audit_time`, `create_by`, `create_time`) 
VALUES 
('WD202501190001', 1, 500.00, 5.00, 495.00, 3, '6222021234567890123', '管理员', '工商银行', 1, '审核通过', 'admin', '2025-01-19 16:00:00', 'admin', '2025-01-19 14:00:00');

-- admin 的已打款
INSERT INTO `dist_withdraw` 
(`withdraw_no`, `distributor_id`, `amount`, `fee`, `actual_amount`, `account_type`, `account_no`, `account_name`, `bank_name`, `status`, `remark`, `audit_user`, `audit_time`, `pay_time`, `create_by`, `create_time`) 
VALUES 
('WD202501150001', 1, 300.00, 3.00, 297.00, 1, 'admin@alipay.com', '管理员', NULL, 2, '已打款', 'admin', '2025-01-15 16:00:00', '2025-01-15 17:00:00', 'admin', '2025-01-15 14:00:00'),
('WD202501160001', 1, 200.00, 2.00, 198.00, 2, 'admin_wx', '管理员', NULL, 2, '已打款', 'admin', '2025-01-16 16:00:00', '2025-01-16 17:00:00', 'admin', '2025-01-16 14:00:00');

-- admin 的已拒绝
INSERT INTO `dist_withdraw` 
(`withdraw_no`, `distributor_id`, `amount`, `fee`, `actual_amount`, `account_type`, `account_no`, `account_name`, `bank_name`, `status`, `remark`, `reject_reason`, `audit_user`, `audit_time`, `create_by`, `create_time`) 
VALUES 
('WD202501170001', 1, 100.00, 1.00, 99.00, 1, 'admin@alipay.com', '管理员', NULL, 3, '提现申请', '账户信息有误，请核实后重新申请', 'admin', '2025-01-17 16:00:00', 'admin', '2025-01-17 14:00:00');

-- ========================================
-- 6. 创建佣金流水记录（admin 的流水）
-- ========================================

-- admin 的佣金收入
INSERT INTO `dist_commission_log` 
(`distributor_id`, `type`, `amount`, `before_amount`, `after_amount`, `order_id`, `remark`, `create_by`, `create_time`) 
VALUES 
(1, 1, 100.00, 4900.00, 5000.00, 1001, '订单ORD202501010001佣金收入', 'system', '2025-01-10 15:00:00'),
(1, 1, 200.00, 5000.00, 5200.00, 1002, '订单ORD202501020001佣金收入', 'system', '2025-01-11 15:00:00'),
(1, 1, 150.00, 5200.00, 5350.00, 1003, '订单ORD202501030001佣金收入', 'system', '2025-01-12 15:00:00');

-- admin 的提现支出
INSERT INTO `dist_commission_log` 
(`distributor_id`, `type`, `amount`, `before_amount`, `after_amount`, `withdraw_id`, `remark`, `create_by`, `create_time`) 
VALUES 
(1, 2, 300.00, 5350.00, 5050.00, 1, '提现WD202501150001', 'system', '2025-01-15 17:00:00'),
(1, 2, 200.00, 5050.00, 4850.00, 2, '提现WD202501160001', 'system', '2025-01-16 17:00:00');

-- admin 的冻结
INSERT INTO `dist_commission_log` 
(`distributor_id`, `type`, `amount`, `before_amount`, `after_amount`, `withdraw_id`, `remark`, `create_by`, `create_time`) 
VALUES 
(1, 3, 1000.00, 4850.00, 3850.00, 3, '提现申请WD202501200001-冻结', 'system', '2025-01-20 14:00:00'),
(1, 3, 500.00, 3850.00, 3350.00, 4, '提现申请WD202501210001-冻结', 'system', '2025-01-21 14:00:00');

-- admin 的解冻（拒绝提现）
INSERT INTO `dist_commission_log` 
(`distributor_id`, `type`, `amount`, `before_amount`, `after_amount`, `withdraw_id`, `remark`, `create_by`, `create_time`) 
VALUES 
(1, 4, 100.00, 3350.00, 3450.00, 5, '提现WD202501170001被拒绝-解冻', 'system', '2025-01-17 16:00:00');

-- admin 的取消（订单退款）
INSERT INTO `dist_commission_log` 
(`distributor_id`, `type`, `amount`, `before_amount`, `after_amount`, `order_id`, `remark`, `create_by`, `create_time`) 
VALUES 
(1, 5, 80.00, 3450.00, 3370.00, 1009, '订单ORD202501180001退款-佣金取消', 'system', '2025-01-18 15:00:00');

-- ========================================
-- 测试数据创建完成
-- ========================================

SELECT '✅ 测试数据创建完成！' AS message;
SELECT '📊 数据统计:' AS '';
SELECT CONCAT('分销商数量: ', COUNT(*)) AS info FROM dist_distributor;
SELECT CONCAT('分销关系数量: ', COUNT(*)) AS info FROM dist_relation;
SELECT CONCAT('佣金配置数量: ', COUNT(*)) AS info FROM dist_commission_config;
SELECT CONCAT('订单佣金数量: ', COUNT(*)) AS info FROM dist_order_commission;
SELECT CONCAT('提现记录数量: ', COUNT(*)) AS info FROM dist_withdraw;
SELECT CONCAT('佣金流水数量: ', COUNT(*)) AS info FROM dist_commission_log;
SELECT '' AS '';
SELECT '🎯 admin 账号数据:' AS '';
SELECT CONCAT('admin 的佣金记录: ', COUNT(*)) AS info FROM dist_order_commission WHERE distributor_id = 1;
SELECT CONCAT('admin 的提现记录: ', COUNT(*)) AS info FROM dist_withdraw WHERE distributor_id = 1;
SELECT CONCAT('admin 的佣金流水: ', COUNT(*)) AS info FROM dist_commission_log WHERE distributor_id = 1;
SELECT '' AS '';
SELECT '💡 提示: 使用 admin/admin 登录即可查看佣金和提现数据' AS info;


-- ========================================
-- 商品管理系统测试数据
-- 用于开发环境测试
-- ========================================

-- ========================================
-- 清空现有商品测试数据
-- ========================================
DELETE FROM product_price_history;
DELETE FROM product_commission_config;
DELETE FROM product_stock_log;
DELETE FROM product_cdkey;
DELETE FROM product_sku;
DELETE FROM product;

-- 重置自增ID
ALTER TABLE product_price_history AUTO_INCREMENT = 1;
ALTER TABLE product_commission_config AUTO_INCREMENT = 1;
ALTER TABLE product_stock_log AUTO_INCREMENT = 1;
ALTER TABLE product_cdkey AUTO_INCREMENT = 1;
ALTER TABLE product_sku AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;

-- ========================================
-- 1. 创建测试商品
-- ========================================

-- 虚拟商品 - 游戏点卡
INSERT INTO `product` 
(`id`, `name`, `category_id`, `type`, `cover_image`, `description`, `price`, `cost_price`, `market_price`, `stock`, `stock_warning`, `sales`, `status`, `sort`, `create_by`) 
VALUES 
(1, '王者荣耀60元点券', 3, 1, '/images/products/wzry-60.jpg', '王者荣耀官方充值卡，60元面值', 58.00, 55.00, 60.00, 1000, 50, 150, 1, 1, 'admin'),
(2, '和平精英98元点券', 3, 1, '/images/products/hpjy-98.jpg', '和平精英官方充值卡，98元面值', 95.00, 92.00, 98.00, 800, 50, 80, 1, 2, 'admin'),
(3, '原神648元创世结晶', 3, 1, '/images/products/ys-648.jpg', '原神官方充值，648元创世结晶', 630.00, 620.00, 648.00, 500, 30, 200, 1, 3, 'admin');

-- 虚拟商品 - 软件激活码
INSERT INTO `product` 
(`id`, `name`, `category_id`, `type`, `cover_image`, `description`, `price`, `cost_price`, `market_price`, `stock`, `stock_warning`, `sales`, `status`, `sort`, `create_by`) 
VALUES 
(4, 'Office 365家庭版年卡', 4, 1, '/images/products/office365.jpg', 'Microsoft Office 365家庭版，支持6人使用', 398.00, 350.00, 498.00, 200, 20, 50, 1, 4, 'admin'),
(5, 'Adobe Creative Cloud年卡', 4, 1, '/images/products/adobe-cc.jpg', 'Adobe全家桶年卡，包含PS/AI/PR等', 1980.00, 1800.00, 2388.00, 100, 10, 30, 1, 5, 'admin');

-- 实物商品 - 电子产品
INSERT INTO `product` 
(`id`, `name`, `category_id`, `type`, `cover_image`, `description`, `price`, `cost_price`, `market_price`, `stock`, `stock_warning`, `sales`, `status`, `sort`, `create_by`) 
VALUES 
(6, '小米无线鼠标', 5, 0, '/images/products/mi-mouse.jpg', '小米无线鼠标，人体工学设计', 49.00, 35.00, 69.00, 500, 50, 120, 1, 6, 'admin'),
(7, '罗技机械键盘', 5, 0, '/images/products/logitech-keyboard.jpg', '罗技机械键盘，青轴', 399.00, 300.00, 499.00, 200, 20, 45, 1, 7, 'admin');

-- 实物商品 - 日用百货
INSERT INTO `product` 
(`id`, `name`, `category_id`, `type`, `cover_image`, `description`, `price`, `cost_price`, `market_price`, `stock`, `stock_warning`, `sales`, `status`, `sort`, `create_by`) 
VALUES 
(8, '保温杯304不锈钢', 6, 0, '/images/products/thermos.jpg', '304不锈钢保温杯，500ml', 89.00, 60.00, 129.00, 300, 30, 80, 1, 8, 'admin'),
(9, 'USB充电台灯', 6, 0, '/images/products/desk-lamp.jpg', 'LED护眼台灯，USB充电', 79.00, 50.00, 99.00, 400, 40, 60, 1, 9, 'admin');

-- 草稿状态商品
INSERT INTO `product` 
(`id`, `name`, `category_id`, `type`, `cover_image`, `description`, `price`, `cost_price`, `market_price`, `stock`, `stock_warning`, `sales`, `status`, `sort`, `create_by`) 
VALUES 
(10, '测试商品-草稿', 6, 0, NULL, '这是一个草稿状态的商品', 99.00, 80.00, 120.00, 0, 10, 0, 0, 10, 'admin');

-- 售罄状态商品
INSERT INTO `product` 
(`id`, `name`, `category_id`, `type`, `cover_image`, `description`, `price`, `cost_price`, `market_price`, `stock`, `stock_warning`, `sales`, `status`, `sort`, `create_by`) 
VALUES 
(11, '热销商品-已售罄', 5, 0, '/images/products/sold-out.jpg', '这是一个已售罄的商品', 199.00, 150.00, 249.00, 0, 10, 500, 3, 11, 'admin');

-- ========================================
-- 2. 创建测试SKU
-- ========================================

-- 罗技机械键盘的SKU（不同轴体）
INSERT INTO `product_sku` 
(`product_id`, `sku_code`, `sku_name`, `attributes`, `price`, `cost_price`, `stock`, `status`, `create_by`) 
VALUES 
(7, 'LOGITECH-KB-BLUE', '罗技机械键盘-青轴', '{"轴体":"青轴","颜色":"黑色"}', 399.00, 300.00, 80, 1, 'admin'),
(7, 'LOGITECH-KB-RED', '罗技机械键盘-红轴', '{"轴体":"红轴","颜色":"黑色"}', 399.00, 300.00, 70, 1, 'admin'),
(7, 'LOGITECH-KB-BROWN', '罗技机械键盘-茶轴', '{"轴体":"茶轴","颜色":"白色"}', 419.00, 310.00, 50, 1, 'admin');

-- 保温杯的SKU（不同容量）
INSERT INTO `product_sku` 
(`product_id`, `sku_code`, `sku_name`, `attributes`, `price`, `cost_price`, `stock`, `status`, `create_by`) 
VALUES 
(8, 'THERMOS-500ML', '保温杯-500ml', '{"容量":"500ml","颜色":"白色"}', 89.00, 60.00, 150, 1, 'admin'),
(8, 'THERMOS-750ML', '保温杯-750ml', '{"容量":"750ml","颜色":"黑色"}', 109.00, 75.00, 100, 1, 'admin'),
(8, 'THERMOS-1000ML', '保温杯-1000ml', '{"容量":"1000ml","颜色":"蓝色"}', 129.00, 90.00, 50, 1, 'admin');

-- ========================================
-- 3. 创建测试CDKEY
-- ========================================

-- 王者荣耀60元点券的CDKEY
INSERT INTO `product_cdkey` 
(`product_id`, `cdkey`, `status`, `commission_level1`, `commission_level2`, `commission_level3`, `create_by`) 
VALUES 
(1, 'WZRY60-AAAA-BBBB-CCCC-0001', 0, 10.00, 5.00, 2.00, 'admin'),
(1, 'WZRY60-AAAA-BBBB-CCCC-0002', 0, 10.00, 5.00, 2.00, 'admin'),
(1, 'WZRY60-AAAA-BBBB-CCCC-0003', 0, 10.00, 5.00, 2.00, 'admin'),
(1, 'WZRY60-AAAA-BBBB-CCCC-0004', 1, 10.00, 5.00, 2.00, 'admin'),
(1, 'WZRY60-AAAA-BBBB-CCCC-0005', 1, 10.00, 5.00, 2.00, 'admin');

-- 和平精英98元点券的CDKEY
INSERT INTO `product_cdkey` 
(`product_id`, `cdkey`, `status`, `commission_level1`, `commission_level2`, `commission_level3`, `create_by`) 
VALUES 
(2, 'HPJY98-DDDD-EEEE-FFFF-0001', 0, 10.00, 5.00, 2.00, 'admin'),
(2, 'HPJY98-DDDD-EEEE-FFFF-0002', 0, 10.00, 5.00, 2.00, 'admin'),
(2, 'HPJY98-DDDD-EEEE-FFFF-0003', 1, 10.00, 5.00, 2.00, 'admin');

-- Office 365的CDKEY
INSERT INTO `product_cdkey` 
(`product_id`, `cdkey`, `status`, `commission_level1`, `commission_level2`, `commission_level3`, `create_by`) 
VALUES 
(4, 'OFFICE365-GGGG-HHHH-IIII-0001', 0, 12.00, 6.00, 3.00, 'admin'),
(4, 'OFFICE365-GGGG-HHHH-IIII-0002', 0, 12.00, 6.00, 3.00, 'admin'),
(4, 'OFFICE365-GGGG-HHHH-IIII-0003', 1, 12.00, 6.00, 3.00, 'admin');

-- ========================================
-- 4. 创建库存日志
-- ========================================

-- 商品入库记录
INSERT INTO `product_stock_log` 
(`product_id`, `type`, `quantity`, `before_stock`, `after_stock`, `remark`, `create_by`) 
VALUES 
(1, 1, 1000, 0, 1000, '初始入库', 'admin'),
(2, 1, 800, 0, 800, '初始入库', 'admin'),
(6, 1, 500, 0, 500, '初始入库', 'admin');

-- 订单扣减记录
INSERT INTO `product_stock_log` 
(`product_id`, `type`, `quantity`, `before_stock`, `after_stock`, `order_id`, `remark`, `create_by`) 
VALUES 
(1, 3, -10, 1000, 990, 1001, '订单扣减', 'system'),
(2, 3, -5, 800, 795, 1002, '订单扣减', 'system'),
(6, 3, -20, 500, 480, 1003, '订单扣减', 'system');

-- 订单退回记录
INSERT INTO `product_stock_log` 
(`product_id`, `type`, `quantity`, `before_stock`, `after_stock`, `order_id`, `remark`, `create_by`) 
VALUES 
(1, 4, 2, 990, 992, 1001, '订单退款退回库存', 'system');

-- ========================================
-- 5. 创建商品佣金配置
-- ========================================

-- 游戏点卡分类的默认佣金配置
INSERT INTO `product_commission_config` 
(`category_id`, `level1_rate`, `level2_rate`, `level3_rate`, `status`, `create_by`) 
VALUES 
(3, 10.00, 5.00, 2.00, 1, 'admin');

-- 软件激活码分类的默认佣金配置
INSERT INTO `product_commission_config` 
(`category_id`, `level1_rate`, `level2_rate`, `level3_rate`, `status`, `create_by`) 
VALUES 
(4, 12.00, 6.00, 3.00, 1, 'admin');

-- 电子产品分类的默认佣金配置
INSERT INTO `product_commission_config` 
(`category_id`, `level1_rate`, `level2_rate`, `level3_rate`, `status`, `create_by`) 
VALUES 
(5, 8.00, 4.00, 2.00, 1, 'admin');

-- 原神648的独立佣金配置（高佣金）
INSERT INTO `product_commission_config` 
(`product_id`, `level1_rate`, `level2_rate`, `level3_rate`, `status`, `create_by`) 
VALUES 
(3, 15.00, 8.00, 4.00, 1, 'admin');

-- Adobe CC的独立佣金配置（高佣金）
INSERT INTO `product_commission_config` 
(`product_id`, `level1_rate`, `level2_rate`, `level3_rate`, `status`, `create_by`) 
VALUES 
(5, 18.00, 10.00, 5.00, 1, 'admin');

-- ========================================
-- 6. 创建价格变更历史
-- ========================================

-- 王者荣耀点券价格调整
INSERT INTO `product_price_history` 
(`product_id`, `old_price`, `new_price`, `change_reason`, `create_by`) 
VALUES 
(1, 60.00, 58.00, '促销活动降价', 'admin');

-- 罗技键盘价格调整
INSERT INTO `product_price_history` 
(`product_id`, `old_price`, `new_price`, `change_reason`, `create_by`) 
VALUES 
(7, 449.00, 399.00, '新品上市优惠', 'admin');

-- 保温杯价格调整
INSERT INTO `product_price_history` 
(`product_id`, `old_price`, `new_price`, `change_reason`, `create_by`) 
VALUES 
(8, 99.00, 89.00, '清仓促销', 'admin');

-- ========================================
-- 商品测试数据创建完成
-- ========================================

SELECT '✅ 商品测试数据创建完成！' AS message;
SELECT '📊 商品数据统计:' AS '';
SELECT CONCAT('商品数量: ', COUNT(*)) AS info FROM product;
SELECT CONCAT('商品分类数量: ', COUNT(*)) AS info FROM product_category;
SELECT CONCAT('SKU数量: ', COUNT(*)) AS info FROM product_sku;
SELECT CONCAT('CDKEY数量: ', COUNT(*)) AS info FROM product_cdkey;
SELECT CONCAT('库存日志数量: ', COUNT(*)) AS info FROM product_stock_log;
SELECT CONCAT('佣金配置数量: ', COUNT(*)) AS info FROM product_commission_config;
SELECT CONCAT('价格历史数量: ', COUNT(*)) AS info FROM product_price_history;
SELECT '' AS '';
SELECT '🎯 商品状态分布:' AS '';
SELECT CONCAT('上架商品: ', COUNT(*)) AS info FROM product WHERE status = 1;
SELECT CONCAT('草稿商品: ', COUNT(*)) AS info FROM product WHERE status = 0;
SELECT CONCAT('售罄商品: ', COUNT(*)) AS info FROM product WHERE status = 3;
SELECT '' AS '';
SELECT '💡 提示: 商品管理系统测试数据已就绪' AS info;
