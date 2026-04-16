@echo off
docker-compose build --no-cache
docker-compose up -d
echo 部署完成！
echo 前端地址: http://localhost
echo 后端地址: http://localhost:8080
pause