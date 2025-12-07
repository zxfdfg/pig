package com.pig4cloud.pig.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.distribution.api.entity.CommissionConfig;
import com.pig4cloud.pig.distribution.api.entity.DistRelation;
import com.pig4cloud.pig.distribution.api.entity.Distributor;
import com.pig4cloud.pig.distribution.api.entity.OrderCommission;
import com.pig4cloud.pig.distribution.mapper.CommissionConfigMapper;
import com.pig4cloud.pig.distribution.mapper.DistRelationMapper;
import com.pig4cloud.pig.distribution.mapper.DistributorMapper;
import com.pig4cloud.pig.distribution.mapper.OrderCommissionMapper;
import com.pig4cloud.pig.distribution.service.CommissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 佣金服务实现
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommissionServiceImpl extends ServiceImpl<OrderCommissionMapper, OrderCommission>
		implements CommissionService {

	private final DistributorMapper distributorMapper;

	private final DistRelationMapper relationMapper;

	private final CommissionConfigMapper configMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean calculateCommission(Long orderId, String orderNo, Long buyerId, BigDecimal orderAmount) {
		log.info("开始计算佣金: orderId={}, buyerId={}, amount={}", orderId, buyerId, orderAmount);

		// 1. 查询买家是否是分销商
		Distributor buyerDistributor = distributorMapper
			.selectOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, buyerId));

		if (buyerDistributor == null) {
			log.info("买家不是分销商，无需计算佣金: buyerId={}", buyerId);
			return true;
		}

		// 2. 查询买家的推荐链（最多3级）
		List<DistRelation> relations = relationMapper.selectList(
				new LambdaQueryWrapper<DistRelation>().eq(DistRelation::getDistributorId, buyerDistributor.getId())
					.le(DistRelation::getLevel, 3)
					.orderByAsc(DistRelation::getLevel));

		if (relations.isEmpty()) {
			log.info("买家没有上级，无需计算佣金: buyerId={}", buyerId);
			return true;
		}

		// 3. 遍历每一级，计算佣金
		for (DistRelation relation : relations) {
			Long ancestorId = relation.getAncestorId();
			Integer level = relation.getLevel();

			// 4. 查询分销商信息
			Distributor distributor = distributorMapper.selectById(ancestorId);
			if (distributor == null || distributor.getStatus() != 1) {
				log.warn("分销商不存在或已禁用，跳过: distributorId={}", ancestorId);
				continue;
			}

			// 5. 查询佣金配置
			CommissionConfig config = configMapper
				.selectOne(new LambdaQueryWrapper<CommissionConfig>().eq(CommissionConfig::getLevel, level)
					.and(wrapper -> wrapper.eq(CommissionConfig::getDistributorLevel, 0)
						.or()
						.eq(CommissionConfig::getDistributorLevel, distributor.getLevel()))
					.eq(CommissionConfig::getStatus, 1)
					.orderByDesc(CommissionConfig::getDistributorLevel)
					.last("LIMIT 1"));

			if (config == null) {
				log.warn("未找到佣金配置，跳过: level={}, distributorLevel={}", level, distributor.getLevel());
				continue;
			}

			// 6. 计算佣金金额
			BigDecimal commissionAmount = orderAmount.multiply(config.getCommissionRate())
				.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

			// 7. 创建佣金记录
			OrderCommission commission = new OrderCommission();
			commission.setOrderId(orderId);
			commission.setOrderNo(orderNo);
			commission.setDistributorId(ancestorId);
			commission.setBuyerId(buyerId);
			commission.setLevel(level);
			commission.setOrderAmount(orderAmount);
			commission.setCommissionRate(config.getCommissionRate());
			commission.setCommissionAmount(commissionAmount);
			commission.setStatus(0); // 待结算
			baseMapper.insert(commission);

			// 8. 更新分销商累计佣金
			distributor.setTotalCommission(distributor.getTotalCommission().add(commissionAmount));
			distributorMapper.updateById(distributor);

			log.info("佣金计算成功: distributorId={}, level={}, amount={}", ancestorId, level, commissionAmount);
		}

		log.info("订单佣金计算完成: orderId={}", orderId);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean settleCommission(Long commissionId) {
		OrderCommission commission = baseMapper.selectById(commissionId);
		if (commission == null) {
			throw new RuntimeException("佣金记录不存在");
		}

		if (commission.getStatus() != 0) {
			throw new RuntimeException("佣金状态不正确");
		}

		// 更新佣金状态
		commission.setStatus(1);
		commission.setSettleTime(LocalDateTime.now());
		baseMapper.updateById(commission);

		// 更新分销商可用余额
		Distributor distributor = distributorMapper.selectById(commission.getDistributorId());
		distributor.setAvailableCommission(distributor.getAvailableCommission().add(commission.getCommissionAmount()));
		distributorMapper.updateById(distributor);

		log.info("佣金结算成功: commissionId={}, distributorId={}, amount={}", commissionId, commission.getDistributorId(),
				commission.getCommissionAmount());
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean cancelCommission(Long orderId) {
		List<OrderCommission> commissions = baseMapper
			.selectList(new LambdaQueryWrapper<OrderCommission>().eq(OrderCommission::getOrderId, orderId));

		for (OrderCommission commission : commissions) {
			if (commission.getStatus() == 2) {
				continue; // 已取消
			}

			// 如果已结算，需要扣除可用余额
			if (commission.getStatus() == 1) {
				Distributor distributor = distributorMapper.selectById(commission.getDistributorId());
				distributor.setAvailableCommission(
						distributor.getAvailableCommission().subtract(commission.getCommissionAmount()));
				distributorMapper.updateById(distributor);
			}

			// 更新佣金状态
			commission.setStatus(2);
			commission.setCancelTime(LocalDateTime.now());
			baseMapper.updateById(commission);

			// 扣除累计佣金
			Distributor distributor = distributorMapper.selectById(commission.getDistributorId());
			distributor.setTotalCommission(distributor.getTotalCommission().subtract(commission.getCommissionAmount()));
			distributorMapper.updateById(distributor);
		}

		log.info("订单佣金取消成功: orderId={}", orderId);
		return true;
	}

	@Override
	public Map<String, Object> getCommissionStats(Long distributorId) {
		Map<String, Object> stats = new HashMap<>();

		// 累计佣金
		BigDecimal totalCommission = baseMapper
			.selectList(new LambdaQueryWrapper<OrderCommission>().eq(OrderCommission::getDistributorId, distributorId)
				.in(OrderCommission::getStatus, 0, 1))
			.stream()
			.map(OrderCommission::getCommissionAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		// 待结算佣金
		BigDecimal pendingCommission = baseMapper
			.selectList(new LambdaQueryWrapper<OrderCommission>().eq(OrderCommission::getDistributorId, distributorId)
				.eq(OrderCommission::getStatus, 0))
			.stream()
			.map(OrderCommission::getCommissionAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		// 已结算佣金
		BigDecimal settledCommission = baseMapper
			.selectList(new LambdaQueryWrapper<OrderCommission>().eq(OrderCommission::getDistributorId, distributorId)
				.eq(OrderCommission::getStatus, 1))
			.stream()
			.map(OrderCommission::getCommissionAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		stats.put("totalCommission", totalCommission);
		stats.put("pendingCommission", pendingCommission);
		stats.put("settledCommission", settledCommission);

		return stats;
	}

}
