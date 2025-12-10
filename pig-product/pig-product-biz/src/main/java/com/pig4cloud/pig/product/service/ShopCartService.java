package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.ShopCart;

import java.util.List;

/**
 * 购物车服务接口
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
public interface ShopCartService extends IService<ShopCart> {

	/**
	 * 添加到购物车
	 * @param userId 用户ID
	 * @param productId 商品ID
	 * @param skuId SKU ID
	 * @param quantity 数量
	 * @return 购物车ID
	 */
	Long addToCart(Long userId, Long productId, Long skuId, Integer quantity);

	/**
	 * 获取用户购物车列表
	 * @param userId 用户ID
	 * @return 购物车列表
	 */
	List<ShopCart> getCartList(Long userId);

	/**
	 * 更新购物车商品数量
	 * @param id 购物车ID
	 * @param quantity 数量
	 * @return 是否成功
	 */
	boolean updateQuantity(Long id, Integer quantity);

	/**
	 * 删除购物车商品
	 * @param id 购物车ID
	 * @return 是否成功
	 */
	boolean removeItem(Long id);

	/**
	 * 批量删除购物车商品
	 * @param ids 购物车ID列表
	 * @return 是否成功
	 */
	boolean removeItems(List<Long> ids);

	/**
	 * 清空用户购物车
	 * @param userId 用户ID
	 * @return 是否成功
	 */
	boolean clearCart(Long userId);

	/**
	 * 选中/取消选中商品
	 * @param id 购物车ID
	 * @param selected 是否选中：0-否，1-是
	 * @return 是否成功
	 */
	boolean selectItem(Long id, Integer selected);

	/**
	 * 全选/取消全选
	 * @param userId 用户ID
	 * @param selected 是否选中：0-否，1-是
	 * @return 是否成功
	 */
	boolean selectAll(Long userId, Integer selected);

}
