# 快速故障排除参考手册

## 常见问题快速解决方案

### 1. 系统无法启动

**问题**：Docker 容器启动失败
```bash
# 检查 Docker 状态
sudo systemctl status docker

# 重启 Docker
sudo systemctl restart docker

# 重新启动所有服务
cd /opt/chapt003
docker-compose down
docker-compose up -d

# 查看日志
docker-compose logs -f
```

### 2. 前端开发环境问题

**问题**：Vue3 项目无法启动
```bash
# 重新安装依赖
rm -rf node_modules package-lock.json
npm install

# 清除缓存
npm cache clean --force

# 使用不同端口
npm run dev -- --port 5174

# 类型检查
npm run typecheck

# 代码检查
npm run lint
```

### 3. 数据库连接问题

**问题**：应用无法连接 PostgreSQL
```bash
# 检查数据库服务
sudo systemctl status postgresql

# 连接数据库
psql -U chapt003 -d chapt003 -h localhost -p 5432

# 检查数据库配置
cat /opt/chapt003/backend/src/main/resources/application.yml
```

### 4. API 接口问题

**问题**：前端无法访问后端 API
```bash
# 检查后端服务
curl http://localhost:8080/api/health

# 检查 CORS 配置
curl -H "Origin: http://localhost:5173" -v http://localhost:8080/api/health

# 检查代理配置
cat /opt/chapt003/frontend/vite.config.ts
```

### 5. 微信小程序问题

**问题**：小程序无法编译或发布
```bash
# 检查配置文件
cat /opt/chapt003/weixincode/app.json

# 检查域名配置
cat /opt/chapt003/weixincode/project.config.json

# 检查隐私政策
ls -la /opt/chapt003/weixincode/pages/

# 测试 API 调用
curl http://localhost:8080/api/test
```

### 6. 性能问题

**问题**：页面加载缓慢
```bash
# 检查网络请求
# 使用浏览器开发者工具 Network 面板

# 检查构建输出
ls -la /opt/chapt003/frontend/dist/assets/

# 优化构建
npm run build -- --mode production

# 检查数据库查询
psql -U chapt003 -d chapt003 -c "EXPLAIN ANALYZE SELECT * FROM users;"
```

### 7. SSL 证书问题

**问题**：HTTPS 访问失败
```bash
# 检查证书状态
sudo openssl x509 -in /etc/letsencrypt/live/your-domain/cert.pem -enddate -noout

# 续签证书
sudo certbot renew --dry-run

# 检查 Nginx 配置
sudo nginx -t

# 重启 Nginx
sudo systemctl reload nginx
```

### 8. 内存和磁盘空间

**问题**：系统资源不足
```bash
# 检查磁盘空间
df -h

# 检查内存使用
free -h

# 清理 Docker 资源
docker system prune -f

# 清理日志
sudo find /var/log -name "*.log" -size +100M -exec truncate -s 0 {} \;
```

### 9. 权限问题

**问题**：文件访问权限错误
```bash
# 检查文件权限
ls -la /opt/chapt003/

# 修复权限
sudo chown -R chapt003:chapt003 /opt/chapt003/

# 修复文件权限
sudo chmod 644 /opt/chapt003/backend/src/main/resources/application.yml
sudo chmod 755 /opt/chapt003/scripts/*.sh
```

### 10. 网络问题

**问题**：网络连接异常
```bash
# 检查网络连接
ping google.com

# 检查端口占用
netstat -tlnp | grep 8080

# 检查防火墙
sudo systemctl status firewalld

# 测试 API 连接
curl http://localhost:8080/api/users/1
```

## 紧急处理命令

### 系统紧急重启
```bash
# 停止所有服务
docker-compose down

# 清理网络
docker network prune -f

# 重新启动
docker-compose up -d

# 检查状态
docker-compose ps
```

### 数据库紧急恢复
```bash
# 停止服务
docker-compose down

# 恢复数据库
docker-compose exec postgres bash
psql -U chapt003 -d chapt003 < /backup/backup_20240101.sql

# 重启服务
docker-compose up -d
```

### 安全紧急处理
```bash
# 阻止可疑 IP
iptables -A INPUT -s [可疑IP] -j DROP

# 备份日志
tar -czf security_backup_$(date +%Y%m%d).tar.gz /var/log/nginx /var/log/systemd

# 重置密码
passwd username
```

## 常见错误代码

### HTTP 错误代码
- **404 Not Found**: 检查 API 路径和配置
- **500 Internal Server Error**: 检查服务器日志和错误信息
- **403 Forbidden**: 检查权限和认证配置
- **502 Bad Gateway**: 检查后端服务状态
- **503 Service Unavailable**: 检查服务负载和资源

### 数据库错误代码
- **Connection refused**: 检查数据库服务状态
- **Access denied**: 检查用户权限和密码
- **Database not found**: 检查数据库名称和配置
- **Table not found**: 检查表结构初始化

### Docker 错误代码
- **Container failed to start**: 检查 Docker 日志和配置
- **Port already in use**: 检查端口占用情况
- **Image not found**: 检查镜像构建和推送
- **Volume mount failed**: 检查挂载路径和权限

## 监控和检查命令

### 系统状态检查
```bash
# 系统资源
top
free -h
df -h

# Docker 状态
docker ps -a
docker images
docker system df

# 服务状态
systemctl status docker
systemctl status nginx
systemctl status postgresql
```

### 应用状态检查
```bash
# 健康检查
curl http://localhost:8080/api/health

# 数据库连接
psql -U chapt003 -c "SELECT version();"

# 前端构建
npm run build

# 测试覆盖率
npm run test:coverage
```

### 日志查看
```bash
# 应用日志
docker-compose logs -f [服务名]

# 系统日志
journalctl -u docker --since "1 hour ago"

# Nginx 日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

## 联系支持

如果在遇到无法解决的问题时，请联系：

- **技术支持**：support@your-domain.com
- **紧急联系**：13800138000
- **问题报告**：https://github.com/your-repo/issues

## 快速检查清单

### 每日检查
- [ ] 系统资源使用情况
- [ ] 容器运行状态
- [ ] 数据库连接数
- [ ] API 响应时间
- [ ] 错误日志

### 每周检查
- [ ] 备份完整性
- [ ] 安全更新
- [ ] 性能指标
- [ ] 证书有效期
- [ ] 磁盘空间

### 每月检查
- [ ] 数据库性能
- [ ] 应用性能
- [ ] 安全扫描
- [ ] 依赖更新
- [ ] 灾难恢复演练

---

*本手册为快速参考，详细解决方案请参考完整版故障排除和维护指南。*