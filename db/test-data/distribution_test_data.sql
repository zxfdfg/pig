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
