package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类服务接口
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
public interface CategoryService extends IService<ProductCategory> {

	/**
	 * 创建分类
	 * @param category 分类信息
	 * @return 分类ID
	 */
	Long createCategory(ProductCategory category);

	/**
	 * 更新分类
	 * @param category 分类信息
	 * @return 是否成功
	 */
	boolean updateCategory(ProductCategory category);

	/**
	 * 删除分类（验证无商品）
	 * @param id 分类ID
	 * @return 是否成功
	 */
	boolean deleteCategory(Long id);

	/**
	 * 查询分类树
	 * @return 分类树列表
	 */
	List<ProductCategory> getCategoryTree();

	/**
	 * 查询分类下的商品
	 * @param categoryId 分类ID
	 * @return 商品列表
	 */
	List<Product> getProductsByCategory(Long categoryId);

}
