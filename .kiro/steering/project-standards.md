# PIG 后端微服务开发规范

## 项目概述

PIG 是基于 Spring Cloud 2025、Spring Boot 3.5、OAuth2 的 RBAC 企业快速开发平台，支持微服务架构和单体架构。

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.5.8
- **Spring Cloud**: 2025.0.0
- **Spring Cloud Alibaba**: 2025.0.0.0
- **Spring Authorization Server**: 1.5.2
- **MyBatis Plus**: 3.5.15
- **构建工具**: Maven

## 模块结构

```
pig/
├── pig-auth          # 授权服务 [3000]
├── pig-gateway       # 网关服务 [9999]
├── pig-register      # Nacos 注册中心 [8848]
├── pig-upms          # 用户权限管理
│   ├── pig-upms-api  # API 模块
│   └── pig-upms-biz  # 业务模块 [4000]
├── pig-common        # 公共模块
│   ├── pig-common-core       # 核心工具类
│   ├── pig-common-security   # 安全工具
│   ├── pig-common-mybatis    # MyBatis 扩展
│   ├── pig-common-datasource # 动态数据源
│   ├── pig-common-log        # 日志服务
│   ├── pig-common-swagger    # API 文档
│   ├── pig-common-feign      # Feign 扩展
│   └── ...
├── pig-visual        # 可视化模块
│   ├── pig-monitor   # 服务监控 [5001]
│   ├── pig-codegen   # 代码生成 [5002]
│   └── pig-quartz    # 定时任务 [5007]
└── pig-boot          # 单体启动器 [9999]
```

## 编码规范

### 代码格式化

**必须遵守 Spring Java Format 规范**

- 使用 `spring-javaformat-maven-plugin` 进行代码格式化
- 提交代码前运行: `mvn spring-javaformat:apply`
- IDEA 用户安装插件: [spring-javaformat-intellij-idea-plugin](https://repo1.maven.org/maven2/io/spring/javaformat/spring-javaformat-intellij-idea-plugin/)
- 未按格式提交的代码将无法通过合并

### 命名规范

- **包名**: 全小写，使用点分隔 `com.pig4cloud.pig.模块名`
- **类名**: 大驼峰 `UserController`, `UserServiceImpl`
- **方法名**: 小驼峰 `getUserById`, `saveUser`
- **常量**: 全大写下划线分隔 `MAX_SIZE`, `DEFAULT_TIMEOUT`
- **变量**: 小驼峰，见名知意

### 注解使用

- **Controller**: 使用 `@RestController` + `@RequestMapping`
- **Service**: 接口 + `@Service` 实现类
- **Mapper**: 使用 `@Mapper` 注解
- **实体类**: 使用 Lombok 注解 `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **API 文档**: 使用 Swagger 注解 `@Tag`, `@Operation`, `@Parameter`

### 分层架构

```
Controller -> Service -> Mapper -> Entity
     ↓          ↓          ↓         ↓
   DTO       业务逻辑    数据访问   数据库表
```

- **Controller**: 只负责接收请求、参数校验、返回响应
- **Service**: 业务逻辑处理，事务控制
- **Mapper**: 数据访问层，使用 MyBatis Plus
- **Entity**: 数据库实体，与表一一对应
- **DTO/VO**: 数据传输对象，用于前后端交互

## 依赖管理

### 版本统一

- 所有模块版本使用 `${revision}` 统一管理
- 公共依赖在父 pom.xml 的 `<dependencies>` 中定义
- 版本号在 `<properties>` 中统一声明

### 引入新依赖

1. 优先使用 Spring Boot/Cloud 已管理的依赖
2. 新增第三方依赖需在父 pom 的 `<dependencyManagement>` 中声明版本
3. 子模块引入时不指定版本号

## 配置管理

### 配置文件

- 使用 `application.yml` 作为主配置文件
- 敏感信息使用 Jasypt 加密: `ENC(加密后的内容)`
- 环境配置使用 Nacos 配置中心管理

### 配置优先级

```
Nacos 配置中心 > application-{profile}.yml > application.yml
```

## 数据库规范

### 表设计

- 表名: 小写下划线分隔 `sys_user`, `sys_role`
- 字段名: 小写下划线分隔 `user_id`, `create_time`
- 主键: 统一使用 `id` (BIGINT)
- 必备字段: `create_time`, `update_time`, `del_flag`

### MyBatis Plus

- 使用 `BaseMapper<T>` 继承基础 CRUD
- 复杂查询使用 `QueryWrapper` 或 `LambdaQueryWrapper`
- 自定义 SQL 写在对应的 XML 文件中

## 异常处理

- 使用统一异常处理 `@RestControllerAdvice`
- 业务异常抛出自定义异常
- 返回统一响应格式 `R<T>`

## 日志规范

- 使用 SLF4J + Logback
- 日志级别: ERROR > WARN > INFO > DEBUG
- 关键业务操作必须记录日志
- 避免打印敏感信息（密码、token 等）

## 安全规范

- 所有接口需要权限控制（除公开接口）
- 使用 `@PreAuthorize` 进行权限校验
- 密码使用 BCrypt 加密
- 敏感数据传输使用 HTTPS

## API 设计

### RESTful 风格

- GET: 查询
- POST: 新增
- PUT: 修改
- DELETE: 删除

### 响应格式

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## 测试规范

- 单元测试使用 JUnit 5
- 集成测试使用 Spring Boot Test
- 测试覆盖率建议 > 70%

## Git 提交规范

- feat: 新功能
- fix: 修复 bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具链相关

## 构建部署

### 本地开发

```bash
# 启动 Nacos
cd pig-register && mvn spring-boot:run

# 启动网关
cd pig-gateway && mvn spring-boot:run

# 启动业务服务
cd pig-upms/pig-upms-biz && mvn spring-boot:run
```

### Docker 部署

```bash
# 构建镜像
mvn clean package docker:build

# 使用 docker-compose
docker-compose up -d
```

## 性能优化

- 使用 Redis 缓存热点数据
- 数据库查询添加合适索引
- 大数据量查询使用分页
- 异步处理耗时操作

## 文档

- 配套文档: https://wiki.pig4cloud.com
- API 文档通过 Swagger 自动生成
- 重要功能需编写设计文档
