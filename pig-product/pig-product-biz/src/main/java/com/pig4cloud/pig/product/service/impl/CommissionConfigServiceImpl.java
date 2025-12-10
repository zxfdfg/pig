package com.pig4cloud.pig.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.product.api.entity.ProductCommissionConfig;
import com.pig4cloud.pig.product.mapper.CommissionConfigMapper;
import com.pig4cloud.pig.product.service.CommissionConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 佣金配置服务实现
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommissionConfigServiceImpl extends ServiceImpl<CommissionConfigMapper, ProductCommissionConfig>
		implements CommissionConfigService {

	@Override
	public Object getConfigPage(Integer current, Integer size, Integer type, String productName) {
		LambdaQueryWrapper<ProductCommissionConfig> query = Wrappers.lambdaQuery();
		query.eq(type != null, ProductCommissionConfig::getStatus, type);
		query.orderByDesc(ProductCommissionConfig::getId);

		return this.page(new Page<>(current, size), query);
	}

}
