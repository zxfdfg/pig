package com.pig4cloud.pig.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.distribution.api.entity.CommissionConfig;
import com.pig4cloud.pig.distribution.mapper.CommissionConfigMapper;
import com.pig4cloud.pig.distribution.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 佣金配置服务实现
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<CommissionConfigMapper, CommissionConfig> implements ConfigService {

	@Override
	@Cacheable(value = "commission:config", key = "#level + ':' + #distributorLevel")
	public CommissionConfig getConfig(Integer level, Integer distributorLevel) {
		// 优先查询指定等级的配置
		CommissionConfig config = baseMapper
			.selectOne(new LambdaQueryWrapper<CommissionConfig>().eq(CommissionConfig::getLevel, level)
				.eq(CommissionConfig::getDistributorLevel, distributorLevel)
				.eq(CommissionConfig::getStatus, 1)
				.last("LIMIT 1"));

		// 如果没有，查询通用配置（distributorLevel=0）
		if (config == null) {
			config = baseMapper
				.selectOne(new LambdaQueryWrapper<CommissionConfig>().eq(CommissionConfig::getLevel, level)
					.eq(CommissionConfig::getDistributorLevel, 0)
					.eq(CommissionConfig::getStatus, 1)
					.last("LIMIT 1"));
		}

		return config;
	}

}
