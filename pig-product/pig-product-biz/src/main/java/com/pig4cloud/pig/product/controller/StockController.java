package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductStockLog;
import com.pig4cloud.pig.product.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理控制器
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
@Tag(name = "库存管理", description = "库存管理接口")
public class StockController {

	private final StockService stockService;

	/**
	 * 分页查询库存
	 * @param current 当前页
	 * @param size 每页大小
	 * @param productName 商品名称
	 * @param stockStatus 库存状态
	 * @return 库存分页列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询库存", description = "分页查询商品库存列表")
	public R getStockPage(@RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "10") Integer size,
			@RequestParam(required = false) String productName,
			@RequestParam(required = false) String stockStatus) {
		return R.ok(stockService.getStockPage(current, size, productName, stockStatus));
	}

	/**
	 * 查询低库存商品
	 * @return 低库存商品列表
	 */
	@GetMapping("/low")
	@Operation(summary = "查询低库存商品", description = "查询库存低于预警值的商品列表")
	public R<List<Product>> getLowStockProducts() {
		List<Product> products = stockService.getLowStockProducts();
		return R.ok(products);
	}

	/**
	 * 增加库存
	 * @param productId 商品ID
	 * @param quantity 增加数量
	 * @param remark 备注
	 * @return 是否成功
	 */
	@PutMapping("/increase")
	@SysLog("增加库存")
	@Operation(summary = "增加库存", description = "增加商品库存并记录日志")
	@PreAuthorize("@pms.hasPermission('product:stock:increase')")
	public R<Boolean> increaseStock(@RequestParam Long productId, @RequestParam Integer quantity,
			@RequestParam(required = false) String remark) {
		boolean result = stockService.increaseStock(productId, quantity, remark);
		return R.ok(result);
	}

	/**
	 * 减少库存
	 * @param productId 商品ID
	 * @param quantity 减少数量
	 * @param remark 备注
	 * @return 是否成功
	 */
	@PutMapping("/decrease")
	@SysLog("减少库存")
	@Operation(summary = "减少库存", description = "减少商品库存，验证库存充足")
	@PreAuthorize("@pms.hasPermission('product:stock:decrease')")
	public R<Boolean> decreaseStock(@RequestParam Long productId, @RequestParam Integer quantity,
			@RequestParam(required = false) String remark) {
		boolean result = stockService.decreaseStock(productId, quantity, remark);
		return R.ok(result);
	}

	/**
	 * 查询商品库存
	 * @param productId 商品ID
	 * @return 库存数量
	 */
	@GetMapping("/{productId}")
	@Operation(summary = "查询商品库存", description = "查询指定商品的当前库存")
	@Parameter(name = "productId", description = "商品ID", required = true)
	public R<Integer> getStock(@PathVariable Long productId) {
		Integer stock = stockService.getStock(productId);
		return R.ok(stock);
	}

	/**
	 * 查询库存日志
	 * @param productId 商品ID
	 * @return 库存日志列表
	 */
	@GetMapping("/log/{productId}")
	@Operation(summary = "查询库存日志", description = "查询指定商品的库存变更日志")
	@Parameter(name = "productId", description = "商品ID", required = true)
	public R<List<ProductStockLog>> getStockLogs(@PathVariable Long productId) {
		List<ProductStockLog> logs = stockService.getStockLogs(productId);
		return R.ok(logs);
	}

}
