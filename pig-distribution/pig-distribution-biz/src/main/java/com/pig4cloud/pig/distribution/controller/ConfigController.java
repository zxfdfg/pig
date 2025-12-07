package com.pig4cloud.pig.distribution.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.distribution.api.entity.CommissionConfig;
import com.pig4cloud.pig.distribution.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 佣金配置控制器
 *
 * @author pig4cloud
 * @date 2025-12-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/config")
@Tag(description = "config", name = "佣金配置管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ConfigController {

	private final ConfigService configService;

	/**
	 * 分页查询配置列表
	 * @param page 分页对象
	 * @param config 查询条件
	 * @return 配置列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询配置列表")
	public R<IPage<CommissionConfig>> page(Page<CommissionConfig> page, CommissionConfig config) {
		LambdaQueryWrapper<CommissionConfig> wrapper = new LambdaQueryWrapper<>();

		if (config.getLevel() != null) {
			wrapper.eq(CommissionConfig::getLevel, config.getLevel());
		}
		if (config.getDistributorLevel() != null) {
			wrapper.eq(CommissionConfig::getDistributorLevel, config.getDistributorLevel());
		}
		if (config.getStatus() != null) {
			wrapper.eq(CommissionConfig::getStatus, config.getStatus());
		}

		wrapper.orderByAsc(CommissionConfig::getLevel).orderByAsc(CommissionConfig::getDistributorLevel);

		return R.ok(configService.page(page, wrapper));
	}

	/**
	 * 查询所有配置
	 * @return 配置列表
	 */
	@GetMapping("/list")
	@Operation(summary = "查询所有配置")
	public R<List<CommissionConfig>> list() {
		return R.ok(configService.list(new LambdaQueryWrapper<CommissionConfig>().orderByAsc(CommissionConfig::getLevel)
			.orderByAsc(CommissionConfig::getDistributorLevel)));
	}

	/**
	 * 根据ID查询配置
	 * @param id 配置ID
	 * @return 配置详情
	 */
	@GetMapping("/{id}")
	@Operation(summary = "根据ID查询配置")
	public R<CommissionConfig> getById(@PathVariable Long id) {
		return R.ok(configService.getById(id));
	}

	/**
	 * 新增配置
	 * @param config 配置信息
	 * @return 是否成功
	 */
	@SysLog("新增佣金配置")
	@PostMapping
	@Operation(summary = "新增配置")
	public R<Boolean> save(@RequestBody CommissionConfig config) {
		return R.ok(configService.save(config));
	}

	/**
	 * 修改配置
	 * @param config 配置信息
	 * @return 是否成功
	 */
	@SysLog("修改佣金配置")
	@PutMapping
	@Operation(summary = "修改配置")
	public R<Boolean> update(@RequestBody CommissionConfig config) {
		return R.ok(configService.updateById(config));
	}

	/**
	 * 删除配置
	 * @param id 配置ID
	 * @return 是否成功
	 */
	@SysLog("删除佣金配置")
	@DeleteMapping("/{id}")
	@Operation(summary = "删除配置")
	public R<Boolean> delete(@PathVariable Long id) {
		return R.ok(configService.removeById(id));
	}

}
