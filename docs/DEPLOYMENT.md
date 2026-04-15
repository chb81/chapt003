# 志愿汇系统部署指南

## 目录

1. [系统架构](#系统架构)
2. [环境要求](#环境要求)
3. [快速开始](#快速开始)
4. [生产环境部署](#生产环境部署)
5. [开发环境配置](#开发环境配置)
6. [数据备份与恢复](#数据备份与恢复)
7. [监控与运维](#监控与运维)
8. [常见问题](#常见问题)

## 系统架构

志愿汇系统采用微服务架构，包含以下组件：

- **前端**: Vue3 + Vite + TypeScript + Element Plus
- **后端**: Spring Boot + PostgreSQL + Redis
- **微信小程序**: 原生微信小程序开发
- **代理服务器**: Nginx
- **容器化**: Docker + Docker Compose

```
┌─────────────┐
│   Nginx     │ (反向代理)
└──────┬──────┘
       │
   ┌───┴─────┐
   │         │
┌──▼──┐   ┌─▼──────┐
│前端  │   │ 后端   │
│Vue3 │   │Spring  │
└──┬──┘   │  Boot  │
   │      └─┬───┬──┘
   │        │   │
   │    ┌───▼─┐ │
   │    │Redis│ │
   │    └────┘ │
   │          │
   │       ┌──▼────┐
   │       │PostgreSQL│
   │       └────────┘
   │
┌──▼──────────────────────┐
│    微信小程序            │
└─────────────────────────┘
```

## 环境要求

### 硬件要求

- **CPU**: 2核心或以上
- **内存**: 4GB或以上
- **硬盘**: 20GB或以上
- **网络**: 稳定的互联网连接

### 软件要求

- **操作系统**: Linux (推荐 Ubuntu 20.04+) 或 Windows 10+
- **Docker**: 20.10.0或更高版本
- **Docker Compose**: 1.29.0或更高版本
- **Node.js**: 18.0.0或更高版本 (开发环境)
- **Java**: JDK 8或更高版本 (开发环境)
- **Maven**: 3.6.0或更高版本 (开发环境)

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/volunteer-system.git
cd volunteer-system
```

### 2. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑环境变量文件
nano .env
```

### 3. 启动服务

```bash
# 使用部署脚本启动
./scripts/deploy.sh

# 或使用 Docker Compose 启动
docker-compose up -d
```

### 4. 访问系统

- **前端地址**: http://localhost
- **后端API**: http://localhost:8080/api/v1
- **管理后台**: http://localhost/admin

## 生产环境部署

### 完整部署流程

1. **准备服务器环境**
   ```bash
   # 更新系统
   sudo apt update && sudo apt upgrade -y
   
   # 安装 Docker
   curl -fsSL https://get.docker.com -o get-docker.sh
   sudo sh get-docker.sh
   
   # 安装 Docker Compose
   sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose
   ```

2. **配置域名和SSL证书**
   ```bash
   # 购买域名并配置DNS解析到服务器IP
   
   # 使用 Let's Encrypt 获取免费SSL证书
   sudo apt install certbot -y
   sudo certbot certonly --standalone -d volunteer-system.com -d www.volunteer-system.com
   
   # 复制证书到项目目录
   sudo cp /etc/letsencrypt/live/volunteer-system.com/fullchain.pem nginx/ssl/cert.pem
   sudo cp /etc/letsencrypt/live/volunteer-system.com/privkey.pem nginx/ssl/key.pem
   ```

3. **配置环境变量**
   ```bash
   # 复制并编辑环境变量文件
   cp .env.example .env
   nano .env
   
   # 修改关键配置:
   # - APP_URL: 设置为实际域名
   # - DB_PASSWORD: 设置强密码
   # - JWT_SECRET: 设置JWT密钥
   # - WECHAT_APP_ID/SECRET: 设置微信小程序配置
   ```

4. **执行部署**
   ```bash
   # 运行部署脚本
   ./scripts/deploy.sh
   
   # 或手动执行
   docker-compose pull
   docker-compose up -d
   ```

5. **验证部署**
   ```bash
   # 检查服务状态
   docker-compose ps
   
   # 查看日志
   docker-compose logs -f
   
   # 测试服务
   curl http://localhost/health
   curl http://localhost:8080/actuator/health
   ```

### 性能优化

1. **数据库优化**
   ```bash
   # 调整 PostgreSQL 配置
   nano database/postgresql.conf
   
   # 优化内存和连接数
   shared_buffers = 256MB
   max_connections = 200
   work_mem = 4MB
   maintenance_work_mem = 64MB
   ```

2. **Redis优化**
   ```yaml
   # 在 docker-compose.yml 中添加
   command: redis-server --maxmemory 512mb --maxmemory-policy allkeys-lru
   ```

3. **Nginx优化**
   ```nginx
   # 在 nginx.conf 中添加
   worker_processes auto;
   worker_connections 1024;
   keepalive_timeout 65;
   ```

### 安全加固

1. **防火墙配置**
   ```bash
   sudo ufw allow 22/tcp
   sudo ufw allow 80/tcp
   sudo ufw allow 443/tcp
   sudo ufw enable
   ```

2. **数据库安全**
   ```bash
   # 只允许本地连接
   # 修改 docker-compose.yml 中数据库配置
   ports:
     - "127.0.0.1:5432:5432"
   ```

3. **定期更新**
   ```bash
   # 定期更新系统和Docker镜像
   sudo apt update && sudo apt upgrade -y
   docker-compose pull
   docker-compose up -d
   ```

## 开发环境配置

### 快速设置

```bash
# 运行开发环境设置脚本
./scripts/setup-dev.sh
```

### 手动配置

1. **启动数据库和Redis**
   ```bash
   docker-compose -f docker-compose.dev.yml up -d database redis
   ```

2. **配置后端**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

3. **配置前端**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

4. **访问开发环境**
   - 前端开发服务器: http://localhost:3000
   - 后端API: http://localhost:8080
   - 数据库: localhost:5432
   - Redis: localhost:6379

### 开发工具

- **代码编辑器**: VSCode 或 IntelliJ IDEA
- **API测试**: Postman 或 Insomnia
- **数据库管理**: pgAdmin 或 DBeaver
- **Redis管理**: RedisInsight

## 数据备份与恢复

### 自动备份

```bash
# 使用备份脚本
./scripts/backup.sh backup

# 设置定时任务 (crontab)
# 每天凌晨2点执行备份
0 2 * * * /path/to/volunteer-system/scripts/backup.sh backup
```

### 手动备份

```bash
# 备份数据库
docker exec volunteer-db pg_dump -U postgres volunteer_db > backup.sql

# 备份Redis
docker exec volunteer-redis redis-cli --rdb backup.rdb

# 备份配置文件
tar -czf configs_backup.tar.gz .env nginx/*.conf docker-compose*.yml
```

### 数据恢复

```bash
# 恢复数据库
./scripts/backup.sh restore-db backup.sql

# 恢复Redis
./scripts/backup.sh restore-redis backup.rdb
```

### 备份策略

- **完整备份**: 每天一次
- **增量备份**: 每4小时一次
- **保留周期**: 30天
- **异地备份**: 每周同步到异地服务器

## 监控与运维

### 健康检查

```bash
# 检查所有服务健康状态
curl http://localhost/health
curl http://localhost:8080/actuator/health

# 检查容器状态
docker-compose ps
docker-compose logs -f
```

### 日志管理

```bash
# 查看实时日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 查看特定时间段日志
docker-compose logs --since="2024-01-01T00:00:00" --until="2024-01-01T23:59:59"

# 导出日志
docker-compose logs > app.log
```

### 性能监控

1. **系统资源监控**
   ```bash
   # 使用 htop
   htop
   
   # 使用 iotop
   sudo iotop
   ```

2. **应用监控**
   ```bash
   # 查看JVM状态
   docker exec volunteer-backend jstat -gc <pid> 1s
   
   # 查看线程状态
   docker exec volunteer-backend jstack <pid>
   ```

3. **数据库监控**
   ```bash
   # 查看数据库连接
   docker exec volunteer-db psql -U postgres -d volunteer_db -c "SELECT count(*) FROM pg_stat_activity;"
   
   # 查看慢查询
   docker exec volunteer-db psql -U postgres -d volunteer_db -c "SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;"
   ```

### 故障排查

1. **服务无法启动**
   ```bash
   # 检查日志
   docker-compose logs <service_name>
   
   # 检查端口占用
   netstat -tulpn | grep <port>
   
   # 检查磁盘空间
   df -h
   ```

2. **数据库连接失败**
   ```bash
   # 检查数据库状态
   docker-compose ps database
   
   # 测试数据库连接
   docker exec volunteer-db psql -U postgres -d volunteer_db -c "SELECT 1;"
   
   # 检查网络连接
   docker network inspect volunteer-network
   ```

3. **性能问题**
   ```bash
   # 查看资源使用情况
   docker stats
   
   # 查看慢查询日志
   docker exec volunteer-db cat /var/log/postgresql/postgresql-*.log | grep "duration:"
   ```

## 常见问题

### Q1: Docker 容器启动失败

**原因**: 端口冲突或权限问题

**解决方案**:
```bash
# 检查端口占用
netstat -tulpn | grep <port>

# 修改 docker-compose.yml 中的端口映射
# 或者停止占用端口的程序

# 检查Docker权限
sudo usermod -aG docker $USER
newgrp docker
```

### Q2: 数据库连接超时

**原因**: 数据库未完全启动或网络配置问题

**解决方案**:
```bash
# 等待数据库完全启动
docker-compose logs -f database

# 检查数据库健康状态
docker exec volunteer-db pg_isready

# 检查网络连接
docker network inspect volunteer-network
```

### Q3: 前端无法访问后端API

**原因**: 跨域问题或API地址配置错误

**解决方案**:
```bash
# 检查 CORS 配置
# 在后端 application.yml 中配置 CORS

# 检查前端 API 地址
# 在前端 .env 文件中配置正确的 API 地址

# 检查 Nginx 代理配置
# 确保 nginx.conf 中代理配置正确
```

### Q4: SSL证书过期

**原因**: Let's Encrypt 证书有效期为90天

**解决方案**:
```bash
# 续期证书
sudo certbot renew

# 自动续期 (添加到 crontab)
0 3 * * * certbot renew --quiet --post-hook "docker-compose restart nginx"
```

### Q5: 内存不足

**原因**: 系统内存使用过高

**解决方案**:
```bash
# 限制容器内存使用
# 在 docker-compose.yml 中添加
deploy:
  resources:
    limits:
      memory: 1G
    reservations:
      memory: 512M

# 增加 Swap 空间
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

## 技术支持

- **文档**: [项目文档](https://github.com/yourusername/volunteer-system/wiki)
- **问题反馈**: [GitHub Issues](https://github.com/yourusername/volunteer-system/issues)
- **社区支持**: [Discord](https://discord.gg/volunteer-system)

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 完整的志愿管理系统功能
- 微信小程序支持
- Docker 容器化部署

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件