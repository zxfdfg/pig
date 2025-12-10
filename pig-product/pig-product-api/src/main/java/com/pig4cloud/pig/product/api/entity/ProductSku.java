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

/**
 * 商品SKU实体
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_sku")
@Schema(description = "商品SKU")
public class ProductSku extends BaseEntity {

	/**
	 * SKU ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "SKU ID")
	private Long id;

	/**
	 * 商品ID
	 */
	@Schema(description = "商品ID")
	private Long productId;

	/**
	 * SKU编码
	 */
	@Schema(description = "SKU编码")
	private String skuCode;

	/**
	 * SKU名称
	 */
	@Schema(description = "SKU名称")
	private String skuName;

	/**
	 * SKU属性（JSON）
	 */
	@Schema(description = "SKU属性（JSON）")
	private String attributes;

	/**
	 * SKU价格
	 */
	@Schema(description = "SKU价格")
	private BigDecimal price;

	/**
	 * SKU成本价
	 */
	@Schema(description = "SKU成本价")
	private BigDecimal costPrice;

	/**
	 * SKU库存
	 */
	@Schema(description = "SKU库存")
	private Integer stock;

	/**
	 * SKU图片
	 */
	@Schema(description = "SKU图片")
	private String image;

	/**
	 * 状态：0-禁用，1-启用
	 */
	@Schema(description = "状态：0-禁用，1-启用")
	private Integer status;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

}
