package com.pig4cloud.pig.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.product.api.entity.ProductPriceHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 价格变更历史Mapper
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Mapper
public interface PriceHistoryMapper extends BaseMapper<ProductPriceHistory> {

}
