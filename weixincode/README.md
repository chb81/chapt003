# 中考志愿填报系统 - 微信小程序

微信小程序客户端，连接本地后端 Spring Boot API 服务。

## 项目结构

```
weixincode/
├── api/                    # API 接口封装
│   ├── auth.js            # 认证相关接口
│   ├── volunteer.js       # 志愿服务接口
│   └── admin.js           # 管理员接口
├── pages/                  # 页面目录
│   ├── index/             # 首页
│   ├── login/             # 登录
│   ├── register/          # 注册
│   ├── projects/          # 项目列表
│   ├── applications/      # 申请管理
│   ├── profile/           # 个人中心
│   └── ...                # 其他页面
├── utils/                  # 工具函数
│   ├── request.js         # 网络请求封装
│   ├── cache.js           # 缓存管理
│   └── ...
├── images/                 # 图片资源
├── app.js                 # 应用入口
├── app.json               # 应用配置
├── app.wxss               # 全局样式
└── project.config.json    # 项目配置
```

## 开发环境

1. 后端服务地址配置在 `app.js` 的 `globalData.apiBaseUrl` 中
2. 默认地址：`http://localhost:8080/api/v1`
3. 确保 Spring Boot 后端服务已启动

## 使用说明

1. 使用微信开发者工具打开 `weixincode` 目录
2. AppID：`wxc82ce01ef7c7f4f5`（或替换为自己的 AppID）
3. 编译运行即可

## 后端 API

小程序通过 `utils/request.js` 封装的 HTTP 请求连接本地后端 API：
- 认证：`/v1/auth/*`
- 用户：`/v1/user/*`
- 学校：`/v1/schools/*`
- 志愿：`/v1/volunteer-applications/*`
- 管理：`/v1/admin/*`
