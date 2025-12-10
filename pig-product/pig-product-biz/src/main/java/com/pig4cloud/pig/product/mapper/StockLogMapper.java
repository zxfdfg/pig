package com.pig4cloud.pig.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.product.api.entity.ProductStockLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存日志Mapper
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Mapper
public interface StockLogMapper extends BaseMapper<ProductStockLog> {

}
