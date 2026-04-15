#!/bin/bash

# 志愿汇系统部署脚本
# 用于生产环境自动化部署

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Docker和Docker Compose
check_requirements() {
    log_info "检查部署环境..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装，请先安装Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose未安装，请先安装Docker Compose"
        exit 1
    fi
    
    log_success "Docker环境检查通过"
}

# 检查环境变量
check_env() {
    log_info "检查环境变量..."
    
    if [ ! -f .env ]; then
        log_warning ".env文件不存在，正在复制示例文件..."
        cp .env.example .env
        log_warning "请编辑.env文件并填入正确的配置值"
        exit 1
    fi
    
    # 检查关键环境变量
    source .env
    
    if [ -z "$DB_DATABASE" ] || [ -z "$DB_USERNAME" ] || [ -z "$DB_PASSWORD" ]; then
        log_error "数据库配置不完整，请检查.env文件"
        exit 1
    fi
    
    if [ -z "$JWT_SECRET" ]; then
        log_error "JWT密钥未设置，请检查.env文件"
        exit 1
    fi
    
    log_success "环境变量检查通过"
}

# 创建必要的目录
create_directories() {
    log_info "创建必要的目录..."
    
    mkdir -p logs
    mkdir -p storage
    mkdir -p nginx/ssl
    mkdir -p database/backups
    
    log_success "目录创建完成"
}

# 构建Docker镜像
build_images() {
    log_info "构建Docker镜像..."
    
    # 构建后端镜像
    docker-compose build backend
    
    # 构建前端镜像
    docker-compose build frontend
    
    # 构建Nginx镜像
    docker-compose build nginx
    
    log_success "Docker镜像构建完成"
}

# 启动服务
start_services() {
    log_info "启动服务..."
    
    # 拉取镜像
    docker-compose pull
    
    # 启动所有服务
    docker-compose up -d
    
    # 等待服务启动
    log_info "等待服务启动..."
    sleep 30
    
    # 检查服务状态
    check_services
    
    log_success "服务启动完成"
}

# 检查服务状态
check_services() {
    log_info "检查服务状态..."
    
    # 检查后端服务
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log_success "后端服务运行正常"
    else
        log_error "后端服务未正常启动"
        exit 1
    fi
    
    # 检查前端服务
    if curl -f http://localhost:80/ &> /dev/null; then
        log_success "前端服务运行正常"
    else
        log_error "前端服务未正常启动"
        exit 1
    fi
    
    # 检查数据库服务
    if docker-compose exec database pg_isready &> /dev/null; then
        log_success "数据库服务运行正常"
    else
        log_error "数据库服务未正常启动"
        exit 1
    fi
    
    # 检查Redis服务
    if docker-compose exec redis redis-cli ping &> /dev/null; then
        log_success "Redis服务运行正常"
    else
        log_error "Redis服务未正常启动"
        exit 1
    fi
}

# 数据库迁移
run_migrations() {
    log_info "运行数据库迁移..."
    
    docker-compose exec backend java -jar app.jar migrate
    
    log_success "数据库迁移完成"
}

# 清理旧镜像
cleanup_images() {
    log_info "清理旧镜像..."
    
    docker image prune -f
    docker volume prune -f
    
    log_success "镜像清理完成"
}

# 显示部署信息
show_deployment_info() {
    log_info "部署信息："
    echo "========================================"
    echo "应用名称: 志愿汇"
    echo "前端地址: http://localhost"
    echo "后端API: http://localhost:8080/api/v1"
    echo "数据库: localhost:5432"
    echo "Redis: localhost:6379"
    echo "管理后台: http://localhost/admin"
    echo "========================================"
}

# 主函数
main() {
    log_info "开始部署志愿汇系统..."
    
    # 执行部署步骤
    check_requirements
    check_env
    create_directories
    build_images
    start_services
    run_migrations
    cleanup_images
    show_deployment_info
    
    log_success "志愿汇系统部署完成！"
}

# 脚本参数处理
case "${1:-deploy}" in
    "start")
        docker-compose up -d
        check_services
        ;;
    "stop")
        docker-compose down
        ;;
    "restart")
        docker-compose restart
        check_services
        ;;
    "status")
        docker-compose ps
        ;;
    "logs")
        docker-compose logs -f $2
        ;;
    "migrate")
        run_migrations
        ;;
    "clean")
        docker-compose down -v --remove-orphans
        cleanup_images
        ;;
    "deploy")
        main
        ;;
    *)
        echo "用法: $0 [start|stop|restart|status|logs|migrate|clean|deploy]"
        echo ""
        echo "命令说明:"
        echo "  start    - 启动服务"
        echo "  stop     - 停止服务"
        echo "  restart  - 重启服务"
        echo "  status   - 查看服务状态"
        echo "  logs     - 查看日志"
        echo "  migrate  - 运行数据库迁移"
        echo "  clean    - 清理环境和镜像"
        echo "  deploy   - 完整部署（默认）"
        exit 1
        ;;
esac