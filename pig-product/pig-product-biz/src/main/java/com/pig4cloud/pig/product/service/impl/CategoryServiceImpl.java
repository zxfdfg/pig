package com.pig4cloud.pig.product.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.ErrorCodes;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductCategory;
import com.pig4cloud.pig.product.mapper.CategoryMapper;
import com.pig4cloud.pig.product.mapper.ProductMapper;
import com.pig4cloud.pig.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, ProductCategory> implements CategoryService {

	private final ProductMapper productMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long createCategory(ProductCategory category) {
		// 验证父分类是否存在
		if (category.getParentId() != null && category.getParentId() > 0) {
			ProductCategory parent = this.getById(category.getParentId());
			Assert.notNull(parent, "父分类不存在");

			// 设置层级
			category.setLevel(parent.getLevel() + 1);

			// 验证层级不超过3级
			Assert.isTrue(category.getLevel() <= 3, "分类层级不能超过3级");
		}
		else {
			// 顶级分类
			category.setParentId(0L);
			category.setLevel(1);
		}

		// 保存分类
		this.save(category);
		log.info("创建分类成功，分类ID: {}, 分类名称: {}", category.getId(), category.getName());

		return category.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateCategory(ProductCategory category) {
		Assert.notNull(category.getId(), "分类ID不能为空");

		// 验证分类是否存在
		ProductCategory existCategory = this.getById(category.getId());
		Assert.notNull(existCategory, "分类不存在");

		// 如果修改了父分类，需要重新计算层级
		if (category.getParentId() != null && !category.getParentId().equals(existCategory.getParentId())) {
			if (category.getParentId() > 0) {
				ProductCategory parent = this.getById(category.getParentId());
				Assert.notNull(parent, "父分类不存在");

				// 不能将分类移动到自己的子分类下
				Assert.isTrue(!category.getId().equals(parent.getId()), "不能将分类移动到自己下");

				// 设置层级
				category.setLevel(parent.getLevel() + 1);
				Assert.isTrue(category.getLevel() <= 3, "分类层级不能超过3级");
			}
			else {
				category.setParentId(0L);
				category.setLevel(1);
			}
		}

		// 更新分类
		boolean result = this.updateById(category);
		log.info("更新分类成功，分类ID: {}, 分类名称: {}", category.getId(), category.getName());

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteCategory(Long id) {
		Assert.notNull(id, "分类ID不能为空");

		// 验证分类是否存在
		ProductCategory category = this.getById(id);
		Assert.notNull(category, "分类不存在");

		// 验证是否有子分类
		LambdaQueryWrapper<ProductCategory> childQuery = Wrappers.lambdaQuery();
		childQuery.eq(ProductCategory::getParentId, id);
		childQuery.eq(ProductCategory::getDelFlag, 0);
		long childCount = this.count(childQuery);
		Assert.isTrue(childCount == 0, "分类下存在子分类，无法删除");

		// 验证分类下是否有商品
		LambdaQueryWrapper<Product> productQuery = Wrappers.lambdaQuery();
		productQuery.eq(Product::getCategoryId, id);
		productQuery.eq(Product::getDelFlag, 0);
		long productCount = productMapper.selectCount(productQuery);
		Assert.isTrue(productCount == 0, "分类下存在商品，无法删除");

		// 逻辑删除分类
		category.setDelFlag(1);
		boolean result = this.updateById(category);
		log.info("删除分类成功，分类ID: {}, 分类名称: {}", id, category.getName());

		return result;
	}

	@Override
	public List<ProductCategory> getCategoryTree() {
		// 查询所有未删除的分类
		LambdaQueryWrapper<ProductCategory> query = Wrappers.lambdaQuery();
		query.eq(ProductCategory::getDelFlag, 0);
		query.orderByAsc(ProductCategory::getSort);
		query.orderByAsc(ProductCategory::getId);
		List<ProductCategory> allCategories = this.list(query);

		if (CollUtil.isEmpty(allCategories)) {
			return new ArrayList<>();
		}

		// 构建分类树
		return buildCategoryTree(allCategories, 0L);
	}

	@Override
	public List<Product> getProductsByCategory(Long categoryId) {
		Assert.notNull(categoryId, "分类ID不能为空");

		// 验证分类是否存在
		ProductCategory category = this.getById(categoryId);
		Assert.notNull(category, "分类不存在");

		// 获取当前分类及所有子分类的ID
		List<Long> categoryIds = new ArrayList<>();
		categoryIds.add(categoryId);

		// 递归获取所有子分类ID
		collectChildCategoryIds(categoryId, categoryIds);

		// 查询这些分类下的所有商品
		LambdaQueryWrapper<Product> query = Wrappers.lambdaQuery();
		query.in(Product::getCategoryId, categoryIds);
		query.eq(Product::getDelFlag, 0);
		query.orderByDesc(Product::getCreateTime);

		List<Product> products = productMapper.selectList(query);
		log.info("查询分类商品成功，分类ID: {}, 商品数量: {}", categoryId, products.size());

		return products;
	}

	/**
	 * 构建分类树
	 * @param allCategories 所有分类
	 * @param parentId 父分类ID
	 * @return 分类树
	 */
	private List<ProductCategory> buildCategoryTree(List<ProductCategory> allCategories, Long parentId) {
		List<ProductCategory> tree = new ArrayList<>();

		for (ProductCategory category : allCategories) {
			if (category.getParentId().equals(parentId)) {
				// 递归查找子分类
				List<ProductCategory> children = buildCategoryTree(allCategories, category.getId());
				category.setChildren(children);
				tree.add(category);
			}
		}

		return tree;
	}

	/**
	 * 递归收集所有子分类ID
	 * @param parentId 父分类ID
	 * @param categoryIds 分类ID列表
	 */
	private void collectChildCategoryIds(Long parentId, List<Long> categoryIds) {
		LambdaQueryWrapper<ProductCategory> query = Wrappers.lambdaQuery();
		query.eq(ProductCategory::getParentId, parentId);
		query.eq(ProductCategory::getDelFlag, 0);
		List<ProductCategory> children = this.list(query);

		for (ProductCategory child : children) {
			categoryIds.add(child.getId());
			// 递归获取子分类的子分类
			collectChildCategoryIds(child.getId(), categoryIds);
		}
	}

}
