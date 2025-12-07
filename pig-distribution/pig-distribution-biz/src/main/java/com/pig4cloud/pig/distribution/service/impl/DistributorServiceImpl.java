package com.pig4cloud.pig.distribution.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.distribution.api.dto.DistributorApplyDTO;
import com.pig4cloud.pig.distribution.api.entity.DistRelation;
import com.pig4cloud.pig.distribution.api.entity.Distributor;
import com.pig4cloud.pig.distribution.api.vo.DistributorVO;
import com.pig4cloud.pig.distribution.mapper.DistRelationMapper;
import com.pig4cloud.pig.distribution.mapper.DistributorMapper;
import com.pig4cloud.pig.distribution.service.DistributorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分销商Service实现
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributorServiceImpl extends ServiceImpl<DistributorMapper, Distributor> implements DistributorService {

	private final DistRelationMapper relationMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean applyDistributor(DistributorApplyDTO dto) {
		Long userId = SecurityUtils.getUser().getId();

		// 检查是否已经是分销商
		Distributor existDistributor = baseMapper
			.selectOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));

		if (existDistributor != null) {
			throw new RuntimeException("您已经是分销商了");
		}

		// 创建分销商记录
		Distributor distributor = new Distributor();
		distributor.setUserId(userId);
		distributor.setParentId(dto.getParentId());
		distributor.setLevel(1);
		distributor.setStatus(2); // 待审核
		distributor.setTotalSales(BigDecimal.ZERO);
		distributor.setTotalCommission(BigDecimal.ZERO);
		distributor.setAvailableCommission(BigDecimal.ZERO);
		distributor.setFrozenCommission(BigDecimal.ZERO);
		distributor.setWithdrawnCommission(BigDecimal.ZERO);
		distributor.setDirectCount(0);
		distributor.setTeamCount(0);
		distributor.setRealName(dto.getRealName());
		distributor.setPhone(dto.getPhone());
		distributor.setIdCard(dto.getIdCard());
		distributor.setApplyTime(LocalDateTime.now());

		baseMapper.insert(distributor);

		// 如果有推荐人，建立分销关系
		if (dto.getParentId() != null) {
			buildRelation(userId, dto.getParentId());
		}

		log.info("用户 {} 申请成为分销商: userId={}", "apply", userId);
		return true;
	}

	@Override
	public DistributorVO getCurrentDistributor() {
		Long userId = SecurityUtils.getUser().getId();

		Distributor distributor = baseMapper
			.selectOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));

		if (distributor == null) {
			return null;
		}

		DistributorVO vo = new DistributorVO();
		BeanUtil.copyProperties(distributor, vo);

		// 设置等级名称
		vo.setLevelName(getLevelName(distributor.getLevel()));

		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void buildRelation(Long userId, Long parentId) {
		// 获取分销商ID
		Distributor distributor = baseMapper
			.selectOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));

		if (distributor == null) {
			throw new RuntimeException("分销商不存在");
		}

		// 查询上级的所有上级关系
		List<DistRelation> parentRelations = relationMapper
			.selectList(new LambdaQueryWrapper<DistRelation>().eq(DistRelation::getDistributorId, parentId));

		// 建立直接关系
		DistRelation directRelation = new DistRelation();
		directRelation.setDistributorId(distributor.getId());
		directRelation.setAncestorId(parentId);
		directRelation.setLevel(1);
		directRelation.setCreateTime(LocalDateTime.now());
		relationMapper.insert(directRelation);

		// 建立间接关系（最多3级）
		for (DistRelation parentRelation : parentRelations) {
			if (parentRelation.getLevel() < 3) {
				DistRelation indirectRelation = new DistRelation();
				indirectRelation.setDistributorId(distributor.getId());
				indirectRelation.setAncestorId(parentRelation.getAncestorId());
				indirectRelation.setLevel(parentRelation.getLevel() + 1);
				indirectRelation.setCreateTime(LocalDateTime.now());
				relationMapper.insert(indirectRelation);
			}
		}

		// 更新上级的直推人数和团队人数
		updateParentCount(parentId);

		log.info("建立分销关系: distributorId={}, parentId={}", distributor.getId(), parentId);
	}

	@Override
	public void updateParentCount(Long parentId) {
		Distributor parent = baseMapper.selectById(parentId);
		if (parent == null) {
			return;
		}

		// 统计直推人数
		Long directCount = baseMapper
			.selectCount(new LambdaQueryWrapper<Distributor>().eq(Distributor::getParentId, parentId));

		// 统计团队总人数
		Long teamCount = relationMapper
			.selectCount(new LambdaQueryWrapper<DistRelation>().eq(DistRelation::getAncestorId, parentId));

		parent.setDirectCount(directCount.intValue());
		parent.setTeamCount(teamCount.intValue());
		baseMapper.updateById(parent);

		// 递归更新上级的上级
		if (parent.getParentId() != null) {
			updateParentCount(parent.getParentId());
		}
	}

	/**
	 * 获取等级名称
	 */
	private String getLevelName(Integer level) {
		return switch (level) {
			case 1 -> "普通会员";
			case 2 -> "铜牌分销商";
			case 3 -> "银牌分销商";
			case 4 -> "金牌分销商";
			case 5 -> "钻石分销商";
			default -> "未知";
		};
	}

}
