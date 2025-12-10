package com.pig4cloud.pig.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.product.api.entity.ProductCdkey;
import com.pig4cloud.pig.product.mapper.CdkeyMapper;
import com.pig4cloud.pig.product.service.CdkeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * CDKey管理服务实现
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CdkeyServiceImpl extends ServiceImpl<CdkeyMapper, ProductCdkey> implements CdkeyService {

	@Override
	public Object getCdkeyPage(Integer current, Integer size, String productName, String cdkey, Integer status) {
		LambdaQueryWrapper<ProductCdkey> query = Wrappers.lambdaQuery();
		query.like(StrUtil.isNotBlank(cdkey), ProductCdkey::getCdkey, cdkey);
		query.eq(status != null, ProductCdkey::getStatus, status);
		query.orderByDesc(ProductCdkey::getId);

		return this.page(new Page<>(current, size), query);
	}

}
