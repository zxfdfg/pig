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
 * 商品实体
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
@Schema(description = "商品")
public class Product extends BaseEntity {

	/**
	 * 商品ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "商品ID")
	private Long id;

	/**
	 * 商品名称
	 */
	@Schema(description = "商品名称")
	private String name;

	/**
	 * 分类ID
	 */
	@Schema(description = "分类ID")
	private Long categoryId;

	/**
	 * 商品类型：0-实物商品，1-虚拟商品
	 */
	@Schema(description = "商品类型：0-实物商品，1-虚拟商品")
	private Integer type;

	/**
	 * 封面图片
	 */
	@Schema(description = "封面图片")
	private String coverImage;

	/**
	 * 商品图片（JSON数组）
	 */
	@Schema(description = "商品图片（JSON数组）")
	private String images;

	/**
	 * 商品描述
	 */
	@Schema(description = "商品描述")
	private String description;

	/**
	 * 商品详情（富文本）
	 */
	@Schema(description = "商品详情（富文本）")
	private String detail;

	/**
	 * 商品价格
	 */
	@Schema(description = "商品价格")
	private BigDecimal price;

	/**
	 * 成本价
	 */
	@Schema(description = "成本价")
	private BigDecimal costPrice;

	/**
	 * 市场价
	 */
	@Schema(description = "市场价")
	private BigDecimal marketPrice;

	/**
	 * 库存数量
	 */
	@Schema(description = "库存数量")
	private Integer stock;

	/**
	 * 库存预警值
	 */
	@Schema(description = "库存预警值")
	private Integer stockWarning;

	/**
	 * 销量
	 */
	@Schema(description = "销量")
	private Integer sales;

	/**
	 * 状态：0-草稿，1-上架，2-下架，3-售罄
	 */
	@Schema(description = "状态：0-草稿，1-上架，2-下架，3-售罄")
	private Integer status;

	/**
	 * 排序
	 */
	@Schema(description = "排序")
	private Integer sort;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

}
