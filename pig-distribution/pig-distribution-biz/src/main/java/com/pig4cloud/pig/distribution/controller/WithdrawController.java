package com.pig4cloud.pig.distribution.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.distribution.api.entity.Distributor;
import com.pig4cloud.pig.distribution.api.entity.Withdraw;
import com.pig4cloud.pig.distribution.service.DistributorService;
import com.pig4cloud.pig.distribution.service.WithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 提现管理控制器
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/withdraw")
@Tag(description = "withdraw", name = "提现管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class WithdrawController {

	private final WithdrawService withdrawService;

	private final DistributorService distributorService;

	/**
	 * 分页查询提现列表
	 * @param page 分页对象
	 * @param withdraw 查询条件
	 * @return 提现列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询提现列表")
	public R<IPage<Withdraw>> page(Page<Withdraw> page, Withdraw withdraw) {
		Long userId = SecurityUtils.getUser().getId();
		Distributor distributor = distributorService
			.getOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));

		if (distributor == null) {
			return R.ok(new Page<>());
		}

		// 只能查询自己的提现记录
		LambdaQueryWrapper<Withdraw> wrapper = new LambdaQueryWrapper<Withdraw>()
			.eq(Withdraw::getDistributorId, distributor.getId())
			.orderByDesc(Withdraw::getCreateTime);

		if (withdraw.getStatus() != null) {
			wrapper.eq(Withdraw::getStatus, withdraw.getStatus());
		}

		return R.ok(withdrawService.page(page, wrapper));
	}

	/**
	 * 申请提现
	 * @param dto 提现申请信息
	 * @return 提现单号
	 */
	@SysLog("申请提现")
	@PostMapping("/apply")
	@Operation(summary = "申请提现")
	public R<String> apply(@RequestBody WithdrawApplyDTO dto) {
		Long userId = SecurityUtils.getUser().getId();
		Distributor distributor = distributorService
			.getOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));

		if (distributor == null) {
			return R.failed("您还不是分销商");
		}

		String withdrawNo = withdrawService.applyWithdraw(distributor.getId(), dto.getAmount(), dto.getBankName(),
				dto.getBankAccount(), dto.getAccountName());
		return R.ok(withdrawNo, "申请成功");
	}

	/**
	 * 审核提现
	 * @param id 提现ID
	 * @param dto 审核信息
	 * @return 是否成功
	 */
	@SysLog("审核提现")
	@PutMapping("/audit/{id}")
	@Operation(summary = "审核提现")
	public R<Boolean> audit(@PathVariable Long id, @RequestBody WithdrawAuditDTO dto) {
		return R.ok(withdrawService.auditWithdraw(id, dto.getStatus(), dto.getRemark()));
	}

	/**
	 * 确认打款
	 * @param id 提现ID
	 * @param dto 打款信息
	 * @return 是否成功
	 */
	@SysLog("确认打款")
	@PutMapping("/pay/{id}")
	@Operation(summary = "确认打款")
	public R<Boolean> pay(@PathVariable Long id, @RequestBody WithdrawPayDTO dto) {
		return R.ok(withdrawService.confirmPay(id, dto.getPayNo()));
	}

	/**
	 * 提现申请DTO
	 */
	@Data
	public static class WithdrawApplyDTO {

		private BigDecimal amount;

		private String bankName;

		private String bankAccount;

		private String accountName;

	}

	/**
	 * 提现审核DTO
	 */
	@Data
	public static class WithdrawAuditDTO {

		private Integer status;

		private String remark;

	}

	/**
	 * 提现打款DTO
	 */
	@Data
	public static class WithdrawPayDTO {

		private String payNo;

	}

}
