package com.pig4cloud.pig.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.product.api.entity.ShopPayment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录Mapper
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Mapper
public interface ShopPaymentMapper extends BaseMapper<ShopPayment> {

}
