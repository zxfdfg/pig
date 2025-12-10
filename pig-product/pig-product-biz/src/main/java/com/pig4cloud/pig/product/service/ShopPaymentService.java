package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.ShopPayment;

/**
 * 支付服务接口
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
public interface ShopPaymentService extends IService<ShopPayment> {

	/**
	 * 创建支付单
	 * @param orderNo 订单号
	 * @param payType 支付方式：1-微信，2-支付宝，3-余额
	 * @return 支付单号
	 */
	String createPayment(String orderNo, Integer payType);

	/**
	 * 模拟支付
	 * @param paymentNo 支付单号
	 * @return 是否成功
	 */
	boolean mockPay(String paymentNo);

	/**
	 * 查询支付状态
	 * @param orderNo 订单号
	 * @return 支付记录
	 */
	ShopPayment getPaymentByOrderNo(String orderNo);

}
