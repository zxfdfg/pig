package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.product.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制器
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
@Tag(name = "统计管理", description = "商品统计接口")
public class StatisticsController {

	private final StatisticsService statisticsService;

	/**
	 * 获取概览统计
	 * @return 统计数据
	 */
	@GetMapping("/overview")
	@Operation(summary = "概览统计", description = "获取商品概览统计数据")
	public R getOverview() {
		return R.ok(statisticsService.getOverview());
	}

	/**
	 * 获取分类统计
	 * @return 分类统计数据
	 */
	@GetMapping("/category")
	@Operation(summary = "分类统计", description = "获取商品分类统计数据")
	public R getCategoryStats() {
		return R.ok(statisticsService.getCategoryStats());
	}

	/**
	 * 获取库存统计
	 * @return 库存统计数据
	 */
	@GetMapping("/stock")
	@Operation(summary = "库存统计", description = "获取商品库存统计数据")
	public R getStockStats() {
		return R.ok(statisticsService.getStockStats());
	}

}
