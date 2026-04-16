// WebSocket 工具类
// 用于实现实时通知功能

class WebSocketManager {
  constructor(options = {}) {
    this.url = options.url || 'ws://localhost:8080/ws/notifications'
    this.reconnectInterval = options.reconnectInterval || 3000
    this.maxReconnectAttempts = options.maxReconnectAttempts || 5
    this.reconnectAttempts = 0
    
    this.ws = null
    this.isConnected = false
    this.messageHandlers = new Map()
    this.globalHandlers = []
    this.eventHandlers = new Map()
    
    this.connect()
  }
  
  // 连接 WebSocket
  connect() {
    try {
      console.log('正在连接 WebSocket:', this.url)
      this.ws = new WebSocket(this.url)
      
      this.ws.onopen = this.handleOpen.bind(this)
      this.ws.onmessage = this.handleMessage.bind(this)
      this.ws.onclose = this.handleClose.bind(this)
      this.ws.onerror = this.handleError.bind(this)
      
    } catch (error) {
      console.error('WebSocket 连接失败:', error)
      this.scheduleReconnect()
    }
  }
  
  // 处理连接成功
  handleOpen() {
    console.log('WebSocket 连接成功')
    this.isConnected = true
    this.reconnectAttempts = 0
    
    // 触发连接成功事件
    this.emit('connected')
    
    // 重新订阅之前订阅的事件
    this.eventHandlers.forEach((handlers, eventType) => {
      this.subscribe(eventType)
    })
  }
  
  // 处理消息
  handleMessage(event) {
    try {
      const message = JSON.parse(event.data)
      console.log('收到 WebSocket 消息:', message)
      
      // 处理特定消息类型
      if (message.type && this.messageHandlers.has(message.type)) {
        const handlers = this.messageHandlers.get(message.type)
        handlers.forEach(handler => handler(message.data))
      }
      
      // 处理全局消息
      this.globalHandlers.forEach(handler => handler(message))
      
      // 触发事件
      if (message.type) {
        this.emit(message.type, message.data)
      }
      
    } catch (error) {
      console.error('处理 WebSocket 消息失败:', error)
    }
  }
  
  // 处理连接关闭
  handleClose() {
    console.log('WebSocket 连接关闭')
    this.isConnected = false
    this.emit('disconnected')
    
    // 自动重连
    this.scheduleReconnect()
  }
  
  // 处理连接错误
  handleError(error) {
    console.error('WebSocket 连接错误:', error)
    this.emit('error', error)
  }
  
  // 计划重连
  scheduleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('WebSocket 重连次数达到上限，停止重连')
      return
    }
    
    this.reconnectAttempts++
    console.log(`WebSocket 重连中... (尝试 ${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
    
    setTimeout(() => {
      this.connect()
    }, this.reconnectInterval)
  }
  
  // 发送消息
  send(type, data) {
    if (!this.isConnected) {
      console.error('WebSocket 未连接，无法发送消息')
      return false
    }
    
    try {
      const message = { type, data, timestamp: Date.now() }
      this.ws.send(JSON.stringify(message))
      return true
    } catch (error) {
      console.error('发送 WebSocket 消息失败:', error)
      return false
    }
  }
  
  // 订阅消息类型
  subscribe(type, handler) {
    if (handler) {
      if (!this.messageHandlers.has(type)) {
        this.messageHandlers.set(type, [])
      }
      this.messageHandlers.get(type).push(handler)
    }
    
    // 发送订阅消息
    if (this.isConnected) {
      this.send('subscribe', { type })
    }
    
    // 存储订阅的事件类型用于重连后重新订阅
    this.eventHandlers.set(type, this.eventHandlers.get(type) || [])
  }
  
  // 取消订阅
  unsubscribe(type, handler) {
    if (this.messageHandlers.has(type)) {
      const handlers = this.messageHandlers.get(type)
      if (handler) {
        const index = handlers.indexOf(handler)
        if (index > -1) {
          handlers.splice(index, 1)
        }
      } else {
        this.messageHandlers.delete(type)
      }
    }
    
    // 发送取消订阅消息
    if (this.isConnected) {
      this.send('unsubscribe', { type })
    }
  }
  
  // 添加全局消息处理器
  onMessage(handler) {
    this.globalHandlers.push(handler)
  }
  
  // 移除全局消息处理器
  offMessage(handler) {
    const index = this.globalHandlers.indexOf(handler)
    if (index > -1) {
      this.globalHandlers.splice(index, 1)
    }
  }
  
  // 监听事件
  on(event, handler) {
    if (!this.eventHandlers.has(event)) {
      this.eventHandlers.set(event, [])
    }
    this.eventHandlers.get(event).push(handler)
  }
  
  // 取消监听事件
  off(event, handler) {
    if (this.eventHandlers.has(event)) {
      const handlers = this.eventHandlers.get(event)
      const index = handlers.indexOf(handler)
      if (index > -1) {
        handlers.splice(index, 1)
      }
    }
  }
  
  // 触发事件
  emit(event, data) {
    if (this.eventHandlers.has(event)) {
      const handlers = this.eventHandlers.get(event)
      handlers.forEach(handler => {
        try {
          handler(data)
        } catch (error) {
          console.error(`事件处理器错误 ${event}:`, error)
        }
      })
    }
  }
  
  // 关闭连接
  disconnect() {
    if (this.ws) {
      this.ws.close()
    }
    this.isConnected = false
    this.messageHandlers.clear()
    this.globalHandlers = []
    this.eventHandlers.clear()
  }
  
  // 获取连接状态
  getStatus() {
    return {
      connected: this.isConnected,
      reconnectAttempts: this.reconnectAttempts,
      maxReconnectAttempts: this.maxReconnectAttempts
    }
  }
}

// 创建全局 WebSocket 实例
let globalWebSocket = null

// 获取全局 WebSocket 实例
function getWebSocket(options = {}) {
  if (!globalWebSocket) {
    globalWebSocket = new WebSocketManager(options)
  }
  return globalWebSocket
}

// 销毁全局 WebSocket 实例
function destroyWebSocket() {
  if (globalWebSocket) {
    globalWebSocket.disconnect()
    globalWebSocket = null
  }
}

module.exports = {
  WebSocketManager,
  getWebSocket,
  destroyWebSocket
}