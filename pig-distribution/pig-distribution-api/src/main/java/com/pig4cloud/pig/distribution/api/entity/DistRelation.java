package com.pig4cloud.pig.distribution.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分销关系实体
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Data
@TableName("dist_relation")
public class DistRelation implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 分销商ID
	 */
	private Long distributorId;

	/**
	 * 上级分销商ID
	 */
	private Long ancestorId;

	/**
	 * 层级：1-直接上级 2-二级上级 3-三级上级
	 */
	private Integer level;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

}
