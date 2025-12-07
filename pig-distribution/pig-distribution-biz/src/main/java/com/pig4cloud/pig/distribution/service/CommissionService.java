package com.pig4cloud.pig.distribution.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.distribution.api.entity.OrderCommission;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 佣金服务接口
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
public interface CommissionService extends IService<OrderCommission> {

	/**
	 * 计算订单佣金
	 * @param orderId 订单ID
	 * @param orderNo 订单号
	 * @param buyerId 购买者ID
	 * @param orderAmount 订单金额
	 * @return 是否成功
	 */
	Boolean calculateCommission(Long orderId, String orderNo, Long buyerId, BigDecimal orderAmount);

	/**
	 * 结算佣金
	 * @param commissionId 佣金ID
	 * @return 是否成功
	 */
	Boolean settleCommission(Long commissionId);

	/**
	 * 取消佣金
	 * @param orderId 订单ID
	 * @return 是否成功
	 */
	Boolean cancelCommission(Long orderId);

	/**
	 * 获取佣金统计
	 * @param distributorId 分销商ID
	 * @return 统计数据
	 */
	Map<String, Object> getCommissionStats(Long distributorId);

}
