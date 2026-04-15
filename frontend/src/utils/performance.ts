// 性能监控工具
export class PerformanceMonitor {
  private metrics: Map<string, number[]> = new Map()
  private observers: PerformanceObserver[] = []

  constructor() {
    this.setupObservers()
  }

  // 设置性能观察者
  private setupObservers() {
    try {
      // 资源加载观察者
      const resourceObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach(entry => {
          const duration = entry.duration
          this.recordMetric('resource_load', duration)
          
          if (entry.duration > 5000) {
            console.warn(`Slow resource: ${entry.name} took ${duration}ms`)
          }
        })
      })

      // 布局偏移观察者
      const layoutObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach(entry => {
          this.recordMetric('cls', entry.value)
        })
      })

      resourceObserver.observe({ entryTypes: ['resource'] })
      layoutObserver.observe({ entryTypes: ['layout-shift'] })

      this.observers.push(resourceObserver, layoutObserver)
    } catch (error) {
      console.warn('Performance Observer not supported:', error)
    }
  }

  // 记录性能指标
  private recordMetric(name: string, value: number) {
    if (!this.metrics.has(name)) {
      this.metrics.set(name, [])
    }
    this.metrics.get(name)!.push(value)
  }

  // 获取平均性能值
  getAverageMetric(name: string): number {
    const values = this.metrics.get(name)
    if (!values || values.length === 0) return 0
    
    const sum = values.reduce((acc, val) => acc + val, 0)
    return sum / values.length
  }

  // 获取性能分数
  getPerformanceScore(): number {
    const fcp = this.getAverageMetric('first-contentful-paint') || 0
    const lcp = this.getAverageMetric('largest-contentful-paint') || 0
    const cls = this.getAverageMetric('cls') || 0
    const fid = this.getAverageMetric('first-input-delay') || 0

    // 根据Google Web Vitals计算分数
    const fcpScore = Math.max(0, 100 - fcp / 10) // FCP < 1.5s = 100分
    const lcpScore = Math.max(0, 100 - lcp / 10) // LCP < 2.5s = 100分
    const clsScore = Math.max(0, 100 - cls * 100) // CLS < 0.1 = 100分
    const fidScore = Math.max(0, 100 - fid * 10) // FID < 100ms = 100分

    return (fcpScore + lcpScore + clsScore + fidScore) / 4
  }

  // 获取页面加载性能
  getPageLoadTime(): number {
    const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming
    if (!navigation) return 0
    
    return navigation.loadEventEnd - navigation.startTime
  }

  // 获取首次内容绘制
  getFirstContentfulPaint(): number {
    const paint = performance.getEntriesByType('paint')[0] as PerformancePaintTiming
    if (!paint || paint.name !== 'first-contentful-paint') return 0
    
    return paint.startTime
  }

  // 清理观察者
  destroy() {
    this.observers.forEach(observer => observer.disconnect())
    this.metrics.clear()
  }
}

// 创建全局性能监控实例
export const performanceMonitor = new PerformanceMonitor()

// 性能分析工具
export class PerformanceAnalyzer {
  private marks: Map<string, number> = new Map()

  // 开始性能标记
  mark(name: string) {
    performance.mark(name)
    this.marks.set(name, performance.now())
  }

  // 结束性能标记
  measure(name: string, startMark?: string, endMark?: string) {
    const startTime = startMark ? this.marks.get(startMark) || 0 : 0
    const endTime = endMark ? this.marks.get(endMark) || 0 : performance.now()
    
    const duration = endTime - startTime
    this.recordMetric(name, duration)
    
    // 如果时间过长，发出警告
    if (duration > 1000) {
      console.warn(`Performance issue: ${name} took ${duration}ms`)
    }
    
    return duration
  }

  // 记录性能指标
  private recordMetric(name: string, value: number) {
    performanceMonitor.recordMetric(name, value)
  }

  // 分析组件渲染性能
  analyzeComponentRender(componentName: string, renderTime: number) {
    const avgRenderTime = performanceMonitor.getAverageMetric(`${componentName}_render`)
    const isSlow = renderTime > 1000 // 1秒
    
    if (isSlow) {
      console.warn(`Slow component render: ${componentName} took ${renderTime}ms (avg: ${avgRenderTime}ms)`)
    }
    
    return {
      component: componentName,
      renderTime,
      averageTime: avgRenderTime,
      isSlow
    }
  }

  // 分析网络请求性能
  analyzeNetworkRequest(url: string, duration: number, size: number) {
    const avgDuration = performanceMonitor.getAverageMetric('network_request')
    const avgSize = performanceMonitor.getAverageMetric('network_size')
    
    const isSlow = duration > 3000 // 3秒
    const isLarge = size > 1024 * 1024 // 1MB
    
    if (isSlow || isLarge) {
      console.warn(`Slow/Large network request: ${url} took ${duration}ms, size: ${size}bytes`)
    }
    
    return {
      url,
      duration,
      size,
      averageDuration: avgDuration,
      averageSize: avgSize,
      isSlow,
      isLarge
    }
  }
}

// 创建全局性能分析器实例
export const performanceAnalyzer = new PerformanceAnalyzer()

// 性能监控组件
export function usePerformance() {
  const performanceAnalyzer = new PerformanceAnalyzer()
  
  // 监控页面加载
  const onPageLoad = () => {
    const loadTime = performanceMonitor.getPageLoadTime()
    const fcp = performanceMonitor.getFirstContentfulPaint()
    const score = performanceMonitor.getPerformanceScore()
    
    console.log('Performance metrics:', {
      loadTime: `${loadTime}ms`,
      fcp: `${fcp}ms`,
      score: `${score.toFixed(2)}/100`
    })
  }

  // 监控组件渲染
  const onComponentRender = (componentName: string) => {
    const renderTime = performance.now()
    performanceAnalyzer.mark(`${componentName}_start`)
    
    return {
      end: () => {
        const endTime = performance.now()
        const duration = performanceAnalyzer.measure(
          `${componentName}_render`,
          `${componentName}_start`,
          `${componentName}_end`
        )
        
        return performanceAnalyzer.analyzeComponentRender(componentName, duration)
      }
    }
  }

  // 监控网络请求
  const onNetworkRequest = (url: string) => {
    const startTime = performance.now()
    
    return {
      end: (size: number) => {
        const endTime = performance.now()
        const duration = endTime - startTime
        
        return performanceAnalyzer.analyzeNetworkRequest(url, duration, size)
      }
    }
  }

  return {
    onPageLoad,
    onComponentRender,
    onNetworkRequest,
    getPerformanceScore: () => performanceMonitor.getPerformanceScore(),
    getAverageMetric: (name: string) => performanceMonitor.getAverageMetric(name)
  }
}