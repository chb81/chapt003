/**
 * 性能测试工具类
 * 提供性能监控、分析和优化的工具函数
 */

export interface PerformanceMetrics {
  loadTime: number;
  renderTime: number;
  responseTime: number;
  memoryUsage: number;
  cpuUsage: number;
  networkLatency: number;
  interactionLatency: number;
  bundleSize: number;
}

export interface PerformanceBenchmark {
  name: string;
  metrics: PerformanceMetrics;
  threshold: PerformanceMetrics;
  status: 'pass' | 'fail' | 'warning';
}

export interface PerformanceReport {
  overallScore: number;
  benchmarks: PerformanceBenchmark[];
  recommendations: string[];
  summary: string;
}

/**
 * 性能监控类
 */
export class PerformanceMonitor {
  private metrics: PerformanceMetrics = {
    loadTime: 0,
    renderTime: 0,
    responseTime: 0,
    memoryUsage: 0,
    cpuUsage: 0,
    networkLatency: 0,
    interactionLatency: 0,
    bundleSize: 0
  };

  private startTime: number = 0;
  private markers: Map<string, number> = new Map();

  /**
   * 开始性能监控
   */
  start(): void {
    this.startTime = performance.now();
    this.markers.set('start', this.startTime);
  }

  /**
   * 设置性能标记点
   */
  mark(name: string): void {
    this.markers.set(name, performance.now());
  }

  /**
   * 测量性能指标
   */
  measure(name: string, startMark?: string, endMark?: string): number {
    const start = startMark ? this.markers.get(startMark) : this.startTime;
    const end = this.markers.get(endMark || name) || performance.now();
    
    const duration = end - (start || 0);
    this.markers.set(name, end);
    
    return duration;
  }

  /**
   * 获取当前性能指标
   */
  getMetrics(): PerformanceMetrics {
    return { ...this.metrics };
  }

  /**
   * 更新性能指标
   */
  updateMetrics(updates: Partial<PerformanceMetrics>): void {
    this.metrics = { ...this.metrics, ...updates };
  }

  /**
   * 记录页面加载时间
   */
  recordLoadTime(): void {
    this.metrics.loadTime = this.measure('pageLoad', 'start', 'loadComplete');
  }

  /**
   * 记录渲染时间
   */
  recordRenderTime(): void {
    this.metrics.renderTime = this.measure('renderComplete', 'start', 'renderComplete');
  }

  /**
   * 记录网络响应时间
   */
  recordResponseTime(url: string, startTime: number): void {
    const endTime = performance.now();
    const duration = endTime - startTime;
    this.metrics.responseTime = Math.max(this.metrics.responseTime, duration);
  }

  /**
   * 记录内存使用情况
   */
  recordMemoryUsage(): void {
    if ('memory' in performance) {
      const memory = (performance as any).memory;
      this.metrics.memoryUsage = memory.usedJSHeapSize / 1024 / 1024; // MB
    }
  }

  /**
   * 记录交互延迟
   */
  recordInteractionLatency(element: HTMLElement, startTime: number): void {
    const endTime = performance.now();
    const duration = endTime - startTime;
    this.metrics.interactionLatency = Math.max(this.metrics.interactionLatency, duration);
  }

  /**
   * 获取资源大小
   */
  getBundleSize(): number {
    const scripts = document.querySelectorAll('script[src]');
    const totalSize = Array.from(scripts).reduce((sum, script) => {
      return sum + (script as HTMLScriptElement).src.length;
    }, 0);
    
    this.metrics.bundleSize = totalSize / 1024; // KB
    return this.metrics.bundleSize;
  }

  /**
   * 清理监控数据
   */
  clear(): void {
    this.markers.clear();
    this.startTime = 0;
    this.metrics = {
      loadTime: 0,
      renderTime: 0,
      responseTime: 0,
      memoryUsage: 0,
      cpuUsage: 0,
      networkLatency: 0,
      interactionLatency: 0,
      bundleSize: 0
    };
  }
}

/**
 * 性能基准测试类
 */
export class PerformanceBenchmarkRunner {
  private benchmarks: PerformanceBenchmark[] = [];
  private monitor: PerformanceMonitor;

  constructor() {
    this.monitor = new PerformanceMonitor();
  }

  /**
   * 添加性能基准
   */
  addBenchmark(name: string, threshold: PerformanceMetrics): void {
    this.benchmarks.push({
      name,
      metrics: {
        loadTime: 0,
        renderTime: 0,
        responseTime: 0,
        memoryUsage: 0,
        cpuUsage: 0,
        networkLatency: 0,
        interactionLatency: 0,
        bundleSize: 0
      },
      threshold,
      status: 'pass'
    });
  }

  /**
   * 运行基准测试
   */
  async runBenchmark(name: string, testFn: () => Promise<void> | void): Promise<void> {
    const benchmark = this.benchmarks.find(b => b.name === name);
    if (!benchmark) {
      throw new Error(`Benchmark ${name} not found`);
    }

    // 清理之前的指标
    this.monitor.clear();

    // 开始监控
    this.monitor.start();

    try {
      // 运行测试
      await testFn();

      // 记录指标
      benchmark.metrics = this.monitor.getMetrics();
      
      // 评估状态
      benchmark.status = this.evaluateBenchmark(benchmark);
    } catch (error) {
      console.error(`Benchmark ${name} failed:`, error);
      benchmark.status = 'fail';
    } finally {
      this.monitor.clear();
    }
  }

  /**
   * 评估基准测试结果
   */
  private evaluateBenchmark(benchmark: PerformanceBenchmark): 'pass' | 'fail' | 'warning' {
    const { metrics, threshold } = benchmark;
    
    let hasFailures = false;
    let hasWarnings = false;

    // 检查各个指标
    const checks = [
      { metric: 'loadTime', value: metrics.loadTime, threshold: threshold.loadTime },
      { metric: 'renderTime', value: metrics.renderTime, threshold: threshold.renderTime },
      { metric: 'responseTime', value: metrics.responseTime, threshold: threshold.responseTime },
      { metric: 'memoryUsage', value: metrics.memoryUsage, threshold: threshold.memoryUsage },
      { metric: 'interactionLatency', value: metrics.interactionLatency, threshold: threshold.interactionLatency },
      { metric: 'bundleSize', value: metrics.bundleSize, threshold: threshold.bundleSize }
    ];

    for (const check of checks) {
      if (check.value > check.threshold * 1.5) {
        hasFailures = true;
      } else if (check.value > check.threshold * 1.2) {
        hasWarnings = true;
      }
    }

    if (hasFailures) return 'fail';
    if (hasWarnings) return 'warning';
    return 'pass';
  }

  /**
   * 获取性能报告
   */
  getReport(): PerformanceReport {
    let overallScore = 0;
    let passedCount = 0;
    let warningCount = 0;
    let failCount = 0;

    const recommendations: string[] = [];

    for (const benchmark of this.benchmarks) {
      switch (benchmark.status) {
        case 'pass':
          passedCount++;
          overallScore += 100;
          break;
        case 'warning':
          warningCount++;
          overallScore += 70;
          recommendations.push(`警告: ${benchmark.name} 性能接近阈值`);
          break;
        case 'fail':
          failCount++;
          overallScore += 30;
          recommendations.push(`失败: ${benchmark.name} 性能不达标`);
          break;
      }
    }

    overallScore = Math.round(overallScore / this.benchmarks.length);

    const summary = `性能测试完成: ${passedCount} 个通过, ${warningCount} 个警告, ${failCount} 个失败. 总体评分: ${overallScore}/100`;

    return {
      overallScore,
      benchmarks: this.benchmarks,
      recommendations,
      summary
    };
  }
}

/**
 * 性能优化工具类
 */
export class PerformanceOptimizer {
  private static instance: PerformanceOptimizer;
  private monitor: PerformanceMonitor;

  private constructor() {
    this.monitor = new PerformanceMonitor();
  }

  static getInstance(): PerformanceOptimizer {
    if (!PerformanceOptimizer.instance) {
      PerformanceOptimizer.instance = new PerformanceOptimizer();
    }
    return PerformanceOptimizer.instance;
  }

  /**
   * 图片懒加载优化
   */
  optimizeImageLazyLoading(): void {
    const images = document.querySelectorAll('img[data-src]');
    
    const imageObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = entry.target as HTMLImageElement;
          img.src = img.dataset.src!;
          img.removeAttribute('data-src');
          imageObserver.unobserve(img);
        }
      });
    });

    images.forEach(img => imageObserver.observe(img));
  }

  /**
   * 代码分割优化
   */
  optimizeCodeSplitting(): void {
    if ('requestIdleCallback' in window) {
      const idleCallback = (callback: IdleRequestCallback) => {
        requestIdleCallback(callback);
      };

      // 低优先级加载非关键资源
      const nonCriticalResources = document.querySelectorAll('[data-priority="low"]');
      nonCriticalResources.forEach((element, index) => {
        idleCallback(() => {
          const lazyElement = element as HTMLElement;
          const src = lazyElement.getAttribute('data-src');
          if (src) {
            const script = document.createElement('script');
            script.src = src;
            lazyElement.appendChild(script);
          }
        });
      });
    }
  }

  /**
   * 内存泄漏检测
   */
  detectMemoryLeaks(): void {
    // 检查未清理的事件监听器
    const eventListeners = this.getEventListeners();
    console.log('事件监听器数量:', eventListeners.length);

    // 检查定时器
    const timers = this.getTimers();
    console.log('定时器数量:', timers.length);

    // 检查DOM引用
    const domReferences = this.getDOMReferences();
    console.log('DOM引用数量:', domReferences.length);
  }

  /**
   * 缓存优化
   */
  optimizeCaching(): void {
    // Service Worker 注册
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker.register('/service-worker.js')
        .then(registration => {
          console.log('Service Worker 注册成功:', registration);
        })
        .catch(error => {
          console.error('Service Worker 注册失败:', error);
        });
    }

    // 内存缓存
    const memoryCache = new Map();
    window.memoryCache = memoryCache;
  }

  /**
   * 网络优化
   */
  optimizeNetwork(): void {
    // 预连接关键域名
    const criticalDomains = ['api.example.com', 'cdn.example.com'];
    criticalDomains.forEach(domain => {
      const link = document.createElement('link');
      link.rel = 'preconnect';
      link.href = `https://${domain}`;
      document.head.appendChild(link);
    });

    // DNS 预解析
    criticalDomains.forEach(domain => {
      const link = document.createElement('link');
      link.rel = 'dns-prefetch';
      link.href = `https://${domain}`;
      document.head.appendChild(link);
    });
  }

  /**
   * 渲染优化
   */
  optimizeRendering(): void {
    // 使用 requestAnimationFrame 优化动画
    const optimizedAnimations = new Set();
    
    const animate = (callback: FrameRequestCallback) => {
      return requestAnimationFrame(callback);
    };

    // 节流和防抖
    const throttle = (func: Function, limit: number) => {
      let inThrottle = false;
      return function(this: any, ...args: any[]) {
        if (!inThrottle) {
          func.apply(this, args);
          inThrottle = true;
          setTimeout(() => inThrottle = false, limit);
        }
      };
    };

    const debounce = (func: Function, delay: number) => {
      let timeoutId: NodeJS.Timeout;
      return function(this: any, ...args: any[]) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(this, args), delay);
      };
    };

    window.performanceTools = {
      animate,
      throttle,
      debounce
    };
  }

  /**
   * 获取事件监听器（简化版）
   */
  private getEventListeners(): any[] {
    // 这里简化实现，实际应该使用更复杂的检测逻辑
    return [];
  }

  /**
   * 获取定时器
   */
  private getTimers(): any[] {
    const timers: any[] = [];
    
    // 检查 setInterval
    for (let i = 1; i < 99999; i++) {
      const timer = setInterval(() => {}, 1000);
      clearInterval(timer);
      if (timer._idleNext || timer._idlePrev) {
        timers.push(timer);
      }
    }

    // 检查 setTimeout
    for (let i = 1; i < 99999; i++) {
      const timer = setTimeout(() => {}, 1000);
      clearTimeout(timer);
      if (timer._idleNext || timer._idlePrev) {
        timers.push(timer);
      }
    }

    return timers;
  }

  /**
   * 获取DOM引用
   */
  private getDOMReferences(): any[] {
    const references: any[] = [];
    
    // 检查全局变量中的DOM引用
    const globalKeys = Object.keys(window);
    globalKeys.forEach(key => {
      const value = (window as any)[key];
      if (value && typeof value === 'object' && 
          (value.nodeType === 1 || value.nodeType === 9)) {
        references.push(value);
      }
    });

    return references;
  }
}

/**
 * 性能测试工具函数
 */
export const performanceUtils = {
  /**
   * 测量函数执行时间
   */
  measureTime: <T>(fn: () => T, name: string = 'measurement'): T => {
    const start = performance.now();
    const result = fn();
    const end = performance.now();
    console.log(`${name} 执行时间: ${end - start}ms`);
    return result;
  },

  /**
   * 创建性能测试套件
   */
  createTestSuite: (tests: Array<{ name: string; fn: () => void }>) => {
    const results: Array<{ name: string; passed: boolean; duration: number }> = [];
    
    tests.forEach(test => {
      try {
        const start = performance.now();
        test.fn();
        const end = performance.now();
        results.push({
          name: test.name,
          passed: true,
          duration: end - start
        });
      } catch (error) {
        results.push({
          name: test.name,
          passed: false,
          duration: 0
        });
      }
    });

    return results;
  },

  /**
   * 生成性能报告
   */
  generateReport: (results: Array<{ name: string; passed: boolean; duration: number }>) => {
    const passed = results.filter(r => r.passed).length;
    const failed = results.filter(r => !r.passed).length;
    const totalDuration = results.reduce((sum, r) => sum + r.duration, 0);
    const averageDuration = totalDuration / results.length;

    return {
      total: results.length,
      passed,
      failed,
      totalDuration,
      averageDuration,
      successRate: (passed / results.length) * 100
    };
  }
};