package com.pig4cloud.pig.distribution.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.distribution.api.entity.Distributor;
import com.pig4cloud.pig.distribution.api.entity.Withdraw;
import com.pig4cloud.pig.distribution.mapper.DistributorMapper;
import com.pig4cloud.pig.distribution.mapper.WithdrawMapper;
import com.pig4cloud.pig.distribution.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现服务实现
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl extends ServiceImpl<WithdrawMapper, Withdraw> implements WithdrawService {

	private final DistributorMapper distributorMapper;

	private static final BigDecimal FEE_RATE = new BigDecimal("0.01"); // 手续费率 1%

	private static final BigDecimal MIN_WITHDRAW_AMOUNT = new BigDecimal("10"); // 最小提现金额

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String applyWithdraw(Long distributorId, BigDecimal amount, String bankName, String bankAccount,
			String accountName) {
		log.info("申请提现: distributorId={}, amount={}", distributorId, amount);

		// 1. 验证提现金额
		if (amount.compareTo(MIN_WITHDRAW_AMOUNT) < 0) {
			throw new RuntimeException("提现金额不能小于" + MIN_WITHDRAW_AMOUNT + "元");
		}

		// 2. 查询分销商信息
		Distributor distributor = distributorMapper.selectById(distributorId);
		if (distributor == null) {
			throw new RuntimeException("分销商不存在");
		}

		if (distributor.getStatus() != 1) {
			throw new RuntimeException("分销商状态异常，无法提现");
		}

		// 3. 验证余额
		if (distributor.getAvailableCommission().compareTo(amount) < 0) {
			throw new RuntimeException("可用余额不足");
		}

		// 4. 计算手续费
		BigDecimal fee = amount.multiply(FEE_RATE).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal actualAmount = amount.subtract(fee);

		// 5. 创建提现记录
		Withdraw withdraw = new Withdraw();
		withdraw.setWithdrawNo("W" + IdUtil.getSnowflakeNextIdStr());
		withdraw.setDistributorId(distributorId);
		withdraw.setAmount(amount);
		withdraw.setFee(fee);
		withdraw.setActualAmount(actualAmount);
		withdraw.setAccountType(3); // 银行卡
		withdraw.setBankName(bankName);
		withdraw.setAccountNo(bankAccount);
		withdraw.setAccountName(accountName);
		withdraw.setStatus(0); // 待审核
		baseMapper.insert(withdraw);

		// 6. 冻结余额
		distributor.setAvailableCommission(distributor.getAvailableCommission().subtract(amount));
		distributor.setFrozenCommission(distributor.getFrozenCommission().add(amount));
		distributorMapper.updateById(distributor);

		log.info("提现申请成功: withdrawNo={}", withdraw.getWithdrawNo());
		return withdraw.getWithdrawNo();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean auditWithdraw(Long withdrawId, Integer status, String remark) {
		Withdraw withdraw = baseMapper.selectById(withdrawId);
		if (withdraw == null) {
			throw new RuntimeException("提现记录不存在");
		}

		if (withdraw.getStatus() != 0) {
			throw new RuntimeException("提现状态不正确");
		}

		Distributor distributor = distributorMapper.selectById(withdraw.getDistributorId());

		// 审核通过
		if (status == 1) {
			withdraw.setStatus(1);
			withdraw.setAuditUser(SecurityUtils.getUser().getUsername());
			withdraw.setAuditTime(LocalDateTime.now());
			withdraw.setRemark(remark);
			baseMapper.updateById(withdraw);

			log.info("提现审核通过: withdrawId={}", withdrawId);
		}
		// 审核拒绝
		else if (status == 3) {
			withdraw.setStatus(3);
			withdraw.setAuditUser(SecurityUtils.getUser().getUsername());
			withdraw.setAuditTime(LocalDateTime.now());
			withdraw.setRejectReason(remark);
			baseMapper.updateById(withdraw);

			// 解冻余额
			distributor.setAvailableCommission(distributor.getAvailableCommission().add(withdraw.getAmount()));
			distributor.setFrozenCommission(distributor.getFrozenCommission().subtract(withdraw.getAmount()));
			distributorMapper.updateById(distributor);

			log.info("提现审核拒绝: withdrawId={}", withdrawId);
		}
		else {
			throw new RuntimeException("审核状态不正确");
		}

		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean confirmPay(Long withdrawId, String payNo) {
		Withdraw withdraw = baseMapper.selectById(withdrawId);
		if (withdraw == null) {
			throw new RuntimeException("提现记录不存在");
		}

		if (withdraw.getStatus() != 1) {
			throw new RuntimeException("提现状态不正确");
		}

		// 更新提现状态
		withdraw.setStatus(2);
		withdraw.setPayTime(LocalDateTime.now());
		baseMapper.updateById(withdraw);

		// 扣除冻结余额，增加已提现金额
		Distributor distributor = distributorMapper.selectById(withdraw.getDistributorId());
		distributor.setFrozenCommission(distributor.getFrozenCommission().subtract(withdraw.getAmount()));
		distributor.setWithdrawnCommission(distributor.getWithdrawnCommission().add(withdraw.getAmount()));
		distributorMapper.updateById(distributor);

		log.info("提现打款成功: withdrawId={}, payNo={}", withdrawId, payNo);
		return true;
	}

}
