package com.pig4cloud.pig.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ShopCart;
import com.pig4cloud.pig.product.mapper.ProductMapper;
import com.pig4cloud.pig.product.mapper.ShopCartMapper;
import com.pig4cloud.pig.product.service.ShopCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 购物车服务实现
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopCartServiceImpl extends ServiceImpl<ShopCartMapper, ShopCart> implements ShopCartService {

	private final ProductMapper productMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addToCart(Long userId, Long productId, Long skuId, Integer quantity) {
		Assert.notNull(userId, "用户ID不能为空");
		Assert.notNull(productId, "商品ID不能为空");
		Assert.isTrue(quantity > 0, "数量必须大于0");

		// 验证商品是否存在且上架
		Product product = productMapper.selectById(productId);
		Assert.notNull(product, "商品不存在");
		Assert.isTrue(product.getStatus() == 1, "商品未上架");

		// 验证库存
		Assert.isTrue(product.getStock() >= quantity, "库存不足");

		// 查询购物车中是否已存在该商品
		LambdaQueryWrapper<ShopCart> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopCart::getUserId, userId).eq(ShopCart::getProductId, productId);

		if (skuId != null && skuId > 0) {
			wrapper.eq(ShopCart::getSkuId, skuId);
		}
		else {
			wrapper.and(w -> w.isNull(ShopCart::getSkuId).or().eq(ShopCart::getSkuId, 0));
		}

		ShopCart existCart = this.getOne(wrapper);

		if (existCart != null) {
			// 已存在，更新数量
			existCart.setQuantity(existCart.getQuantity() + quantity);
			this.updateById(existCart);
			log.info("更新购物车商品数量，购物车ID：{}，数量：{}", existCart.getId(), existCart.getQuantity());
			return existCart.getId();
		}
		else {
			// 不存在，新增
			ShopCart cart = new ShopCart();
			cart.setUserId(userId);
			cart.setProductId(productId);
			cart.setSkuId(skuId != null && skuId > 0 ? skuId : 0L);
			cart.setQuantity(quantity);
			cart.setSelected(1); // 默认选中
			this.save(cart);
			log.info("添加商品到购物车，购物车ID：{}", cart.getId());
			return cart.getId();
		}
	}

	@Override
	public List<ShopCart> getCartList(Long userId) {
		Assert.notNull(userId, "用户ID不能为空");

		LambdaQueryWrapper<ShopCart> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopCart::getUserId, userId).orderByDesc(ShopCart::getCreateTime);

		List<ShopCart> cartList = this.list(wrapper);
		
		// 填充商品信息
		cartList.forEach(cart -> {
			Product product = productMapper.selectById(cart.getProductId());
			if (product != null) {
				cart.setProductName(product.getName());
				cart.setProductImage(product.getCoverImage());
				cart.setPrice(product.getPrice());
				cart.setStock(product.getStock());
				// SKU名称暂时留空，后续如果有SKU表再补充
				cart.setSkuName(null);
			}
		});
		
		log.info("查询用户购物车，用户ID：{}，商品数量：{}", userId, cartList.size());
		return cartList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateQuantity(Long id, Integer quantity) {
		Assert.notNull(id, "购物车ID不能为空");
		Assert.isTrue(quantity > 0, "数量必须大于0");

		ShopCart cart = this.getById(id);
		Assert.notNull(cart, "购物车商品不存在");

		// 验证库存
		Product product = productMapper.selectById(cart.getProductId());
		Assert.notNull(product, "商品不存在");
		Assert.isTrue(product.getStock() >= quantity, "库存不足");

		cart.setQuantity(quantity);
		boolean result = this.updateById(cart);

		log.info("更新购物车商品数量，购物车ID：{}，数量：{}", id, quantity);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeItem(Long id) {
		Assert.notNull(id, "购物车ID不能为空");

		boolean result = this.removeById(id);
		log.info("删除购物车商品，购物车ID：{}", id);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeItems(List<Long> ids) {
		Assert.notEmpty(ids, "购物车ID列表不能为空");

		boolean result = this.removeByIds(ids);
		log.info("批量删除购物车商品，数量：{}", ids.size());
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean clearCart(Long userId) {
		Assert.notNull(userId, "用户ID不能为空");

		LambdaQueryWrapper<ShopCart> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopCart::getUserId, userId);

		boolean result = this.remove(wrapper);
		log.info("清空用户购物车，用户ID：{}", userId);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean selectItem(Long id, Integer selected) {
		Assert.notNull(id, "购物车ID不能为空");
		Assert.notNull(selected, "选中状态不能为空");

		ShopCart cart = this.getById(id);
		Assert.notNull(cart, "购物车商品不存在");

		cart.setSelected(selected);
		boolean result = this.updateById(cart);

		log.info("更新购物车商品选中状态，购物车ID：{}，选中：{}", id, selected);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean selectAll(Long userId, Integer selected) {
		Assert.notNull(userId, "用户ID不能为空");
		Assert.notNull(selected, "选中状态不能为空");

		LambdaQueryWrapper<ShopCart> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ShopCart::getUserId, userId);

		List<ShopCart> cartList = this.list(wrapper);
		if (cartList.isEmpty()) {
			return true;
		}

		cartList.forEach(cart -> cart.setSelected(selected));
		boolean result = this.updateBatchById(cartList);

		log.info("更新用户购物车全选状态，用户ID：{}，选中：{}", userId, selected);
		return result;
	}

}
