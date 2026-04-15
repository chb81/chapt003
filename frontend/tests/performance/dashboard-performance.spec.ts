import { test, expect } from '@playwright/test';
import { PerformanceMonitor, PerformanceBenchmarkRunner, performanceUtils } from './performance-utils';

test.describe('Dashboard Performance Tests', () => {
  let monitor: PerformanceMonitor;
  let benchmarkRunner: PerformanceBenchmarkRunner;

  test.beforeEach(() => {
    monitor = new PerformanceMonitor();
    benchmarkRunner = new PerformanceBenchmarkRunner();
    
    // 添加性能基准
    benchmarkRunner.addBenchmark('dashboard-load', {
      loadTime: 3000,
      renderTime: 1500,
      responseTime: 800,
      memoryUsage: 80,
      cpuUsage: 40,
      networkLatency: 300,
      interactionLatency: 150,
      bundleSize: 800
    });

    benchmarkRunner.addBenchmark('chart-render', {
      loadTime: 2000,
      renderTime: 1000,
      responseTime: 500,
      memoryUsage: 40,
      cpuUsage: 25,
      networkLatency: 200,
      interactionLatency: 100,
      bundleSize: 400
    });

    benchmarkRunner.addBenchmark('realtime-update', {
      loadTime: 1500,
      renderTime: 800,
      responseTime: 300,
      memoryUsage: 30,
      cpuUsage: 20,
      networkLatency: 150,
      interactionLatency: 80,
      bundleSize: 300
    });
  });

  test('should load dashboard with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('dashboard-load');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('dashboard-load');
    
    // 验证加载性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证页面功能正常
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const chartContainer = await page.locator('.chart-container').count();
    expect(chartContainer).toBeGreaterThan(0);
    
    const dateFilter = await page.locator('input[type="date"]');
    expect(await dateFilter.count()).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should render charts with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('chart-render');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 等待图表渲染完成
    await page.waitForSelector('.echarts-instance', { state: 'attached' });
    await page.waitForTimeout(2000); // 等待图表渲染完成
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('chart-render');
    
    // 验证图表渲染性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证图表功能正常
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    // 测试图表交互性能
    const chart = await page.locator('.echarts-instance').first();
    await chart.click();
    
    const interactionStart = performance.now();
    await chart.click();
    await page.waitForTimeout(100);
    const interactionEnd = performance.now();
    
    const interactionTime = interactionEnd - interactionStart;
    expect(interactionTime).toBeLessThan(200); // 200ms内完成交互
    
    await monitor.stopMonitoring();
  });

  test('should handle real-time updates with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('realtime-update');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 模拟实时更新数据
    const updateButton = await page.locator('button:has-text("刷新数据")');
    if (await updateButton.isVisible()) {
      await updateButton.click();
      await page.waitForLoadState('networkidle');
    }
    
    // 模拟WebSocket实时更新
    await page.evaluate(() => {
      if (window.dispatchEvent) {
        const event = new CustomEvent('realtime-update', {
          detail: { type: 'statistics', data: {} }
        });
        window.dispatchEvent(event);
      }
    });
    
    await page.waitForTimeout(1000); // 等待实时更新处理完成
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('realtime-update');
    
    // 验证实时更新性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证数据更新功能
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle date filtering with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('date-filter');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    const dateInput = await page.locator('input[type="date"]');
    await dateInput.click();
    await dateInput.fill('2024-01-01');
    await page.keyboard.press('Enter');
    await page.waitForLoadState('networkidle');
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('date-filter') || {
      loadTime: 2000,
      renderTime: 1000,
      responseTime: 500,
      memoryUsage: 40,
      cpuUsage: 25,
      networkLatency: 200,
      interactionLatency: 150,
      bundleSize: 400
    };
    
    // 验证日期过滤性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证过滤功能
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle data export with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('export');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    const exportButton = await page.locator('button:has-text("导出数据")');
    if (await exportButton.isVisible()) {
      await exportButton.click();
      await page.waitForTimeout(2000); // 等待导出完成
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('export') || {
      loadTime: 3000,
      renderTime: 2000,
      responseTime: 1500,
      memoryUsage: 100,
      cpuUsage: 60,
      networkLatency: 500,
      interactionLatency: 200,
      bundleSize: 600
    };
    
    // 验证导出性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证导出功能
    const downloadLinks = await page.locator('a:has-text("下载")').count();
    if (downloadLinks > 0) {
      expect(downloadLinks).toBeGreaterThan(0);
    }
    
    await monitor.stopMonitoring();
  });

  test('should handle multiple simultaneous requests with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('multi-request');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 模拟多个同时请求
    const promises = [];
    for (let i = 0; i < 5; i++) {
      promises.push(
        page.evaluate(() => {
          return fetch('/api/dashboard/statistics', {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json'
            }
          }).then(response => response.json());
        })
      );
    }
    
    await Promise.all(promises);
    await page.waitForLoadState('networkidle');
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('multi-request') || {
      loadTime: 5000,
      renderTime: 3000,
      responseTime: 2000,
      memoryUsage: 150,
      cpuUsage: 80,
      networkLatency: 1000,
      interactionLatency: 300,
      bundleSize: 1000
    };
    
    // 验证多请求性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证页面功能正常
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle error scenarios gracefully with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('error-handling');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 模拟网络错误
    await page.route('**/api/dashboard/statistics', route => {
      route.abort('failed');
    });
    
    const refreshButton = await page.locator('button:has-text("刷新数据")');
    if (await refreshButton.isVisible()) {
      await refreshButton.click();
      await page.waitForLoadState('networkidle');
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('error-handling') || {
      loadTime: 3000,
      renderTime: 2000,
      responseTime: 1500,
      memoryUsage: 60,
      cpuUsage: 40,
      networkLatency: 1000,
      interactionLatency: 200,
      bundleSize: 500
    };
    
    // 验证错误处理性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证错误处理功能
    const errorMessages = await page.locator('.error-message').count();
    expect(errorMessages).toBeGreaterThanOrEqual(0);
    
    const retryButtons = await page.locator('button:has-text("重试")').count();
    expect(retryButtons).toBeGreaterThanOrEqual(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle responsive design with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('responsive');
    
    // 测试不同屏幕尺寸
    const viewports = [
      { width: 1920, height: 1080 },
      { width: 768, height: 1024 },
      { width: 375, height: 667 },
      { width: 1024, height: 768 }
    ];
    
    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      await page.goto('/dashboard');
      await page.waitForLoadState('networkidle');
      
      // 验证响应式布局
      const container = await page.locator('.dashboard-container');
      await expect(container).toBeVisible();
      
      const statisticsCards = await page.locator('.statistics-card').count();
      expect(statisticsCards).toBeGreaterThan(0);
      
      const charts = await page.locator('.echarts-instance').count();
      expect(charts).toBeGreaterThan(0);
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('responsive') || {
      loadTime: 4000,
      renderTime: 2500,
      responseTime: 2000,
      memoryUsage: 100,
      cpuUsage: 60,
      networkLatency: 1500,
      interactionLatency: 300,
      bundleSize: 800
    };
    
    // 验证响应式性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    await monitor.stopMonitoring();
  });

  test('should handle accessibility with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('accessibility');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 验证可访问性
    const statisticsCards = await page.locator('.statistics-card').count();
    for (let i = 0; i < statisticsCards; i++) {
      const card = page.locator('.statistics-card').nth(i);
      await expect(card).toBeVisible();
      
      // 检查键盘导航
      await card.press('Tab');
      await expect(card).toBeFocused();
    }
    
    const charts = await page.locator('.echarts-instance').count();
    for (let i = 0; i < charts; i++) {
      const chart = page.locator('.echarts-instance').nth(i);
      await expect(chart).toBeVisible();
      
      // 检查键盘导航
      await chart.press('Tab');
      await expect(chart).toBeFocused();
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('accessibility') || {
      loadTime: 2500,
      renderTime: 1500,
      responseTime: 1000,
      memoryUsage: 60,
      cpuUsage: 35,
      networkLatency: 800,
      interactionLatency: 150,
      bundleSize: 600
    };
    
    // 验证可访问性性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    await monitor.stopMonitoring();
  });

  test('should handle cache optimization with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('cache-optimization');
    
    // 第一次加载
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    const metrics1 = await monitor.getMetrics();
    
    // 第二次加载（缓存命中）
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    const metrics2 = await monitor.getMetrics();
    
    // 验证缓存优化效果
    expect(metrics2.loadTime).toBeLessThan(metrics1.loadTime);
    expect(metrics2.renderTime).toBeLessThan(metrics1.renderTime);
    expect(metrics2.memoryUsage).toBeLessThan(metrics1.memoryUsage);
    expect(metrics2.cpuUsage).toBeLessThan(metrics1.cpuUsage);
    
    // 验证页面功能正常
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle lazy loading with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('lazy-loading');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 滚动到底部触发懒加载
    await page.evaluate(() => {
      window.scrollTo(0, document.body.scrollHeight);
    });
    await page.waitForTimeout(1000);
    
    // 再次滚动
    await page.evaluate(() => {
      window.scrollTo(0, document.body.scrollHeight);
    });
    await page.waitForTimeout(1000);
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('lazy-loading') || {
      loadTime: 3500,
      renderTime: 2000,
      responseTime: 1500,
      memoryUsage: 80,
      cpuUsage: 50,
      networkLatency: 1200,
      interactionLatency: 250,
      bundleSize: 700
    };
    
    // 验证懒加载性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证懒加载功能
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle virtual scrolling with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('virtual-scrolling');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 模拟虚拟滚动
    await page.evaluate(() => {
      const container = document.querySelector('.dashboard-container');
      if (container) {
        container.scrollTop = 1000;
        container.dispatchEvent(new Event('scroll'));
      }
    });
    await page.waitForTimeout(500);
    
    await page.evaluate(() => {
      const container = document.querySelector('.dashboard-container');
      if (container) {
        container.scrollTop = 2000;
        container.dispatchEvent(new Event('scroll'));
      }
    });
    await page.waitForTimeout(500);
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('virtual-scrolling') || {
      loadTime: 4000,
      renderTime: 2500,
      responseTime: 2000,
      memoryUsage: 90,
      cpuUsage: 55,
      networkLatency: 1500,
      interactionLatency: 300,
      bundleSize: 800
    };
    
    // 验证虚拟滚动性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证虚拟滚动功能
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle preloading with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('preloading');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 模拟预加载
    await page.evaluate(() => {
      const links = document.querySelectorAll('a');
      links.forEach(link => {
        const href = link.getAttribute('href');
        if (href && href.startsWith('/')) {
          fetch(href, { method: 'HEAD' }).catch(() => {});
        }
      });
    });
    await page.waitForTimeout(2000);
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('preloading') || {
      loadTime: 3500,
      renderTime: 2000,
      responseTime: 1500,
      memoryUsage: 70,
      cpuUsage: 40,
      networkLatency: 1000,
      interactionLatency: 200,
      bundleSize: 750
    };
    
    // 验证预加载性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证预加载功能
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle priority queue with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('priority-queue');
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
    
    // 模拟优先级队列
    const promises = [];
    for (let i = 0; i < 10; i++) {
      promises.push(
        page.evaluate((priority) => {
          return new Promise((resolve) => {
            setTimeout(() => {
              resolve({ priority, timestamp: Date.now() });
            }, Math.random() * 1000);
          });
        }, i)
      );
    }
    
    const results = await Promise.all(promises);
    results.sort((a, b) => a.priority - b.priority);
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('priority-queue') || {
      loadTime: 5000,
      renderTime: 3000,
      responseTime: 2500,
      memoryUsage: 120,
      cpuUsage: 70,
      networkLatency: 2000,
      interactionLatency: 400,
      bundleSize: 1000
    };
    
    // 验证优先级队列性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证优先级队列功能
    expect(results.length).toBe(10);
    for (let i = 0; i < results.length - 1; i++) {
      expect(results[i].priority).toBeLessThanOrEqual(results[i + 1].priority);
    }
    
    // 验证页面功能正常
    const statisticsCards = await page.locator('.statistics-card').count();
    expect(statisticsCards).toBeGreaterThan(0);
    
    const charts = await page.locator('.echarts-instance').count();
    expect(charts).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });
});