package com.pig4cloud.pig.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.product.api.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SKU Mapper
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Mapper
public interface SkuMapper extends BaseMapper<ProductSku> {

}
