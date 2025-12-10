package com.pig4cloud.pig.product.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.product.api.entity.ShopOrder;
import com.pig4cloud.pig.product.api.entity.ShopPayment;
import com.pig4cloud.pig.product.mapper.ShopPaymentMapper;
import com.pig4cloud.pig.product.service.ShopOrderService;
import com.pig4cloud.pig.product.service.ShopPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 支付服务实现
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopPaymentServiceImpl extends ServiceImpl<ShopPaymentMapper, ShopPayment>
		implements ShopPaymentService {

	private final ShopOrderService shopOrderService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createPayment(String orderNo, Integer payType) {
		Assert.hasText(orderNo, "订单号不能为空");
		Assert.notNull(payType, "支付方式不能为空");

		// 查询订单
		ShopOrder order = shopOrderService.getOrderByNo(orderNo);
		Assert.notNull(order, "订单不存在");
		Assert.isTrue(order.getStatus() == 0, "订单状态异常");

		// 生成支付单号
		String paymentNo = "PAY" + IdUtil.getSnowflakeNextIdStr();

		// 创建支付记录
		ShopPayment payment = new ShopPayment();
		payment.setPaymentNo(paymentNo);
		payment.setOrderNo(orderNo);
		payment.setUserId(order.getUserId());
		payment.setPayAmount(order.getPayAmount());
		payment.setPayType(payType);
		payment.setStatus(0); // 待支付

		this.save(payment);

		log.info("创建支付单成功，支付单号：{}，订单号：{}", paymentNo, orderNo);
		return paymentNo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean mockPay(String paymentNo) {
		Assert.hasText(paymentNo, "支付单号不能为空");

		// 查询支付记录
		LambdaQueryWrapper<ShopPayment> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopPayment::getPaymentNo, paymentNo);

		ShopPayment payment = this.getOne(wrapper);
		Assert.notNull(payment, "支付记录不存在");
		Assert.isTrue(payment.getStatus() == 0, "支付状态异常");

		// 模拟第三方交易号
		String tradeNo = "MOCK" + IdUtil.getSnowflakeNextIdStr();

		// 更新支付状态
		payment.setStatus(1); // 支付成功
		payment.setTradeNo(tradeNo);
		payment.setPayTime(LocalDateTime.now());

		boolean result = this.updateById(payment);

		// 回调订单服务
		shopOrderService.paySuccess(payment.getOrderNo(), payment.getPayType(), tradeNo);

		log.info("模拟支付成功，支付单号：{}，订单号：{}", paymentNo, payment.getOrderNo());
		return result;
	}

	@Override
	public ShopPayment getPaymentByOrderNo(String orderNo) {
		Assert.hasText(orderNo, "订单号不能为空");

		LambdaQueryWrapper<ShopPayment> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopPayment::getOrderNo, orderNo).orderByDesc(ShopPayment::getCreateTime).last("LIMIT 1");

		return this.getOne(wrapper);
	}

}
