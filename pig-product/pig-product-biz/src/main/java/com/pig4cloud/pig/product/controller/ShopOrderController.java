package com.pig4cloud.pig.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.product.api.entity.ShopOrder;
import com.pig4cloud.pig.product.service.ShopOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单控制器
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/order")
@Tag(name = "订单管理", description = "订单管理接口")
public class ShopOrderController {

	private final ShopOrderService shopOrderService;

	/**
	 * 创建订单（从购物车）
	 * @param params 订单参数
	 * @return 订单号
	 */
	@PostMapping("/create-from-cart")
	@SysLog("从购物车创建订单")
	@Operation(summary = "从购物车创建订单", description = "从购物车中选中的商品创建订单")
	public R<String> createOrderFromCart(@RequestBody Map<String, Object> params) {
		Long userId = SecurityUtils.getUser().getId();
		String cartIds = (String) params.get("cartIds");
		Long addressId = Long.valueOf(params.get("addressId").toString());
		String remark = (String) params.get("remark");
		Long distributorId = params.get("distributorId") != null ? Long.valueOf(params.get("distributorId").toString())
				: null;

		String orderNo = shopOrderService.createOrderFromCart(userId, cartIds, addressId, remark, distributorId);
		return R.ok(orderNo);
	}

	/**
	 * 创建订单（立即购买）
	 * @param params 订单参数
	 * @return 订单号
	 */
	@PostMapping("/create-direct")
	@SysLog("立即购买创建订单")
	@Operation(summary = "立即购买创建订单", description = "直接购买商品创建订单")
	public R<String> createOrderDirect(@RequestBody Map<String, Object> params) {
		Long userId = SecurityUtils.getUser().getId();
		Long productId = Long.valueOf(params.get("productId").toString());
		Long skuId = params.get("skuId") != null ? Long.valueOf(params.get("skuId").toString()) : null;
		Integer quantity = Integer.valueOf(params.get("quantity").toString());
		Long addressId = Long.valueOf(params.get("addressId").toString());
		String remark = (String) params.get("remark");
		Long distributorId = params.get("distributorId") != null ? Long.valueOf(params.get("distributorId").toString())
				: null;

		String orderNo = shopOrderService.createOrderDirect(userId, productId, skuId, quantity, addressId, remark,
				distributorId);
		return R.ok(orderNo);
	}

	/**
	 * 分页查询订单
	 * @param page 分页参数
	 * @param status 订单状态
	 * @return 订单分页列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询订单", description = "分页查询当前用户的订单列表")
	@Parameter(name = "status", description = "订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消")
	public R<IPage<ShopOrder>> getOrderPage(Page<ShopOrder> page, @RequestParam(required = false) Integer status) {
		Long userId = SecurityUtils.getUser().getId();
		IPage<ShopOrder> orderPage = shopOrderService.getOrderPage(page, userId, status);
		return R.ok(orderPage);
	}

	/**
	 * 根据订单号查询订单
	 * @param orderNo 订单号
	 * @return 订单信息
	 */
	@GetMapping("/no/{orderNo}")
	@Operation(summary = "根据订单号查询订单", description = "根据订单号查询订单信息")
	@Parameter(name = "orderNo", description = "订单号", required = true)
	public R<ShopOrder> getOrderByNo(@PathVariable String orderNo) {
		ShopOrder order = shopOrderService.getOrderByNo(orderNo);
		return R.ok(order);
	}

	/**
	 * 查询订单详情
	 * @param id 订单ID
	 * @return 订单信息
	 */
	@GetMapping("/{id}")
	@Operation(summary = "查询订单详情", description = "根据ID查询订单详细信息")
	@Parameter(name = "id", description = "订单ID", required = true)
	public R<ShopOrder> getOrderDetail(@PathVariable Long id) {
		ShopOrder order = shopOrderService.getOrderDetail(id);
		return R.ok(order);
	}

	/**
	 * 取消订单
	 * @param id 订单ID
	 * @param params 取消参数
	 * @return 是否成功
	 */
	@PutMapping("/cancel/{id}")
	@SysLog("取消订单")
	@Operation(summary = "取消订单", description = "取消待支付订单，释放库存")
	@Parameter(name = "id", description = "订单ID", required = true)
	public R<Boolean> cancelOrder(@PathVariable Long id, @RequestBody Map<String, String> params) {
		String reason = params.get("reason");
		boolean result = shopOrderService.cancelOrder(id, reason);
		return R.ok(result);
	}

	/**
	 * 确认收货
	 * @param id 订单ID
	 * @return 是否成功
	 */
	@PutMapping("/confirm/{id}")
	@SysLog("确认收货")
	@Operation(summary = "确认收货", description = "确认收货，订单完成")
	@Parameter(name = "id", description = "订单ID", required = true)
	public R<Boolean> confirmReceive(@PathVariable Long id) {
		boolean result = shopOrderService.confirmReceive(id);
		return R.ok(result);
	}

	/**
	 * 商家发货
	 * @param id 订单ID
	 * @param params 物流信息
	 * @return 是否成功
	 */
	@PutMapping("/ship/{id}")
	@SysLog("商家发货")
	@Operation(summary = "商家发货", description = "商家发货，更新订单状态为待收货")
	@Parameter(name = "id", description = "订单ID", required = true)
	public R<Boolean> shipOrder(@PathVariable Long id, @RequestBody Map<String, String> params) {
		String logisticsCompany = params.get("logisticsCompany");
		String logisticsNo = params.get("logisticsNo");
		boolean result = shopOrderService.shipOrder(id, logisticsCompany, logisticsNo);
		return R.ok(result);
	}

	/**
	 * 商家查询所有订单（分页）
	 * @param page 分页参数
	 * @param status 订单状态
	 * @param orderNo 订单号
	 * @return 订单分页列表
	 */
	@GetMapping("/admin/page")
	@Operation(summary = "商家查询所有订单", description = "商家后台查询所有用户的订单列表")
	@Parameter(name = "status", description = "订单状态：0-待支付，1-待发货，2-待收货，3-已完成，4-已取消")
	@Parameter(name = "orderNo", description = "订单号")
	public R<IPage<ShopOrder>> getAdminOrderPage(Page<ShopOrder> page, @RequestParam(required = false) Integer status,
			@RequestParam(required = false) String orderNo) {
		IPage<ShopOrder> orderPage = shopOrderService.getAdminOrderPage(page, status, orderNo);
		return R.ok(orderPage);
	}

}
