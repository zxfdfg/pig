package com.pig4cloud.pig.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductCategory;
import com.pig4cloud.pig.product.mapper.CategoryMapper;
import com.pig4cloud.pig.product.mapper.ProductMapper;
import com.pig4cloud.pig.product.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计服务实现
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

	private final ProductMapper productMapper;

	private final CategoryMapper categoryMapper;

	@Override
	public Map<String, Object> getOverview() {
		Map<String, Object> result = new HashMap<>();

		// 商品总数
		LambdaQueryWrapper<Product> allQuery = Wrappers.lambdaQuery();
		allQuery.eq(Product::getDelFlag, 0);
		long totalProducts = productMapper.selectCount(allQuery);

		// 在售商品数
		LambdaQueryWrapper<Product> onSaleQuery = Wrappers.lambdaQuery();
		onSaleQuery.eq(Product::getStatus, 1);
		onSaleQuery.eq(Product::getDelFlag, 0);
		long onSaleProducts = productMapper.selectCount(onSaleQuery);

		// 下架商品数
		LambdaQueryWrapper<Product> offSaleQuery = Wrappers.lambdaQuery();
		offSaleQuery.eq(Product::getStatus, 2);
		offSaleQuery.eq(Product::getDelFlag, 0);
		long offSaleProducts = productMapper.selectCount(offSaleQuery);

		// 售罄商品数
		LambdaQueryWrapper<Product> soldOutQuery = Wrappers.lambdaQuery();
		soldOutQuery.eq(Product::getStatus, 3);
		soldOutQuery.eq(Product::getDelFlag, 0);
		long soldOutProducts = productMapper.selectCount(soldOutQuery);

		// 低库存商品数
		LambdaQueryWrapper<Product> lowStockQuery = Wrappers.lambdaQuery();
		lowStockQuery.apply("stock <= stock_warning");
		lowStockQuery.eq(Product::getDelFlag, 0);
		long lowStockProducts = productMapper.selectCount(lowStockQuery);

		result.put("totalProducts", totalProducts);
		result.put("onSaleProducts", onSaleProducts);
		result.put("offSaleProducts", offSaleProducts);
		result.put("soldOutProducts", soldOutProducts);
		result.put("lowStockProducts", lowStockProducts);

		return result;
	}

	@Override
	public Object getCategoryStats() {
		List<Map<String, Object>> result = new ArrayList<>();

		// 获取所有分类
		LambdaQueryWrapper<ProductCategory> categoryQuery = Wrappers.lambdaQuery();
		categoryQuery.eq(ProductCategory::getDelFlag, 0);
		categoryQuery.orderByAsc(ProductCategory::getSort);
		List<ProductCategory> categories = categoryMapper.selectList(categoryQuery);

		// 统计每个分类的商品数
		for (ProductCategory category : categories) {
			Map<String, Object> stat = new HashMap<>();
			stat.put("categoryId", category.getId());
			stat.put("categoryName", category.getName());

			LambdaQueryWrapper<Product> productQuery = Wrappers.lambdaQuery();
			productQuery.eq(Product::getCategoryId, category.getId());
			productQuery.eq(Product::getDelFlag, 0);
			long count = productMapper.selectCount(productQuery);

			stat.put("productCount", count);
			result.add(stat);
		}

		return result;
	}

	@Override
	public Object getStockStats() {
		Map<String, Object> result = new HashMap<>();

		// 总库存
		List<Product> allProducts = productMapper
			.selectList(Wrappers.lambdaQuery(Product.class).eq(Product::getDelFlag, 0));
		int totalStock = allProducts.stream().mapToInt(Product::getStock).sum();

		// 低库存商品数
		LambdaQueryWrapper<Product> lowStockQuery = Wrappers.lambdaQuery();
		lowStockQuery.apply("stock <= stock_warning");
		lowStockQuery.eq(Product::getDelFlag, 0);
		long lowStockCount = productMapper.selectCount(lowStockQuery);

		// 售罄商品数
		LambdaQueryWrapper<Product> soldOutQuery = Wrappers.lambdaQuery();
		soldOutQuery.eq(Product::getStock, 0);
		soldOutQuery.eq(Product::getDelFlag, 0);
		long soldOutCount = productMapper.selectCount(soldOutQuery);

		result.put("totalStock", totalStock);
		result.put("lowStockCount", lowStockCount);
		result.put("soldOutCount", soldOutCount);

		return result;
	}

}
