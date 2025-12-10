package com.pig4cloud.pig.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.product.api.entity.Product;
import com.pig4cloud.pig.product.api.entity.ProductStockLog;
import com.pig4cloud.pig.product.mapper.ProductMapper;
import com.pig4cloud.pig.product.mapper.StockLogMapper;
import com.pig4cloud.pig.product.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 库存管理服务实现
 *
 * @author pig4cloud
 * @date 2025-12-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

	private final ProductMapper productMapper;

	private final StockLogMapper stockLogMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean increaseStock(Long productId, Integer quantity, String remark) {
		Assert.notNull(productId, "商品ID不能为空");
		Assert.notNull(quantity, "数量不能为空");
		Assert.isTrue(quantity > 0, "增加数量必须大于0");

		// 查询商品
		Product product = productMapper.selectById(productId);
		Assert.notNull(product, "商品不存在");

		// 记录操作前库存
		Integer beforeStock = product.getStock();

		// 增加库存
		product.setStock(beforeStock + quantity);

		// 如果商品是售罄状态且库存大于0，自动改为下架状态
		if (product.getStatus() == 3 && product.getStock() > 0) {
			product.setStatus(2); // 改为下架状态
			log.info("商品库存恢复，自动改为下架状态，商品ID: {}", productId);
		}

		// 更新商品
		productMapper.updateById(product);

		// 记录库存日志
		recordStockLog(productId, null, 1, quantity, beforeStock, product.getStock(), null, remark);

		log.info("增加库存成功，商品ID: {}, 增加数量: {}, 当前库存: {}", productId, quantity, product.getStock());

		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean decreaseStock(Long productId, Integer quantity, String remark) {
		Assert.notNull(productId, "商品ID不能为空");
		Assert.notNull(quantity, "数量不能为空");
		Assert.isTrue(quantity > 0, "减少数量必须大于0");

		// 查询商品
		Product product = productMapper.selectById(productId);
		Assert.notNull(product, "商品不存在");

		// 验证库存充足
		Assert.isTrue(product.getStock() >= quantity, "库存不足，当前库存: " + product.getStock());

		// 记录操作前库存
		Integer beforeStock = product.getStock();

		// 减少库存
		product.setStock(beforeStock - quantity);

		// 如果库存为0，自动改为售罄状态
		if (product.getStock() == 0) {
			product.setStatus(3); // 售罄状态
			log.info("商品库存为0，自动改为售罄状态，商品ID: {}", productId);
		}
		// 如果库存低于预警值，记录日志
		else if (product.getStock() < product.getStockWarning()) {
			log.warn("商品库存低于预警值，商品ID: {}, 当前库存: {}, 预警值: {}", productId, product.getStock(), product.getStockWarning());
		}

		// 更新商品
		productMapper.updateById(product);

		// 记录库存日志
		recordStockLog(productId, null, 2, -quantity, beforeStock, product.getStock(), null, remark);

		log.info("减少库存成功，商品ID: {}, 减少数量: {}, 当前库存: {}", productId, quantity, product.getStock());

		return true;
	}

	@Override
	public Integer getStock(Long productId) {
		Assert.notNull(productId, "商品ID不能为空");

		Product product = productMapper.selectById(productId);
		Assert.notNull(product, "商品不存在");

		return product.getStock();
	}

	@Override
	public List<Product> getLowStockProducts() {
		// 查询库存低于预警值的商品
		LambdaQueryWrapper<Product> query = Wrappers.lambdaQuery();
		query.apply("stock < stock_warning");
		query.eq(Product::getDelFlag, 0);
		query.orderByAsc(Product::getStock);

		return productMapper.selectList(query);
	}

	@Override
	public List<ProductStockLog> getStockLogs(Long productId) {
		Assert.notNull(productId, "商品ID不能为空");

		LambdaQueryWrapper<ProductStockLog> query = Wrappers.lambdaQuery();
		query.eq(ProductStockLog::getProductId, productId);
		query.orderByDesc(ProductStockLog::getCreateTime);

		return stockLogMapper.selectList(query);
	}

	@Override
	public Object getStockPage(Integer current, Integer size, String productName, String stockStatus) {
		// 查询商品列表（带库存信息）
		LambdaQueryWrapper<Product> query = Wrappers.lambdaQuery();
		query.like(StrUtil.isNotBlank(productName), Product::getName, productName);
		
		// 根据库存状态筛选
		if (StrUtil.isNotBlank(stockStatus)) {
			if ("low".equals(stockStatus)) {
				// 低库存：库存 <= 预警值
				query.apply("stock <= stock_warning");
			}
			else if ("out".equals(stockStatus)) {
				// 售罄：库存 = 0
				query.eq(Product::getStock, 0);
			}
			else if ("normal".equals(stockStatus)) {
				// 正常：库存 > 预警值
				query.apply("stock > stock_warning");
			}
		}
		
		query.eq(Product::getDelFlag, 0);
		query.orderByDesc(Product::getId);

		return productMapper.selectPage(new Page<>(current, size), query);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deductStockForOrder(Long productId, Integer quantity, Long orderId) {
		Assert.notNull(productId, "商品ID不能为空");
		Assert.notNull(quantity, "数量不能为空");
		Assert.notNull(orderId, "订单ID不能为空");
		Assert.isTrue(quantity > 0, "数量必须大于0");

		// 查询商品
		Product product = productMapper.selectById(productId);
		Assert.notNull(product, "商品不存在");

		// 验证库存充足
		Assert.isTrue(product.getStock() >= quantity, "库存不足，当前库存: " + product.getStock());

		// 记录操作前库存
		Integer beforeStock = product.getStock();

		// 扣减库存
		product.setStock(beforeStock - quantity);

		// 增加销量
		product.setSales(product.getSales() + quantity);

		// 如果库存为0，自动改为售罄状态
		if (product.getStock() == 0) {
			product.setStatus(3); // 售罄状态
			log.info("订单扣库存后库存为0，自动改为售罄状态，商品ID: {}, 订单ID: {}", productId, orderId);
		}

		// 更新商品
		productMapper.updateById(product);

		// 记录库存日志
		recordStockLog(productId, null, 3, -quantity, beforeStock, product.getStock(), orderId, "订单扣减库存");

		log.info("订单扣库存成功，商品ID: {}, 订单ID: {}, 扣减数量: {}, 当前库存: {}", productId, orderId, quantity, product.getStock());

		return true;
	}

	/**
	 * 记录库存日志
	 * @param productId 商品ID
	 * @param skuId SKU ID
	 * @param type 类型：1-入库，2-出库，3-订单扣减，4-订单退回
	 * @param quantity 数量（正数为增加，负数为减少）
	 * @param beforeStock 操作前库存
	 * @param afterStock 操作后库存
	 * @param orderId 订单ID
	 * @param remark 备注
	 */
	private void recordStockLog(Long productId, Long skuId, Integer type, Integer quantity, Integer beforeStock,
			Integer afterStock, Long orderId, String remark) {
		ProductStockLog log = new ProductStockLog();
		log.setProductId(productId);
		log.setSkuId(skuId);
		log.setType(type);
		log.setQuantity(quantity);
		log.setBeforeStock(beforeStock);
		log.setAfterStock(afterStock);
		log.setOrderId(orderId);
		log.setRemark(remark);

		stockLogMapper.insert(log);
	}

}
