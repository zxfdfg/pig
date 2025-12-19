package com.pig4cloud.pig.product.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.distribution.api.feign.RemoteCommissionService;
import com.pig4cloud.pig.product.api.entity.*;
import com.pig4cloud.pig.product.mapper.*;
import com.pig4cloud.pig.product.service.ShopOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopOrderServiceImpl extends ServiceImpl<ShopOrderMapper, ShopOrder> implements ShopOrderService {

	private final ShopCartMapper shopCartMapper;

	private final ShopAddressMapper shopAddressMapper;

	private final ProductMapper productMapper;

	private final SkuMapper skuMapper;

	private final ShopOrderItemMapper shopOrderItemMapper;

	private final RemoteCommissionService remoteCommissionService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOrderFromCart(Long userId, String cartIds, Long addressId, String remark, Long distributorId) {
		Assert.notNull(userId, "用户ID不能为空");
		Assert.hasText(cartIds, "购物车ID不能为空");
		Assert.notNull(addressId, "收货地址ID不能为空");

		// 查询购物车商品
		List<Long> cartIdList = Arrays.stream(cartIds.split(",")).map(Long::parseLong).collect(Collectors.toList());

		List<ShopCart> cartList = shopCartMapper.selectBatchIds(cartIdList);
		Assert.notEmpty(cartList, "购物车商品不存在");

		// 创建订单
		String orderNo = createOrder(userId, cartList, addressId, remark, distributorId);

		// 清空购物车
		shopCartMapper.deleteBatchIds(cartIdList);

		log.info("从购物车创建订单成功，订单号：{}", orderNo);
		return orderNo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOrderDirect(Long userId, Long productId, Long skuId, Integer quantity, Long addressId,
			String remark, Long distributorId) {
		Assert.notNull(userId, "用户ID不能为空");
		Assert.notNull(productId, "商品ID不能为空");
		Assert.notNull(quantity, "数量不能为空");
		Assert.isTrue(quantity > 0, "数量必须大于0");
		Assert.notNull(addressId, "收货地址ID不能为空");

		// 构造购物车数据
		ShopCart cart = new ShopCart();
		cart.setUserId(userId);
		cart.setProductId(productId);
		cart.setSkuId(skuId != null && skuId > 0 ? skuId : 0L);
		cart.setQuantity(quantity);

		List<ShopCart> cartList = new ArrayList<>();
		cartList.add(cart);

		// 创建订单
		String orderNo = createOrder(userId, cartList, addressId, remark, distributorId);

		log.info("立即购买创建订单成功，订单号：{}", orderNo);
		return orderNo;
	}

	/**
	 * 创建订单（核心逻辑）
	 */
	private String createOrder(Long userId, List<ShopCart> cartList, Long addressId, String remark,
			Long distributorId) {
		// 查询收货地址
		ShopAddress address = shopAddressMapper.selectById(addressId);
		Assert.notNull(address, "收货地址不存在");
		Assert.isTrue(address.getUserId().equals(userId), "无权使用该地址");

		// 生成订单号
		String orderNo = "ORDER" + IdUtil.getSnowflakeNextIdStr();

		// 计算订单金额
		BigDecimal totalAmount = BigDecimal.ZERO;
		List<ShopOrderItem> orderItems = new ArrayList<>();

		for (ShopCart cart : cartList) {
			// 查询商品信息
			Product product = productMapper.selectById(cart.getProductId());
			Assert.notNull(product, "商品不存在：" + cart.getProductId());
			Assert.isTrue(product.getStatus() == 1, "商品未上架：" + product.getName());

			// 获取价格和库存
			BigDecimal price = product.getPrice();
			Integer stock = product.getStock();
			String skuName = null;

			// 如果有SKU，使用SKU的价格和库存
			if (cart.getSkuId() != null && cart.getSkuId() > 0) {
				ProductSku sku = skuMapper.selectById(cart.getSkuId());
				Assert.notNull(sku, "SKU不存在：" + cart.getSkuId());
				Assert.isTrue(sku.getStatus() == 1, "SKU未上架");
				price = sku.getPrice();
				stock = sku.getStock();
				skuName = sku.getSkuName();
			}

			// 验证库存
			Assert.isTrue(stock >= cart.getQuantity(), "库存不足：" + product.getName());

			// 扣减库存
			if (cart.getSkuId() != null && cart.getSkuId() > 0) {
				ProductSku sku = skuMapper.selectById(cart.getSkuId());
				sku.setStock(sku.getStock() - cart.getQuantity());
				skuMapper.updateById(sku);
			}
			else {
				product.setStock(product.getStock() - cart.getQuantity());
				productMapper.updateById(product);
			}

			// 计算小计
			BigDecimal itemTotal = price.multiply(new BigDecimal(cart.getQuantity()));
			totalAmount = totalAmount.add(itemTotal);

			// 创建订单明细
			ShopOrderItem orderItem = new ShopOrderItem();
			orderItem.setOrderNo(orderNo);
			orderItem.setProductId(product.getId());
			orderItem.setProductName(product.getName());
			orderItem.setProductImage(product.getCoverImage());
			orderItem.setSkuId(cart.getSkuId() != null && cart.getSkuId() > 0 ? cart.getSkuId() : 0L);
			orderItem.setSkuName(skuName);
			orderItem.setPrice(price);
			orderItem.setQuantity(cart.getQuantity());
			orderItem.setTotalAmount(itemTotal);
			orderItem.setCommissionAmount(BigDecimal.ZERO); // 佣金后续计算

			orderItems.add(orderItem);
		}

		// 创建订单
		ShopOrder order = new ShopOrder();
		order.setOrderNo(orderNo);
		order.setUserId(userId);
		order.setDistributorId(distributorId);
		order.setTotalAmount(totalAmount);
		order.setPayAmount(totalAmount);
		order.setDiscountAmount(BigDecimal.ZERO);
		order.setStatus(0); // 待支付
		order.setPayStatus(0); // 未支付
		order.setReceiverName(address.getReceiverName());
		order.setReceiverPhone(address.getReceiverPhone());
		order.setReceiverProvince(address.getProvince());
		order.setReceiverCity(address.getCity());
		order.setReceiverDistrict(address.getDistrict());
		order.setReceiverAddress(address.getAddress());
		order.setRemark(remark);

		this.save(order);

		// 保存订单明细
		orderItems.forEach(item -> item.setOrderId(order.getId()));
		orderItems.forEach(shopOrderItemMapper::insert);

		log.info("创建订单成功，订单号：{}，金额：{}", orderNo, totalAmount);
		return orderNo;
	}

	@Override
	public IPage<ShopOrder> getOrderPage(Page<ShopOrder> page, Long userId, Integer status) {
		LambdaQueryWrapper<ShopOrder> wrapper = Wrappers.lambdaQuery();

		if (userId != null) {
			wrapper.eq(ShopOrder::getUserId, userId);
		}

		if (status != null) {
			wrapper.eq(ShopOrder::getStatus, status);
		}

		wrapper.orderByDesc(ShopOrder::getCreateTime);

		IPage<ShopOrder> orderPage = this.page(page, wrapper);

		// 查询订单明细
		orderPage.getRecords().forEach(order -> {
			LambdaQueryWrapper<ShopOrderItem> itemWrapper = Wrappers.lambdaQuery();
			itemWrapper.eq(ShopOrderItem::getOrderId, order.getId());
			List<ShopOrderItem> items = shopOrderItemMapper.selectList(itemWrapper);
			order.setItems(items);
		});

		return orderPage;
	}

	@Override
	public ShopOrder getOrderDetail(Long id) {
		Assert.notNull(id, "订单ID不能为空");
		
		ShopOrder order = this.getById(id);
		if (order == null) {
			return null;
		}
		
		// 查询订单明细
		LambdaQueryWrapper<ShopOrderItem> itemWrapper = Wrappers.lambdaQuery();
		itemWrapper.eq(ShopOrderItem::getOrderId, order.getId());
		List<ShopOrderItem> items = shopOrderItemMapper.selectList(itemWrapper);
		order.setItems(items);
		
		return order;
	}

	@Override
	public ShopOrder getOrderByNo(String orderNo) {
		Assert.hasText(orderNo, "订单号不能为空");

		LambdaQueryWrapper<ShopOrder> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopOrder::getOrderNo, orderNo);

		return this.getOne(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean cancelOrder(Long id, String reason) {
		Assert.notNull(id, "订单ID不能为空");

		ShopOrder order = this.getById(id);
		Assert.notNull(order, "订单不存在");
		Assert.isTrue(order.getStatus() == 0, "只能取消待支付订单");

		// 释放库存
		releaseStock(order.getOrderNo());

		// 取消佣金（如果订单已支付且有分销商）
		if (order.getDistributorId() != null && order.getPayStatus() == 1) {
			try {
				log.info("取消订单，同时取消佣金，订单ID：{}", id);
				R<Boolean> result = remoteCommissionService.cancelCommission(id);
				if (result.getCode() != 0) {
					log.error("取消佣金失败: {}", result.getMsg());
				}
				else {
					log.info("订单 {} 佣金取消成功", order.getOrderNo());
				}
			}
			catch (Exception e) {
				log.error("取消佣金异常", e);
				// 不影响订单取消流程
			}
		}

		// 更新订单状态
		order.setStatus(4); // 已取消
		order.setCancelReason(reason);
		order.setCancelTime(LocalDateTime.now());

		boolean result = this.updateById(order);
		log.info("取消订单成功，订单号：{}", order.getOrderNo());
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean confirmReceive(Long id) {
		Assert.notNull(id, "订单ID不能为空");

		ShopOrder order = this.getById(id);
		Assert.notNull(order, "订单不存在");
		Assert.isTrue(order.getStatus() == 2, "只能确认已发货订单");

		// 更新订单状态
		order.setStatus(3); // 已完成

		boolean result = this.updateById(order);
		log.info("确认收货成功，订单号：{}", order.getOrderNo());
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean paySuccess(String orderNo, Integer payType, String tradeNo) {
		Assert.hasText(orderNo, "订单号不能为空");

		ShopOrder order = getOrderByNo(orderNo);
		Assert.notNull(order, "订单不存在");
		Assert.isTrue(order.getStatus() == 0, "订单状态异常");

		// 更新订单状态
		order.setStatus(1); // 已支付
		order.setPayStatus(1); // 已支付
		order.setPayType(payType);
		order.setPayTime(LocalDateTime.now());

		boolean result = this.updateById(order);

		// 触发佣金计算（调用分销系统接口）
		if (order.getDistributorId() != null) {
			try {
				log.info("订单支付成功，开始计算佣金，订单号：{}，分销商ID：{}", orderNo, order.getDistributorId());
				calculateCommission(order);
			}
			catch (Exception e) {
				// 佣金计算失败不影响订单支付流程
				log.error("订单 {} 佣金计算失败", orderNo, e);
			}
		}

		log.info("订单支付成功，订单号：{}", orderNo);
		return result;
	}

	/**
	 * 计算订单佣金
	 */
	private void calculateCommission(ShopOrder order) {
		log.info("触发佣金计算：订单ID={}，订单号={}，购买者ID={}，订单金额={}", order.getId(), order.getOrderNo(),
				order.getUserId(), order.getPayAmount());

		try {
			R<Boolean> result = remoteCommissionService.calculateCommission(order.getId(), order.getOrderNo(),
					order.getUserId(), order.getPayAmount());

			if (result.getCode() == 0 && Boolean.TRUE.equals(result.getData())) {
				log.info("订单 {} 佣金计算成功", order.getOrderNo());
			}
			else {
				log.error("订单 {} 佣金计算失败: {}", order.getOrderNo(), result.getMsg());
			}
		}
		catch (Exception e) {
			log.error("订单 {} 佣金计算异常", order.getOrderNo(), e);
			throw e;
		}
	}

	/**
	 * 释放库存
	 */
	private void releaseStock(String orderNo) {
		LambdaQueryWrapper<ShopOrderItem> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopOrderItem::getOrderNo, orderNo);

		List<ShopOrderItem> orderItems = shopOrderItemMapper.selectList(wrapper);

		for (ShopOrderItem item : orderItems) {
			if (item.getSkuId() != null && item.getSkuId() > 0) {
				// 释放SKU库存
				ProductSku sku = skuMapper.selectById(item.getSkuId());
				if (sku != null) {
					sku.setStock(sku.getStock() + item.getQuantity());
					skuMapper.updateById(sku);
				}
			}
			else {
				// 释放商品库存
				Product product = productMapper.selectById(item.getProductId());
				if (product != null) {
					product.setStock(product.getStock() + item.getQuantity());
					productMapper.updateById(product);
				}
			}
		}

		log.info("释放库存成功，订单号：{}", orderNo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean shipOrder(Long id, String logisticsCompany, String logisticsNo) {
		Assert.notNull(id, "订单ID不能为空");
		Assert.hasText(logisticsCompany, "物流公司不能为空");
		Assert.hasText(logisticsNo, "物流单号不能为空");

		ShopOrder order = this.getById(id);
		Assert.notNull(order, "订单不存在");
		Assert.isTrue(order.getStatus() == 1, "订单状态异常，只有待发货订单才能发货");

		// 更新订单状态
		order.setStatus(2); // 待收货
		order.setShipTime(LocalDateTime.now());
		order.setLogisticsCompany(logisticsCompany);
		order.setLogisticsNo(logisticsNo);

		log.info("订单发货成功，订单号：{}，物流公司：{}，物流单号：{}", order.getOrderNo(), logisticsCompany, logisticsNo);
		return this.updateById(order);
	}

	@Override
	public IPage<ShopOrder> getAdminOrderPage(Page<ShopOrder> page, Integer status, String orderNo) {
		LambdaQueryWrapper<ShopOrder> wrapper = Wrappers.lambdaQuery();

		if (status != null) {
			wrapper.eq(ShopOrder::getStatus, status);
		}

		if (StringUtils.hasText(orderNo)) {
			wrapper.like(ShopOrder::getOrderNo, orderNo);
		}

		wrapper.orderByDesc(ShopOrder::getCreateTime);

		IPage<ShopOrder> orderPage = this.page(page, wrapper);

		// 查询订单明细
		orderPage.getRecords().forEach(order -> {
			LambdaQueryWrapper<ShopOrderItem> itemWrapper = Wrappers.lambdaQuery();
			itemWrapper.eq(ShopOrderItem::getOrderId, order.getId());
			List<ShopOrderItem> items = shopOrderItemMapper.selectList(itemWrapper);
			order.setItems(items);
		});

		return orderPage;
	}

}
