package com.pig4cloud.pig.distribution.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 提现申请DTO
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Data
public class WithdrawApplyDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 提现金额
	 */
	private BigDecimal amount;

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
	 * 银行名称（银行卡必填）
	 */
	private String bankName;

}
