package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品分类实体
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_category")
@Schema(description = "商品分类")
public class ProductCategory extends BaseEntity {

	/**
	 * 分类ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "分类ID")
	private Long id;

	/**
	 * 父分类ID，0表示顶级分类
	 */
	@Schema(description = "父分类ID，0表示顶级分类")
	private Long parentId;

	/**
	 * 分类名称
	 */
	@Schema(description = "分类名称")
	private String name;

	/**
	 * 分类图标
	 */
	@Schema(description = "分类图标")
	private String icon;

	/**
	 * 分类描述
	 */
	@Schema(description = "分类描述")
	private String description;

	/**
	 * 排序
	 */
	@Schema(description = "排序")
	private Integer sort;

	/**
	 * 层级：1-一级，2-二级，3-三级
	 */
	@Schema(description = "层级：1-一级，2-二级，3-三级")
	private Integer level;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

	/**
	 * 子分类列表（非数据库字段）
	 */
	@TableField(exist = false)
	@Schema(description = "子分类列表")
	private List<ProductCategory> children;

}
