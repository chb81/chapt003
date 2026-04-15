# 故障排除和维护指南

## 目录
1. [系统概述](#系统概述)
2. [常见故障排除](#常见故障排除)
3. [Vue3 前端故障排除](#vue3-前端故障排除)
4. [微信小程序故障排除](#微信小程序故障排除)
5. [后端服务故障排除](#后端服务故障排除)
6. [部署相关故障排除](#部署相关故障排除)
7. [性能问题排查](#性能问题排查)
8. [数据恢复和备份](#数据恢复和备份)
9. [定期维护计划](#定期维护计划)
10. [监控系统配置](#监控系统配置)
11. [安全维护](#安全维护)
12. [紧急响应流程](#紧急响应流程)

## 系统概述

本系统由以下主要组件构成：

- **Vue3 前端管理平台**：使用 Vue3 + Vite + TypeScript + ElementPlus 构建
- **微信小程序**：用于移动端访问和用户交互
- **Spring Boot 后端**：提供 RESTful API 和业务逻辑处理
- **PostgreSQL 数据库**：存储核心业务数据
- **Redis 缓存**：提供缓存服务
- **Docker 容器化**：生产环境部署

## 常见故障排除

### 1. 系统无法启动

**症状**：整个系统无法访问
**排查步骤**：
1. 检查服务器状态：`systemctl status docker`
2. 检查 Docker 容器状态：`docker-compose ps`
3. 查看容器日志：`docker-compose logs -f [服务名]`
4. 检查端口占用：`netstat -tlnp | grep [端口号]`
5. 检查防火墙状态：`systemctl status firewalld`

**解决方案**：
```bash
# 重启 Docker 服务
sudo systemctl restart docker

# 重启所有服务
cd /opt/chapt003
docker-compose down
docker-compose up -d

# 检查服务状态
docker-compose ps
```

### 2. 数据库连接失败

**症状**：应用无法连接到数据库
**排查步骤**：
1. 检查 PostgreSQL 服务状态：`systemctl status postgresql`
2. 检查数据库容器日志：`docker-compose logs postgres`
3. 验证数据库连接信息是否正确
4. 检查数据库磁盘空间：`df -h`

**解决方案**：
```bash
# 进入数据库容器
docker-compose exec postgres bash

# 连接到数据库
psql -U chapt003 -d chapt003

# 检查表结构
\dt

# 检查连接状态
\conninfo
```

## Vue3 前端故障排除

### 1. 开发环境启动失败

**症状**：`npm run dev` 命令执行失败
**排查步骤**：
1. 检查 Node.js 版本：`node -v`
2. 检查 npm 版本：`npm -v`
3. 检查依赖是否完整：`npm list`
4. 检查端口是否被占用：`netstat -tlnp | grep 5173`

**解决方案**：
```bash
# 重新安装依赖
rm -rf node_modules package-lock.json
npm install

# 清除缓存
npm cache clean --force

# 检查环境变量
echo $NODE_ENV

# 使用不同端口启动
npm run dev -- --port 5174
```

### 2. 构建失败

**症状**：`npm run build` 命令执行失败
**排查步骤**：
1. 检查 TypeScript 类型错误：`npm run typecheck`
2. 检查 ESLint 错误：`npm run lint`
3. 检查文件权限：`ls -la`
4. 检查磁盘空间：`df -h`

**解决方案**：
```bash
# 类型检查
npm run typecheck

# 代码检查
npm run lint

# 修复 ESLint 错误
npm run lint -- --fix

# 检查构建输出目录
ls -la dist/
```

### 3. API 接口调用失败

**症状**：前端无法连接后端 API
**排查步骤**：
1. 检查代理配置：`cat vite.config.ts`
2. 检查 API 服务器是否运行：`curl http://localhost:8080/api/health`
3. 检查网络连接：`ping [服务器IP]`
4. 检查 CORS 配置

**解决方案**：
```bash
# 检查 API 健康状态
curl http://localhost:8080/api/health

# 检查 CORS 配置
curl -H "Origin: http://localhost:5173" -v http://localhost:8080/api/health

# 测试具体接口
curl http://localhost:8080/api/users/1
```

### 4. 路由问题

**症状**：页面跳转失败或路由不正确
**排查步骤**：
1. 检查路由配置：`cat src/router/index.ts`
2. 检查路由守卫逻辑
3. 检查组件导入路径
4. 检查路由元数据配置

**解决方案**：
```bash
# 检查路由文件
cat src/router/index.ts

# 检查路由守卫
grep -n "beforeEach" src/router/index.ts

# 检查组件路径
ls -la src/views/
```

### 5. 状态管理问题

**症状**：状态更新不生效或数据不同步
**排查步骤**：
1. 检查 Pinia store 配置
2. 检查 action 和 mutation 调用
3. 检查组件中状态的使用
4. 检查 localStorage 操作

**解决方案**：
```bash
# 检查 store 文件
ls -la src/store/

# 检查 store 配置
cat src/store/user.ts

# 检查组件中状态使用
grep -n "useUser" src/components/*.vue
```

## 微信小程序故障排除

### 1. 小程序无法编译

**症状**：微信开发者工具编译失败
**排查步骤**：
1. 检查小程序配置文件：`app.json`
2. 检查页面配置：`pages/*/page.json`
3. 检查文件路径是否正确
4. 检查是否有语法错误

**解决方案**：
```bash
# 检查配置文件
cat app.json

# 检查页面配置
find pages/ -name "page.json" -exec echo "=== {} ===" \; -exec cat {} \;

# 检查 JS 文件语法
node -c pages/index/index.js

# 检查 WXML 语法
# 使用微信开发者工具内置检查器
```

### 2. API 请求失败

**症状**：小程序无法获取后端数据
**排查步骤**：
1. 检查域名配置：`project.config.json`
2. 检查 API 路径是否正确
3. 检查网络请求是否被拒绝
4. 检查响应数据格式

**解决方案**：
```bash
# 检查域名配置
cat project.config.json

# 检查 API 路径
grep -r "baseUrl" utils/

# 检查网络请求
grep -r "wx.request" pages/

# 模拟 API 调用
curl -H "Content-Type: application/json" -d '{"test": true}' http://localhost:8080/api/test
```

### 3. 页面加载问题

**症状**：页面无法加载或显示异常
**排查步骤**：
1. 检查页面生命周期
2. 检查数据绑定
3. 检查组件引用
4. 检查样式文件

**解决方案**：
```bash
# 检查页面生命周期
grep -n "onLoad\|onShow\|onReady" pages/*/index.js

# 检查数据绑定
grep -n "{{" pages/*/index.wxml

# 检查样式
grep -n "style" pages/*/index.wxss

# 检查组件引用
grep -n "import" pages/*/index.js
```

### 4. 缓存问题

**症状**：数据更新后页面显示旧数据
**排查步骤**：
1. 检查缓存配置
2. 检查缓存清除逻辑
3. 检查数据刷新机制
4. 检查版本更新

**解决方案**：
```bash
# 检查缓存工具
cat utils/cache.js

# 检查缓存清理
grep -n "clearCache" pages/*/index.js

# 检查版本控制
grep -n "version" app.js

# 手动清除缓存
# 在开发者工具中清除缓存
```

### 5. 发布审核问题

**症状**：小程序无法通过微信审核
**排查步骤**：
1. 检查隐私政策配置
2. 检查用户协议配置
3. 检查域名配置
4. 检查功能权限

**解决方案**：
```bash
# 检查隐私政策
cat pages/privacy/privacy.js

# 检查用户协议
cat pages/terms/terms.js

# 检查域名配置
grep -n "requestDomain" project.config.json

# 检查权限配置
grep -n "permission" app.json
```

## 后端服务故障排除

### 1. Spring Boot 启动失败

**症状**：`mvn spring-boot:run` 失败
**排查步骤**：
1. 检查 Java 版本：`java -version`
2. 检查 Maven 配置：`mvn -version`
3. 检查依赖冲突：`mvn dependency:tree`
4. 检查端口占用：`netstat -tlnp | grep 8080`

**解决方案**：
```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version

# 检查依赖树
mvn dependency:tree

# 检查端口占用
netstat -tlnp | grep 8080

# 重新编译
mvn clean compile
```

### 2. 数据库连接问题

**症状**：应用无法连接 PostgreSQL
**排查步骤**：
1. 检查数据库配置：`src/main/resources/application.yml`
2. 检查数据库服务状态
3. 检查用户权限
4. 检查连接池配置

**解决方案**：
```bash
# 检查配置文件
cat src/main/resources/application.yml

# 检查数据库连接
psql -U chapt003 -d chapt003 -h localhost -p 5432

# 测试连接池配置
# 在应用中添加日志记录

# 检查数据库权限
\du
```

### 3. API 接口异常

**症状**：REST API 返回错误
**排查步骤**：
1. 检查控制器日志
2. 检查业务逻辑
3. 检查数据验证
4. 检查异常处理

**解决方案**：
```bash
# 检查控制器日志
grep -r "Controller" src/main/java/com/chapt003/controller/

# 检查异常处理
grep -r "@ExceptionHandler" src/main/java/

# 检查数据验证
grep -r "@Valid" src/main/java/

# 测试 API 接口
curl -X GET http://localhost:8080/api/users/1
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"name":"test"}'
```

### 4. 缓存问题

**症状**：Redis 缓存异常
**排查步骤**：
1. 检查 Redis 服务状态
2. 检查缓存配置
3. 检查缓存键值
4. 检查缓存过期策略

**解决方案**：
```bash
# 检查 Redis 服务
redis-cli ping

# 检查缓存配置
grep -r "RedisConfig" src/main/java/

# 检查缓存键值
redis-cli KEYS "*"

# 检查缓存过期时间
redis-cli TTL key_name
```

## 部署相关故障排除

### 1. Docker 容器启动失败

**症状**：`docker-compose up` 失败
**排查步骤**：
1. 检查 Docker 服务状态
2. 检查端口冲突
3. 检查镜像是否存在
4. 检查配置文件语法

**解决方案**：
```bash
# 检查 Docker 服务
sudo systemctl status docker

# 检查端口占用
netstat -tlnp | grep 8080

# 检查镜像
docker images | grep chapt003

# 检查配置文件
docker-compose config

# 清理容器
docker-compose down -v --remove-orphans
```

### 2. Nginx 配置问题

**症状**：Nginx 反向代理失败
**排查步骤**：
1. 检查 Nginx 配置文件
2. 检查 SSL 证书
3. 检查端口映射
4. 检查访问日志

**解决方案**：
```bash
# 检查 Nginx 配置
sudo nginx -t

# 检查 Nginx 日志
sudo tail -f /var/log/nginx/error.log

# 检查 SSL 证书
sudo openssl x509 -in /etc/letsencrypt/live/your-domain/fullchain.pem -text -noout

# 测试配置
curl -I http://localhost:80
```

### 3. SSL 证书问题

**症状**：HTTPS 访问失败
**排查步骤**：
1. 检查证书过期时间
2. 检查证书链
3. 检查私钥权限
4. 检查证书配置

**解决方案**：
```bash
# 检查证书过期时间
sudo openssl x509 -in /etc/letsencrypt/live/your-domain/cert.pem -enddate -noout

# 检查证书链
sudo openssl x509 -in /etc/letsencrypt/live/your-domain/fullchain.pem -text -noout

# 检查私钥权限
sudo ls -la /etc/letsencrypt/live/your-domain/privkey.pem

# 续签证书
sudo certbot renew --dry-run
```

## 性能问题排查

### 1. 前端性能问题

**症状**：页面加载缓慢
**排查步骤**：
1. 检查资源大小
2. 检查网络请求
3. 检查渲染性能
4. 检查 JavaScript 执行

**解决方案**：
```bash
# 检查构建输出
ls -la dist/assets/

# 检查网络请求
# 使用 Chrome DevTools Network 面板

# 检查性能
# 使用 Chrome DevTools Performance 面板

# 优化构建
npm run build -- --mode production
```

### 2. 后端性能问题

**症状**：API 响应慢
**排查步骤**：
1. 检查数据库查询
2. 检查缓存使用
3. 检查线程池配置
4. 检查内存使用

**解决方案**：
```bash
# 检查数据库查询
# 启用 SQL 日志
logging.level.org.hibernate.SQL=DEBUG

# 检查缓存使用
redis-cli INFO memory

# 检查线程池
# 在应用中添加监控

# 检查内存使用
jmap -histo <PID>
```

### 3. 数据库性能问题

**症状**：数据库查询缓慢
**排查步骤**：
1. 检查索引使用
2. 检查查询优化
3. 检查数据库配置
4. 检查连接池配置

**解决方案**：
```bash
# 检查索引
psql -U chapt003 -d chapt003 -c "SELECT * FROM pg_indexes WHERE schemaname = 'public';"

# 检查查询计划
EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'test@example.com';

# 检查数据库配置
cat /etc/postgresql/14/main/postgresql.conf

# 优化配置
# 调整 shared_buffers, effective_cache_size 等
```

## 数据恢复和备份

### 1. 数据库备份

**自动备份脚本**：
```bash
#!/bin/bash
# backup-database.sh

BACKUP_DIR="/opt/chapt003/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="chapt003"
DB_USER="chapt003"
DB_HOST="localhost"
DB_PORT="5432"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME > $BACKUP_DIR/$DB_NAME_$DATE.sql

# 压缩备份
gzip $BACKUP_DIR/$DB_NAME_$DATE.sql

# 保留最近7天的备份
find $BACKUP_DIR -name "$DB_NAME_*.sql.gz" -mtime +7 -delete

echo "Database backup completed: $BACKUP_DIR/$DB_NAME_$DATE.sql.gz"
```

**手动备份**：
```bash
# 手动备份
pg_dump -h localhost -p 5432 -U chapt003 -d chapt003 > backup_$(date +%Y%m%d).sql

# 压缩备份
gzip backup_$(date +%Y%m%d).sql
```

### 2. 数据恢复

**恢复数据库**：
```bash
# 解压备份
gunzip backup_20240101.sql.gz

# 恢复数据库
psql -h localhost -p 5432 -U chapt003 -d chapt003 < backup_20240101.sql

# 或者使用 pg_restore
pg_restore -h localhost -p 5432 -U chapt003 -d chapt003 backup_20240101.dump
```

### 3. 文件系统备份

**备份脚本**：
```bash
#!/bin/bash
# backup-filesystem.sh

BACKUP_DIR="/opt/chapt003/backups"
DATE=$(date +%Y%m%d_%H%M%S)
APP_DIR="/opt/chapt003"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份应用文件
tar -czf $BACKUP_DIR/app_$DATE.tar.gz $APP_DIR

# 备份配置文件
tar -czf $BACKUP_DIR/config_$DATE.tar.gz /etc/nginx /etc/systemd/system

echo "File system backup completed"
```

## 定期维护计划

### 1. 每日维护

**检查清单**：
- [ ] 检查系统日志：`journalctl -u docker --since today`
- [ ] 检查容器状态：`docker-compose ps`
- [ ] 检查磁盘空间：`df -h`
- [ ] 检查内存使用：`free -h`
- [ ] 检查数据库连接：`psql -U chapt003 -c "SELECT count(*) FROM pg_stat_activity;"`

**维护脚本**：
```bash
#!/bin/bash
# daily-maintenance.sh

echo "=== Daily Maintenance Check ==="

# 检查系统资源
echo "Disk usage:"
df -h | grep -E "Filesystem|/dev/sda"

echo "Memory usage:"
free -h

echo "CPU usage:"
top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{print 100 - $1"%"}'

# 检查服务状态
echo "Service status:"
docker-compose ps

# 检查日志大小
echo "Log sizes:"
find /var/log -name "*.log" -exec ls -lh {} \; | head -10

echo "=== Daily Maintenance Completed ==="
```

### 2. 每周维护

**检查清单**：
- [ ] 清理旧的 Docker 镜像：`docker system prune -f`
- [ ] 清理旧的备份文件：`find /opt/chapt003/backups -name "*.gz" -mtime +7 -delete`
- [ ] 检查数据库性能：`psql -U chapt003 -d chapt003 -c "SELECT * FROM pg_stat_database;"`
- [ ] 检查 SSL 证书有效期
- [ ] 更新系统安全补丁

**维护脚本**：
```bash
#!/bin/bash
# weekly-maintenance.sh

echo "=== Weekly Maintenance Check ==="

# 清理 Docker 资源
echo "Cleaning Docker resources..."
docker system prune -f
docker volume prune -f

# 清理旧备份
echo "Cleaning old backups..."
find /opt/chapt003/backups -name "*.gz" -mtime +7 -delete

# 数据库维护
echo "Database maintenance..."
psql -U chapt003 -d chapt003 -c "VACUUM ANALYZE;"

# 检查 SSL 证书
echo "Checking SSL certificates..."
openssl x509 -in /etc/letsencrypt/live/your-domain/cert.pem -enddate -noout

# 系统更新
echo "System updates..."
sudo apt update && sudo apt upgrade -y

echo "=== Weekly Maintenance Completed ==="
```

### 3. 每月维护

**检查清单**：
- [ ] 执行完整数据库备份
- [ ] 检查数据库索引使用情况
- [ ] 检查应用性能指标
- [ ] 更新依赖包
- [ ] 检查安全配置
- [ ] 生成月度报告

**维护脚本**：
```bash
#!/bin/bash
# monthly-maintenance.sh

echo "=== Monthly Maintenance Check ==="

# 完整数据库备份
echo "Performing full database backup..."
/opt/chapt003/scripts/backup-database.sh

# 数据库索引分析
echo "Analyzing database indexes..."
psql -U chapt003 -d chapt003 -c "
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
"

# 应用性能报告
echo "Generating performance report..."
# 这里可以集成性能监控工具生成报告

# 更新依赖
echo "Updating dependencies..."
cd /opt/chapt003/frontend
npm update
cd /opt/chapt003/backend
mvn dependency:updates

# 安全检查
echo "Security check..."
# 执行安全扫描脚本

echo "=== Monthly Maintenance Completed ==="
```

## 监控系统配置

### 1. 日志监控

**日志配置**：
```bash
# Docker 日志配置
echo "{
  \"log-driver\": \"json-file\",
  \"log-opts\": {
    \"max-size\": \"10m\",
    \"max-file\": \"3\"
  }
}" | sudo tee /etc/docker/daemon.json

sudo systemctl restart docker
```

**日志轮转配置**：
```bash
# /etc/logrotate.d/chapt003
/opt/chapt003/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 chapt003 chapt003
}
```

### 2. 性能监控

**Prometheus 配置**：
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'

  - job_name: 'node-exporter'
    static_configs:
      - targets: ['localhost:9100']
```

**Grafana 仪表板**：
- 创建系统资源监控仪表板
- 创建应用性能监控仪表板
- 创建数据库性能监控仪表板
- 创建业务指标监控仪表板

### 3. 告警配置

**Alertmanager 配置**：
```yaml
# alertmanager.yml
global:
  smtp_smarthost: 'localhost:587'
  smtp_from: 'alerts@your-domain.com'
  smtp_auth_username: 'alerts@your-domain.com'
  smtp_auth_password: 'password'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'web.hook'

receivers:
- name: 'web.hook'
  email_configs:
  - to: 'admin@your-domain.com'
    subject: 'Alert: {{ .GroupLabels.alertname }}'
```

## 安全维护

### 1. 定期安全扫描

**安全扫描脚本**：
```bash
#!/bin/bash
# security-scan.sh

echo "=== Security Scan ==="

# 检查系统漏洞
sudo apt update
sudo apt list --upgradable

# 检查 Docker 安全
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v /etc:/host-etc -v $(pwd):/tmp aquasec/trivy image chapt003:latest

# 检查应用依赖漏洞
cd /opt/chapt003/frontend
npm audit
cd /opt/chapt003/backend
mvn dependency-check:check

# 检查文件权限
find /opt/chapt003 -type f -exec ls -la {} \; | grep -E "rwxrwxrwx"

echo "=== Security Scan Completed ==="
```

### 2. 证书管理

**证书更新脚本**：
```bash
#!/bin/bash
# renew-certificates.sh

echo "=== Renewing Certificates ==="

# 更新 Let's Encrypt 证书
sudo certbot renew --dry-run

# 重启 Nginx
sudo systemctl reload nginx

# 检查证书状态
sudo openssl x509 -in /etc/letsencrypt/live/your-domain/cert.pem -text -noout | grep "Not After"

echo "=== Certificates Renewed ==="
```

### 3. 访问控制

**定期审计用户权限**：
```bash
# 检查数据库用户
psql -U chapt003 -d chapt003 -c "SELECT usename, usecreatedb, userepl, usesuper FROM pg_user;"

# 检查系统用户
cut -d: -f1 /etc/passwd | grep -vE "nologin|false"

# 检查 sudo 权限
sudo -l -U [username]
```

## 紧急响应流程

### 1. 服务宕机处理

**响应步骤**：
1. **确认故障**：检查监控系统和用户反馈
2. **影响评估**：确定故障范围和影响程度
3. **应急处理**：
   ```bash
   # 检查服务状态
   docker-compose ps
   
   # 重启服务
   docker-compose restart
   
   # 如果重启失败，重建容器
   docker-compose up -d --force-recreate
   ```
4. **故障诊断**：使用上述故障排除方法
5. **系统恢复**：执行数据恢复和备份恢复
6. **事后总结**：记录故障原因和解决方案

### 2. 数据丢失处理

**响应步骤**：
1. **立即停止写入**：避免数据进一步损坏
2. **备份当前状态**：在恢复前保存当前数据
3. **恢复最新备份**：
   ```bash
   # 停止服务
   docker-compose down
   
   # 恢复数据库
   psql -U chapt003 -d chapt003 < backup_20240101.sql
   
   # 重启服务
   docker-compose up -d
   ```
4. **数据验证**：确认恢复的数据完整性
5. **业务恢复**：逐步恢复业务功能
6. **事后分析**：分析数据丢失原因，预防措施

### 3. 安全事件处理

**响应步骤**：
1. **事件确认**：通过监控系统或用户报告确认安全事件
2. **紧急响应**：
   ```bash
   # 隔离受影响系统
   iptables -A INPUT -s [攻击者IP] -j DROP
   
   # 备份证据
   tar -czf security_evidence_$(date +%Y%m%d).tar.gz /var/log/nginx /var/log/systemd
   
   # 重置密码
   passwd username
   ```
3. **损害评估**：确定攻击范围和数据泄露情况
4. **系统修复**：修补安全漏洞，加强防护
5. **业务恢复**：逐步恢复受影响的服务
6. **事件报告**：向相关方报告事件详情

### 4. 联系信息

**紧急联系人**：
- 系统管理员：admin@your-domain.com
- 开发团队：dev@your-domain.com
- 运维团队：ops@your-domain.com
- 安全团队：security@your-domain.com

**监控报警**：
- 邮件报警：alerts@your-domain.com
- 短信报警：13800138000
- 即时通讯：企业微信/钉钉群

## 总结

本故障排除和维护指南涵盖了系统运行中可能遇到的各种问题，包括前端、后端、数据库、部署等各个层面的故障排查和维护操作。建议：

1. **定期演练**：定期进行故障排除演练，确保团队熟悉处理流程
2. **更新文档**：随着系统更新，及时更新本维护指南
3. **监控维护**：建立完善的监控体系，提前发现潜在问题
4. **备份验证**：定期测试备份恢复流程，确保数据安全
5. **团队培训**：定期对运维团队进行培训，提高故障处理能力

通过遵循本指南，可以有效提高系统的稳定性和可维护性，确保业务的连续运行。