---
inclusion: manual
---

# MCP 服务使用指南

本项目已配置 5 个 MCP 服务，用于增强开发效率。

## 已配置的 MCP 服务

### 1. Git MCP
**功能：** Git 版本控制操作
- 查看提交历史和分支信息
- 分析代码变更
- 查看文件修改记录
- 帮助编写规范的 commit message

**使用场景：**
- "查看最近 10 次提交记录"
- "显示当前分支信息"
- "查看某个文件的修改历史"

### 2. Filesystem MCP
**功能：** 文件系统操作
- 快速搜索文件
- 批量文件操作
- 分析项目结构
- 查找特定代码模式

**使用场景：**
- "搜索所有包含 @RestController 的文件"
- "查找所有 application.yml 配置文件"
- "列出所有 Service 实现类"

### 3. Docker MCP
**功能：** Docker 容器管理
- 查看运行中的容器
- 管理容器生命周期
- 查看容器日志
- 监控容器状态

**使用场景：**
- "查看所有运行中的容器"
- "显示 MySQL 容器的日志"
- "重启 Nacos 容器"
- "查看 docker-compose 服务状态"

### 4. Memory MCP
**功能：** 知识库管理
- 存储项目特定信息
- 记录常用配置
- 保存代码片段
- 记录团队约定

**使用场景：**
- "记住：Nacos 默认用户名密码是 nacos/nacos"
- "保存这个 SQL 查询模板"
- "记录 Redis 配置信息"

### 5. Playwright MCP
**功能：** 浏览器自动化测试
- 自动化 UI 测试
- 网页截图
- 表单自动填充测试
- 端到端测试

**使用场景：**
- "测试登录页面功能"
- "截取首页的屏幕截图"
- "自动化测试用户注册流程"

## 前置要求

确保已安装 `uv` 和 `uvx`：

```bash
# 使用 pip 安装
pip install uv

# 或使用 pipx
pipx install uv
```

## 配置文件位置

- 后端：`pig/.kiro/settings/mcp.json`
- 前端：`pig-ui/.kiro/settings/mcp.json`

## 重新连接 MCP 服务

如果 MCP 服务连接失败：

1. 打开命令面板（Ctrl+Shift+P）
2. 搜索 "MCP"
3. 选择 "Reconnect MCP Servers"

或者在 Kiro 侧边栏的 "MCP Servers" 视图中手动重连。

## 常见问题

### Q: MCP 服务无法启动？
A: 确保已安装 `uv` 和 `uvx`，并且网络连接正常。首次使用时 uvx 会自动下载所需的包。

### Q: 如何禁用某个 MCP 服务？
A: 在 mcp.json 中将对应服务的 `disabled` 设置为 `true`。

### Q: 如何自动批准某些工具调用？
A: 在 `autoApprove` 数组中添加工具名称，例如：
```json
"autoApprove": ["git_log", "git_status"]
```

## 最佳实践

1. **Git MCP**: 在提交代码前查看变更，确保符合提交规范
2. **Filesystem MCP**: 快速定位需要修改的文件
3. **Docker MCP**: 开发时快速查看服务状态和日志
4. **Memory MCP**: 记录项目特定的配置和约定
5. **Playwright MCP**: 自动化测试前端功能

## 扩展阅读

- MCP 官方文档: https://modelcontextprotocol.io
- Kiro MCP 使用指南: 在命令面板搜索 "MCP" 查看更多命令
