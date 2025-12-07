package com.pig4cloud.pig.distribution.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单佣金实体
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dist_order_commission")
public class OrderCommission extends BaseEntity {

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 订单ID
	 */
	private Long orderId;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 分销商ID
	 */
	private Long distributorId;

	/**
	 * 购买者ID
	 */
	private Long buyerId;

	/**
	 * 分销层级
	 */
	private Integer level;

	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;

	/**
	 * 佣金比例
	 */
	private BigDecimal commissionRate;

	/**
	 * 佣金金额
	 */
	private BigDecimal commissionAmount;

	/**
	 * 状态：0-待结算 1-已结算 2-已取消
	 */
	private Integer status;

	/**
	 * 结算时间
	 */
	private LocalDateTime settleTime;

	/**
	 * 取消时间
	 */
	private LocalDateTime cancelTime;

	/**
	 * 备注
	 */
	private String remark;

}
