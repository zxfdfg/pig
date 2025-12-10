package com.pig4cloud.pig.distribution.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.distribution.api.entity.Distributor;
import com.pig4cloud.pig.distribution.api.entity.OrderCommission;
import com.pig4cloud.pig.distribution.service.CommissionService;
import com.pig4cloud.pig.distribution.service.DistributorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 佣金管理控制器
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/commission")
@Tag(description = "commission", name = "佣金管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CommissionController {

	private final CommissionService commissionService;

	private final DistributorService distributorService;

	/**
	 * 分页查询佣金列表
	 * @param page 分页对象
	 * @param commission 查询条件
	 * @return 佣金列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询佣金列表")
	public R<IPage<OrderCommission>> page(Page<OrderCommission> page, OrderCommission commission) {
		// 获取当前用户的分销商信息
		Long userId = SecurityUtils.getUser().getId();
		Distributor distributor = distributorService
			.getOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));

		if (distributor == null) {
			return R.ok(new Page<>());
		}

		// 只能查询自己的佣金
		LambdaQueryWrapper<OrderCommission> wrapper = new LambdaQueryWrapper<OrderCommission>()
			.eq(OrderCommission::getDistributorId, distributor.getId())
			.orderByDesc(OrderCommission::getCreateTime);

		return R.ok(commissionService.page(page, wrapper));
	}

	/**
	 * 获取佣金统计
	 * @return 统计数据
	 */
	@GetMapping("/stats")
	@Operation(summary = "获取佣金统计")
	public R<Map<String, Object>> getStats() {
		Long userId = SecurityUtils.getUser().getId();
		Distributor distributor = distributorService
			.getOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));

		if (distributor == null) {
			return R.failed("您还不是分销商");
		}

		return R.ok(commissionService.getCommissionStats(distributor.getId()));
	}

	/**
	 * 计算订单佣金（内部接口）
	 * @param orderId 订单ID
	 * @param orderNo 订单号
	 * @param buyerId 购买者ID
	 * @param orderAmount 订单金额
	 * @return 是否成功
	 */
	@SysLog("计算订单佣金")
	@PostMapping("/calculate")
	@Operation(summary = "计算订单佣金")
	public R<Boolean> calculate(@RequestParam Long orderId, @RequestParam String orderNo, @RequestParam Long buyerId,
			@RequestParam BigDecimal orderAmount) {
		return R.ok(commissionService.calculateCommission(orderId, orderNo, buyerId, orderAmount));
	}

	/**
	 * 结算佣金
	 * @param commissionId 佣金ID
	 * @return 是否成功
	 */
	@SysLog("结算佣金")
	@PutMapping("/settle/{commissionId}")
	@Operation(summary = "结算佣金")
	public R<Boolean> settle(@PathVariable Long commissionId) {
		return R.ok(commissionService.settleCommission(commissionId));
	}

	/**
	 * 取消订单佣金（内部接口）
	 * @param orderId 订单ID
	 * @return 是否成功
	 */
	@SysLog("取消订单佣金")
	@PostMapping("/cancel")
	@Operation(summary = "取消订单佣金")
	public R<Boolean> cancel(@RequestParam Long orderId) {
		return R.ok(commissionService.cancelCommission(orderId));
	}

}
