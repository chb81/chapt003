import { ref, onMounted, onUnmounted } from 'vue'

// 加载状态管理
export function useLoading() {
  const isLoading = ref(false)
  const loadingStack = ref<number[]>([])

  const setLoading = (state: boolean) => {
    if (state) {
      const id = Date.now()
      loadingStack.value.push(id)
      isLoading.value = true
      return id
    } else {
      const stack = [...loadingStack.value]
      stack.pop()
      loadingStack.value = stack
      isLoading.value = stack.length > 0
      return Date.now()
    }
  }

  const withLoading = async <T>(fn: () => Promise<T>): Promise<T> => {
    const id = setLoading(true)
    try {
      return await fn()
    } finally {
      setLoading(false)
    }
  }

  return {
    isLoading,
    setLoading,
    withLoading
  }
}

// 错误处理
export function useError() {
  const error = ref<Error | null>(null)
  const errorStack = ref<Error[]>([])

  const setError = (err: Error) => {
    errorStack.value.push(err)
    error.value = err
  }

  const clearError = () => {
    errorStack.value = []
    error.value = null
  }

  const hasError = (type?: string): boolean => {
    if (!type) return errorStack.value.length > 0
    return errorStack.value.some(err => err.name === type)
  }

  const getLatestError = (): Error | null => {
    return errorStack.value[errorStack.value.length - 1] || null
  }

  return {
    error,
    setError,
    clearError,
    hasError,
    getLatestError
  }
}

// 防抖函数
export function useDebounce() {
  const debounce = <T extends (...args: any[]) => any>(
    fn: T,
    delay: number
  ): (...args: Parameters<T>) => void => {
    let timeoutId: NodeJS.Timeout | null = null

    return (...args: Parameters<T>) => {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }

      timeoutId = setTimeout(() => {
        fn(...args)
      }, delay)
    }
  }

  const debounceAsync = <T extends (...args: any[]) => Promise<any>>(
    fn: T,
    delay: number
  ): (...args: Parameters<T>) => Promise<void> => {
    let timeoutId: NodeJS.Timeout | null = null

    return (...args: Parameters<T>): Promise<void> => {
      return new Promise((resolve) => {
        if (timeoutId) {
          clearTimeout(timeoutId)
        }

        timeoutId = setTimeout(() => {
          fn(...args).then(resolve).catch(resolve)
        }, delay)
      })
    }
  }

  return {
    debounce,
    debounceAsync
  }
}

// 节流函数
export function useThrottle() {
  const throttle = <T extends (...args: any[]) => any>(
    fn: T,
    delay: number
  ): (...args: Parameters<T>) => void => {
    let lastCall = 0

    return (...args: Parameters<T>) => {
      const now = Date.now()

      if (now - lastCall >= delay) {
        lastCall = now
        fn(...args)
      }
    }
  }

  const throttleAsync = <T extends (...args: any[]) => Promise<any>>(
    fn: T,
    delay: number
  ): (...args: Parameters<T>) => Promise<void> => {
    let lastCall = 0

    return (...args: Parameters<T>): Promise<void> => {
      return new Promise((resolve) => {
        const now = Date.now()

        if (now - lastCall >= delay) {
          lastCall = now
          fn(...args).then(resolve).catch(resolve)
        } else {
          setTimeout(() => {
            fn(...args).then(resolve).catch(resolve)
          }, delay - (now - lastCall))
        }
      })
    }
  }

  return {
    throttle,
    throttleAsync
  }
}

// 本地存储优化
export function useOptimizedStorage() {
  const { getItem, setItem, removeItem, clearStorage } = useStorage()

  // 批量存储操作
  const batchSet = (data: Record<string, any>): boolean => {
    try {
      const serialized = JSON.stringify(data)
      localStorage.setItem('batch_data', serialized)
      return true
    } catch (error) {
      console.error('Batch set failed:', error)
      return false
    }
  }

  // 批量获取
  const batchGet = (): Record<string, any> => {
    try {
      const serialized = localStorage.getItem('batch_data')
      return serialized ? JSON.parse(serialized) : {}
    } catch (error) {
      console.error('Batch get failed:', error)
      return {}
    }
  }

  // 压缩存储
  const compress = (data: string): string => {
    try {
      // 简单的压缩算法（实际项目中可以使用LZ-string等库）
      return btoa(unescape(encodeURIComponent(data)))
    } catch (error) {
      console.error('Compression failed:', error)
      return data
    }
  }

  // 解压存储
  const decompress = (compressed: string): string => {
    try {
      return decodeURIComponent(escape(atob(compressed)))
    } catch (error) {
      console.error('Decompression failed:', error)
      return compressed
    }
  }

  // 分片存储大数据
  const chunkSet = (key: string, data: any, chunkSize: number = 1000): boolean => {
    try {
      const serialized = JSON.stringify(data)
      const chunks = []
      
      for (let i = 0; i < serialized.length; i += chunkSize) {
        const chunk = serialized.slice(i, i + chunkSize)
        chunks.push(chunk)
      }

      // 存储分片信息
      setItem(`${key}_chunks`, chunks.length)
      
      // 存储各个分片
      chunks.forEach((chunk, index) => {
        setItem(`${key}_chunk_${index}`, chunk)
      })

      return true
    } catch (error) {
      console.error('Chunk set failed:', error)
      return false
    }
  }

  // 获取分片数据
  const chunkGet = (key: string): any => {
    try {
      const chunkCount = getItem(`${key}_chunks`)
      if (!chunkCount) return null

      const chunks = []
      for (let i = 0; i < chunkCount; i++) {
        const chunk = getItem(`${key}_chunk_${i}`)
        if (chunk) {
          chunks.push(chunk)
        }
      }

      const serialized = chunks.join('')
      return JSON.parse(serialized)
    } catch (error) {
      console.error('Chunk get failed:', error)
      return null
    }
  }

  // 清理分片数据
  const chunkClear = (key: string): boolean => {
    try {
      const chunkCount = getItem(`${key}_chunks`)
      if (!chunkCount) return false

      // 清理分片
      for (let i = 0; i < chunkCount; i++) {
        removeItem(`${key}_chunk_${i}`)
      }
      
      // 清理分片信息
      removeItem(`${key}_chunks`)
      return true
    } catch (error) {
      console.error('Chunk clear failed:', error)
      return false
    }
  }

  return {
    getItem,
    setItem,
    removeItem,
    clearStorage,
    batchSet,
    batchGet,
    compress,
    decompress,
    chunkSet,
    chunkGet,
    chunkClear
  }
}

// 网络状态监控
export function useNetwork() {
  const isOnline = ref(navigator.onLine)
  const networkType = ref<'unknown' | 'wifi' | 'cellular' | 'ethernet' | 'wired' | 'other'>('unknown')
  const connection = ref<NetworkInformation | null>(null)

  const updateNetworkInfo = () => {
    isOnline.value = navigator.onLine
    
    // 获取网络连接信息
    if ('connection' in navigator) {
      connection.value = (navigator as any).connection
      if (connection.value) {
        const { effectiveType, downlink, rtt } = connection.value
        
        // 简单判断网络类型
        if (effectiveType.includes('4g') || effectiveType.includes('5g')) {
          networkType.value = 'cellular'
        } else if (effectiveType.includes('wifi')) {
          networkType.value = 'wifi'
        } else if (effectiveType.includes('ethernet')) {
          networkType.value = 'ethernet'
        } else {
          networkType.value = 'other'
        }
      }
    }
  }

  onMounted(() => {
    updateNetworkInfo()
    
    window.addEventListener('online', updateNetworkInfo)
    window.addEventListener('offline', updateNetworkInfo)
    
    if ('connection' in navigator) {
      (navigator as any).connection.addEventListener('change', updateNetworkInfo)
    }
  })

  onUnmounted(() => {
    window.removeEventListener('online', updateNetworkInfo)
    window.removeEventListener('offline', updateNetworkInfo)
    
    if ('connection' in navigator && connection.value) {
      connection.value.removeEventListener('change', updateNetworkInfo)
    }
  })

  // 判断是否适合大文件下载
  const isGoodConnection = (): boolean => {
    return networkType.value === 'wifi' || networkType.value === 'ethernet'
  }

  // 获取网络质量评分
  const getNetworkScore = (): number => {
    if (!connection.value) return 0
    
    const { effectiveType, downlink, rtt } = connection.value
    
    // 基于网络类型评分
    let typeScore = 0
    switch (effectiveType) {
      case '4g': case '5g': typeScore = 60; break
      case 'wifi': typeScore = 90; break
      case 'ethernet': typeScore = 100; break
      default: typeScore = 30; break
    }
    
    // 基于下载速度评分
    const downlinkScore = Math.min(100, downlink * 10) // Mbps * 10
    
    // 基于延迟评分
    const rttScore = Math.max(0, 100 - rtt * 2) // RTT in ms
    
    return (typeScore + downlinkScore + rttScore) / 3
  }

  return {
    isOnline,
    networkType,
    connection,
    isGoodConnection,
    getNetworkScore,
    updateNetworkInfo
  }
}

// 资源预加载
export function usePreload() {
  const loadedResources = new Set<string>()
  const loadingResources = new Set<string>()

  // 预加载图片
  const preloadImage = (src: string): Promise<HTMLImageElement> => {
    return new Promise((resolve, reject) => {
      if (loadedResources.has(src)) {
        const img = new Image()
        img.src = src
        resolve(img)
        return
      }

      if (loadingResources.has(src)) {
        // 等待正在加载的资源
        const checkInterval = setInterval(() => {
          if (loadedResources.has(src)) {
            clearInterval(checkInterval)
            const img = new Image()
            img.src = src
            resolve(img)
          }
        }, 100)
        return
      }

      loadingResources.add(src)
      const img = new Image()
      img.onload = () => {
        loadedResources.add(src)
        loadingResources.delete(src)
        resolve(img)
      }
      img.onerror = () => {
        loadingResources.delete(src)
        reject(new Error(`Failed to load image: ${src}`))
      }
      img.src = src
    })
  }

  // 预加载CSS
  const preloadCSS = (href: string): Promise<void> => {
    return new Promise((resolve, reject) => {
      if (loadedResources.has(href)) {
        resolve()
        return
      }

      if (loadingResources.has(href)) {
        const checkInterval = setInterval(() => {
          if (loadedResources.has(href)) {
            clearInterval(checkInterval)
            resolve()
          }
        }, 100)
        return
      }

      loadingResources.add(href)
      const link = document.createElement('link')
      link.rel = 'stylesheet'
      link.href = href
      link.onload = () => {
        loadedResources.add(href)
        loadingResources.delete(href)
        resolve()
      }
      link.onerror = () => {
        loadingResources.delete(href)
        reject(new Error(`Failed to load CSS: ${href}`))
      }
      document.head.appendChild(link)
    })
  }

  // 预加载JS
  const preloadJS = (src: string): Promise<void> => {
    return new Promise((resolve, reject) => {
      if (loadedResources.has(src)) {
        resolve()
        return
      }

      if (loadingResources.has(src)) {
        const checkInterval = setInterval(() => {
          if (loadedResources.has(src)) {
            clearInterval(checkInterval)
            resolve()
          }
        }, 100)
        return
      }

      loadingResources.add(src)
      const script = document.createElement('script')
      script.src = src
      script.onload = () => {
        loadedResources.add(src)
        loadingResources.delete(src)
        resolve()
      }
      script.onerror = () => {
        loadingResources.delete(src)
        reject(new Error(`Failed to load JS: ${src}`))
      }
      document.head.appendChild(script)
    })
  }

  return {
    preloadImage,
    preloadCSS,
    preloadJS,
    loadedResources,
    loadingResources
  }
}