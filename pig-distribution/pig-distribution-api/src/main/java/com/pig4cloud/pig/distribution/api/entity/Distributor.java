package com.pig4cloud.pig.distribution.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分销商实体
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dist_distributor")
public class Distributor extends BaseEntity {

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 上级分销商ID
	 */
	private Long parentId;

	/**
	 * 分销商等级：1-普通 2-铜牌 3-银牌 4-金牌 5-钻石
	 */
	private Integer level;

	/**
	 * 状态：0-禁用 1-启用 2-待审核
	 */
	private Integer status;

	/**
	 * 累计销售额
	 */
	private BigDecimal totalSales;

	/**
	 * 累计佣金
	 */
	private BigDecimal totalCommission;

	/**
	 * 可提现佣金
	 */
	private BigDecimal availableCommission;

	/**
	 * 冻结佣金
	 */
	private BigDecimal frozenCommission;

	/**
	 * 已提现佣金
	 */
	private BigDecimal withdrawnCommission;

	/**
	 * 直推人数
	 */
	private Integer directCount;

	/**
	 * 团队总人数
	 */
	private Integer teamCount;

	/**
	 * 真实姓名
	 */
	private String realName;

	/**
	 * 联系电话
	 */
	private String phone;

	/**
	 * 身份证号
	 */
	private String idCard;

	/**
	 * 申请时间
	 */
	private LocalDateTime applyTime;

	/**
	 * 审核时间
	 */
	private LocalDateTime auditTime;

	/**
	 * 删除标记：0-正常 1-删除
	 */
	@TableLogic
	private Integer delFlag;

}
