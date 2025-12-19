package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车实体
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Data
@TableName("shop_cart")
@Schema(description = "购物车")
public class ShopCart implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "主键")
	private Long id;

	/**
	 * 用户ID
	 */
	@Schema(description = "用户ID")
	private Long userId;

	/**
	 * SKU ID
	 */
	@Schema(description = "SKU ID")
	private Long skuId;

	/**
	 * 商品ID
	 */
	@Schema(description = "商品ID")
	private Long productId;

	/**
	 * 数量
	 */
	@Schema(description = "数量")
	private Integer quantity;

	/**
	 * 是否选中：0-否，1-是
	 */
	@Schema(description = "是否选中：0-否，1-是")
	private Integer selected;

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
	 * 创建人
	 */
	@Schema(description = "创建人")
	private String createBy;

	/**
	 * 更新人
	 */
	@Schema(description = "更新人")
	private String updateBy;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

	// ========== 以下字段不存储到数据库，仅用于返回给前端 ==========
	
	/**
	 * 商品名称（非数据库字段）
	 */
	@com.baomidou.mybatisplus.annotation.TableField(exist = false)
	@Schema(description = "商品名称")
	private String productName;

	/**
	 * 商品图片（非数据库字段）
	 */
	@com.baomidou.mybatisplus.annotation.TableField(exist = false)
	@Schema(description = "商品图片")
	private String productImage;

	/**
	 * 商品价格（非数据库字段）
	 */
	@com.baomidou.mybatisplus.annotation.TableField(exist = false)
	@Schema(description = "商品价格")
	private java.math.BigDecimal price;

	/**
	 * 商品库存（非数据库字段）
	 */
	@com.baomidou.mybatisplus.annotation.TableField(exist = false)
	@Schema(description = "商品库存")
	private Integer stock;

	/**
	 * SKU名称（非数据库字段）
	 */
	@com.baomidou.mybatisplus.annotation.TableField(exist = false)
	@Schema(description = "SKU名称")
	private String skuName;

}
