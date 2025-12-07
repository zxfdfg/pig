package com.pig4cloud.pig.distribution.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dist_withdraw")
public class Withdraw extends BaseEntity {

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 提现单号
	 */
	private String withdrawNo;

	/**
	 * 分销商ID
	 */
	private Long distributorId;

	/**
	 * 提现金额
	 */
	private BigDecimal amount;

	/**
	 * 手续费
	 */
	private BigDecimal fee;

	/**
	 * 实际到账金额
	 */
	private BigDecimal actualAmount;

	/**
	 * 账户类型：1-支付宝 2-微信 3-银行卡
	 */
	private Integer accountType;

	/**
	 * 账户号
	 */
	private String accountNo;

	/**
	 * 账户名
	 */
	private String accountName;

	/**
	 * 银行名称
	 */
	private String bankName;

	/**
	 * 状态：0-待审核 1-审核通过 2-已打款 3-已拒绝
	 */
	private Integer status;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 拒绝原因
	 */
	private String rejectReason;

	/**
	 * 审核人
	 */
	private String auditUser;

	/**
	 * 审核时间
	 */
	private LocalDateTime auditTime;

	/**
	 * 打款时间
	 */
	private LocalDateTime payTime;

}
