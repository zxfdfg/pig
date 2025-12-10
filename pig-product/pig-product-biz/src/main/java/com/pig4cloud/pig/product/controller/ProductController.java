package com.pig4cloud.pig.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理控制器
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@Tag(name = "商品管理", description = "商品管理接口")
public class ProductController {

	private final ProductService productService;

	/**
	 * 创建商品
	 * @param product 商品信息
	 * @return 商品ID
	 */
	@PostMapping
	@SysLog("创建商品")
	@Operation(summary = "创建商品", description = "创建新商品，验证必填字段和价格")
	public R<Long> createProduct(@RequestBody Product product) {
		Long productId = productService.createProduct(product);
		return R.ok(productId);
	}

	/**
	 * 更新商品
	 * @param product 商品信息
	 * @return 是否成功
	 */
	@PutMapping
	@SysLog("更新商品")
	@Operation(summary = "更新商品", description = "更新商品信息，自动记录修改时间")
	public R<Boolean> updateProduct(@RequestBody Product product) {
		boolean result = productService.updateProduct(product);
		return R.ok(result);
	}

	/**
	 * 删除商品
	 * @param id 商品ID
	 * @return 是否成功
	 */
	@DeleteMapping("/{id}")
	@SysLog("删除商品")
	@Operation(summary = "删除商品", description = "删除商品（验证无订单）")
	@Parameter(name = "id", description = "商品ID", required = true)
	public R<Boolean> deleteProduct(@PathVariable Long id) {
		boolean result = productService.deleteProduct(id);
		return R.ok(result);
	}

	/**
	 * 查询商品详情
	 * @param id 商品ID
	 * @return 商品信息
	 */
	@GetMapping("/{id}")
	@Operation(summary = "查询商品详情", description = "根据ID查询商品详细信息")
	@Parameter(name = "id", description = "商品ID", required = true)
	public R<Product> getProductDetail(@PathVariable Long id) {
		Product product = productService.getProductDetail(id);
		return R.ok(product);
	}

	/**
	 * 分页查询商品
	 * @param page 分页参数
	 * @param product 查询条件
	 * @return 商品分页列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询商品", description = "分页查询商品列表，支持名称、分类、状态等筛选")
	public R<IPage<Product>> getProductPage(Page<Product> page, Product product) {
		IPage<Product> productPage = productService.getProductPage(page, product);
		return R.ok(productPage);
	}

	/**
	 * 更新商品状态
	 * @param id 商品ID
	 * @param status 状态：1-上架，2-下架
	 * @return 是否成功
	 */
	@PutMapping("/status/{id}")
	@SysLog("更新商品状态")
	@Operation(summary = "更新商品状态", description = "商品上下架管理")
	@Parameter(name = "id", description = "商品ID", required = true)
	@Parameter(name = "status", description = "状态：1-上架，2-下架", required = true)
	public R<Boolean> updateProductStatus(@PathVariable Long id, @RequestParam Integer status) {
		boolean result = productService.updateProductStatus(id, status);
		return R.ok(result);
	}

}
