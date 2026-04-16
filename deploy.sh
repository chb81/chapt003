#!/bin/bash

# Docker部署脚本
set -e

echo "🐳 开始部署志愿填报系统..."

# 创建日志目录
mkdir -p logs

# 构建并启动服务
echo "📦 构建Docker镜像..."
docker-compose build --no-cache

echo "🚀 启动服务..."
docker-compose up -d

echo "📊 检查服务状态..."
docker-compose ps

echo "🎉 部署完成！"
echo "前端地址: http://localhost"
echo "后端地址: http://localhost:8080"

# 等待服务启动
sleep 10

# 检查健康状态
echo "🔍 检查服务健康状态..."
curl -f http://localhost:8080/api/health || echo "后端服务健康检查失败"
curl -f http://localhost || echo "前端服务健康检查失败"