import { ref, onMounted, onUnmounted } from 'vue'
import { performanceMonitor } from '@/utils/performance'

export function useOffline() {
  const isOnline = ref(navigator.onLine)
  const isSupported = ref('serviceWorker' in navigator && 'caches' in window)

  // 检查网络状态
  const checkNetworkStatus = () => {
    isOnline.value = navigator.onLine
  }

  // 监听网络状态变化
  onMounted(() => {
    window.addEventListener('online', checkNetworkStatus)
    window.addEventListener('offline', checkNetworkStatus)
  })

  onUnmounted(() => {
    window.removeEventListener('online', checkNetworkStatus)
    window.removeEventListener('offline', checkNetworkStatus)
  })

  // 获取缓存的资源
  const getCachedResources = async () => {
    if (!isSupported.value) return []

    try {
      const cacheNames = await caches.keys()
      const resources: string[] = []

      for (const cacheName of cacheNames) {
        const cache = await caches.open(cacheName)
        const cachedKeys = await cache.keys()
        
        for (const request of cachedKeys) {
          resources.push(request.url)
        }
      }

      return resources
    } catch (error) {
      console.error('Failed to get cached resources:', error)
      return []
    }
  }

  // 清除特定缓存
  const clearCache = async (cacheName?: string) => {
    if (!isSupported.value) return false

    try {
      if (cacheName) {
        await caches.delete(cacheName)
      } else {
        const cacheNames = await caches.keys()
        await Promise.all(cacheNames.map(name => caches.delete(name)))
      }
      return true
    } catch (error) {
      console.error('Failed to clear cache:', error)
      return false
    }
  }

  // 缓存新资源
  const cacheResource = async (url: string, response?: Response) => {
    if (!isSupported.value) return false

    try {
      const cache = await caches.open('volunteer-app-v1')
      
      if (response) {
        await cache.put(url, response)
      } else {
        const response = await fetch(url)
        await cache.put(url, response.clone())
      }
      
      return true
    } catch (error) {
      console.error('Failed to cache resource:', error)
      return false
    }
  }

  return {
    isOnline,
    isSupported,
    checkNetworkStatus,
    getCachedResources,
    clearCache,
    cacheResource
  }
}

export function useStorage() {
  const storage = ref<Storage>()

  // 初始化存储
  const initStorage = (type: 'local' | 'session' = 'local') => {
    try {
      storage.value = type === 'local' ? localStorage : sessionStorage
      return true
    } catch (error) {
      console.error('Storage not available:', error)
      return false
    }
  }

  // 获取存储数据
  const getItem = <T>(key: string, defaultValue?: T): T | null => {
    if (!storage.value) return defaultValue ?? null

    try {
      const item = storage.value.getItem(key)
      return item ? JSON.parse(item) : defaultValue ?? null
    } catch (error) {
      console.error('Failed to get item from storage:', error)
      return defaultValue ?? null
    }
  }

  // 设置存储数据
  const setItem = <T>(key: string, value: T): boolean => {
    if (!storage.value) return false

    try {
      storage.value.setItem(key, JSON.stringify(value))
      return true
    } catch (error) {
      console.error('Failed to set item in storage:', error)
      return false
    }
  }

  // 删除存储数据
  const removeItem = (key: string): boolean => {
    if (!storage.value) return false

    try {
      storage.value.removeItem(key)
      return true
    } catch (error) {
      console.error('Failed to remove item from storage:', error)
      return false
    }
  }

  // 清空存储
  const clearStorage = (): boolean => {
    if (!storage.value) return false

    try {
      storage.value.clear()
      return true
    } catch (error) {
      console.error('Failed to clear storage:', error)
      return false
    }
  }

  // 检查存储大小
  const getStorageSize = (): number => {
    if (!storage.value) return 0

    try {
      const size = JSON.stringify(storage.value).length
      return size
    } catch (error) {
      console.error('Failed to get storage size:', error)
      return 0
    }
  }

  // 检查存储是否已满
  const isStorageFull = (): boolean => {
    const size = getStorageSize()
    return size > 5 * 1024 * 1024 // 5MB限制
  }

  // 自动清理过期数据
  const cleanupExpiredData = (): boolean => {
    if (!storage.value) return false

    try {
      const now = Date.now()
      const keysToRemove: string[] = []

      // 遍历所有键
      for (let i = 0; i < storage.value.length; i++) {
        const key = storage.value.key(i)
        if (!key) continue

        const item = storage.value.getItem(key)
        if (!item) continue

        try {
          const data = JSON.parse(item)
          // 如果数据有过期时间且已过期，标记删除
          if (data.expiresAt && data.expiresAt < now) {
            keysToRemove.push(key)
          }
        } catch (error) {
          // 如果解析失败，删除该数据
          keysToRemove.push(key)
        }
      }

      // 删除过期数据
      keysToRemove.forEach(key => storage.value!.removeItem(key))

      return keysToRemove.length > 0
    } catch (error) {
      console.error('Failed to cleanup expired data:', error)
      return false
    }
  }

  return {
    initStorage,
    getItem,
    setItem,
    removeItem,
    clearStorage,
    getStorageSize,
    isStorageFull,
    cleanupExpiredData
  }
}

export function useCache() {
  const { isSupported } = useOffline()

  // 缓存用户数据
  const cacheUserData = async (userData: any) => {
    if (!isSupported.value) return false

    try {
      const cache = await caches.open('user-data')
      const response = new Response(JSON.stringify(userData), {
        headers: { 'Content-Type': 'application/json' }
      })
      await cache.put('/api/user/data', response)
      return true
    } catch (error) {
      console.error('Failed to cache user data:', error)
      return false
    }
  }

  // 获取缓存的用户数据
  const getCachedUserData = async () => {
    if (!isSupported.value) return null

    try {
      const cache = await caches.open('user-data')
      const response = await cache.match('/api/user/data')
      if (!response) return null

      const userData = await response.json()
      return userData
    } catch (error) {
      console.error('Failed to get cached user data:', error)
      return null
    }
  }

  // 缓存API响应
  const cacheApiResponse = async (url: string, response: Response) => {
    if (!isSupported.value) return false

    try {
      const cache = await caches.open('api-responses')
      await cache.put(url, response)
      return true
    } catch (error) {
      console.error('Failed to cache API response:', error)
      return false
    }
  }

  // 获取缓存的API响应
  const getCachedApiResponse = async (url: string) => {
    if (!isSupported.value) return null

    try {
      const cache = await caches.open('api-responses')
      const response = await cache.match(url)
      return response
    } catch (error) {
      console.error('Failed to get cached API response:', error)
      return null
    }
  }

  // 清除特定API的缓存
  const clearApiCache = async (url?: string) => {
    if (!isSupported.value) return false

    try {
      const cache = await caches.open('api-responses')
      
      if (url) {
        await cache.delete(url)
      } else {
        const cachedKeys = await cache.keys()
        await Promise.all(cachedKeys.map(key => cache.delete(key)))
      }
      
      return true
    } catch (error) {
      console.error('Failed to clear API cache:', error)
      return false
    }
  }

  return {
    cacheUserData,
    getCachedUserData,
    cacheApiResponse,
    getCachedApiResponse,
    clearApiCache
  }
}