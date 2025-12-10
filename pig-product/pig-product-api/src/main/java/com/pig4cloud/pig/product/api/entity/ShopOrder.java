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
 * 订单实体
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shop_order")
@Schema(description = "订单")
public class ShopOrder extends BaseEntity {

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "主键")
	private Long id;

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
	 * 推荐分销商ID
	 */
	@Schema(description = "推荐分销商ID")
	private Long distributorId;

	/**
	 * 订单总金额
	 */
	@Schema(description = "订单总金额")
	private BigDecimal totalAmount;

	/**
	 * 实付金额
	 */
	@Schema(description = "实付金额")
	private BigDecimal payAmount;

	/**
	 * 优惠金额
	 */
	@Schema(description = "优惠金额")
	private BigDecimal discountAmount;

	/**
	 * 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-已退款
	 */
	@Schema(description = "订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-已退款")
	private Integer status;

	/**
	 * 支付状态：0-未支付，1-已支付，2-已退款
	 */
	@Schema(description = "支付状态：0-未支付，1-已支付，2-已退款")
	private Integer payStatus;

	/**
	 * 支付时间
	 */
	@Schema(description = "支付时间")
	private LocalDateTime payTime;

	/**
	 * 支付方式：1-微信，2-支付宝，3-余额
	 */
	@Schema(description = "支付方式：1-微信，2-支付宝，3-余额")
	private Integer payType;

	/**
	 * 收货人姓名
	 */
	@Schema(description = "收货人姓名")
	private String receiverName;

	/**
	 * 收货人电话
	 */
	@Schema(description = "收货人电话")
	private String receiverPhone;

	/**
	 * 省份
	 */
	@Schema(description = "省份")
	private String receiverProvince;

	/**
	 * 城市
	 */
	@Schema(description = "城市")
	private String receiverCity;

	/**
	 * 区县
	 */
	@Schema(description = "区县")
	private String receiverDistrict;

	/**
	 * 详细地址
	 */
	@Schema(description = "详细地址")
	private String receiverAddress;

	/**
	 * 订单备注
	 */
	@Schema(description = "订单备注")
	private String remark;

	/**
	 * 取消原因
	 */
	@Schema(description = "取消原因")
	private String cancelReason;

	/**
	 * 取消时间
	 */
	@Schema(description = "取消时间")
	private LocalDateTime cancelTime;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

}
