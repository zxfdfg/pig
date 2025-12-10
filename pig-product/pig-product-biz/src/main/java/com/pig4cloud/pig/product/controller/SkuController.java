package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.product.api.entity.ProductSku;
import com.pig4cloud.pig.product.service.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SKU管理控制器
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sku")
@Tag(name = "SKU管理", description = "SKU多规格管理接口")
public class SkuController {

	private final SkuService skuService;

	/**
	 * 创建SKU
	 * @param sku SKU信息
	 * @return SKU ID
	 */
	@PostMapping
	@SysLog("创建SKU")
	@Operation(summary = "创建SKU", description = "创建商品SKU")
	public R<Long> createSku(@RequestBody ProductSku sku) {
		Long skuId = skuService.createSku(sku);
		return R.ok(skuId);
	}

	/**
	 * 更新SKU
	 * @param sku SKU信息
	 * @return 是否成功
	 */
	@PutMapping
	@SysLog("更新SKU")
	@Operation(summary = "更新SKU", description = "更新SKU信息")
	public R<Boolean> updateSku(@RequestBody ProductSku sku) {
		boolean result = skuService.updateSku(sku);
		return R.ok(result);
	}

	/**
	 * 删除SKU
	 * @param id SKU ID
	 * @return 是否成功
	 */
	@DeleteMapping("/{id}")
	@SysLog("删除SKU")
	@Operation(summary = "删除SKU", description = "删除SKU（验证无订单）")
	@Parameter(name = "id", description = "SKU ID", required = true)
	public R<Boolean> deleteSku(@PathVariable Long id) {
		boolean result = skuService.deleteSku(id);
		return R.ok(result);
	}

	/**
	 * 查询商品的所有SKU
	 * @param productId 商品ID
	 * @return SKU列表
	 */
	@GetMapping("/product/{productId}")
	@Operation(summary = "查询商品SKU", description = "查询指定商品的所有SKU")
	@Parameter(name = "productId", description = "商品ID", required = true)
	public R<List<ProductSku>> getSkusByProductId(@PathVariable Long productId) {
		List<ProductSku> skus = skuService.getSkusByProductId(productId);
		return R.ok(skus);
	}

}
