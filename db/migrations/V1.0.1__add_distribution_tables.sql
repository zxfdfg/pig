-- ========================================
-- 版本: V1.0.1
-- 描述: 添加分销系统表（6个表）
-- 日期: 2025-12-07
-- 作者: 开发团队
-- 说明: 此脚本用于在已有数据库中添加分销系统表
-- ========================================

USE pig;

-- 1. 分销商表
DROP TABLE IF EXISTS `dist_distributor`;
CREATE TABLE `dist_distributor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '上级分销商ID',
  `level` INT DEFAULT 1 COMMENT '分销商等级：1-普通 2-铜牌 3-银牌 4-金牌 5-钻石',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用 2-待审核',
  `total_sales` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计销售额',
  `total_commission` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计佣金',
  `available_commission` DECIMAL(10,2) DEFAULT 0.00 COMMENT '可提现佣金',
  `frozen_commission` DECIMAL(10,2) DEFAULT 0.00 COMMENT '冻结佣金',
  `withdrawn_commission` DECIMAL(10,2) DEFAULT 0.00 COMMENT '已提现佣金',
  `direct_count` INT DEFAULT 0 COMMENT '直推人数',
  `team_count` INT DEFAULT 0 COMMENT '团队总人数',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `id_card` VARCHAR(50) DEFAULT NULL COMMENT '身份证号',
  `apply_time` DATETIME DEFAULT NULL COMMENT '申请时间',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `del_flag` TINYINT DEFAULT 0 COMMENT '删除标记：0-正常 1-删除',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销商表';

-- 2. 分销关系表
DROP TABLE IF EXISTS `dist_relation`;
CREATE TABLE `dist_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `ancestor_id` BIGINT NOT NULL COMMENT '上级分销商ID',
  `level` INT NOT NULL COMMENT '层级：1-直接上级 2-二级上级 3-三级上级',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_distributor_ancestor` (`distributor_id`, `ancestor_id`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_ancestor_id` (`ancestor_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销关系表（闭包表）';

-- 3. 佣金配置表
DROP TABLE IF EXISTS `dist_commission_config`;
CREATE TABLE `dist_commission_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(100) NOT NULL COMMENT '配置名称',
  `level` INT NOT NULL COMMENT '分销层级：1-一级 2-二级 3-三级',
  `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '佣金比例(%)',
  `distributor_level` INT DEFAULT 0 COMMENT '分销商等级：0-全部 1-普通 2-铜牌 3-银牌 4-金牌 5-钻石',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金配置表';

-- 4. 订单佣金表
DROP TABLE IF EXISTS `dist_order_commission`;
CREATE TABLE `dist_order_commission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `buyer_id` BIGINT NOT NULL COMMENT '购买者ID',
  `level` INT NOT NULL COMMENT '分销层级',
  `order_amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '佣金比例',
  `commission_amount` DECIMAL(10,2) NOT NULL COMMENT '佣金金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待结算 1-已结算 2-已取消',
  `settle_time` DATETIME DEFAULT NULL COMMENT '结算时间',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单佣金表';

-- 5. 提现记录表
DROP TABLE IF EXISTS `dist_withdraw`;
CREATE TABLE `dist_withdraw` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `withdraw_no` VARCHAR(50) NOT NULL COMMENT '提现单号',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
  `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '手续费',
  `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际到账金额',
  `account_type` TINYINT NOT NULL COMMENT '账户类型：1-支付宝 2-微信 3-银行卡',
  `account_no` VARCHAR(100) NOT NULL COMMENT '账户号',
  `account_name` VARCHAR(50) NOT NULL COMMENT '账户名',
  `bank_name` VARCHAR(100) DEFAULT NULL COMMENT '银行名称',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-审核通过 2-已打款 3-已拒绝',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '拒绝原因',
  `audit_user` VARCHAR(50) DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `pay_time` DATETIME DEFAULT NULL COMMENT '打款时间',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_withdraw_no` (`withdraw_no`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现记录表';

-- 6. 佣金流水表
DROP TABLE IF EXISTS `dist_commission_log`;
CREATE TABLE `dist_commission_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `distributor_id` BIGINT NOT NULL COMMENT '分销商ID',
  `type` TINYINT NOT NULL COMMENT '类型：1-佣金收入 2-提现支出 3-冻结 4-解冻 5-取消',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `before_amount` DECIMAL(10,2) NOT NULL COMMENT '变动前金额',
  `after_amount` DECIMAL(10,2) NOT NULL COMMENT '变动后金额',
  `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
  `withdraw_id` BIGINT DEFAULT NULL COMMENT '关联提现ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金流水表';

-- 初始化佣金配置数据
INSERT INTO `dist_commission_config` (`name`, `level`, `commission_rate`, `distributor_level`, `status`, `remark`) VALUES
('一级分销-普通会员', 1, 10.00, 0, 1, '一级分销商基础佣金'),
('二级分销-普通会员', 2, 5.00, 0, 1, '二级分销商基础佣金'),
('三级分销-普通会员', 3, 2.00, 0, 1, '三级分销商基础佣金')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ========================================
-- 回滚脚本（需要时手动执行）
-- ========================================
-- DROP TABLE IF EXISTS `dist_commission_log`;
-- DROP TABLE IF EXISTS `dist_withdraw`;
-- DROP TABLE IF EXISTS `dist_order_commission`;
-- DROP TABLE IF EXISTS `dist_commission_config`;
-- DROP TABLE IF EXISTS `dist_relation`;
-- DROP TABLE IF EXISTS `dist_distributor`;
