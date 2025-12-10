package com.pig4cloud.pig.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.product.api.entity.ShopAddress;
import com.pig4cloud.pig.product.mapper.ShopAddressMapper;
import com.pig4cloud.pig.product.service.ShopAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 收货地址服务实现
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopAddressServiceImpl extends ServiceImpl<ShopAddressMapper, ShopAddress>
		implements ShopAddressService {

	@Override
	public List<ShopAddress> getAddressList(Long userId) {
		Assert.notNull(userId, "用户ID不能为空");

		LambdaQueryWrapper<ShopAddress> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopAddress::getUserId, userId).orderByDesc(ShopAddress::getIsDefault)
				.orderByDesc(ShopAddress::getCreateTime);

		return this.list(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addAddress(ShopAddress address) {
		Assert.notNull(address.getUserId(), "用户ID不能为空");
		Assert.hasText(address.getReceiverName(), "收货人姓名不能为空");
		Assert.hasText(address.getReceiverPhone(), "收货人电话不能为空");
		Assert.hasText(address.getProvince(), "省份不能为空");
		Assert.hasText(address.getCity(), "城市不能为空");
		Assert.hasText(address.getDistrict(), "区县不能为空");
		Assert.hasText(address.getAddress(), "详细地址不能为空");

		// 如果设为默认，先取消其他默认地址
		if (address.getIsDefault() != null && address.getIsDefault() == 1) {
			cancelOtherDefault(address.getUserId());
		}
		else {
			address.setIsDefault(0);
		}

		this.save(address);
		log.info("新增收货地址，地址ID：{}", address.getId());
		return address.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateAddress(ShopAddress address) {
		Assert.notNull(address.getId(), "地址ID不能为空");

		ShopAddress existAddress = this.getById(address.getId());
		Assert.notNull(existAddress, "地址不存在");

		// 如果设为默认，先取消其他默认地址
		if (address.getIsDefault() != null && address.getIsDefault() == 1) {
			cancelOtherDefault(existAddress.getUserId());
		}

		boolean result = this.updateById(address);
		log.info("更新收货地址，地址ID：{}", address.getId());
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteAddress(Long id) {
		Assert.notNull(id, "地址ID不能为空");

		boolean result = this.removeById(id);
		log.info("删除收货地址，地址ID：{}", id);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean setDefault(Long userId, Long id) {
		Assert.notNull(userId, "用户ID不能为空");
		Assert.notNull(id, "地址ID不能为空");

		ShopAddress address = this.getById(id);
		Assert.notNull(address, "地址不存在");
		Assert.isTrue(address.getUserId().equals(userId), "无权操作该地址");

		// 取消其他默认地址
		cancelOtherDefault(userId);

		// 设为默认
		address.setIsDefault(1);
		boolean result = this.updateById(address);

		log.info("设置默认地址，用户ID：{}，地址ID：{}", userId, id);
		return result;
	}

	@Override
	public ShopAddress getDefaultAddress(Long userId) {
		Assert.notNull(userId, "用户ID不能为空");

		LambdaQueryWrapper<ShopAddress> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopAddress::getUserId, userId).eq(ShopAddress::getIsDefault, 1);

		return this.getOne(wrapper);
	}

	/**
	 * 取消其他默认地址
	 * @param userId 用户ID
	 */
	private void cancelOtherDefault(Long userId) {
		LambdaQueryWrapper<ShopAddress> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopAddress::getUserId, userId).eq(ShopAddress::getIsDefault, 1);

		List<ShopAddress> defaultAddresses = this.list(wrapper);
		if (!defaultAddresses.isEmpty()) {
			defaultAddresses.forEach(addr -> addr.setIsDefault(0));
			this.updateBatchById(defaultAddresses);
		}
	}

}
