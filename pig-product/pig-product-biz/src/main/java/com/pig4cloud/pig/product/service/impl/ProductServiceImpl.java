package com.pig4cloud.pig.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductCategory;
import com.pig4cloud.pig.product.mapper.CategoryMapper;
import com.pig4cloud.pig.product.mapper.ProductMapper;
import com.pig4cloud.pig.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * 商品服务实现
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

	private final CategoryMapper categoryMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long createProduct(Product product) {
		// 验证必填字段
		validateRequiredFields(product);

		// 验证价格
		validatePrice(product);

		// 验证分类是否存在
		validateCategory(product.getCategoryId());

		// 设置默认值
		if (product.getStock() == null) {
			product.setStock(0);
		}
		if (product.getSales() == null) {
			product.setSales(0);
		}
		if (product.getStatus() == null) {
			product.setStatus(0); // 默认草稿状态
		}
		if (product.getSort() == null) {
			product.setSort(0);
		}

		// 保存商品
		this.save(product);
		log.info("创建商品成功，商品ID: {}, 商品名称: {}", product.getId(), product.getName());

		return product.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateProduct(Product product) {
		Assert.notNull(product.getId(), "商品ID不能为空");

		// 验证商品是否存在
		Product existProduct = this.getById(product.getId());
		Assert.notNull(existProduct, "商品不存在");

		// 如果修改了必填字段，需要验证
		if (StrUtil.isNotBlank(product.getName())) {
			Assert.hasText(product.getName(), "商品名称不能为空");
		}

		// 如果修改了价格，需要验证
		if (product.getPrice() != null) {
			validatePrice(product);
		}

		// 如果修改了分类，需要验证
		if (product.getCategoryId() != null && !product.getCategoryId().equals(existProduct.getCategoryId())) {
			validateCategory(product.getCategoryId());
		}

		// 更新商品（update_time 会自动更新）
		boolean result = this.updateById(product);
		log.info("更新商品成功，商品ID: {}, 商品名称: {}", product.getId(), product.getName());

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteProduct(Long id) {
		Assert.notNull(id, "商品ID不能为空");

		// 验证商品是否存在
		Product product = this.getById(id);
		Assert.notNull(product, "商品不存在");

		// TODO: 验证商品是否有未完成的订单
		// 这里需要调用订单服务接口查询，暂时跳过
		// 实际实现时需要：
		// boolean hasOrders = orderService.hasUnfinishedOrders(id);
		// Assert.isTrue(!hasOrders, "商品存在未完成订单，无法删除");

		// 逻辑删除商品
		product.setDelFlag(1);
		boolean result = this.updateById(product);
		log.info("删除商品成功，商品ID: {}, 商品名称: {}", id, product.getName());

		return result;
	}

	@Override
	public Product getProductDetail(Long id) {
		Assert.notNull(id, "商品ID不能为空");

		Product product = this.getById(id);
		Assert.notNull(product, "商品不存在");

		return product;
	}

	@Override
	public IPage<Product> getProductPage(Page<Product> page, Product product) {
		LambdaQueryWrapper<Product> query = Wrappers.lambdaQuery();

		// 商品名称模糊查询
		query.like(StrUtil.isNotBlank(product.getName()), Product::getName, product.getName());

		// 分类筛选
		query.eq(product.getCategoryId() != null, Product::getCategoryId, product.getCategoryId());

		// 商品类型筛选
		query.eq(product.getType() != null, Product::getType, product.getType());

		// 状态筛选
		query.eq(product.getStatus() != null, Product::getStatus, product.getStatus());

		// 价格区间筛选
		if (product.getPrice() != null) {
			// 这里可以扩展为价格区间查询
			query.eq(Product::getPrice, product.getPrice());
		}

		// 只查询未删除的商品
		query.eq(Product::getDelFlag, 0);

		// 按创建时间倒序
		query.orderByDesc(Product::getCreateTime);

		return this.page(page, query);
	}

	/**
	 * 验证必填字段
	 * @param product 商品信息
	 */
	private void validateRequiredFields(Product product) {
		Assert.hasText(product.getName(), "商品名称不能为空");
		Assert.notNull(product.getCategoryId(), "商品分类不能为空");
		Assert.notNull(product.getType(), "商品类型不能为空");
		Assert.notNull(product.getPrice(), "商品价格不能为空");
	}

	/**
	 * 验证价格
	 * @param product 商品信息
	 */
	private void validatePrice(Product product) {
		Assert.notNull(product.getPrice(), "商品价格不能为空");
		Assert.isTrue(product.getPrice().compareTo(BigDecimal.ZERO) > 0, "商品价格必须大于0");

		// 验证成本价
		if (product.getCostPrice() != null) {
			Assert.isTrue(product.getCostPrice().compareTo(BigDecimal.ZERO) >= 0, "成本价不能为负数");
		}

		// 验证市场价
		if (product.getMarketPrice() != null) {
			Assert.isTrue(product.getMarketPrice().compareTo(BigDecimal.ZERO) >= 0, "市场价不能为负数");
		}
	}

	/**
	 * 验证分类是否存在
	 * @param categoryId 分类ID
	 */
	private void validateCategory(Long categoryId) {
		ProductCategory category = categoryMapper.selectById(categoryId);
		Assert.notNull(category, "商品分类不存在");
		Assert.isTrue(category.getDelFlag() == 0, "商品分类已被删除");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateProductStatus(Long id, Integer status) {
		Assert.notNull(id, "商品ID不能为空");
		Assert.notNull(status, "状态不能为空");
		Assert.isTrue(status == 1 || status == 2, "状态值无效，1-上架，2-下架");

		// 查询商品
		Product product = this.getById(id);
		Assert.notNull(product, "商品不存在");

		// 如果是上架操作，需要验证
		if (status == 1) {
			// 验证商品信息完整
			Assert.hasText(product.getName(), "商品名称不能为空");
			Assert.notNull(product.getPrice(), "商品价格不能为空");
			Assert.notNull(product.getCategoryId(), "商品分类不能为空");

			// 验证不能是草稿状态
			Assert.isTrue(product.getStatus() != 0, "草稿状态商品无法上架");

			// 验证库存充足
			Assert.isTrue(product.getStock() > 0, "库存为0的商品无法上架");
		}

		// 更新状态
		product.setStatus(status);
		boolean result = this.updateById(product);

		String statusName = status == 1 ? "上架" : "下架";
		log.info("商品{}成功，商品ID: {}, 商品名称: {}", statusName, id, product.getName());

		return result;
	}

}
