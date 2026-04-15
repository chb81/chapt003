import { ref, onUnmounted } from 'vue'

export interface WebSocketMessage {
  type: string
  data: any
  timestamp: number
}

export interface WebSocketStatus {
  connected: boolean
  reconnectAttempts: number
  maxReconnectAttempts: number
}

export interface WebSocketOptions {
  url?: string
  reconnectInterval?: number
  maxReconnectAttempts?: number
  autoReconnect?: boolean
}

class WebSocketManager {
  private ws: WebSocket | null = null
  private reconnectTimer: number | null = null
  private messageHandlers: Map<string, Array<(data: any) => void>> = new Map()
  private eventHandlers: Map<string, Array<(data: any) => void>> = new Map()
  private globalHandlers: Array<(message: WebSocketMessage) => void> = []
  private options: Required<WebSocketOptions>

  constructor(options: WebSocketOptions = {}) {
    this.options = {
      url: options.url || `ws://${window.location.host}/ws/notifications`,
      reconnectInterval: options.reconnectInterval || 3000,
      maxReconnectAttempts: options.maxReconnectAttempts || 5,
      autoReconnect: options.autoReconnect !== false
    }
  }

  connect() {
    try {
      console.log('Connecting to WebSocket:', this.options.url)
      this.ws = new WebSocket(this.options.url)

      this.ws.onopen = this.handleOpen.bind(this)
      this.ws.onmessage = this.handleMessage.bind(this)
      this.ws.onclose = this.handleClose.bind(this)
      this.ws.onerror = this.handleError.bind(this)
    } catch (error) {
      console.error('WebSocket connection failed:', error)
      this.scheduleReconnect()
    }
  }

  private handleOpen() {
    console.log('WebSocket connected')
    this.options.autoReconnect = true
    
    this.emit('connected', null)
    
    this.eventHandlers.forEach((handlers, eventType) => {
      this.subscribe(eventType)
    })
  }

  private handleMessage(event: MessageEvent) {
    try {
      const message: WebSocketMessage = JSON.parse(event.data)
      console.log('WebSocket message received:', message)

      if (message.type && this.messageHandlers.has(message.type)) {
        const handlers = this.messageHandlers.get(message.type)!
        handlers.forEach(handler => handler(message.data))
      }

      this.globalHandlers.forEach(handler => handler(message))

      if (message.type) {
        this.emit(message.type, message.data)
      }
    } catch (error) {
      console.error('Failed to process WebSocket message:', error)
    }
  }

  private handleClose() {
    console.log('WebSocket closed')
    this.ws = null
    
    this.emit('disconnected', null)
    
    if (this.options.autoReconnect) {
      this.scheduleReconnect()
    }
  }

  private handleError(error: Event) {
    console.error('WebSocket error:', error)
    this.emit('error', error)
  }

  private scheduleReconnect() {
    if (this.reconnectTimer !== null) {
      return
    }

    const reconnectAttempts = this.getStatus().reconnectAttempts
    if (reconnectAttempts >= this.options.maxReconnectAttempts) {
      console.error('Max reconnect attempts reached, stopping reconnect')
      return
    }

    this.reconnectTimer = window.setTimeout(() => {
      this.reconnectTimer = null
      this.connect()
    }, this.options.reconnectInterval)
  }

  send(type: string, data: any): boolean {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.error('WebSocket not connected, cannot send message')
      return false
    }

    try {
      const message: WebSocketMessage = { type, data, timestamp: Date.now() }
      this.ws.send(JSON.stringify(message))
      return true
    } catch (error) {
      console.error('Failed to send WebSocket message:', error)
      return false
    }
  }

  subscribe(type: string, handler?: (data: any) => void) {
    if (handler) {
      if (!this.messageHandlers.has(type)) {
        this.messageHandlers.set(type, [])
      }
      this.messageHandlers.get(type)!.push(handler)
    }

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.send('subscribe', { type })
    }

    if (!this.eventHandlers.has(type)) {
      this.eventHandlers.set(type, [])
    }
  }

  unsubscribe(type: string, handler?: (data: any) => void) {
    if (this.messageHandlers.has(type)) {
      const handlers = this.messageHandlers.get(type)!
      if (handler) {
        const index = handlers.indexOf(handler)
        if (index > -1) {
          handlers.splice(index, 1)
        }
      } else {
        this.messageHandlers.delete(type)
      }
    }

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.send('unsubscribe', { type })
    }
  }

  onMessage(handler: (message: WebSocketMessage) => void) {
    this.globalHandlers.push(handler)
  }

  offMessage(handler: (message: WebSocketMessage) => void) {
    const index = this.globalHandlers.indexOf(handler)
    if (index > -1) {
      this.globalHandlers.splice(index, 1)
    }
  }

  on(event: string, handler: (data: any) => void) {
    if (!this.eventHandlers.has(event)) {
      this.eventHandlers.set(event, [])
    }
    this.eventHandlers.get(event)!.push(handler)
  }

  off(event: string, handler: (data: any) => void) {
    if (this.eventHandlers.has(event)) {
      const handlers = this.eventHandlers.get(event)!
      const index = handlers.indexOf(handler)
      if (index > -1) {
        handlers.splice(index, 1)
      }
    }
  }

  private emit(event: string, data: any) {
    if (this.eventHandlers.has(event)) {
      const handlers = this.eventHandlers.get(event)!
      handlers.forEach(handler => {
        try {
          handler(data)
        } catch (error) {
          console.error(`Event handler error for ${event}:`, error)
        }
      })
    }
  }

  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    
    if (this.reconnectTimer !== null) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    
    this.options.autoReconnect = false
  }

  getStatus(): WebSocketStatus {
    return {
      connected: this.ws !== null && this.ws.readyState === WebSocket.OPEN,
      reconnectAttempts: this.eventHandlers.size || 0,
      maxReconnectAttempts: this.options.maxReconnectAttempts
    }
  }
}

let globalWebSocket: WebSocketManager | null = null

export function getWebSocket(options?: WebSocketOptions): WebSocketManager {
  if (!globalWebSocket) {
    globalWebSocket = new WebSocketManager(options)
  }
  return globalWebSocket
}

export function destroyWebSocket() {
  if (globalWebSocket) {
    globalWebSocket.disconnect()
    globalWebSocket = null
  }
}

export function useWebSocket(options?: WebSocketOptions) {
  const ws = getWebSocket(options)
  const status = ref<WebSocketStatus>(ws.getStatus())

  const updateStatus = () => {
    status.value = ws.getStatus()
  }

  ws.on('connected', updateStatus)
  ws.on('disconnected', updateStatus)

  onUnmounted(() => {
    ws.off('connected', updateStatus)
    ws.off('disconnected', updateStatus)
  })

  return {
    ws,
    status
  }
}