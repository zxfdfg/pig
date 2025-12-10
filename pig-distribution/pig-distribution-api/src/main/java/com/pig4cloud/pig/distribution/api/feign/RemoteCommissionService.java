package com.pig4cloud.pig.distribution.api.feign;

import com.pig4cloud.pig.common.core.constant.ServiceNameConstants;
import com.pig4cloud.pig.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 佣金服务 Feign 客户端
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@FeignClient(contextId = "remoteCommissionService", value = ServiceNameConstants.DISTRIBUTION_SERVICE)
public interface RemoteCommissionService {

	/**
	 * 计算订单佣金
	 * @param orderId 订单ID
	 * @param orderNo 订单号
	 * @param buyerId 购买者ID
	 * @param orderAmount 订单金额
	 * @return 是否成功
	 */
	@PostMapping("/commission/calculate")
	R<Boolean> calculateCommission(@RequestParam("orderId") Long orderId, @RequestParam("orderNo") String orderNo,
			@RequestParam("buyerId") Long buyerId, @RequestParam("orderAmount") BigDecimal orderAmount);

	/**
	 * 取消订单佣金
	 * @param orderId 订单ID
	 * @return 是否成功
	 */
	@PostMapping("/commission/cancel")
	R<Boolean> cancelCommission(@RequestParam("orderId") Long orderId);

}
