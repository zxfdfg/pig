package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shop_payment")
@Schema(description = "支付记录")
public class ShopPayment extends BaseEntity {

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "主键")
	private Long id;

	/**
	 * 支付单号
	 */
	@Schema(description = "支付单号")
	private String paymentNo;

	/**
	 * 订单号
	 */
	@Schema(description = "订单号")
	private String orderNo;

	/**
	 * 用户ID
	 */
	@Schema(description = "用户ID")
	private Long userId;

	/**
	 * 支付金额
	 */
	@Schema(description = "支付金额")
	private BigDecimal payAmount;

	/**
	 * 支付方式：1-微信，2-支付宝，3-余额
	 */
	@Schema(description = "支付方式：1-微信，2-支付宝，3-余额")
	private Integer payType;

	/**
	 * 支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款
	 */
	@Schema(description = "支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款")
	private Integer status;

	/**
	 * 第三方交易号
	 */
	@Schema(description = "第三方交易号")
	private String tradeNo;

	/**
	 * 支付时间
	 */
	@Schema(description = "支付时间")
	private LocalDateTime payTime;

	/**
	 * 退款时间
	 */
	@Schema(description = "退款时间")
	private LocalDateTime refundTime;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

}
