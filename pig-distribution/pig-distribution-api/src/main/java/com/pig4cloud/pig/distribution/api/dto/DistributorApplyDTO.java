package com.pig4cloud.pig.distribution.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分销商申请DTO
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Data
public class DistributorApplyDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 真实姓名
	 */
	private String realName;

	/**
	 * 联系电话
	 */
	private String phone;

	/**
	 * 身份证号
	 */
	private String idCard;

	/**
	 * 推荐人ID（可选）
	 */
	private Long parentId;

}
