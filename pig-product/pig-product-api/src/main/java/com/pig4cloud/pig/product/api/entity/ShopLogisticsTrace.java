package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 物流轨迹实体
 *
 * @author pig4cloud
 * @date 2025-12-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shop_logistics_trace")
@Schema(description = "物流轨迹")
public class ShopLogisticsTrace extends BaseEntity {

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "主键")
	private Long id;

	/**
	 * 物流ID
	 */
	@Schema(description = "物流ID")
	private Long logisticsId;

	/**
	 * 轨迹内容
	 */
	@Schema(description = "轨迹内容")
	private String content;

	/**
	 * 轨迹时间
	 */
	@Schema(description = "轨迹时间")
	private LocalDateTime traceTime;

	/**
	 * 删除标记：0-正常，1-删除
	 */
	@TableLogic
	@Schema(description = "删除标记：0-正常，1-删除")
	private Integer delFlag;

}
