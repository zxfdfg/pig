@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ==========================================
echo   PIG 平台 Windows 部署脚本
echo ==========================================
echo.

REM 配置变量
set SERVER_USER=root
set SERVER_HOST=
set DEPLOY_DIR=/opt/pig-platform
set LOCAL_PIG_DIR=..\pig
set LOCAL_PIG_UI_DIR=..\pig-ui
set LOCAL_DOCS_DIR=..\znhaas-docs

REM 获取服务器地址
set /p SERVER_HOST="请输入服务器地址 (例: 192.168.1.100): "
if "%SERVER_HOST%"=="" (
    echo [错误] 服务器地址不能为空
    pause
    exit /b 1
)

echo.
echo [1/10] 检查本地环境...
where ssh >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到 SSH 命令，请安装 Git for Windows 或 OpenSSH
    pause
    exit /b 1
)

where node >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到 Node.js，请先安装 Node.js
    pause
    exit /b 1
)

echo [✓] 本地环境检查完成

echo.
echo [2/10] 检查服务器环境...
ssh %SERVER_USER%@%SERVER_HOST% "docker --version && docker-compose --version" >nul 2>&1
if errorlevel 1 (
    echo [错误] 服务器未安装 Docker 或 Docker Compose
    echo 请先在服务器上运行: curl -fsSL https://get.docker.com | bash
    pause
    exit /b 1
)
echo [✓] 服务器环境检查完成

echo.
echo [3/10] 构建前端项目...
cd %LOCAL_PIG_UI_DIR%
if not exist "node_modules" (
    echo 安装依赖...
    call npm install
)
echo 构建 PIG-UI...
call npm run build
if errorlevel 1 (
    echo [错误] PIG-UI 构建失败
    pause
    exit /b 1
)
cd ..\deployment-guide
echo [✓] PIG-UI 构建完成

echo.
echo [4/10] 构建文档项目...
cd %LOCAL_DOCS_DIR%
if not exist "node_modules" (
    echo 安装依赖...
    call npm install
)
echo 构建文档...
call npm run build
if errorlevel 1 (
    echo [错误] 文档构建失败
    pause
    exit /b 1
)
cd ..\deployment-guide
echo [✓] 文档构建完成

echo.
echo [5/10] 创建服务器目录...
ssh %SERVER_USER%@%SERVER_HOST% "mkdir -p %DEPLOY_DIR%/{nginx/{conf.d,ssl},mysql/{data,init},redis/data,pig-ui/dist,docs/build}"
echo [✓] 目录创建完成

echo.
echo [6/10] 上传配置文件...
scp docker-compose.yml %SERVER_USER%@%SERVER_HOST%:%DEPLOY_DIR%/
scp .env %SERVER_USER%@%SERVER_HOST%:%DEPLOY_DIR%/
scp nginx/nginx.conf %SERVER_USER%@%SERVER_HOST%:%DEPLOY_DIR%/nginx/
scp nginx/conf.d/default.conf %SERVER_USER%@%SERVER_HOST%:%DEPLOY_DIR%/nginx/conf.d/
echo [✓] 配置文件上传完成

echo.
echo [7/10] 上传前端文件...
scp -r %LOCAL_PIG_UI_DIR%\dist\* %SERVER_USER%@%SERVER_HOST%:%DEPLOY_DIR%/pig-ui/dist/
echo [✓] PIG-UI 上传完成

echo.
echo [8/10] 上传文档文件...
scp -r %LOCAL_DOCS_DIR%\build\* %SERVER_USER%@%SERVER_HOST%:%DEPLOY_DIR%/docs/build/
echo [✓] 文档上传完成

echo.
echo [9/10] 上传数据库脚本...
scp %LOCAL_PIG_DIR%\db\pig.sql %SERVER_USER%@%SERVER_HOST%:%DEPLOY_DIR%/mysql/init/
scp %LOCAL_PIG_DIR%\db\pig_config.sql %SERVER_USER%@%SERVER_HOST%:%DEPLOY_DIR%/mysql/init/
echo [✓] 数据库脚本上传完成

echo.
echo [10/10] 启动服务...
ssh %SERVER_USER%@%SERVER_HOST% "cd %DEPLOY_DIR% && docker-compose up -d"
echo [✓] 服务启动完成

echo.
echo ==========================================
echo   部署完成！
echo ==========================================
echo.
echo 访问地址:
echo   - 管理后台: http://%SERVER_HOST%/admin/
echo   - 文档系统: http://%SERVER_HOST%/docs/
echo   - 监控面板: http://%SERVER_HOST%/monitor/
echo.
echo 查看日志:
echo   ssh %SERVER_USER%@%SERVER_HOST% "cd %DEPLOY_DIR% && docker-compose logs -f"
echo.
pause
