package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductCategory;
import com.pig4cloud.pig.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理控制器
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@Tag(name = "商品分类管理", description = "商品分类管理接口")
public class CategoryController {

	private final CategoryService categoryService;

	/**
	 * 创建分类
	 * @param category 分类信息
	 * @return 分类ID
	 */
	@PostMapping
	@SysLog("创建商品分类")
	@Operation(summary = "创建分类", description = "创建商品分类")
	@PreAuthorize("@pms.hasPermission('product:category:add')")
	public R<Long> createCategory(@RequestBody ProductCategory category) {
		Long categoryId = categoryService.createCategory(category);
		return R.ok(categoryId);
	}

	/**
	 * 更新分类
	 * @param category 分类信息
	 * @return 是否成功
	 */
	@PutMapping
	@SysLog("更新商品分类")
	@Operation(summary = "更新分类", description = "更新商品分类信息")
	@PreAuthorize("@pms.hasPermission('product:category:edit')")
	public R<Boolean> updateCategory(@RequestBody ProductCategory category) {
		boolean result = categoryService.updateCategory(category);
		return R.ok(result);
	}

	/**
	 * 删除分类
	 * @param id 分类ID
	 * @return 是否成功
	 */
	@DeleteMapping("/{id}")
	@SysLog("删除商品分类")
	@Operation(summary = "删除分类", description = "删除商品分类（验证无商品）")
	@Parameter(name = "id", description = "分类ID", required = true)
	@PreAuthorize("@pms.hasPermission('product:category:del')")
	public R<Boolean> deleteCategory(@PathVariable Long id) {
		boolean result = categoryService.deleteCategory(id);
		return R.ok(result);
	}

	/**
	 * 查询分类树
	 * @return 分类树列表
	 */
	@GetMapping("/tree")
	@Operation(summary = "查询分类树", description = "查询所有分类的树形结构")
	public R<List<ProductCategory>> getCategoryTree() {
		List<ProductCategory> tree = categoryService.getCategoryTree();
		return R.ok(tree);
	}

	/**
	 * 查询分类下的商品
	 * @param id 分类ID
	 * @return 商品列表
	 */
	@GetMapping("/{id}/products")
	@Operation(summary = "查询分类商品", description = "查询分类及其子分类下的所有商品")
	@Parameter(name = "id", description = "分类ID", required = true)
	public R<List<Product>> getProductsByCategory(@PathVariable Long id) {
		List<Product> products = categoryService.getProductsByCategory(id);
		return R.ok(products);
	}

}
