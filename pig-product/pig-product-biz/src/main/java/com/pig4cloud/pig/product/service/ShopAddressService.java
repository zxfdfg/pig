package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.ShopAddress;

import java.util.List;

/**
 * 收货地址服务接口
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
public interface ShopAddressService extends IService<ShopAddress> {

	/**
	 * 获取用户地址列表
	 * @param userId 用户ID
	 * @return 地址列表
	 */
	List<ShopAddress> getAddressList(Long userId);

	/**
	 * 新增地址
	 * @param address 地址信息
	 * @return 地址ID
	 */
	Long addAddress(ShopAddress address);

	/**
	 * 更新地址
	 * @param address 地址信息
	 * @return 是否成功
	 */
	boolean updateAddress(ShopAddress address);

	/**
	 * 删除地址
	 * @param id 地址ID
	 * @return 是否成功
	 */
	boolean deleteAddress(Long id);

	/**
	 * 设为默认地址
	 * @param userId 用户ID
	 * @param id 地址ID
	 * @return 是否成功
	 */
	boolean setDefault(Long userId, Long id);

	/**
	 * 获取默认地址
	 * @param userId 用户ID
	 * @return 默认地址
	 */
	ShopAddress getDefaultAddress(Long userId);

}
