package com.pig4cloud.pig.distribution.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 佣金配置实体
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dist_commission_config")
public class CommissionConfig extends BaseEntity {

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 配置名称
	 */
	private String name;

	/**
	 * 分销层级：1-一级 2-二级 3-三级
	 */
	private Integer level;

	/**
	 * 佣金比例(%)
	 */
	private BigDecimal commissionRate;

	/**
	 * 分销商等级：0-全部 1-普通 2-铜牌 3-银牌 4-金牌 5-钻石
	 */
	private Integer distributorLevel;

	/**
	 * 状态：0-禁用 1-启用
	 */
	private Integer status;

	/**
	 * 备注
	 */
	private String remark;

}
