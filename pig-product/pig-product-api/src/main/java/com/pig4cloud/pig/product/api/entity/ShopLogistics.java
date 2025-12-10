package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 物流信息实体
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shop_logistics")
@Schema(description = "物流信息")
public class ShopLogistics extends BaseEntity {

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "主键")
	private Long id;

	/**
	 * 订单ID
	 */
	@Schema(description = "订单ID")
	private Long orderId;

	/**
	 * 订单号
	 */
	@Schema(description = "订单号")
	private String orderNo;

	/**
	 * 物流公司
	 */
	@Schema(description = "物流公司")
	private String logisticsCompany;

	/**
	 * 物流单号
	 */
	@Schema(description = "物流单号")
	private String logisticsNo;

	/**
	 * 物流状态：0-待发货，1-已发货，2-运输中，3-派送中，4-已签收
	 */
	@Schema(description = "物流状态：0-待发货，1-已发货，2-运输中，3-派送中，4-已签收")
	private Integer status;

	/**
	 * 发货时间
	 */
	@Schema(description = "发货时间")
	private LocalDateTime shipTime;

	/**
	 * 签收时间
	 */
	@Schema(description = "签收时间")
	private LocalDateTime receiveTime;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

}
