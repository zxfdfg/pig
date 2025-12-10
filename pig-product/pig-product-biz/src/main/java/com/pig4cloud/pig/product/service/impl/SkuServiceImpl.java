package com.pig4cloud.pig.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductSku;
import com.pig4cloud.pig.product.mapper.ProductMapper;
import com.pig4cloud.pig.product.mapper.SkuMapper;
import com.pig4cloud.pig.product.service.SkuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

/**
 * SKU管理服务实现
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkuServiceImpl extends ServiceImpl<SkuMapper, ProductSku> implements SkuService {

	private final ProductMapper productMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long createSku(ProductSku sku) {
		// 验证必填字段
		Assert.notNull(sku.getProductId(), "商品ID不能为空");
		Assert.hasText(sku.getSkuCode(), "SKU编码不能为空");
		Assert.notNull(sku.getPrice(), "SKU价格不能为空");

		// 验证商品是否存在
		Product product = productMapper.selectById(sku.getProductId());
		Assert.notNull(product, "商品不存在");

		// 验证SKU编码是否重复
		LambdaQueryWrapper<ProductSku> query = Wrappers.lambdaQuery();
		query.eq(ProductSku::getSkuCode, sku.getSkuCode());
		query.eq(ProductSku::getDelFlag, 0);
		long count = this.count(query);
		Assert.isTrue(count == 0, "SKU编码已存在");

		// 验证价格
		Assert.isTrue(sku.getPrice().compareTo(BigDecimal.ZERO) > 0, "SKU价格必须大于0");
		if (sku.getCostPrice() != null) {
			Assert.isTrue(sku.getCostPrice().compareTo(BigDecimal.ZERO) >= 0, "成本价不能为负数");
		}

		// 设置默认值
		if (sku.getStock() == null) {
			sku.setStock(0);
		}
		if (sku.getStatus() == null) {
			sku.setStatus(1); // 默认启用
		}

		// 保存SKU
		this.save(sku);
		log.info("创建SKU成功，SKU ID: {}, SKU编码: {}", sku.getId(), sku.getSkuCode());

		return sku.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSku(ProductSku sku) {
		Assert.notNull(sku.getId(), "SKU ID不能为空");

		// 验证SKU是否存在
		ProductSku existSku = this.getById(sku.getId());
		Assert.notNull(existSku, "SKU不存在");

		// 如果修改了SKU编码，验证是否重复
		if (StrUtil.isNotBlank(sku.getSkuCode()) && !sku.getSkuCode().equals(existSku.getSkuCode())) {
			LambdaQueryWrapper<ProductSku> query = Wrappers.lambdaQuery();
			query.eq(ProductSku::getSkuCode, sku.getSkuCode());
			query.ne(ProductSku::getId, sku.getId());
			query.eq(ProductSku::getDelFlag, 0);
			long count = this.count(query);
			Assert.isTrue(count == 0, "SKU编码已存在");
		}

		// 如果修改了价格，验证
		if (sku.getPrice() != null) {
			Assert.isTrue(sku.getPrice().compareTo(BigDecimal.ZERO) > 0, "SKU价格必须大于0");
		}

		// 更新SKU
		boolean result = this.updateById(sku);
		log.info("更新SKU成功，SKU ID: {}, SKU编码: {}", sku.getId(), sku.getSkuCode());

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteSku(Long id) {
		Assert.notNull(id, "SKU ID不能为空");

		// 验证SKU是否存在
		ProductSku sku = this.getById(id);
		Assert.notNull(sku, "SKU不存在");

		// TODO: 验证SKU是否有未完成的订单
		// 实际实现时需要调用订单服务接口

		// 逻辑删除SKU
		sku.setDelFlag(1);
		boolean result = this.updateById(sku);
		log.info("删除SKU成功，SKU ID: {}, SKU编码: {}", id, sku.getSkuCode());

		return result;
	}

	@Override
	public List<ProductSku> getSkusByProductId(Long productId) {
		Assert.notNull(productId, "商品ID不能为空");

		LambdaQueryWrapper<ProductSku> query = Wrappers.lambdaQuery();
		query.eq(ProductSku::getProductId, productId);
		query.eq(ProductSku::getDelFlag, 0);
		query.orderByAsc(ProductSku::getId);

		return this.list(query);
	}

}
