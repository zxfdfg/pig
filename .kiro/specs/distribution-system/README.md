# 分销系统 Spec 文档

## 📖 文档概述

本目录包含分销系统的完整 Spec 文档，包括需求、设计和实施计划。这些文档确保即使在上下文丢失的情况下，也能继续项目开发。

---

## 📚 文档列表

| 文档 | 说明 | 状态 |
|------|------|------|
| [需求文档](./requirements.md) | 8个核心需求，30+验收标准 | ✅ 完成 |
| [设计文档](./design.md) | 系统架构、数据库设计、核心算法 | ✅ 完成 |
| [任务清单](./tasks.md) | 56个详细任务，133小时工作量 | ✅ 完成 |
| [快速开发指南](./QUICK-START.md) | 快速启动和开发指南 | ✅ 完成 |

---

## 🎯 项目状态

**当前完成度**: 70%  
**已完成**: 基础架构、菜单权限、前端页面  
**进行中**: 无  
**待完成**: 后端业务逻辑、前后端对接、权限控制

详细状态请查看: [当前状态报告](../../.kiro/steering/distribution-current-status.md)

---

## 🚀 快速开始

### 1. 阅读文档

建议按以下顺序阅读：

1. **[快速开发指南](./QUICK-START.md)** - 了解项目结构和快速启动
2. **[需求文档](./requirements.md)** - 理解业务需求和验收标准
3. **[设计文档](./design.md)** - 了解技术架构和实现方案
4. **[任务清单](./tasks.md)** - 查看详细的开发任务

### 2. 环境准备

```bash
# 1. 初始化数据库
mysql -u root -p pig < pig/db/pig.sql
mysql -u root -p pig_config < pig/db/pig_config.sql
mysql -u root -p pig < pig/db/distribution-schema.sql

# 2. 启动后端服务
cd pig/pig-distribution/pig-distribution-biz
mvn spring-boot:run

# 3. 启动前端
cd pig-ui
npm run dev
```

### 3. 开始开发

参考 [任务清单](./tasks.md) 中的待完成任务，按优先级开发：

**优先级 1**: 佣金计算服务 (核心)  
**优先级 2**: 分销关系管理  
**优先级 3**: 分销商管理  

---

## 📊 项目概览

### 核心功能

1. **分销商管理** - 申请、审核、等级管理
2. **分销关系管理** - 闭包表、团队统计
3. **佣金计算** - 自动计算、分配、结算
4. **提现管理** - 申请、审核、打款
5. **佣金配置** - 灵活配置佣金比例
6. **数据统计** - 多维度数据统计

### 技术栈

- **后端**: Spring Boot 3.5 + Spring Cloud 2025 + MyBatis Plus 3.5
- **前端**: Vue 3.5 + Element Plus 2.8 + Pinia 2.3
- **数据库**: MySQL 8.0 + Redis 7.0
- **架构**: 微服务架构

### 数据库表

1. `dist_distributor` - 分销商表
2. `dist_relation` - 分销关系表（闭包表）
3. `dist_commission_config` - 佣金配置表
4. `dist_order_commission` - 订单佣金表
5. `dist_withdraw` - 提现表
6. `dist_commission_log` - 佣金流水表

---

## 📋 开发任务

### 已完成 (70%)

- ✅ 基础架构搭建
- ✅ 数据库设计
- ✅ 菜单权限配置
- ✅ 前端页面开发
- ✅ API 接口文件

### 待完成 (30%)

- ⏳ 后端业务逻辑实现
- ⏳ Controller 层实现
- ⏳ 前后端接口对接
- ⏳ 权限控制完善
- ⏳ 性能优化
- ⏳ 测试与文档

详细任务列表请查看: [任务清单](./tasks.md)

---

## 🎯 里程碑

- [x] **里程碑 1**: 基础架构搭建完成 (2025-11-20)
- [x] **里程碑 2**: 菜单权限配置完成 (2025-12-07)
- [x] **里程碑 3**: 前端页面开发完成 (2025-12-07)
- [ ] **里程碑 4**: 核心业务逻辑完成 (预计 2025-12-14)
- [ ] **里程碑 5**: 前后端联调完成 (预计 2025-12-18)
- [ ] **里程碑 6**: 测试与优化完成 (预计 2025-12-22)
- [ ] **里程碑 7**: 正式上线 (预计 2025-12-25)

---

## 📁 文件结构

```
.kiro/specs/distribution-system/
├── README.md                    # 本文档
├── requirements.md              # 需求文档
├── design.md                    # 设计文档
├── tasks.md                     # 任务清单
└── QUICK-START.md               # 快速开发指南
```

---

## 🔗 相关资源

### 项目文档
- [系统方案](../../.kiro/steering/distribution-system-plan.md)
- [实施总结](../../.kiro/steering/distribution-implementation-summary.md)
- [交付清单](../../.kiro/steering/distribution-delivery-checklist.md)
- [当前状态](../../.kiro/steering/distribution-current-status.md)

### 代码位置
- 后端代码: `pig/pig-distribution/`
- 前端代码: `pig-ui/src/views/distribution/`
- API 接口: `pig-ui/src/api/distribution/`
- 数据库脚本: `pig/db/distribution-schema.sql`

### 开发规范
- [后端开发规范](../../.kiro/steering/project-standards.md) (pig)
- [前端开发规范](../../.kiro/steering/project-standards.md) (pig-ui)

---

## ⚠️ 重要提醒

1. **法律合规**: 分销层级不超过 3 级
2. **数据安全**: 敏感信息需加密存储
3. **性能要求**: 佣金计算应在 3 秒内完成
4. **代码规范**: 遵循 PIG 项目规范
5. **测试覆盖**: 核心逻辑需要单元测试

---

## 📞 获取帮助

如有问题，请：

1. 查看 [快速开发指南](./QUICK-START.md)
2. 查看 [当前状态报告](../../.kiro/steering/distribution-current-status.md)
3. 参考 [设计文档](./design.md) 中的实现方案
4. 查看 [任务清单](./tasks.md) 中的详细说明

---

**文档版本**: v1.0  
**创建时间**: 2025-12-07  
**最后更新**: 2025-12-07  
**维护者**: 开发团队

---

**开始开发吧！** 🚀
