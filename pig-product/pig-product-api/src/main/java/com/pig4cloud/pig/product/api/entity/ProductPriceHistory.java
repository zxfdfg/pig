package com.pig4cloud.pig.product.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格变更历史实体
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Data
@TableName("product_price_history")
@Schema(description = "价格变更历史")
public class ProductPriceHistory implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 历史ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "历史ID")
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
	 * 原价格
	 */
	@Schema(description = "原价格")
	private BigDecimal oldPrice;

	/**
	 * 新价格
	 */
	@Schema(description = "新价格")
	private BigDecimal newPrice;

	/**
	 * 变更原因
	 */
	@Schema(description = "变更原因")
	private String changeReason;

	/**
	 * 变更时间
	 */
	@Schema(description = "变更时间")
	private LocalDateTime createTime;

	/**
	 * 操作人
	 */
	@Schema(description = "操作人")
	private String createBy;

}
