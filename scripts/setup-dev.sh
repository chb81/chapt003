#!/bin/bash

# 志愿汇开发环境设置脚本
# 用于快速搭建本地开发环境

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

# 检查基本工具
check_tools() {
    log_info "检查开发工具..."
    
    if ! command -v java &> /dev/null; then
        log_error "Java未安装，请先安装Java 8或更高版本"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装，请先安装Maven"
        exit 1
    fi
    
    if ! command -v node &> /dev/null; then
        log_error "Node.js未安装，请先安装Node.js 18或更高版本"
        exit 1
    fi
    
    if ! command -v npm &> /dev/null; then
        log_error "npm未安装，请先安装npm"
        exit 1
    fi
    
    log_success "开发工具检查通过"
}

# 设置环境变量
setup_env() {
    log_info "设置环境变量..."
    
    if [ ! -f .env.dev ]; then
        cp .env.dev.example .env.dev
        log_warning ".env.dev文件已创建，请根据需要修改配置"
    fi
    
    # 复制到前端目录
    if [ ! -f frontend/.env ]; then
        cp .env.dev.example frontend/.env
        log_warning "前端环境变量文件已创建"
    fi
    
    # 设置Java环境变量
    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    export PATH=$JAVA_HOME/bin:$PATH
    
    log_success "环境变量设置完成"
}

# 初始化数据库
init_database() {
    log_info "初始化数据库..."
    
    # 启动数据库服务
    docker-compose -f docker-compose.dev.yml up -d database
    
    # 等待数据库启动
    log_info "等待数据库启动..."
    sleep 20
    
    # 检查数据库状态
    if docker-compose -f docker-compose.dev.yml exec database pg_isready &> /dev/null; then
        log_success "数据库初始化完成"
    else
        log_error "数据库启动失败"
        exit 1
    fi
}

# 初始化Redis
init_redis() {
    log_info "初始化Redis..."
    
    docker-compose -f docker-compose.dev.yml up -d redis
    
    log_info "等待Redis启动..."
    sleep 10
    
    if docker-compose -f docker-compose.dev.yml exec redis redis-cli ping &> /dev/null; then
        log_success "Redis初始化完成"
    else
        log_error "Redis启动失败"
        exit 1
    fi
}

# 安装后端依赖
setup_backend() {
    log_info "安装后端依赖..."
    
    cd backend
    
    # 检查是否有pom.xml
    if [ -f "pom.xml" ]; then
        log_info "编译后端项目..."
        mvn clean compile
        
        if [ $? -eq 0 ]; then
            log_success "后端编译成功"
        else
            log_error "后端编译失败"
            exit 1
        fi
    else
        log_warning "未找到pom.xml文件"
    fi
    
    cd ..
}

# 安装前端依赖
setup_frontend() {
    log_info "安装前端依赖..."
    
    cd frontend
    
    # 检查是否有package.json
    if [ -f "package.json" ]; then
        log_info "安装npm包..."
        npm install
        
        if [ $? -eq 0 ]; then
            log_success "前端依赖安装成功"
        else
            log_error "前端依赖安装失败"
            exit 1
        fi
    else
        log_warning "未找到package.json文件"
    fi
    
    cd ..
}

# 创建配置文件
create_config() {
    log_info "创建配置文件..."
    
    # 创建后端配置文件
    if [ ! -f backend/src/main/resources/application-dev.yml ]; then
        cat > backend/src/main/resources/application-dev.yml << EOF
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/volunteer_db_dev
    username: postgres
    password: password123
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  redis:
    host: localhost
    port: 6379
    password:
  server:
    port: 8080
    servlet:
      context-path: /

logging:
  level:
    root: INFO
    com.chapt003: DEBUG

jwt:
  secret: dev_secret_key_for_testing_only
  expiration: 86400000

app:
  name: 志愿汇
  version: 1.0.0
  base-url: http://localhost:8080/api/v1
EOF
        log_success "后端配置文件创建完成"
    fi
    
    # 创建前端配置文件
    if [ ! -f frontend/.env.local ]; then
        cat > frontend/.env.local << EOF
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_NAME=志愿汇
VITE_APP_VERSION=1.0.0
EOF
        log_success "前端配置文件创建完成"
    fi
}

# 启动开发服务器
start_dev_servers() {
    log_info "启动开发服务器..."
    
    # 启动后端服务器
    docker-compose -f docker-compose.dev.yml up -d
    
    # 启动前端开发服务器
    cd frontend
    npm run dev &
    cd ..
    
    log_info "等待服务启动..."
    sleep 30
    
    # 检查服务状态
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log_success "后端服务运行正常"
    else
        log_warning "后端服务可能未完全启动，请稍后再试"
    fi
    
    if curl -f http://localhost:3000 &> /dev/null; then
        log_success "前端服务运行正常"
    else
        log_warning "前端服务可能未完全启动，请稍后再试"
    fi
    
    log_success "开发环境启动完成！"
}

# 显示访问信息
show_access_info() {
    log_info "开发环境访问信息："
    echo "========================================"
    echo "前端地址: http://localhost:3000"
    echo "后端API: http://localhost:8080"
    echo "数据库: localhost:5432 (用户名: postgres, 密码: password123)"
    echo "Redis: localhost:6379"
    echo "Swagger文档: http://localhost:8080/swagger-ui.html"
    echo "========================================"
    
    log_info "常用命令："
    echo "  查看服务状态: docker-compose -f docker-compose.dev.yml ps"
    echo "  查看服务日志: docker-compose -f docker-compose.dev.yml logs -f"
    echo "  停止服务: docker-compose -f docker-compose.dev.yml down"
    echo "  重启服务: docker-compose -f docker-compose.dev.yml restart"
}

# 主函数
main() {
    log_info "开始设置志愿汇开发环境..."
    
    # 执行设置步骤
    check_tools
    setup_env
    init_database
    init_redis
    setup_backend
    setup_frontend
    create_config
    start_dev_servers
    show_access_info
    
    log_success "志愿汇开发环境设置完成！"
}

# 脚本参数处理
case "${1:-setup}" in
    "setup")
        main
        ;;
    "reset")
        docker-compose -f docker-compose.dev.yml down -v
        setup_env
        init_database
        init_redis
        setup_backend
        setup_frontend
        create_config
        start_dev_servers
        show_access_info
        log_success "开发环境重置完成！"
        ;;
    "start")
        docker-compose -f docker-compose.dev.yml up -d
        ;;
    "stop")
        docker-compose -f docker-compose.dev.yml down
        ;;
    "restart")
        docker-compose -f docker-compose.dev.yml restart
        ;;
    "status")
        docker-compose -f docker-compose.dev.yml ps
        ;;
    "logs")
        docker-compose -f docker-compose.dev.yml logs -f $2
        ;;
    "clean")
        docker-compose -f docker-compose.dev.yml down -v --remove-orphans
        ;;
    *)
        echo "用法: $0 [setup|reset|start|stop|restart|status|logs|clean]"
        echo ""
        echo "命令说明:"
        echo "  setup    - 完整设置开发环境（默认）"
        echo "  reset    - 重置开发环境"
        echo "  start    - 启动服务"
        echo "  stop     - 停止服务"
        echo "  restart  - 重启服务"
        echo "  status   - 查看服务状态"
        echo "  logs     - 查看日志"
        echo "  clean    - 清理环境"
        exit 1
        ;;
esac