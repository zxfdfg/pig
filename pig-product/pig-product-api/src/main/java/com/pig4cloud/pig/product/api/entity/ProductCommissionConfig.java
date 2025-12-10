package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品佣金配置实体
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_commission_config")
@Schema(description = "商品佣金配置")
public class ProductCommissionConfig extends BaseEntity {

	/**
	 * 配置ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "配置ID")
	private Long id;

	/**
	 * 商品ID（NULL表示分类默认配置）
	 */
	@Schema(description = "商品ID（NULL表示分类默认配置）")
	private Long productId;

	/**
	 * 分类ID（NULL表示商品独立配置）
	 */
	@Schema(description = "分类ID（NULL表示商品独立配置）")
	private Long categoryId;

	/**
	 * 一级佣金比例（%）
	 */
	@Schema(description = "一级佣金比例（%）")
	private BigDecimal level1Rate;

	/**
	 * 二级佣金比例（%）
	 */
	@Schema(description = "二级佣金比例（%）")
	private BigDecimal level2Rate;

	/**
	 * 三级佣金比例（%）
	 */
	@Schema(description = "三级佣金比例（%）")
	private BigDecimal level3Rate;

	/**
	 * 状态：0-禁用，1-启用
	 */
	@Schema(description = "状态：0-禁用，1-启用")
	private Integer status;

}
