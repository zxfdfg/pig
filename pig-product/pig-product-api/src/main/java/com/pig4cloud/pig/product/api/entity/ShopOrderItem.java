package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Data
@TableName("shop_order_item")
@Schema(description = "订单明细")
public class ShopOrderItem implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

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
	 * 商品ID
	 */
	@Schema(description = "商品ID")
	private Long productId;

	/**
	 * 商品名称
	 */
	@Schema(description = "商品名称")
	private String productName;

	/**
	 * 商品图片
	 */
	@Schema(description = "商品图片")
	private String productImage;

	/**
	 * SKU ID
	 */
	@Schema(description = "SKU ID")
	private Long skuId;

	/**
	 * SKU名称
	 */
	@Schema(description = "SKU名称")
	private String skuName;

	/**
	 * SKU属性JSON
	 */
	@Schema(description = "SKU属性JSON")
	private String skuAttrs;

	/**
	 * 商品单价
	 */
	@Schema(description = "商品单价")
	private BigDecimal price;

	/**
	 * 购买数量
	 */
	@Schema(description = "购买数量")
	private Integer quantity;

	/**
	 * 小计金额
	 */
	@Schema(description = "小计金额")
	private BigDecimal totalAmount;

	/**
	 * 佣金金额
	 */
	@Schema(description = "佣金金额")
	private BigDecimal commissionAmount;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@Schema(description = "更新时间")
	private LocalDateTime updateTime;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

}
