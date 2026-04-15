# 生产环境部署指南

## 目录

1. [部署概述](#部署概述)
2. [环境准备](#环境准备)
3. [系统配置](#系统配置)
4. [部署流程](#部署流程)
5. [服务启动](#服务启动)
6. [SSL配置](#ssl配置)
7. [监控配置](#监控配置)
8. [安全加固](#安全加固)
9. [备份策略](#备份策略)
10. [性能优化](#性能优化)
11. [故障处理](#故障处理)
12. [维护计划](#维护计划)

## 部署概述

志愿汇系统采用容器化部署方式，使用 Docker 和 Docker Compose 进行服务编排。系统包含以下核心组件：

- **前端服务**: Vue3 + Vite + TypeScript + Element Plus
- **后端服务**: Spring Boot + PostgreSQL + Redis
- **反向代理**: Nginx
- **数据库**: PostgreSQL
- **缓存**: Redis
- **监控**: Prometheus + Grafana

### 部署架构

```
┌─────────────────────────────────────────────────────────────┐
│                     生产环境架构                           │
├─────────────────────────────────────────────────────────────┤
│                                                           │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐  │
│  │   Load      │      │   Nginx     │      │   SSL       │  │
│  │   Balancer  │─────│   Proxy     │─────│   Termination│  │
│  └─────────────┘      └─────────────┘      └─────────────┘  │
│           │                   │                   │         │
│           │                   │                   │         │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐  │
│  │   Frontend │      │   Backend   │      │   Database  │  │
│  │   Service   │      │   Service   │      │   Service   │  │
│  └─────────────┘      └─────────────┘      └─────────────┘  │
│           │                   │                   │         │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐  │
│  │   Cache    │      │   Monitor   │      │   Backup    │  │
│  │   Service   │      │   Service   │      │   Service   │  │
│  └─────────────┘      └─────────────┘      └─────────────┘  │
│                                                           │
└─────────────────────────────────────────────────────────────┘
```

## 环境准备

### 服务器要求

| 组件 | 最低配置 | 推荐配置 | 说明 |
|------|----------|----------|------|
| CPU | 2核心 | 4核心或以上 | 处理并发请求 |
| 内存 | 4GB | 8GB或以上 | 运行多个服务 |
| 硬盘 | 20GB | 100GB或以上 | 存储数据和日志 |
| 网络 | 100Mbps | 1Gbps | 支持高并发访问 |

### 操作系统要求

- **推荐系统**: Ubuntu 20.04 LTS 或 CentOS 8
- **内核版本**: 4.15 或更高
- **文件系统**: ext4 或 XFS
- **网络配置**: 支持IPv4和IPv6

### 软件依赖

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装基础工具
sudo apt install -y \
    curl \
    wget \
    git \
    unzip \
    nano \
    htop \
    net-tools \
    tree

# 安装Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 添加用户到docker组
sudo usermod -aG docker $USER

# 重新登录或使用newgrp docker
newgrp docker
```

### 网络配置

#### 防火墙设置

```bash
# 安装ufw
sudo apt install ufw -y

# 配置防火墙规则
sudo ufw allow ssh
sudo ufw allow http
sudo ufw allow https
sudo ufw allow 8080/tcp  # 后端API
sudo ufw allow 5432/tcp  # PostgreSQL
sudo ufw allow 6379/tcp  # Redis

# 启用防火墙
sudo ufw enable
```

#### 端口映射

| 服务 | 端口 | 协议 | 说明 |
|------|------|------|------|
| HTTP | 80 | TCP | Web服务 |
| HTTPS | 443 | TCP | 加密Web服务 |
| API | 8080 | TCP | 后端API服务 |
| Database | 5432 | TCP | PostgreSQL数据库 |
| Redis | 6379 | TCP | Redis缓存服务 |
| Monitoring | 9090 | TCP | Prometheus监控 |
| Grafana | 3000 | TCP | Grafana监控面板 |

## 系统配置

### 创建用户和目录

```bash
# 创建应用用户
sudo useradd -m -s /bin/bash volunteer
sudo passwd volunteer

# 创建必要目录
sudo mkdir -p /opt/volunteer-system
sudo mkdir -p /opt/volunteer-system/logs
sudo mkdir -p /opt/volunteer-system/backups
sudo mkdir -p /opt/volunteer-system/ssl
sudo mkdir -p /opt/volunteer-system/nginx

# 设置权限
sudo chown -R volunteer:volunteer /opt/volunteer-system
```

### 配置环境变量

创建 `.env` 文件：

```bash
cd /opt/volunteer-system
cat > .env << 'EOF'
# 数据库配置
DB_HOST=localhost
DB_PORT=5432
DB_DATABASE=volunteer_db
DB_USERNAME=volunteer_user
DB_PASSWORD=your_secure_password

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# JWT配置
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=86400

# 应用配置
APP_NAME=VolunteerSystem
APP_VERSION=1.0.0
APP_ENV=production

# 前端配置
FRONTEND_PORT=80
FRONTEND_HOST=localhost

# 后端配置
BACKEND_PORT=8080
BACKEND_HOST=localhost

# 监控配置
MONITORING_ENABLED=true
GRAFANA_PORT=3000
PROMETHEUS_PORT=9090

# 邮件配置
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_password
MAIL_FROM=volunteer@yourdomain.com

# 文件上传配置
UPLOAD_PATH=/opt/volunteer-system/uploads
MAX_FILE_SIZE=10485760

# 日志配置
LOG_LEVEL=INFO
LOG_PATH=/opt/volunteer-system/logs
LOG_MAX_SIZE=50MB
LOG_MAX_FILES=10
EOF
```

### 配置文件权限

```bash
# 设置文件权限
chmod 600 .env
chmod 755 /opt/volunteer-system
chmod 755 /optvolunteer-system/logs
chmod 755 /opt/volunteer-system/backups
chmod 755 /opt/volunteer-system/ssl
chmod 755 /opt/volunteer-system/nginx
```

## 部署流程

### 1. 代码部署

```bash
# 切换到应用用户
sudo -u volunteer bash

# 克隆代码
cd /opt/volunteer-system
git clone https://github.com/yourusername/volunteer-system.git .

# 切换到项目目录
cd /opt/volunteer-system

# 安装前端依赖
cd frontend
npm install
npm run build
cd ..

# 配置数据库
cd database
psql -U postgres -c "CREATE DATABASE volunteer_db;"
psql -U postgres -c "CREATE USER volunteer_user WITH PASSWORD 'your_secure_password';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE volunteer_db TO volunteer_user;"
cd ..

# 运行数据库迁移
cd backend
mvn flyway:migrate
cd ..

# 退出应用用户
exit
```

### 2. Docker配置

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  # 后端服务
  backend:
    build: ./backend
    container_name: volunteer-backend
    restart: unless-stopped
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_DATABASE=volunteer_db
      - DB_USERNAME=volunteer_user
      - DB_PASSWORD=your_secure_password
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - JWT_SECRET=your_jwt_secret_key_here
      - LOG_LEVEL=INFO
    ports:
      - "8080:8080"
    volumes:
      - ./logs:/app/logs
      - ./uploads:/app/uploads
    depends_on:
      - postgres
      - redis
    networks:
      - volunteer-network

  # 前端服务
  frontend:
    build: ./frontend
    container_name: volunteer-frontend
    restart: unless-stopped
    ports:
      - "80:80"
    volumes:
      - ./logs:/app/logs
    depends_on:
      - backend
    networks:
      - volunteer-network

  # 反向代理
  nginx:
    build: ./nginx
    container_name: volunteer-nginx
    restart: unless-stopped
    ports:
      - "443:443"
      - "80:80"
    volumes:
      - ./ssl:/etc/nginx/ssl
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - frontend
      - backend
    networks:
      - volunteer-network

  # 数据库
  postgres:
    image: postgres:13
    container_name: volunteer-postgres
    restart: unless-stopped
    environment:
      - POSTGRES_DB=volunteer_db
      - POSTGRES_USER=volunteer_user
      - POSTGRES_PASSWORD=your_secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./database/backups:/backups
    ports:
      - "5432:5432"
    networks:
      - volunteer-network

  # Redis
  redis:
    image: redis:6-alpine
    container_name: volunteer-redis
    restart: unless-stopped
    command: redis-server --requirepass your_redis_password
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"
    networks:
      - volunteer-network

  # 监控服务
  prometheus:
    image: prom/prometheus:latest
    container_name: volunteer-prometheus
    restart: unless-stopped
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
    networks:
      - volunteer-network

  grafana:
    image: grafana/grafana:latest
    container_name: volunteer-grafana
    restart: unless-stopped
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin_password_here
    volumes:
      - grafana_data:/var/lib/grafana
    networks:
      - volunteer-network

volumes:
  postgres_data:
  redis_data:
  prometheus_data:
  grafana_data:

networks:
  volunteer-network:
    driver: bridge
```

### 3. 配置文件

创建 `nginx/nginx.conf`：

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

events {
    worker_connections 1024;
    multi_accept on;
    use epoll;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # 日志格式
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                   '$status $body_bytes_sent "$http_referer" '
                   '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    # 基础配置
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/json
        application/javascript
        application/xml+rss
        application/atom+xml
        image/svg+xml;

    # 上游服务器
    upstream frontend {
        server frontend:80;
    }

    upstream backend {
        server backend:8080;
    }

    # HTTP服务器
    server {
        listen 80;
        server_name yourdomain.com www.yourdomain.com;

        # 重定向到HTTPS
        return 301 https://$server_name$request_uri;
    }

    # HTTPS服务器
    server {
        listen 443 ssl http2;
        server_name yourdomain.com www.yourdomain.com;

        # SSL配置
        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers on;

        # 安全头
        add_header X-Frame-Options DENY;
        add_header X-Content-Type-Options nosniff;
        add_header X-XSS-Protection "1; mode=block";
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

        # 前端代理
        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # 后端API代理
        location /api/ {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # API特定配置
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }

        # 静态文件缓存
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            expires 1y;
            add_header Cache-Control "public, immutable";
        }

        # 健康检查
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

## 服务启动

### 1. 启动服务

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

### 2. 验证服务

```bash
# 检查前端服务
curl -I http://localhost

# 检查后端API
curl -I http://localhost:8080/api/v1/health

# 检查数据库连接
docker exec volunteer-postgres psql -U volunteer_user -d volunteer_db -c "SELECT 1;"

# 检查Redis连接
docker exec volunteer-redis redis-cli ping
```

### 3. 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除卷
docker-compose down -v

# 停止并删除网络
docker-compose down --remove-orphans
```

## SSL配置

### 1. 获取SSL证书

#### 使用Let's Encrypt

```bash
# 安装Certbot
sudo apt install certbot python3-certbot-nginx -y

# 获取SSL证书
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# 自动续期设置
sudo crontab -e

# 添加以下内容
0 12 * * * /usr/bin/certbot renew --quiet
```

#### 手动配置SSL

```bash
# 生成SSL证书
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /opt/volunteer-system/ssl/key.pem \
    -out /opt/volunteer-system/ssl/cert.pem

# 设置SSL证书权限
chmod 600 /opt/volunteer-system/ssl/*.pem
```

### 2. 配置SSL

更新 `nginx/nginx.conf` 中的SSL配置：

```nginx
# SSL配置
ssl_certificate /etc/nginx/ssl/cert.pem;
ssl_certificate_key /etc/nginx/ssl/key.pem;
ssl_protocols TLSv1.2 TLSv1.3;
ssl_ciphers HIGH:!aNULL:!MD5;
ssl_prefer_server_ciphers on;
```

## 监控配置

### 1. Prometheus配置

创建 `monitoring/prometheus.yml`：

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "rules/*.yml"

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'backend'
    static_configs:
      - targets: ['backend:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s
    scrape_timeout: 5s

  - job_name: 'nginx'
    static_configs:
      - targets: ['nginx:80']
    metrics_path: '/metrics'
    scrape_interval: 30s

  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres:5432']
    metrics_path: '/metrics'
    scrape_interval: 30s

  - job_name: 'redis'
    static_configs:
      - targets: ['redis:6379']
    metrics_path: '/metrics'
    scrape_interval: 30s
```

### 2. Grafana配置

#### 创建数据源

```bash
# 进入Grafana容器
docker exec volunteer-grafana /bin/bash

# 配置Prometheus数据源
curl -X POST -H "Content-Type: application/json" -d '{
  "name": "Prometheus",
  "type": "prometheus",
  "url": "http://prometheus:9090",
  "access": "proxy",
  "basicAuth": false,
  "isDefault": true,
  "version": "1",
  "jsonData": {
    "httpMethod": "POST",
    "queryTimeout": "60s",
    "timeInterval": "15s"
  }
}' http://admin:admin_password_here@localhost:3000/api/datasources
```

#### 导入仪表板

```bash
# 导入Nginx仪表板
curl -X POST -H "Content-Type: application/json" -d '{
  "dashboard": {
    "id": null,
    "title": "Nginx Monitoring",
    "tags": ["nginx"],
    "panels": []
  }
}' http://admin:admin_password_here@localhost:3000/api/dashboards/import

# 导入PostgreSQL仪表板
curl -X POST -H "Content-Type: application/json" -d '{
  "dashboard": {
    "id": null,
    "title": "PostgreSQL Monitoring",
    "tags": ["postgres"],
    "panels": []
  }
}' http://admin:admin_password_here@localhost:3000/api/dashboards/import

# 导入Redis仪表板
curl -X POST -H "Content-Type: application/json" -d '{
  "dashboard": {
    "id": null,
    "title": "Redis Monitoring",
    "tags": ["redis"],
    "panels": []
  }
}' http://admin:admin_password_here@localhost:3000/api/dashboards/import
```

## 安全加固

### 1. 系统安全

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装安全工具
sudo apt install -y fail2ban rkhunter clamav

# 配置防火墙
sudo ufw enable
sudo ufw default deny incoming
sudo ufw default allow outgoing

# 配置Fail2ban
sudo nano /etc/fail2ban/jail.local
```

### 2. 应用安全

#### 环境变量安全

```bash
# 设置环境变量文件权限
chmod 600 .env

# 加密敏感数据
echo "your_sensitive_data" | openssl enc -aes-256-cbc -salt -pbkdf2 -pass pass:your_password > .env.enc
```

#### 数据库安全

```bash
# 创建只读用户
psql -U postgres -c "CREATE ROLE readonly_user WITH LOGIN PASSWORD 'readonly_password';"
psql -U postgres -c "GRANT CONNECT ON DATABASE volunteer_db TO readonly_user;"
psql -U postgres -c "GRANT USAGE ON SCHEMA public TO readonly_user;"
psql -U postgres -c "GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;"

# 限制数据库访问
iptables -A INPUT -p tcp --dport 5432 -s 127.0.0.1 -j ACCEPT
iptables -A INPUT -p tcp --dport 5432 -j DROP
```

### 3. 网络安全

```bash
# 配置SSH安全
sudo nano /etc/ssh/sshd_config

# 修改以下配置
Port 2222
PermitRootLogin no
PasswordAuthentication no
PubkeyAuthentication yes
```

## 备份策略

### 1. 数据库备份

```bash
# 创建备份脚本
cat > backup-database.sh << 'EOF'
#!/bin/bash

# 数据库备份脚本
BACKUP_DIR="/opt/volunteer-system/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="volunteer_db_$DATE.sql"

# 创建备份
docker exec volunteer-postgres pg_dump -U volunteer_user volunteer_db > $BACKUP_DIR/$BACKUP_FILE

# 压缩备份
gzip $BACKUP_DIR/$BACKUP_FILE

# 删除7天前的备份
find $BACKUP_DIR -name "*.gz" -mtime +7 -delete

echo "数据库备份完成: $BACKUP_FILE.gz"
EOF

# 设置执行权限
chmod +x backup-database.sh
```

### 2. 文件备份

```bash
# 创建文件备份脚本
cat > backup-files.sh << 'EOF'
#!/bin/bash

# 文件备份脚本
BACKUP_DIR="/opt/volunteer-system/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="files_$DATE.tar.gz"

# 备份配置文件和上传文件
tar -czf $BACKUP_DIR/$BACKUP_FILE \
    --exclude=$BACKUP_DIR \
    --exclude=*.log \
    --exclude=*.tmp \
    /opt/volunteer-system/

echo "文件备份完成: $BACKUP_FILE"
EOF

# 设置执行权限
chmod +x backup-files.sh
```

### 3. 自动备份设置

```bash
# 设置定时任务
crontab -e

# 添加以下内容
# 每天凌晨2点备份数据库
0 2 * * * /opt/volunteer-system/backup-database.sh

# 每周日凌晨3点备份文件
0 3 * * 0 /opt/volunteer-system/backup-files.sh

# 每月1号清理旧备份
0 4 1 * * find /opt/volunteer-system/backups -name "*.gz" -mtime +30 -delete
```

## 性能优化

### 1. 数据库优化

```sql
-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_applications_status ON applications(status);

-- 优化查询
EXPLAIN ANALYZE SELECT * FROM users WHERE username = 'test';
```

### 2. 缓存优化

```javascript
// Redis缓存配置
const redisConfig = {
  host: process.env.REDIS_HOST || 'localhost',
  port: process.env.REDIS_PORT || 6379,
  password: process.env.REDIS_PASSWORD,
  db: 0,
  // 连接池配置
  connectionPool: {
    min: 2,
    max: 10,
    idleTimeoutMillis: 30000,
  }
};
```

### 3. 前端优化

```javascript
// Webpack优化配置
module.exports = {
  optimization: {
    splitChunks: {
      chunks: 'all',
      minSize: 20000,
      maxSize: 244000,
      minChunks: 1,
      maxAsyncRequests: 30,
      maxInitialRequests: 30,
      automaticNameDelimiter: '~',
      enforceSizeThreshold: 50000,
      cacheGroups: {
        vendors: {
          test: /[\\/]node_modules[\\/]/,
          priority: -10
        },
        default: {
          minChunks: 2,
          priority: -20,
          reuseExistingChunk: true
        }
      }
    }
  }
};
```

## 故障处理

### 1. 常见问题

#### 服务无法启动

```bash
# 检查服务状态
docker-compose ps

# 查看错误日志
docker-compose logs [service_name]

# 检查端口占用
netstat -tulpn | grep :8080

# 重启服务
docker-compose restart [service_name]
```

#### 数据库连接失败

```bash
# 检查数据库容器状态
docker-compose ps postgres

# 查看数据库日志
docker-compose logs postgres

# 测试数据库连接
docker exec volunteer-postgres psql -U volunteer_user -d volunteer_db -c "SELECT 1;"
```

#### 内存不足

```bash
# 检查内存使用
free -h

# 查看容器内存使用
docker stats

# 调整容器内存限制
docker-compose up -d --force-recreate --memory="2g" [service_name]
```

### 2. 应急处理

#### 服务降级

```bash
# 停止非核心服务
docker-compose stop monitoring grafana

# 限制服务资源
docker-compose up -d --memory="512m" --cpus="0.5" frontend backend
```

#### 数据恢复

```bash
# 恢复数据库
docker exec -i volunteer-postgres psql -U volunteer_user volunteer_db < backup.sql

# 恢复文件
tar -xzf files_backup.tar.gz -C /opt/volunteer-system/
```

### 3. 日志管理

```bash
# 设置日志轮转
cat > /etc/logrotate.d/volunteer-system << 'EOF'
/opt/volunteer-system/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 volunteer volunteer
    postrotate
        docker-compose restart backend
    endscript
}
EOF
```

## 维护计划

### 1. 定期维护

#### 每日维护

```bash
# 检查服务状态
docker-compose ps

# 检查磁盘空间
df -h

# 检查日志文件大小
du -sh /opt/volunteer-system/logs/
```

#### 每周维护

```bash
# 清理旧日志
find /opt/volunteer-system/logs -name "*.log" -mtime +7 -delete

# 更新安全补丁
sudo apt update && sudo apt upgrade -y

# 备份数据
./backup-database.sh
./backup-files.sh
```

#### 每月维护

```bash
# 性能分析
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}"

# 安全审计
sudo rkhunter --checkall
sudo fail2ban-client status

# 系统健康检查
docker-compose exec backend curl http://localhost:8080/actuator/health
```

### 2. 版本升级

#### 准备工作

```bash
# 备份当前系统
./backup-database.sh
./backup-files.sh

# 停止服务
docker-compose down

# 备份Docker镜像
docker save volunteer-backend > backend-backup.tar
docker save volunteer-frontend > frontend-backup.tar
```

#### 升级步骤

```bash
# 更新代码
git pull origin main

# 重新构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 验证服务
curl http://localhost/health
curl http://localhost:8080/actuator/health
```

### 3. 监控告警

#### 告警配置

创建 `monitoring/alert-rules.yml`：

```yaml
groups:
  - name: volunteer-system
    rules:
      - alert: HighCPUUsage
        expr: 100 - (avg by(instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High CPU usage on {{ $labels.instance }}"
          description: "CPU usage is {{ $value }}% for more than 5 minutes"

      - alert: HighMemoryUsage
        expr: (node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes * 100 > 85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage on {{ $labels.instance }}"
          description: "Memory usage is {{ $value }}% for more than 5 minutes"

      - alert: ServiceDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Service {{ $labels.job }} is down"
          description: "Service {{ $labels.job }} has been down for more than 1 minute"
```

#### 告警通知

配置邮件通知：

```yaml
global:
  smtp_smarthost: 'localhost:587'
  smtp_from: 'alerts@yourdomain.com'
  smtp_auth_username: 'alerts@yourdomain.com'
  smtp_auth_password: 'your_smtp_password'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'web.hook'

receivers:
  - name: 'web.hook'
    email_configs:
      - to: 'admin@yourdomain.com'
        subject: 'Alert: {{ .GroupLabels.alertname }}'
        body: |
          {{ range .Alerts }}
          Alert: {{ .Annotations.summary }}
          Description: {{ .Annotations.description }}
          {{ end }}
```

---

*本文档最后更新时间: 2024-01-15*
*版本: 1.0.0*
*适用版本: 志愿汇系统 v1.0.0*