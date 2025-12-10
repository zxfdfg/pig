package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.product.api.entity.ShopPayment;
import com.pig4cloud.pig.product.service.ShopPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/payment")
@Tag(name = "支付管理", description = "支付管理接口")
public class ShopPaymentController {

	private final ShopPaymentService shopPaymentService;

	/**
	 * 创建支付单
	 * @param params 支付参数
	 * @return 支付单号
	 */
	@PostMapping("/create")
	@SysLog("创建支付单")
	@Operation(summary = "创建支付单", description = "为订单创建支付单")
	public R<String> createPayment(@RequestBody Map<String, Object> params) {
		String orderNo = (String) params.get("orderNo");
		Integer payType = Integer.valueOf(params.get("payType").toString());

		String paymentNo = shopPaymentService.createPayment(orderNo, payType);
		return R.ok(paymentNo);
	}

	/**
	 * 模拟支付
	 * @param params 支付参数
	 * @return 是否成功
	 */
	@PostMapping("/mock-pay")
	@SysLog("模拟支付")
	@Operation(summary = "模拟支付", description = "模拟支付流程（测试用）")
	public R<Boolean> mockPay(@RequestBody Map<String, String> params) {
		String paymentNo = params.get("paymentNo");
		boolean result = shopPaymentService.mockPay(paymentNo);
		return R.ok(result);
	}

	/**
	 * 查询支付状态
	 * @param orderNo 订单号
	 * @return 支付记录
	 */
	@GetMapping("/status/{orderNo}")
	@Operation(summary = "查询支付状态", description = "根据订单号查询支付状态")
	@Parameter(name = "orderNo", description = "订单号", required = true)
	public R<ShopPayment> getPaymentStatus(@PathVariable String orderNo) {
		ShopPayment payment = shopPaymentService.getPaymentByOrderNo(orderNo);
		return R.ok(payment);
	}

}
