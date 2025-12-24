package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.ShopOrder;

/**
 * 订单服务接口
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
public interface ShopOrderService extends IService<ShopOrder> {

	/**
	 * 创建订单（从购物车）
	 * @param userId 用户ID
	 * @param cartIds 购物车ID列表
	 * @param addressId 收货地址ID
	 * @param remark 订单备注
	 * @param distributorId 推荐分销商ID
	 * @return 订单号
	 */
	String createOrderFromCart(Long userId, String cartIds, Long addressId, String remark, Long distributorId);

	/**
	 * 创建订单（立即购买）
	 * @param userId 用户ID
	 * @param productId 商品ID
	 * @param skuId SKU ID
	 * @param quantity 数量
	 * @param addressId 收货地址ID
	 * @param remark 订单备注
	 * @param distributorId 推荐分销商ID
	 * @return 订单号
	 */
	String createOrderDirect(Long userId, Long productId, Long skuId, Integer quantity, Long addressId, String remark,
			Long distributorId);

	/**
	 * 分页查询订单
	 * @param page 分页参数
	 * @param userId 用户ID
	 * @param status 订单状态
	 * @return 订单分页列表
	 */
	IPage<ShopOrder> getOrderPage(Page<ShopOrder> page, Long userId, Integer status);

	/**
	 * 查询订单详情
	 * @param id 订单ID
	 * @return 订单信息
	 */
	ShopOrder getOrderDetail(Long id);

	/**
	 * 根据订单号查询订单
	 * @param orderNo 订单号
	 * @return 订单信息
	 */
	ShopOrder getOrderByNo(String orderNo);

	/**
	 * 取消订单
	 * @param id 订单ID
	 * @param reason 取消原因
	 * @return 是否成功
	 */
	boolean cancelOrder(Long id, String reason);

	/**
	 * 确认收货
	 * @param id 订单ID
	 * @return 是否成功
	 */
	boolean confirmReceive(Long id);

	/**
	 * 订单支付成功回调
	 * @param orderNo 订单号
	 * @param payType 支付方式
	 * @param tradeNo 第三方交易号
	 * @return 是否成功
	 */
	boolean paySuccess(String orderNo, Integer payType, String tradeNo);

	/**
	 * 商家发货
	 * @param id 订单ID
	 * @param logisticsCompany 物流公司
	 * @param logisticsNo 物流单号
	 * @return 是否成功
	 */
	boolean shipOrder(Long id, String logisticsCompany, String logisticsNo);

	/**
	 * 商家查询所有订单（分页）
	 * @param page 分页参数
	 * @param status 订单状态
	 * @param orderNo 订单号
	 * @return 订单分页列表
	 */
	IPage<ShopOrder> getAdminOrderPage(Page<ShopOrder> page, Integer status, String orderNo);

}
