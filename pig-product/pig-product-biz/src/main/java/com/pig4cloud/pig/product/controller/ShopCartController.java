package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.product.api.entity.ShopCart;
import com.pig4cloud.pig.product.service.ShopCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/cart")
@Tag(name = "购物车管理", description = "购物车管理接口")
public class ShopCartController {

	private final ShopCartService shopCartService;

	/**
	 * 添加到购物车
	 * @param cart 购物车信息
	 * @return 购物车ID
	 */
	@PostMapping
	@SysLog("添加到购物车")
	@Operation(summary = "添加到购物车", description = "添加商品到购物车，如已存在则增加数量")
	public R<Long> addToCart(@RequestBody ShopCart cart) {
		Long userId = SecurityUtils.getUser().getId();
		Long cartId = shopCartService.addToCart(userId, cart.getProductId(), cart.getSkuId(), cart.getQuantity());
		return R.ok(cartId);
	}

	/**
	 * 获取购物车列表
	 * @return 购物车列表
	 */
	@GetMapping("/list")
	@Operation(summary = "获取购物车列表", description = "获取当前用户的购物车列表")
	public R<List<ShopCart>> getCartList() {
		Long userId = SecurityUtils.getUser().getId();
		List<ShopCart> cartList = shopCartService.getCartList(userId);
		return R.ok(cartList);
	}

	/**
	 * 更新购物车商品数量
	 * @param id 购物车ID
	 * @param quantity 数量
	 * @return 是否成功
	 */
	@PutMapping("/{id}/quantity")
	@SysLog("更新购物车商品数量")
	@Operation(summary = "更新购物车商品数量", description = "更新购物车中商品的数量")
	@Parameter(name = "id", description = "购物车ID", required = true)
	@Parameter(name = "quantity", description = "数量", required = true)
	public R<Boolean> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
		boolean result = shopCartService.updateQuantity(id, quantity);
		return R.ok(result);
	}

	/**
	 * 删除购物车商品
	 * @param id 购物车ID
	 * @return 是否成功
	 */
	@DeleteMapping("/{id}")
	@SysLog("删除购物车商品")
	@Operation(summary = "删除购物车商品", description = "从购物车中删除指定商品")
	@Parameter(name = "id", description = "购物车ID", required = true)
	public R<Boolean> removeItem(@PathVariable Long id) {
		boolean result = shopCartService.removeItem(id);
		return R.ok(result);
	}

	/**
	 * 批量删除购物车商品
	 * @param ids 购物车ID列表
	 * @return 是否成功
	 */
	@DeleteMapping("/batch")
	@SysLog("批量删除购物车商品")
	@Operation(summary = "批量删除购物车商品", description = "批量删除购物车中的商品")
	public R<Boolean> removeItems(@RequestBody List<Long> ids) {
		boolean result = shopCartService.removeItems(ids);
		return R.ok(result);
	}

	/**
	 * 清空购物车
	 * @return 是否成功
	 */
	@DeleteMapping("/clear")
	@SysLog("清空购物车")
	@Operation(summary = "清空购物车", description = "清空当前用户的购物车")
	public R<Boolean> clearCart() {
		Long userId = SecurityUtils.getUser().getId();
		boolean result = shopCartService.clearCart(userId);
		return R.ok(result);
	}

	/**
	 * 选中/取消选中商品
	 * @param id 购物车ID
	 * @param selected 是否选中：0-否，1-是
	 * @return 是否成功
	 */
	@PutMapping("/{id}/select")
	@SysLog("选中/取消选中商品")
	@Operation(summary = "选中/取消选中商品", description = "设置购物车商品的选中状态")
	@Parameter(name = "id", description = "购物车ID", required = true)
	@Parameter(name = "selected", description = "是否选中：0-否，1-是", required = true)
	public R<Boolean> selectItem(@PathVariable Long id, @RequestParam Integer selected) {
		boolean result = shopCartService.selectItem(id, selected);
		return R.ok(result);
	}

	/**
	 * 全选/取消全选
	 * @param selected 是否选中：0-否，1-是
	 * @return 是否成功
	 */
	@PutMapping("/select-all")
	@SysLog("全选/取消全选")
	@Operation(summary = "全选/取消全选", description = "设置购物车所有商品的选中状态")
	@Parameter(name = "selected", description = "是否选中：0-否，1-是", required = true)
	public R<Boolean> selectAll(@RequestParam Integer selected) {
		Long userId = SecurityUtils.getUser().getId();
		boolean result = shopCartService.selectAll(userId, selected);
		return R.ok(result);
	}

}
