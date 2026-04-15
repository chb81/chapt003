#!/bin/bash

# 志愿汇系统备份脚本
# 用于数据备份和恢复

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
BACKUP_DIR="./database/backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="volunteer_backup_${TIMESTAMP}"
LOG_FILE="backup_${TIMESTAMP}.log"

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1" | tee -a "$BACKUP_DIR/$LOG_FILE"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1" | tee -a "$BACKUP_DIR/$LOG_FILE"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a "$BACKUP_DIR/$LOG_FILE"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$BACKUP_DIR/$LOG_FILE"
}

# 初始化备份目录
init_backup_dir() {
    log_info "初始化备份目录..."
    
    if [ ! -d "$BACKUP_DIR" ]; then
        mkdir -p "$BACKUP_DIR"
        log_success "备份目录创建成功: $BACKUP_DIR"
    fi
}

# 检查Docker运行状态
check_docker_status() {
    log_info "检查Docker运行状态..."
    
    if ! docker ps &> /dev/null; then
        log_error "Docker未运行，请先启动Docker"
        exit 1
    fi
    
    log_success "Docker运行正常"
}

# 数据库备份
backup_database() {
    log_info "开始备份数据库..."
    
    # 检查容器是否运行
    if ! docker ps | grep -q "volunteer-db"; then
        log_error "数据库容器未运行"
        exit 1
    fi
    
    # 创建备份目录
    DB_BACKUP_DIR="$BACKUP_DIR/database_${TIMESTAMP}"
    mkdir -p "$DB_BACKUP_DIR"
    
    # 执行数据库备份
    docker exec volunteer-db pg_dump -U postgres volunteer_db > "$DB_BACKUP_DIR/database.sql"
    
    if [ $? -eq 0 ]; then
        log_success "数据库备份成功: $DB_BACKUP_DIR/database.sql"
    else
        log_error "数据库备份失败"
        exit 1
    fi
}

# Redis备份
backup_redis() {
    log_info "开始备份Redis..."
    
    # 检查容器是否运行
    if ! docker ps | grep -q "volunteer-redis"; then
        log_warning "Redis容器未运行，跳过Redis备份"
        return
    fi
    
    # 创建备份目录
    REDIS_BACKUP_DIR="$BACKUP_DIR/redis_${TIMESTAMP}"
    mkdir -p "$REDIS_BACKUP_DIR"
    
    # 执行Redis备份
    docker exec volunteer-redis redis-cli --rdb "$REDIS_BACKUP_DIR/redis.rdb"
    
    if [ $? -eq 0 ]; then
        log_success "Redis备份成功: $REDIS_BACKUP_DIR/redis.rdb"
    else
        log_error "Redis备份失败"
        exit 1
    fi
}

# 配置文件备份
backup_configs() {
    log_info "开始备份配置文件..."
    
    CONFIG_BACKUP_DIR="$BACKUP_DIR/configs_${TIMESTAMP}"
    mkdir -p "$CONFIG_BACKUP_DIR"
    
    # 备份环境变量文件
    cp .env "$CONFIG_BACKUP_DIR/"
    cp .env.dev.example "$CONFIG_BACKUP_DIR/"
    cp .env.example "$CONFIG_BACKUP_DIR/"
    
    # 备置Docker配置
    cp docker-compose.yml "$CONFIG_BACKUP_DIR/"
    cp docker-compose.dev.yml "$CONFIG_BACKUP_DIR/"
    cp nginx/*.conf "$CONFIG_BACKUP_DIR/nginx/"
    
    # 备份前端配置
    if [ -d "frontend" ]; then
        cp frontend/package.json "$CONFIG_BACKUP_DIR/frontend_package.json"
        cp frontend/.env* "$CONFIG_BACKUP_DIR/frontend_configs/"
    fi
    
    log_success "配置文件备份完成: $CONFIG_BACKUP_DIR"
}

# 应用数据备份
backup_app_data() {
    log_info "开始备份应用数据..."
    
    APP_BACKUP_DIR="$BACKUP_DIR/app_data_${TIMESTAMP}"
    mkdir -p "$APP_BACKUP_DIR"
    
    # 备份日志文件
    if [ -d "logs" ]; then
        cp -r logs "$APP_BACKUP_DIR/"
    fi
    
    # 备份上传文件
    if [ -d "storage" ]; then
        cp -r storage "$APP_BACKUP_DIR/"
    fi
    
    # 备份构建产物
    if [ -d "frontend/dist" ]; then
        cp -r frontend/dist "$APP_BACKUP_DIR/frontend_dist/"
    fi
    
    log_success "应用数据备份完成: $APP_BACKUP_DIR"
}

# 压缩备份文件
compress_backup() {
    log_info "压缩备份文件..."
    
    cd "$BACKUP_DIR"
    
    # 压缩所有备份
    tar -czf "${BACKUP_FILE}.tar.gz" \
        "database_${TIMESTAMP}" \
        "redis_${TIMESTAMP}" \
        "configs_${TIMESTAMP}" \
        "app_data_${TIMESTAMP}" \
        "$LOG_FILE"
    
    if [ $? -eq 0 ]; then
        log_success "备份压缩成功: $BACKUP_DIR/${BACKUP_FILE}.tar.gz"
        
        # 清理临时文件
        rm -rf "database_${TIMESTAMP}" \
               "redis_${TIMESTAMP}" \
               "configs_${TIMESTAMP}" \
               "app_data_${TIMESTAMP}" \
               "$LOG_FILE"
    else
        log_error "备份压缩失败"
        exit 1
    fi
    
    cd - > /dev/null
}

# 备份信息统计
backup_statistics() {
    log_info "备份信息统计："
    echo "========================================"
    echo "备份时间: $(date)"
    echo "备份文件: $BACKUP_DIR/${BACKUP_FILE}.tar.gz"
    echo "备份大小: $(du -h "$BACKUP_DIR/${BACKUP_FILE}.tar.gz" | cut -f1)"
    echo "包含内容:"
    echo "  - 数据库备份"
    echo "  - Redis备份"
    echo "  - 配置文件备份"
    echo "  - 应用数据备份"
    echo "  - 备份日志"
    echo "========================================"
}

# 清理旧备份
cleanup_old_backups() {
    log_info "清理旧备份文件..."
    
    # 保留最近30天的备份
    find "$BACKUP_DIR" -name "volunteer_backup_*.tar.gz" -mtime +30 -delete
    
    log_success "旧备份清理完成"
}

# 完整备份流程
full_backup() {
    init_backup_dir
    check_docker_status
    backup_database
    backup_redis
    backup_configs
    backup_app_data
    compress_backup
    backup_statistics
    cleanup_old_backups
    
    log_success "完整备份完成！"
}

# 数据库恢复
restore_database() {
    if [ -z "$1" ]; then
        log_error "请指定备份文件路径"
        exit 1
    fi
    
    local backup_file="$1"
    
    if [ ! -f "$backup_file" ]; then
        log_error "备份文件不存在: $backup_file"
        exit 1
    fi
    
    log_info "开始恢复数据库..."
    
    # 停止数据库容器
    docker-compose stop database
    
    # 备份当前数据库
    log_info "备份当前数据库..."
    docker exec volunteer-db pg_dump -U postgres volunteer_db > "$BACKUP_DIR/current_db_before_restore.sql"
    
    # 恢复数据库
    docker exec -i volunteer-db psql -U postgres volunteer_db < "$backup_file"
    
    # 启动数据库容器
    docker-compose start database
    
    log_success "数据库恢复完成"
}

# Redis恢复
restore_redis() {
    if [ -z "$1" ]; then
        log_error "请指定Redis备份文件路径"
        exit 1
    fi
    
    local backup_file="$1"
    
    if [ ! -f "$backup_file" ]; then
        log_error "Redis备份文件不存在: $backup_file"
        exit 1
    fi
    
    log_info "开始恢复Redis..."
    
    # 停止Redis容器
    docker-compose stop redis
    
    # 恢复Redis
    docker exec volunteer-redis redis-cli --rdb "$backup_file"
    
    # 启动Redis容器
    docker-compose start redis
    
    log_success "Redis恢复完成"
}

# 主函数
main() {
    case "${1:-backup}" in
        "backup")
            full_backup
            ;;
        "restore-db")
            restore_database "$2"
            ;;
        "restore-redis")
            restore_redis "$2"
            ;;
        "list")
            ls -la "$BACKUP_DIR"
            ;;
        "clean")
            find "$BACKUP_DIR" -name "*.tar.gz" -mtime +90 -delete
            log_success "清理完成"
            ;;
        *)
            echo "用法: $0 [backup|restore-db|restore-redis|list|clean]"
            echo ""
            echo "命令说明:"
            echo "  backup           - 执行完整备份（默认）"
            echo "  restore-db <文件> - 恢复数据库"
            echo "  restore-redis <文件> - 恢复Redis"
            echo "  list             - 列出备份文件"
            echo "  clean            - 清理旧备份"
            exit 1
            ;;
    esac
}

main "$@"