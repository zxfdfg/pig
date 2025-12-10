package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.product.service.CommissionConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 佣金配置控制器
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/commission/config")
@Tag(name = "佣金配置", description = "商品佣金配置接口")
public class CommissionConfigController {

	private final CommissionConfigService commissionConfigService;

	/**
	 * 分页查询佣金配置
	 * @param current 当前页
	 * @param size 每页大小
	 * @param type 类型
	 * @param productName 商品名称
	 * @return 佣金配置分页列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询佣金配置", description = "分页查询商品佣金配置列表")
	public R getConfigPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) Integer type,
			@RequestParam(required = false) String productName) {
		return R.ok(commissionConfigService.getConfigPage(current, size, type, productName));
	}

}
