package com.pig4cloud.pig.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.product.api.entity.ProductCdkey;

/**
 * CDKey管理服务
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
public interface CdkeyService extends IService<ProductCdkey> {

	/**
	 * 分页查询CDKey
	 * @param current 当前页
	 * @param size 每页大小
	 * @param productName 商品名称
	 * @param cdkey CDKey
	 * @param status 状态
	 * @return 分页结果
	 */
	Object getCdkeyPage(Integer current, Integer size, String productName, String cdkey, Integer status);

}
