package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.product.service.CdkeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CDKey管理控制器
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/cdkey")
@Tag(name = "CDKey管理", description = "CDKey管理接口")
public class CdkeyController {

	private final CdkeyService cdkeyService;

	/**
	 * 分页查询CDKey
	 * @param current 当前页
	 * @param size 每页大小
	 * @param productName 商品名称
	 * @param cdkey CDKey
	 * @param status 状态
	 * @return CDKey分页列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询CDKey", description = "分页查询CDKey列表")
	public R getCdkeyPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) String productName,
			@RequestParam(required = false) String cdkey, @RequestParam(required = false) Integer status) {
		return R.ok(cdkeyService.getCdkeyPage(current, size, productName, cdkey, status));
	}

}
