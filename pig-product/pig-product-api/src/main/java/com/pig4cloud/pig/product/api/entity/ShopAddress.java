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
 * 收货地址实体
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Data
@TableName("shop_address")
@Schema(description = "收货地址")
public class ShopAddress implements Serializable {

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
	private String province;

	/**
	 * 城市
	 */
	@Schema(description = "城市")
	private String city;

	/**
	 * 区县
	 */
	@Schema(description = "区县")
	private String district;

	/**
	 * 详细地址
	 */
	@Schema(description = "详细地址")
	private String address;

	/**
	 * 是否默认：0-否，1-是
	 */
	@Schema(description = "是否默认：0-否，1-是")
	private Integer isDefault;

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
