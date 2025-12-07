package com.pig4cloud.pig.distribution.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.distribution.api.dto.DistributorApplyDTO;
import com.pig4cloud.pig.distribution.api.entity.Distributor;
import com.pig4cloud.pig.distribution.api.vo.DistributorVO;
import com.pig4cloud.pig.distribution.service.DistributorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * 分销商控制器
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/distributor")
@Tag(description = "distributor", name = "分销商管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class DistributorController {

	private final DistributorService distributorService;

	/**
	 * 申请成为分销商
	 * @param dto 申请信息
	 * @return R
	 */
	@SysLog("申请成为分销商")
	@PostMapping("/apply")
	@Operation(summary = "申请成为分销商")
	public R<Boolean> apply(@RequestBody DistributorApplyDTO dto) {
		return R.ok(distributorService.applyDistributor(dto));
	}

	/**
	 * 获取当前用户的分销商信息
	 * @return R
	 */
	@GetMapping("/info")
	@Operation(summary = "获取分销商信息")
	public R<DistributorVO> getInfo() {
		return R.ok(distributorService.getCurrentDistributor());
	}

	/**
	 * 分页查询分销商列表
	 * @param page 分页对象
	 * @param distributor 查询条件
	 * @return 分销商列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询分销商列表")
	public R<IPage<Distributor>> page(Page<Distributor> page, Distributor distributor) {
		LambdaQueryWrapper<Distributor> wrapper = new LambdaQueryWrapper<>();

		if (distributor.getStatus() != null) {
			wrapper.eq(Distributor::getStatus, distributor.getStatus());
		}
		if (distributor.getLevel() != null) {
			wrapper.eq(Distributor::getLevel, distributor.getLevel());
		}

		wrapper.orderByDesc(Distributor::getCreateTime);

		return R.ok(distributorService.page(page, wrapper));
	}

	/**
	 * 根据ID查询分销商
	 * @param id 分销商ID
	 * @return 分销商详情
	 */
	@GetMapping("/{id}")
	@Operation(summary = "根据ID查询分销商")
	public R<Distributor> getById(@PathVariable Long id) {
		return R.ok(distributorService.getById(id));
	}

	/**
	 * 审核分销商
	 * @param id 分销商ID
	 * @param dto 审核信息
	 * @return 是否成功
	 */
	@SysLog("审核分销商")
	@PutMapping("/audit/{id}")
	@Operation(summary = "审核分销商")
	public R<Boolean> audit(@PathVariable Long id, @RequestBody AuditDTO dto) {
		Distributor distributor = distributorService.getById(id);
		if (distributor == null) {
			return R.failed("分销商不存在");
		}

		distributor.setStatus(dto.getStatus());
		return R.ok(distributorService.updateById(distributor));
	}

	/**
	 * 更新分销商
	 * @param distributor 分销商信息
	 * @return 是否成功
	 */
	@SysLog("更新分销商")
	@PutMapping
	@Operation(summary = "更新分销商")
	public R<Boolean> update(@RequestBody Distributor distributor) {
		return R.ok(distributorService.updateById(distributor));
	}

	/**
	 * 审核DTO
	 */
	@Data
	public static class AuditDTO {

		private Integer status;

		private String remark;

	}

}
