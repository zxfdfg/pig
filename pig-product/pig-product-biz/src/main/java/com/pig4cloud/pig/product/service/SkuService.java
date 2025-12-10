package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.ProductSku;

import java.util.List;

/**
 * SKU管理服务接口
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
public interface SkuService extends IService<ProductSku> {

	/**
	 * 创建SKU
	 * @param sku SKU信息
	 * @return SKU ID
	 */
	Long createSku(ProductSku sku);

	/**
	 * 更新SKU
	 * @param sku SKU信息
	 * @return 是否成功
	 */
	boolean updateSku(ProductSku sku);

	/**
	 * 删除SKU（验证无订单）
	 * @param id SKU ID
	 * @return 是否成功
	 */
	boolean deleteSku(Long id);

	/**
	 * 分页查询SKU
	 * @param current 当前页
	 * @param size 每页大小
	 * @param productName 商品名称
	 * @param skuCode SKU编码
	 * @return SKU分页列表
	 */
	Object getSkuPage(Integer current, Integer size, String productName, String skuCode);

	/**
	 * 查询商品的所有SKU
	 * @param productId 商品ID
	 * @return SKU列表
	 */
	List<ProductSku> getSkusByProductId(Long productId);

}
