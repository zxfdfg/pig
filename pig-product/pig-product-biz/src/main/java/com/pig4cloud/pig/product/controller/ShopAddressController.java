package com.pig4cloud.pig.product.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.product.api.entity.ShopAddress;
import com.pig4cloud.pig.product.service.ShopAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/address")
@Tag(name = "收货地址管理", description = "收货地址管理接口")
public class ShopAddressController {

	private final ShopAddressService shopAddressService;

	/**
	 * 获取地址列表
	 * @return 地址列表
	 */
	@GetMapping("/list")
	@Operation(summary = "获取地址列表", description = "获取当前用户的收货地址列表")
	public R<List<ShopAddress>> getAddressList() {
		Long userId = SecurityUtils.getUser().getId();
		List<ShopAddress> addressList = shopAddressService.getAddressList(userId);
		return R.ok(addressList);
	}

	/**
	 * 获取默认地址
	 * @return 默认地址
	 */
	@GetMapping("/default")
	@Operation(summary = "获取默认地址", description = "获取当前用户的默认收货地址")
	public R<ShopAddress> getDefaultAddress() {
		Long userId = SecurityUtils.getUser().getId();
		ShopAddress address = shopAddressService.getDefaultAddress(userId);
		return R.ok(address);
	}

	/**
	 * 新增地址
	 * @param address 地址信息
	 * @return 地址ID
	 */
	@PostMapping
	@SysLog("新增收货地址")
	@Operation(summary = "新增地址", description = "新增收货地址")
	public R<Long> addAddress(@RequestBody ShopAddress address) {
		Long userId = SecurityUtils.getUser().getId();
		address.setUserId(userId);
		Long addressId = shopAddressService.addAddress(address);
		return R.ok(addressId);
	}

	/**
	 * 更新地址
	 * @param address 地址信息
	 * @return 是否成功
	 */
	@PutMapping
	@SysLog("更新收货地址")
	@Operation(summary = "更新地址", description = "更新收货地址信息")
	public R<Boolean> updateAddress(@RequestBody ShopAddress address) {
		boolean result = shopAddressService.updateAddress(address);
		return R.ok(result);
	}

	/**
	 * 删除地址
	 * @param id 地址ID
	 * @return 是否成功
	 */
	@DeleteMapping("/{id}")
	@SysLog("删除收货地址")
	@Operation(summary = "删除地址", description = "删除收货地址")
	@Parameter(name = "id", description = "地址ID", required = true)
	public R<Boolean> deleteAddress(@PathVariable Long id) {
		boolean result = shopAddressService.deleteAddress(id);
		return R.ok(result);
	}

	/**
	 * 设为默认地址
	 * @param id 地址ID
	 * @return 是否成功
	 */
	@PutMapping("/default/{id}")
	@SysLog("设置默认地址")
	@Operation(summary = "设为默认地址", description = "将指定地址设为默认地址")
	@Parameter(name = "id", description = "地址ID", required = true)
	public R<Boolean> setDefault(@PathVariable Long id) {
		Long userId = SecurityUtils.getUser().getId();
		boolean result = shopAddressService.setDefault(userId, id);
		return R.ok(result);
	}

}
