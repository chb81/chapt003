const app = getApp()

class PerformanceMonitor {
  constructor() {
    this.metrics = {
      pageLoadTimes: {},
      apiRequestTimes: {},
      renderTimes: {}
    }
    this.startTime = {}
  }

  startMeasure(name) {
    this.startTime[name] = Date.now()
  }

  endMeasure(name) {
    if (this.startTime[name]) {
      const duration = Date.now() - this.startTime[name]
      this.recordMetric(name, duration)
      delete this.startTime[name]
      return duration
    }
    return 0
  }

  recordMetric(name, value) {
    if (!this.metrics[name]) {
      this.metrics[name] = []
    }
    this.metrics[name].push(value)
    
    const maxMetrics = 100
    if (this.metrics[name].length > maxMetrics) {
      this.metrics[name].shift()
    }
  }

  getAverage(name) {
    const values = this.metrics[name]
    if (!values || values.length === 0) return 0
    const sum = values.reduce((a, b) => a + b, 0)
    return Math.round(sum / values.length)
  }

  getMax(name) {
    const values = this.metrics[name]
    if (!values || values.length === 0) return 0
    return Math.max(...values)
  }

  getMin(name) {
    const values = this.metrics[name]
    if (!values || values.length === 0) return 0
    return Math.min(...values)
  }

  getMetricsReport() {
    const report = {}
    
    for (const name in this.metrics) {
      const values = this.metrics[name]
      if (values.length > 0) {
        report[name] = {
          average: this.getAverage(name),
          max: this.getMax(name),
          min: this.getMin(name),
          count: values.length
        }
      }
    }
    
    return report
  }

  resetMetrics() {
    this.metrics = {
      pageLoadTimes: {},
      apiRequestTimes: {},
      renderTimes: {}
    }
  }

  logPerformance() {
    const report = this.getMetricsReport()
    console.log('Performance Metrics:', report)
    return report
  }
}

const performanceMonitor = new PerformanceMonitor()

function measurePageLoad(pageName) {
  const loadTime = performanceMonitor.endMeasure(`pageLoad_${pageName}`)
  if (loadTime > 0) {
    console.log(`Page ${pageName} load time: ${loadTime}ms`)
    
    if (loadTime > 3000) {
      console.warn(`Page ${pageName} loaded slowly (${loadTime}ms)`)
    }
  }
}

function measureApiRequest(url, method) {
  const key = `api_${method}_${url}`
  const duration = performanceMonitor.endMeasure(key)
  if (duration > 0) {
    console.log(`API ${method} ${url}: ${duration}ms`)
    
    if (duration > 3000) {
      console.warn(`API ${method} ${url} is slow (${duration}ms)`)
    }
  }
}

function measureRender(componentName) {
  const renderTime = performanceMonitor.endMeasure(`render_${componentName}`)
  if (renderTime > 0) {
    console.log(`Component ${componentName} render time: ${renderTime}ms`)
  }
}

function startPageLoad(pageName) {
  performanceMonitor.startMeasure(`pageLoad_${pageName}`)
}

function startApiRequest(url, method) {
  const key = `api_${method}_${url}`
  performanceMonitor.startMeasure(key)
}

function startRender(componentName) {
  performanceMonitor.startMeasure(`render_${componentName}`)
}

module.exports = {
  performanceMonitor,
  measurePageLoad,
  measureApiRequest,
  measureRender,
  startPageLoad,
  startApiRequest,
  startRender
}
