package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.ProductCommissionConfig;

/**
 * 佣金配置服务
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
public interface CommissionConfigService extends IService<ProductCommissionConfig> {

	/**
	 * 分页查询佣金配置
	 * @param current 当前页
	 * @param size 每页大小
	 * @param type 类型
	 * @param productName 商品名称
	 * @return 分页结果
	 */
	Object getConfigPage(Integer current, Integer size, Integer type, String productName);

}
