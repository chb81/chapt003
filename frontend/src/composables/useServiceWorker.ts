import { registerSW } from 'virtual:pwa-register'

export function useServiceWorker() {
  // 检查是否支持Service Worker
  const isSupported = 'serviceWorker' in navigator && 'PushManager' in window

  // 注册Service Worker
  const register = async () => {
    if (!isSupported) {
      console.log('Service Worker not supported')
      return false
    }

    try {
      const updateSW = registerSW({
        onOfflineReady() {
          console.log('App is ready to work offline')
        },
        onNeedRefresh() {
          console.log('New content available')
          // 可以在这里添加用户界面提示
        },
        onRegisterError(error) {
          console.error('Service Worker registration failed:', error)
        }
      })
      
      return true
    } catch (error) {
      console.error('Service Worker registration failed:', error)
      return false
    }
  }

  // 检查Service Worker状态
  const checkStatus = () => {
    if (!isSupported) return 'unsupported'
    
    return navigator.serviceWorker ? 'registered' : 'not-registered'
  }

  // 缓存管理
  const cacheAssets = async () => {
    if (!isSupported) return false

    try {
      const cache = await caches.open('volunteer-app-v1')
      
      // 缓存关键资源
      const assets = [
        '/',
        '/manifest.json',
        '/favicon.ico',
        '/logo.png',
        '/css/admin.css',
        '/js/admin.js'
      ]
      
      await cache.addAll(assets)
      console.log('Assets cached successfully')
      return true
    } catch (error) {
      console.error('Cache failed:', error)
      return false
    }
  }

  // 清理过期缓存
  const clearCache = async () => {
    if (!isSupported) return false

    try {
      const cacheNames = await caches.keys()
      const promises = cacheNames.map(async (cacheName) => {
        if (cacheName !== 'volunteer-app-v1') {
          await caches.delete(cacheName)
        }
      })
      
      await Promise.all(promises)
      console.log('Cache cleaned successfully')
      return true
    } catch (error) {
      console.error('Cache cleanup failed:', error)
      return false
    }
  }

  return {
    isSupported,
    register,
    checkStatus,
    cacheAssets,
    clearCache
  }
}