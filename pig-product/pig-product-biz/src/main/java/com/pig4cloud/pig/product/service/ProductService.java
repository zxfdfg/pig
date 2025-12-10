package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.Product;

/**
 * 商品服务接口
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
public interface ProductService extends IService<Product> {

	/**
	 * 创建商品（验证必填字段、价格）
	 * @param product 商品信息
	 * @return 商品ID
	 */
	Long createProduct(Product product);

	/**
	 * 更新商品（记录修改时间）
	 * @param product 商品信息
	 * @return 是否成功
	 */
	boolean updateProduct(Product product);

	/**
	 * 删除商品（验证无订单）
	 * @param id 商品ID
	 * @return 是否成功
	 */
	boolean deleteProduct(Long id);

	/**
	 * 查询商品详情
	 * @param id 商品ID
	 * @return 商品信息
	 */
	Product getProductDetail(Long id);

	/**
	 * 分页查询商品
	 * @param page 分页参数
	 * @param product 查询条件
	 * @return 商品分页列表
	 */
	IPage<Product> getProductPage(Page<Product> page, Product product);

	/**
	 * 更新商品状态（上下架）
	 * @param id 商品ID
	 * @param status 状态：1-上架，2-下架
	 * @return 是否成功
	 */
	boolean updateProductStatus(Long id, Integer status);

}
