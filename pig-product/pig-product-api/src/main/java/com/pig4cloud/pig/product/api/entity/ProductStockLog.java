package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存日志实体
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Data
@TableName("product_stock_log")
@Schema(description = "库存日志")
public class ProductStockLog implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 日志ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "日志ID")
	private Long id;

	/**
	 * 商品ID
	 */
	@Schema(description = "商品ID")
	private Long productId;

	/**
	 * SKU ID
	 */
	@Schema(description = "SKU ID")
	private Long skuId;

	/**
	 * 类型：1-入库，2-出库，3-订单扣减，4-订单退回
	 */
	@Schema(description = "类型：1-入库，2-出库，3-订单扣减，4-订单退回")
	private Integer type;

	/**
	 * 数量（正数为增加，负数为减少）
	 */
	@Schema(description = "数量（正数为增加，负数为减少）")
	private Integer quantity;

	/**
	 * 操作前库存
	 */
	@Schema(description = "操作前库存")
	private Integer beforeStock;

	/**
	 * 操作后库存
	 */
	@Schema(description = "操作后库存")
	private Integer afterStock;

	/**
	 * 关联订单ID
	 */
	@Schema(description = "关联订单ID")
	private Long orderId;

	/**
	 * 备注
	 */
	@Schema(description = "备注")
	private String remark;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private LocalDateTime createTime;

	/**
	 * 操作人
	 */
	@Schema(description = "操作人")
	private String createBy;

}
