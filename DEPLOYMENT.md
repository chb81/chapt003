# 部署文档

## Docker部署指南

### 前提条件
- Docker 20.10+
- Docker Compose 2.0+

### 快速部署

#### Windows系统
```bash
# 运行部署脚本
deploy.bat
```

#### Linux/Mac系统
```bash
# 给脚本执行权限
chmod +x deploy.sh

# 运行部署脚本
./deploy.sh
```

#### 手动部署
```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 停止并删除数据
docker-compose down -v
```

## 服务访问地址

- **前端应用**: http://localhost
- **后端API**: http://localhost:8080
- **数据库**: localhost:5432
- **Redis**: localhost:6379

## 服务管理

### 查看日志
```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### 重启服务
```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart backend
docker-compose restart frontend
```

### 进入容器
```bash
# 进入后端容器
docker-compose exec backend bash

# 进入前端容器
docker-compose exec frontend sh

# 进入数据库容器
docker-compose exec postgres psql -U postgres -d volunteer_system
```

## 数据备份

### 数据库备份
```bash
# 备份数据库
docker-compose exec postgres pg_dump -U postgres volunteer_system > backup.sql

# 恢复数据库
docker-compose exec -T postgres psql -U postgres volunteer_system < backup.sql
```

## 生产环境配置

### 环境变量配置
创建 `.env` 文件：
```env
# 数据库配置
POSTGRES_DB=volunteer_system
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password

# Redis配置
REDIS_PASSWORD=your_redis_password

# 后端配置
SPRING_PROFILES_ACTIVE=production
JWT_SECRET=your_jwt_secret_key
```

### SSL配置
修改 `nginx.conf` 添加SSL支持：
```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    # 其他配置...
}
```

## 性能优化

### 前端优化
- 启用Gzip压缩（已配置）
- 静态文件缓存（已配置）
- CDN加速
- 图片懒加载

### 后端优化
- 数据库连接池配置
- Redis缓存
- 异步处理

### 数据库优化
- 索引优化
- 查询优化
- 连接池配置

## 监控和日志

### 日志管理
```bash
# 查看实时日志
docker-compose logs -f --tail=100

# 导出日志
docker-compose logs > app.log
```

### 性能监控
- 应用性能监控（APM）
- 数据库监控
- 服务器资源监控

## 故障排查

### 常见问题

1. **服务无法启动**
   - 检查端口占用
   - 查看日志: `docker-compose logs`

2. **数据库连接失败**
   - 检查数据库服务状态
   - 验证连接配置

3. **前端无法访问后端**
   - 检查网络配置
   - 验证代理配置

## 安全建议

1. 使用强密码
2. 启用SSL/TLS
3. 配置防火墙规则
4. 定期更新依赖
5. 实施访问控制
6. 定期备份数据

## 扩展部署

### 多实例部署
```yaml
# docker-compose.yml
backend:
  deploy:
    replicas: 3  # 部署3个后端实例
```

### 负载均衡
使用Nginx或HAProxy进行负载均衡配置。

## 回滚部署

```bash
# 停止当前服务
docker-compose down

# 恢复之前的镜像
docker pull your-registry/backend:previous-version

# 重新部署
docker-compose up -d
```

## 联系支持

如遇问题，请联系技术支持团队或查阅相关文档。