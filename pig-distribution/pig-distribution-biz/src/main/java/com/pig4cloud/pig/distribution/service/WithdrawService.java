package com.pig4cloud.pig.distribution.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.distribution.api.entity.Withdraw;

/**
 * 提现服务接口
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
public interface WithdrawService extends IService<Withdraw> {

	/**
	 * 申请提现
	 * @param distributorId 分销商ID
	 * @param amount 提现金额
	 * @param bankName 银行名称
	 * @param bankAccount 银行账号
	 * @param accountName 账户名
	 * @return 提现单号
	 */
	String applyWithdraw(Long distributorId, java.math.BigDecimal amount, String bankName, String bankAccount,
			String accountName);

	/**
	 * 审核提现
	 * @param withdrawId 提现ID
	 * @param status 状态：1-通过 2-拒绝
	 * @param remark 备注
	 * @return 是否成功
	 */
	Boolean auditWithdraw(Long withdrawId, Integer status, String remark);

	/**
	 * 确认打款
	 * @param withdrawId 提现ID
	 * @param payNo 支付流水号
	 * @return 是否成功
	 */
	Boolean confirmPay(Long withdrawId, String payNo);

}
