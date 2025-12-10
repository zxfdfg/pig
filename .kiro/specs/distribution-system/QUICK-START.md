# 分销系统快速开发指南

## 📋 文档导航

| 文档 | 说明 | 路径 |
|------|------|------|
| 需求文档 | 8个核心需求，30+验收标准 | `requirements.md` |
| 设计文档 | 架构、数据库、算法设计 | `design.md` |
| 任务清单 | 56个任务，133小时工作量 | `tasks.md` |
| 当前状态 | 实时进度和问题跟踪 | `../../.kiro/steering/distribution-current-status.md` |

---

## 🚀 快速启动

### 1. 数据库初始化

```bash
# 进入数据库目录
cd pig/db

# 导入主数据库（包含菜单配置）
mysql -u root -p pig < pig.sql

# 导入配置数据库（包含网关路由）
mysql -u root -p pig_config < pig_config.sql

# 导入分销系统表
mysql -u root -p pig < distribution-schema.sql
```

### 2. 启动后端服务

```bash
# 1. 启动 Nacos (8848)
cd pig/pig-register
mvn spring-boot:run

# 2. 启动网关 (9999)
cd pig/pig-gateway
mvn spring-boot:run

# 3. 启动 UPMS (4000)
cd pig/pig-upms/pig-upms-biz
mvn spring-boot:run

# 4. 启动 Auth (3000)
cd pig/pig-auth
mvn spring-boot:run

# 5. 启动 Distribution (4200)
cd pig/pig-distribution/pig-distribution-biz
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd pig-ui
npm run dev
# 访问: http://localhost:8888
```

### 4. 配置权限

1. 登录系统 (admin/admin)
2. 进入 **权限管理 → 角色管理**
3. 找到 **管理员** 角色，点击 **分配权限**
4. 勾选 **分销管理** 及所有子菜单
5. 保存并重新登录

---

## 📂 项目结构

```
pig/
├── pig-distribution/                    # 分销系统模块
│   ├── pig-distribution-api/           # API 模块
│   │   ├── entity/                     # 实体类
│   │   ├── dto/                        # 数据传输对象
│   │   └── vo/                         # 视图对象
│   └── pig-distribution-biz/           # 业务模块
│       ├── controller/                 # 控制器
│       ├── service/                    # 服务层
│       │   └── impl/                   # 服务实现
│       ├── mapper/                     # 数据访问层
│       └── resources/
│           ├── application.yml         # 配置文件
│           └── mapper/                 # MyBatis XML
├── db/
│   ├── pig.sql                         # 主数据库（含菜单）
│   ├── pig_config.sql                  # 配置数据库（含路由）
│   └── distribution-schema.sql         # 分销系统表
└── .kiro/
    ├── specs/distribution-system/      # Spec 文档
    │   ├── requirements.md             # 需求文档
    │   ├── design.md                   # 设计文档
    │   ├── tasks.md                    # 任务清单
    │   └── QUICK-START.md              # 本文档
    └── steering/
        ├── distribution-current-status.md  # 当前状态
        ├── distribution-system-plan.md     # 系统方案
        └── distribution-implementation-summary.md  # 实施总结

pig-ui/
├── src/
│   ├── views/distribution/             # 分销页面
│   │   ├── index.vue                   # 分销中心首页
│   │   ├── distributor/                # 分销商管理
│   │   ├── commission/                 # 佣金管理
│   │   ├── withdraw/                   # 提现管理
│   │   └── config/                     # 佣金配置
│   └── api/distribution/               # API 接口
│       ├── distributor.ts
│       ├── commission.ts
│       ├── withdraw.ts
│       └── config.ts
```

---

## 🎯 下一步开发任务

### 优先级 1：核心业务逻辑（必须）

#### 任务 4.3：佣金计算服务 ⭐ 最核心
```
文件: pig/pig-distribution/pig-distribution-biz/src/main/java/com/pig4cloud/pig/distribution/service/impl/CommissionServiceImpl.java

需要实现:
1. calculateCommission() - 订单佣金计算
2. createCommissionRecord() - 创建佣金记录
3. updateDistributorBalance() - 更新分销商余额
4. createCommissionLog() - 创建佣金流水

参考: design.md 第 5.1 节 - 佣金计算算法
预计时间: 6-8 小时
```

#### 任务 4.2：分销关系管理
```
文件: pig/pig-distribution/pig-distribution-biz/src/main/java/com/pig4cloud/pig/distribution/service/impl/RelationServiceImpl.java

需要实现:
1. buildRelation() - 建立分销关系（闭包表）
2. queryAncestors() - 查询所有上级
3. queryDescendants() - 查询所有下级
4. updateTeamStats() - 更新团队统计

参考: design.md 第 5.2 节 - 分销关系建立算法
预计时间: 4-6 小时
```

#### 任务 4.1：分销商管理服务
```
文件: pig/pig-distribution/pig-distribution-biz/src/main/java/com/pig4cloud/pig/distribution/service/impl/DistributorServiceImpl.java

需要实现:
1. applyDistributor() - 申请成为分销商
2. auditDistributor() - 审核分销商
3. queryDistributors() - 查询分销商列表
4. updateLevel() - 更新分销商等级

参考: requirements.md 需求 1
预计时间: 4-6 小时
```

### 优先级 2：提现管理（重要）

#### 任务 4.4：提现管理服务
```
文件: pig/pig-distribution/pig-distribution-biz/src/main/java/com/pig4cloud/pig/distribution/service/impl/WithdrawServiceImpl.java

需要实现:
1. applyWithdraw() - 申请提现
2. auditWithdraw() - 审核提现
3. confirmPay() - 确认打款
4. validateWithdrawLimit() - 验证提现限制

参考: requirements.md 需求 5
预计时间: 6-8 小时
```

### 优先级 3：数据统计（重要）

#### 任务 4.6：数据统计服务
```
文件: pig/pig-distribution/pig-distribution-biz/src/main/java/com/pig4cloud/pig/distribution/service/impl/StatsServiceImpl.java

需要实现:
1. getDistributorStats() - 分销商个人统计
2. getCommissionStats() - 佣金统计
3. getWithdrawStats() - 提现统计
4. getTeamStats() - 团队统计

参考: requirements.md 需求 6
预计时间: 6-8 小时
```

---

## 💡 开发建议

### 1. 开发顺序

建议按以下顺序开发，确保核心功能优先：

1. **分销关系管理** (4.2) - 基础，其他功能依赖它
2. **分销商管理** (4.1) - 用户入口
3. **佣金计算** (4.3) - 核心业务逻辑
4. **数据统计** (4.6) - 展示数据
5. **提现管理** (4.4) - 资金流出
6. **Controller 层** (5.x) - 接口暴露
7. **前后端对接** (6.x) - 功能联调
8. **权限控制** (7.x) - 安全加固

### 2. 代码规范

```java
// 1. 使用 Lombok 简化代码
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistDistributor {
    // ...
}

// 2. 使用 MyBatis Plus 简化 CRUD
public interface DistributorMapper extends BaseMapper<DistDistributor> {
    // 自定义方法
}

// 3. 使用 Lambda 表达式查询
LambdaQueryWrapper<DistDistributor> wrapper = Wrappers.<DistDistributor>lambdaQuery()
    .eq(DistDistributor::getStatus, 1)
    .orderByDesc(DistDistributor::getCreateTime);

// 4. 统一返回格式
return R.ok(data);  // 成功
return R.failed("错误信息");  // 失败

// 5. 添加权限注解
@PreAuthorize("hasAuthority('dist_distributor_view')")
public R<IPage<DistDistributor>> page(Page page) {
    // ...
}

// 6. 添加日志
@Slf4j
public class DistributorServiceImpl {
    public void method() {
        log.info("操作开始");
        // ...
        log.info("操作完成");
    }
}
```

### 3. 测试建议

```java
// 单元测试示例
@SpringBootTest
class CommissionServiceTest {
    
    @Autowired
    private CommissionService commissionService;
    
    @Test
    void testCalculateCommission() {
        // Given
        Long orderId = 1L;
        BigDecimal orderAmount = new BigDecimal("1000");
        
        // When
        commissionService.calculateCommission(orderId, orderAmount);
        
        // Then
        // 验证佣金记录是否创建
        // 验证余额是否更新
    }
}
```

### 4. 常见问题

**Q: 佣金计算性能慢怎么办？**
A: 使用异步处理 + Redis 缓存配置数据

**Q: 分销关系查询慢怎么办？**
A: 确保闭包表有正确的索引，考虑缓存热点数据

**Q: 并发更新余额出现问题？**
A: 使用乐观锁或数据库行锁

**Q: 前端路径别名报错？**
A: 确保使用 `/@/` 而不是 `@/`

---

## 🔍 调试技巧

### 1. 查看服务日志

```bash
# 查看 distribution 服务日志
tail -f pig/pig-distribution/pig-distribution-biz/logs/distribution.log

# 查看网关日志
tail -f pig/pig-gateway/logs/gateway.log
```

### 2. 测试接口

```bash
# 使用 curl 测试接口
curl -X GET "http://localhost:9999/distribution/distributor/page?current=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 或访问 Swagger 文档
http://localhost:4200/doc.html
```

### 3. 查看数据库

```sql
-- 查看分销商数据
SELECT * FROM dist_distributor LIMIT 10;

-- 查看分销关系
SELECT * FROM dist_relation WHERE distributor_id = 1;

-- 查看佣金记录
SELECT * FROM dist_order_commission WHERE distributor_id = 1;
```

---

## 📞 获取帮助

### 文档位置
- 需求文档: `.kiro/specs/distribution-system/requirements.md`
- 设计文档: `.kiro/specs/distribution-system/design.md`
- 任务清单: `.kiro/specs/distribution-system/tasks.md`
- 当前状态: `.kiro/steering/distribution-current-status.md`

### 关键代码位置
- 后端服务: `pig/pig-distribution/pig-distribution-biz/`
- 前端页面: `pig-ui/src/views/distribution/`
- API 接口: `pig-ui/src/api/distribution/`
- 数据库脚本: `pig/db/distribution-schema.sql`

### 开发规范
- 后端规范: `.kiro/steering/project-standards.md` (pig)
- 前端规范: `.kiro/steering/project-standards.md` (pig-ui)

---

**祝开发顺利！** 🚀

如有问题，请参考完整的 Spec 文档或查看当前状态报告。
