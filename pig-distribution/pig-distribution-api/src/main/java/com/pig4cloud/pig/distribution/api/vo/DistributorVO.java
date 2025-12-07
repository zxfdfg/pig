package com.pig4cloud.pig.distribution.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分销商VO
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Data
public class DistributorVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 上级分销商ID
	 */
	private Long parentId;

	/**
	 * 上级分销商名称
	 */
	private String parentName;

	/**
	 * 分销商等级：1-普通 2-铜牌 3-银牌 4-金牌 5-钻石
	 */
	private Integer level;

	/**
	 * 等级名称
	 */
	private String levelName;

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
	 * 申请时间
	 */
	private LocalDateTime applyTime;

	/**
	 * 审核时间
	 */
	private LocalDateTime auditTime;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

}
