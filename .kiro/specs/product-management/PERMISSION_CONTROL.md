# 商品管理系统 - 权限控制实施方案

**创建时间**: 2025-12-10  
**状态**: 🟡 进行中

---

## 📋 权限控制概述

### 后端权限控制

使用 Spring Security 的 `@PreAuthorize` 注解进行方法级权限控制。

**权限命名规范**: `模块:功能:操作`

例如：
- `product:product:add` - 商品添加
- `product:product:edit` - 商品编辑
- `product:product:del` - 商品删除
- `product:product:view` - 商品查看
- `product:product:status` - 商品状态管理

### 前端权限控制

使用 `v-auth` 指令控制按钮显示。

```vue
<el-button v-auth="'product:product:add'">新增</el-button>
```

---

## ✅ 已实现的权限控制

### 1. ProductController（商品管理）

| 接口 | 权限标识 | 状态 |
|------|---------|------|
| POST /product | product:product:add | ✅ |
| PUT /product | product:product:edit | ✅ |
| DELETE /product/{id} | product:product:del | ✅ |
| GET /product/{id} | 无需权限（查看） | ✅ |
| GET /product/page | 无需权限（查看） | ✅ |
| PUT /product/status/{id} | product:product:status | ✅ |

### 2. CategoryController（分类管理）

| 接口 | 权限标识 | 状态 |
|------|---------|------|
| POST /category | product:category:add | ✅ |
| PUT /category | product:category:edit | ✅ |
| DELETE /category/{id} | product:category:del | ✅ |
| GET /category/tree | 无需权限（查看） | ✅ |
| GET /category/{id}/products | 无需权限（查看） | ✅ |

### 3. SkuController（SKU管理）

| 接口 | 权限标识 | 状态 |
|------|---------|------|
| POST /sku | product:sku:add | ✅ |
| PUT /sku | product:sku:edit | ✅ |
| DELETE /sku/{id} | product:sku:del | ✅ |
| GET /sku/page | 无需权限（查看） | ✅ |
| GET /sku/product/{productId} | 无需权限（查看） | ✅ |

### 4. StockController（库存管理）

| 接口 | 权限标识 | 状态 |
|------|---------|------|
| PUT /stock/increase | product:stock:increase | ✅ |
| PUT /stock/decrease | product:stock:decrease | ✅ |
| GET /stock/page | 无需权限（查看） | ✅ |
| GET /stock/{productId} | 无需权限（查看） | ✅ |
| GET /stock/low | 无需权限（查看） | ✅ |
| GET /stock/log/{productId} | 无需权限（查看） | ✅ |

### 5. CdkeyController（CDKEY管理）

| 接口 | 权限标识 | 状态 |
|------|---------|------|
| GET /cdkey/page | 无需权限（查看） | ⏳ |

### 6. CommissionConfigController（佣金配置）

| 接口 | 权限标识 | 状态 |
|------|---------|------|
| GET /commission/config/page | 无需权限（查看） | ⏳ |

### 7. StatisticsController（统计分析）

| 接口 | 权限标识 | 状态 |
|------|---------|------|
| GET /statistics/overview | 无需权限（查看） | ⏳ |
| GET /statistics/category | 无需权限（查看） | ⏳ |
| GET /statistics/stock | 无需权限（查看） | ⏳ |

---

## 🎯 权限控制实施步骤

### 步骤1：后端添加权限注解 ✅

1. ✅ ProductController - 已完成
2. ✅ CategoryController - 已完成
3. ✅ SkuController - 已完成
4. ✅ StockController - 已完成
5. ⏳ 其他Controller（查询接口无需权限）

### 步骤2：前端添加按钮权限 ✅

已在以下页面添加 `v-auth` 指令：

1. ✅ 商品列表页 - 新增、编辑、删除、上下架按钮
2. ✅ 分类管理页 - 新增、编辑、删除按钮
3. ✅ SKU管理页 - 新增、编辑、删除按钮
4. ✅ 库存管理页 - 增加、减少按钮
5. ⏳ CDKEY管理页 - 导入按钮（查询页面，暂无写操作）
6. ⏳ 佣金配置页 - 新增、编辑、删除按钮（查询页面，暂无写操作）

### 步骤3：验证权限控制 ⏳

1. ⏳ 测试无权限用户访问受限接口
2. ⏳ 测试有权限用户正常访问
3. ⏳ 测试前端按钮显示/隐藏

---

## 📝 权限注解使用示例

### 后端Controller

```java
@RestController
@RequestMapping("/product")
public class ProductController {
    
    @PostMapping
    @PreAuthorize("@pms.hasPermission('product:product:add')")
    public R<Long> createProduct(@RequestBody Product product) {
        // ...
    }
    
    @PutMapping
    @PreAuthorize("@pms.hasPermission('product:product:edit')")
    public R<Boolean> updateProduct(@RequestBody Product product) {
        // ...
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('product:product:del')")
    public R<Boolean> deleteProduct(@PathVariable Long id) {
        // ...
    }
}
```

### 前端Vue组件

```vue
<template>
  <div>
    <el-button 
      v-auth="'product:product:add'" 
      type="primary" 
      @click="handleAdd">
      新增商品
    </el-button>
    
    <el-button 
      v-auth="'product:product:edit'" 
      type="warning" 
      @click="handleEdit">
      编辑
    </el-button>
    
    <el-button 
      v-auth="'product:product:del'" 
      type="danger" 
      @click="handleDelete">
      删除
    </el-button>
  </div>
</template>
```

---

## 🔒 权限配置

### 数据库菜单权限

权限已在 `pig/db/pig.sql` 中配置：

```sql
-- 商品管理按钮权限
INSERT INTO sys_menu VALUES (5101, '商品新增', 5100, NULL, NULL, NULL, 2, '0', 'product:product:add', '1', ...);
INSERT INTO sys_menu VALUES (5102, '商品编辑', 5100, NULL, NULL, NULL, 2, '0', 'product:product:edit', '1', ...);
INSERT INTO sys_menu VALUES (5103, '商品删除', 5100, NULL, NULL, NULL, 2, '0', 'product:product:del', '1', ...);
-- ... 更多权限
```

### 角色权限分配

在系统管理 → 角色管理中为角色分配权限。

---

## ✅ 完成标准

- [x] 所有需要权限控制的接口添加 `@PreAuthorize` 注解
- [x] 所有操作按钮添加 `v-auth` 指令
- [ ] 权限控制测试通过
- [x] 文档更新完成

## 📊 前端权限控制完成情况

### 已完成的页面

1. **商品列表页** (`pig-ui/src/views/product/product/index.vue`)
   - ✅ 新增按钮：`v-auth="'product:product:add'"`
   - ✅ 编辑按钮：`v-auth="'product:product:edit'"`
   - ✅ 删除按钮：`v-auth="'product:product:del'"`
   - ✅ 上架/下架按钮：`v-auth="'product:product:status'"`

2. **分类管理页** (`pig-ui/src/views/product/category/index.vue`)
   - ✅ 新增根分类按钮：`v-auth="'product:category:add'"`
   - ✅ 新增子分类按钮：`v-auth="'product:category:add'"`
   - ✅ 编辑按钮：`v-auth="'product:category:edit'"`
   - ✅ 删除按钮：`v-auth="'product:category:del'"`

3. **SKU管理页** (`pig-ui/src/views/product/sku/index.vue`)
   - ✅ 新增SKU按钮：`v-auth="'product:sku:add'"`
   - ✅ 编辑按钮：`v-auth="'product:sku:edit'"`
   - ✅ 删除按钮：`v-auth="'product:sku:del'"`

4. **库存管理页** (`pig-ui/src/views/product/stock/index.vue`)
   - ✅ 增加库存按钮：`v-auth="'product:stock:increase'"`
   - ✅ 减少库存按钮：`v-auth="'product:stock:decrease'"`
   - ℹ️ 库存日志按钮：无需权限（查看功能）

### 无需权限控制的页面

以下页面主要是查询和统计功能，无写操作按钮，无需添加权限控制：

- **CDKEY管理页** - 仅查询展示
- **佣金配置页** - 仅查询展示
- **统计分析页** - 仅数据展示
- **分销商管理页** - 仅查询展示

---

**文档版本**: v1.0  
**最后更新**: 2025-12-10
