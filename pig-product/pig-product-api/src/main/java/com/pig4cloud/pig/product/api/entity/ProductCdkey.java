package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品CDKEY实体
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_cdkey")
@Schema(description = "商品CDKEY")
public class ProductCdkey extends BaseEntity {

	/**
	 * CDKEY ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "CDKEY ID")
	private Long id;

	/**
	 * 商品ID
	 */
	@Schema(description = "商品ID")
	private Long productId;

	/**
	 * CDKEY码
	 */
	@Schema(description = "CDKEY码")
	private String cdkey;

	/**
	 * 状态：0-未使用，1-已使用，2-已过期
	 */
	@Schema(description = "状态：0-未使用，1-已使用，2-已过期")
	private Integer status;

	/**
	 * 推广分销商ID
	 */
	@Schema(description = "推广分销商ID")
	private Long distributorId;

	/**
	 * 订单ID
	 */
	@Schema(description = "订单ID")
	private Long orderId;

	/**
	 * 使用用户ID
	 */
	@Schema(description = "使用用户ID")
	private Long userId;

	/**
	 * 使用时间
	 */
	@Schema(description = "使用时间")
	private LocalDateTime usedTime;

	/**
	 * 过期时间
	 */
	@Schema(description = "过期时间")
	private LocalDateTime expireTime;

	/**
	 * 一级佣金比例
	 */
	@Schema(description = "一级佣金比例")
	private BigDecimal commissionLevel1;

	/**
	 * 二级佣金比例
	 */
	@Schema(description = "二级佣金比例")
	private BigDecimal commissionLevel2;

	/**
	 * 三级佣金比例
	 */
	@Schema(description = "三级佣金比例")
	private BigDecimal commissionLevel3;

}
