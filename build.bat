@echo off
echo ========================================
echo 编译 PIG 项目
echo ========================================

cd /d %~dp0

echo.
echo [1/3] 清理项目...
call mvn clean

echo.
echo [2/3] 编译项目（跳过测试）...
call mvn install -DskipTests -T 4

echo.
echo [3/3] 编译完成！
echo.
echo 接下来可以启动服务了
echo.
pause
