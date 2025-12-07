package com.pig4cloud.pig.distribution.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.distribution.api.entity.CommissionConfig;

/**
 * 佣金配置服务接口
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
public interface ConfigService extends IService<CommissionConfig> {

	/**
	 * 查询佣金配置
	 * @param level 分销层级
	 * @param distributorLevel 分销商等级
	 * @return 佣金配置
	 */
	CommissionConfig getConfig(Integer level, Integer distributorLevel);

}
