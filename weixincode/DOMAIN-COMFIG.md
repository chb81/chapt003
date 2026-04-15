# 小程序服务器域名配置

## 域名配置要求

微信小程序需要配置服务器域名才能调用第三方接口。以下是志愿汇小程序的域名配置说明。

## 配置步骤

### 1. 登录微信公众平台

1. 访问微信公众平台：https://mp.weixin.qq.com
2. 登录小程序账号
3. 进入「开发」-「开发设置」

### 2. 配置服务器域名

#### request合法域名
```
https://api.example.com
https://localhost:8080
```

#### uploadFile合法域名
```
https://cdn.example.com
https://localhost:8080
```

#### downloadFile合法域名
```
https://cdn.example.com
https://localhost:8080
```

#### socket合法域名
```
wss://api.example.com
```

### 3. 开发环境配置

在开发环境中，可以暂时关闭域名检查：

1. 在开发工具中，点击右上角「详情」
2. 选择「本地设置」
3. 勾选「不校验合法域名、web-view（业务域名）、TLS版本以及HTTPS证书」

## 域名配置详情

### 开发环境

```json
{
  "request": {
    "domainList": [
      "https://localhost:8080",
      "http://localhost:8080"
    ]
  },
  "uploadFile": {
    "domainList": [
      "https://localhost:8080"
    ]
  },
  "downloadFile": {
    "domainList": [
      "https://localhost:8080"
    ]
  }
}
```

### 生产环境

```json
{
  "request": {
    "domainList": [
      "https://api.zhiyuanhui.com",
      "https://cdn.zhiyuanhui.com"
    ]
  },
  "uploadFile": {
    "domainList": [
      "https://cdn.zhiyuanhui.com"
    ]
  },
  "downloadFile": {
    "domainList": [
      "https://cdn.zhiyuanhui.com"
    ]
  },
  "socket": {
    "domainList": [
      "wss://api.zhiyuanhui.com"
    ]
  }
}
```

## 域名要求

### 域名格式要求

1. **协议要求**：必须是 `https://`，不支持 `http://`
2. **域名格式**：域名必须为合法域名，不支持IP地址
3. **端口要求**：域名中不能包含端口号
4. **域名层级**：只能配置二级域名，三级域名需要配置二级域名

### 域名验证

配置域名后，系统会自动验证域名的有效性：

1. **SSL证书验证**：域名必须有有效的SSL证书
2. **服务器响应验证**：域名必须有正常的服务器响应
3. **备案验证**：域名必须完成ICP备案

## 域名配置清单

### 必需域名

| 域名类型 | 域名 | 说明 |
|---------|------|------|
| request | https://api.example.com | API接口域名 |
| uploadFile | https://cdn.example.com | 文件上传域名 |
| downloadFile | https://cdn.example.com | 文件下载域名 |
| socket | wss://api.example.com | WebSocket连接域名 |

### 可选域名

| 域名类型 | 域名 | 说明 |
|---------|------|------|
| request | https://payment.example.com | 支付相关接口 |
| request | https://stat.example.com | 统计分析接口 |

## 域名配置最佳实践

### 1. 域名管理

- 使用统一的CDN服务提供商
- 避免使用临时域名
- 定期检查域名的可用性

### 2. 证书管理

- 使用受信任的CA机构颁发的证书
- 设置证书自动续期
- 避免使用自签名证书

### 3. 性能优化

- 使用CDN加速静态资源
- 配置合适的缓存策略
- 启用HTTP/2支持

### 4. 安全配置

- 启用HSTS
- 配置CSP策略
- 启用Gzip压缩

## 域名配置故障排除

### 常见问题

1. **域名配置不生效**
   - 检查域名是否已保存
   - 确认审核状态为「已通过」
   - 清除小程序缓存

2. **HTTPS证书问题**
   - 确保证书有效
   - 检查证书链完整性
   - 验证书面域名与证书域名一致

3. **跨域问题**
   - 检查CORS配置
   - 确认服务器响应正确
   - 检查请求头配置

### 调试方法

1. **微信开发者工具**
   - 查看控制台错误信息
   - 检查网络请求详情

2. **浏览器开发工具**
   - 使用Fiddler或Charles抓包
   - 检查SSL证书详情

3. **服务器日志**
   - 检查访问日志
   - 确认服务正常响应

## 域名配置验证

### 验证步骤

1. **配置完成后验证**
   - 重新编译小程序
   - 测试所有API调用
   - 检查文件上传下载功能

2. **定期验证**
   - 每周检查域名可用性
   - 定期测试所有功能
   - 监控API响应时间

### 验证清单

- [ ] 所有API请求正常
- [ ] 文件上传功能正常
- [ ] 文件下载功能正常
- [ ] WebSocket连接正常
- [ ] 错误处理机制正常

## 总结

域名配置是小程序开发的重要环节，需要严格按照微信官方要求进行配置。建议：

1. 在开发阶段使用开发环境设置进行调试
2. 在发布前完成正式域名的配置
3. 定期检查和维护域名配置
4. 建立域名配置变更的审核流程
