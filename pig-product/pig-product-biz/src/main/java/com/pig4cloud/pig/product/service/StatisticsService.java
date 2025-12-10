package com.pig4cloud.pig.product.service;

import java.util.Map;

/**
 * 统计服务
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
public interface StatisticsService {

	/**
	 * 获取概览统计
	 * @return 统计数据
	 */
	Map<String, Object> getOverview();

	/**
	 * 获取分类统计
	 * @return 分类统计数据
	 */
	Object getCategoryStats();

	/**
	 * 获取库存统计
	 * @return 库存统计数据
	 */
	Object getStockStats();

}
