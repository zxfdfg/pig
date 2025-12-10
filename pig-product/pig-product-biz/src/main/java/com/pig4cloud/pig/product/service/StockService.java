package com.pig4cloud.pig.product.service;

import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductStockLog;

import java.util.List;

/**
 * 库存管理服务接口
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
public interface StockService {

	/**
	 * 增加库存（记录日志）
	 * @param productId 商品ID
	 * @param quantity 增加数量
	 * @param remark 备注
	 * @return 是否成功
	 */
	boolean increaseStock(Long productId, Integer quantity, String remark);

	/**
	 * 减少库存（验证充足）
	 * @param productId 商品ID
	 * @param quantity 减少数量
	 * @param remark 备注
	 * @return 是否成功
	 */
	boolean decreaseStock(Long productId, Integer quantity, String remark);

	/**
	 * 查询商品库存
	 * @param productId 商品ID
	 * @return 库存数量
	 */
	Integer getStock(Long productId);

	/**
	 * 查询低库存商品列表
	 * @return 低库存商品列表
	 */
	List<Product> getLowStockProducts();

	/**
	 * 查询库存日志
	 * @param productId 商品ID
	 * @return 库存日志列表
	 */
	List<ProductStockLog> getStockLogs(Long productId);

	/**
	 * 分页查询库存
	 * @param current 当前页
	 * @param size 每页大小
	 * @param productName 商品名称
	 * @param stockStatus 库存状态
	 * @return 库存分页列表
	 */
	Object getStockPage(Integer current, Integer size, String productName, String stockStatus);

	/**
	 * 订单扣库存
	 * @param productId 商品ID
	 * @param quantity 数量
	 * @param orderId 订单ID
	 * @return 是否成功
	 */
	boolean deductStockForOrder(Long productId, Integer quantity, Long orderId);

}
