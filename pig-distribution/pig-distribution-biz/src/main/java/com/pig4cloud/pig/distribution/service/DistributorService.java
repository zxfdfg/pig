package com.pig4cloud.pig.distribution.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.distribution.api.dto.DistributorApplyDTO;
import com.pig4cloud.pig.distribution.api.entity.Distributor;
import com.pig4cloud.pig.distribution.api.vo.DistributorVO;

/**
 * 分销商Service
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
public interface DistributorService extends IService<Distributor> {

	/**
	 * 申请成为分销商
	 * @param dto 申请信息
	 * @return 是否成功
	 */
	Boolean applyDistributor(DistributorApplyDTO dto);

	/**
	 * 获取当前用户的分销商信息
	 * @return 分销商信息
	 */
	DistributorVO getCurrentDistributor();

	/**
	 * 建立分销关系
	 * @param userId 用户ID
	 * @param parentId 上级分销商ID
	 */
	void buildRelation(Long userId, Long parentId);

	/**
	 * 更新上级的团队人数
	 * @param parentId 上级分销商ID
	 */
	void updateParentCount(Long parentId);

}
