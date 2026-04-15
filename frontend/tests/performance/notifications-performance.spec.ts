import { test, expect } from '@playwright/test';
import { PerformanceMonitor, PerformanceBenchmarkRunner, performanceUtils } from './performance-utils';

test.describe('Notifications Performance Tests', () => {
  let monitor: PerformanceMonitor;
  let benchmarkRunner: PerformanceBenchmarkRunner;

  test.beforeEach(() => {
    monitor = new PerformanceMonitor();
    benchmarkRunner = new PerformanceBenchmarkRunner();
    
    // 添加性能基准
    benchmarkRunner.addBenchmark('notifications-load', {
      loadTime: 2000,
      renderTime: 1000,
      responseTime: 500,
      memoryUsage: 50,
      cpuUsage: 30,
      networkLatency: 200,
      interactionLatency: 100,
      bundleSize: 500
    });

    benchmarkRunner.addBenchmark('realtime-update', {
      loadTime: 1000,
      renderTime: 500,
      responseTime: 200,
      memoryUsage: 20,
      cpuUsage: 15,
      networkLatency: 100,
      interactionLatency: 50,
      bundleSize: 200
    });
  });

  test.afterEach(() => {
    const report = benchmarkRunner.getReport();
    console.log('通知性能报告:', report);
  });

  test('should measure notifications loading performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    // 开始性能监控
    monitor.start();

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 记录页面加载完成
    monitor.mark('loadComplete');

    // 运行基准测试
    await benchmarkRunner.runBenchmark('notifications-load', async () => {
      await page.goto('/notifications');
      await page.waitForLoadState('networkidle');
    });

    // 验证关键性能指标
    const metrics = monitor.getMetrics();
    expect(metrics.loadTime).toBeLessThan(3000);
    expect(metrics.renderTime).toBeLessThan(2000);
    expect(metrics.responseTime).toBeLessThan(1000);
  });

  test('should measure real-time notification performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 模拟实时通知
    const updateTimes: number[] = [];
    
    for (let i = 0; i < 20; i++) {
      const startTime = performance.now();
      
      // 模拟实时通知
      await page.evaluate(() => {
        return new Promise(resolve => {
          setTimeout(() => {
            const event = new CustomEvent('notification-update', {
              detail: {
                type: 'realtime',
                notifications: [
                  {
                    id: `realtime-${i}`,
                    title: `实时通知 ${i}`,
                    message: `这是实时通知 ${i} 的内容`,
                    type: 'info',
                    timestamp: new Date().toISOString(),
                    read: false
                  }
                ]
              }
            });
            window.dispatchEvent(event);
            resolve(null);
          }, 50);
        });
      });

      await page.waitForEvent('notification-update');
      
      const endTime = performance.now();
      updateTimes.push(endTime - startTime);
    }

    const avgUpdateTime = updateTimes.reduce((sum, time) => sum + time, 0) / updateTimes.length;
    console.log(`平均实时更新时间: ${avgUpdateTime}ms`);
    expect(avgUpdateTime).toBeLessThan(200);

    // 运行基准测试
    await benchmarkRunner.runBenchmark('realtime-update', async () => {
      await page.evaluate(() => {
        return new Promise(resolve => {
          setTimeout(() => {
            const event = new CustomEvent('notification-update', {
              detail: {
                type: 'realtime',
                notifications: [
                  {
                    id: 'test',
                    title: '测试通知',
                    message: '测试通知内容',
                    type: 'info',
                    timestamp: new Date().toISOString(),
                    read: false
                  }
                ]
              }
            });
            window.dispatchEvent(event);
            resolve(null);
          }, 50);
        });
      });
    });
  });

  test('should measure notification filtering performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 创建大量通知数据
    await page.evaluate(() => {
      const mockNotifications = [];
      const types = ['info', 'success', 'warning', 'error'];
      const categories = ['系统', '用户', '项目', '报告', '审批'];
      
      for (let i = 0; i < 2000; i++) {
        mockNotifications.push({
          id: `notification-${i}`,
          title: `通知标题 ${i}`,
          message: `这是通知 ${i} 的详细内容，包含大量文本信息来测试过滤性能。`.repeat(2),
          type: types[Math.floor(Math.random() * types.length)],
          category: categories[Math.floor(Math.random() * categories.length)],
          priority: Math.floor(Math.random() * 5) + 1,
          timestamp: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
          read: Math.random() > 0.5,
          action: Math.random() > 0.3 ? 'view' : null,
          metadata: {
            source: 'system',
            targetId: Math.floor(Math.random() * 100),
            relatedId: Math.floor(Math.random() * 100)
          }
        });
      }
      window.mockNotifications = mockNotifications;
    });

    // 测试各种过滤器性能
    const filterTests = [
      { name: 'type-filter', action: () => page.click('.type-filter button') },
      { name: 'category-filter', action: () => page.click('.category-filter button') },
      { name: 'priority-filter', action: () => page.click('.priority-filter button') },
      { name: 'read-status-filter', action: () => page.click('.read-filter button') },
      { name: 'date-filter', action: () => page.click('.date-filter button') }
    ];

    for (const filterTest of filterTests) {
      const startTime = performance.now();
      await filterTest.action();
      await page.waitForLoadState('networkidle');
      const endTime = performance.now();
      
      const filterTime = endTime - startTime;
      console.log(`${filterTest.name} 响应时间: ${filterTime}ms`);
      expect(filterTime).toBeLessThan(1000);
    }
  });

  test('should measure notification interaction performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 创建测试数据
    await page.evaluate(() => {
      const mockData = [];
      for (let i = 0; i < 100; i++) {
        mockData.push({
          id: `notification-${i}`,
          title: `通知 ${i}`,
          message: `这是通知 ${i} 的内容`,
          type: ['info', 'success', 'warning', 'error'][Math.floor(Math.random() * 4)],
          timestamp: new Date().toISOString(),
          read: false
        });
      }
      window.mockNotifications = mockData;
    });

    // 测试各种交互操作
    const interactions = [
      { name: 'mark-as-read', action: () => page.click('.mark-read-button') },
      { name: 'delete-notification', action: () => page.click('.delete-button') },
      { name: 'view-details', action: () => page.click('.view-details-button') },
      { name: 'dismiss-notification', action: () => page.click('.dismiss-button') },
      { name: 'select-all', action: () => page.click('.select-all-button') }
    ];

    for (const interaction of interactions) {
      const startTime = performance.now();
      await interaction.action;
      await page.waitForTimeout(100);
      const endTime = performance.now();
      
      const interactionTime = endTime - startTime;
      console.log(`${interaction.name} 交互时间: ${interactionTime}ms`);
      expect(interactionTime).toBeLessThan(300);
    }
  });

  test('should measure notification batch operations performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 创建批量操作数据
    await page.evaluate(() => {
      const mockData = [];
      for (let i = 0; i < 500; i++) {
        mockData.push({
          id: `notification-${i}`,
          title: `通知 ${i}`,
          message: `这是通知 ${i} 的内容`,
          type: 'info',
          timestamp: new Date().toISOString(),
          read: false
        });
      }
      window.mockNotifications = mockData;
    });

    // 测试批量操作
    const batchOperations = [
      { name: 'batch-mark-read', action: () => page.click('.batch-mark-read') },
      { name: 'batch-delete', action: () => page.click('.batch-delete') },
      { name: 'batch-archive', action: () => page.click('.batch-archive') },
      { name: 'batch-select', action: () => page.click('.batch-select-all') }
    ];

    for (const operation of batchOperations) {
      const startTime = performance.now();
      await operation.action;
      await page.waitForLoadState('networkidle');
      const endTime = performance.now();
      
      const operationTime = endTime - startTime;
      console.log(`${operation.name} 批量操作时间: ${operationTime}ms`);
      expect(operationTime).toBeLessThan(2000);
    }
  });

  test('should measure notification pagination performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 创建分页数据
    await page.evaluate(() => {
      const mockData = [];
      for (let i = 0; i < 5000; i++) {
        mockData.push({
          id: `notification-${i}`,
          title: `通知 ${i}`,
          message: `这是通知 ${i} 的内容，包含大量文本信息来测试分页性能。`.repeat(3),
          type: ['info', 'success', 'warning', 'error'][Math.floor(Math.random() * 4)],
          timestamp: new Date(Date.now() - i * 60 * 60 * 1000).toISOString(),
          read: Math.random() > 0.5
        });
      }
      window.mockNotifications = mockData;
    });

    // 测试分页导航
    const pageNumbers = [1, 5, 10, 20, 50];
    
    for (const pageNum of pageNumbers) {
      const startTime = performance.now();
      await page.click(`[data-testid="page-${pageNum}"]`);
      await page.waitForLoadState('networkidle');
      const endTime = performance.now();
      
      const paginationTime = endTime - startTime;
      console.log(`第${pageNum}页加载时间: ${paginationTime}ms`);
      expect(paginationTime).toBeLessThan(1500);
    }
  });

  test('should measure notification search performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 创建搜索数据
    await page.evaluate(() => {
      const mockData = [];
      const keywords = ['系统', '用户', '项目', '报告', '审批', '通知', '消息', '提醒'];
      
      for (let i = 0; i < 1000; i++) {
        const randomKeyword = keywords[Math.floor(Math.random() * keywords.length)];
        mockData.push({
          id: `notification-${i}`,
          title: `${randomKeyword}通知 ${i}`,
          message: `这是关于${randomKeyword}的通知内容，包含大量文本信息。`.repeat(2),
          type: 'info',
          timestamp: new Date().toISOString(),
          read: false
        });
      }
      window.mockNotifications = mockData;
    });

    // 测试搜索功能
    const searchInputs = [
      '系统',
      '用户',
      '项目',
      '报告',
      '审批'
    ];

    for (const searchTerm of searchInputs) {
      const startTime = performance.now();
      
      const searchInput = await page.$('.search-input input');
      if (searchInput) {
        await searchInput.fill(searchTerm);
        await searchInput.press('Enter');
        await page.waitForLoadState('networkidle');
      }
      
      const endTime = performance.now();
      const searchTime = endTime - startTime;
      console.log(`搜索"${searchTerm}"时间: ${searchTime}ms`);
      expect(searchTime).toBeLessThan(1500);
    }
  });

  test('should measure notification websocket performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 模拟WebSocket连接
    await page.evaluate(() => {
      window.socketMessages = [];
      
      const mockSocket = {
        readyState: 1,
        send: (data: string) => {
          const message = JSON.parse(data);
          window.socketMessages.push(message);
        },
        close: () => {},
        onmessage: null as ((event: MessageEvent) => void) | null,
        onopen: null as (() => void) | null,
        onclose: null as (() => void) | null,
        onerror: null as ((event: Event) => void) | null
      };

      // 模拟接收消息
      setInterval(() => {
        if (mockSocket.readyState === 1) {
          const mockMessage = {
            type: 'notification',
            data: {
              id: `ws-${Date.now()}`,
              title: 'WebSocket通知',
              message: '这是通过WebSocket接收的实时通知',
              timestamp: new Date().toISOString(),
              read: false
            }
          };
          
          if (mockSocket.onmessage) {
            mockSocket.onmessage({ data: JSON.stringify(mockMessage) });
          }
        }
      }, 1000);

      window.mockWebSocket = mockSocket;
    });

    // 测试WebSocket消息处理性能
    const messageTimes: number[] = [];
    
    for (let i = 0; i < 10; i++) {
      const startTime = performance.now();
      
      await page.evaluate(() => {
        const mockMessage = {
          type: 'notification',
          data: {
            id: `test-${i}`,
            title: '测试通知',
            message: '这是测试WebSocket消息',
            timestamp: new Date().toISOString(),
            read: false
          }
        };
        
        if (window.mockWebSocket.onmessage) {
          window.mockWebSocket.onmessage({ data: JSON.stringify(mockMessage) });
        }
      });

      await page.waitForTimeout(100);
      const endTime = performance.now();
      
      messageTimes.push(endTime - startTime);
    }

    const avgMessageTime = messageTimes.reduce((sum, time) => sum + time, 0) / messageTimes.length;
    console.log(`平均WebSocket消息处理时间: ${avgMessageTime}ms`);
    expect(avgMessageTime).toBeLessThan(150);
  });

  test('should measure notification memory usage', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    // 记录初始内存使用
    let initialMemory = 0;
    await page.evaluate(() => {
      if ('memory' in performance) {
        initialMemory = (performance as any).memory.usedJSHeapSize;
      }
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 创建大量数据
    await page.evaluate(() => {
      const largeData = [];
      for (let i = 0; i < 3000; i++) {
        largeData.push({
          id: `notification-${i}`,
          title: `通知标题 ${i}`,
          message: `这是通知 ${i} 的详细内容，包含大量文本信息来测试内存使用情况。`.repeat(5),
          type: ['info', 'success', 'warning', 'error'][Math.floor(Math.random() * 4)],
          timestamp: new Date().toISOString(),
          read: Math.random() > 0.5,
          metadata: {
            source: 'system',
            targetId: Math.floor(Math.random() * 100),
            relatedId: Math.floor(Math.random() * 100),
            priority: Math.floor(Math.random() * 5) + 1,
            category: ['系统', '用户', '项目', '报告', '审批'][Math.floor(Math.random() * 5)]
          }
        });
      }
      window.largeNotifications = largeData;
    });

    // 记录加载后的内存使用
    let loadedMemory = 0;
    await page.evaluate(() => {
      if ('memory' in performance) {
        loadedMemory = (performance as any).memory.usedJSHeapSize;
      }
    });

    const memoryIncrease = (loadedMemory - initialMemory) / 1024 / 1024; // MB
    console.log(`内存增长: ${memoryIncrease}MB`);
    expect(memoryIncrease).toBeLessThan(80); // 80MB 内存增长阈值

    // 测试滚动时的内存使用
    const container = await page.$('.notifications-container');
    if (container) {
      await container.scrollBy(0, 2000);
      await page.waitForTimeout(1000);

      let scrollMemory = 0;
      await page.evaluate(() => {
        if ('memory' in performance) {
          scrollMemory = (performance as any).memory.usedJSHeapSize;
        }
      });

      const scrollMemoryIncrease = (scrollMemory - initialMemory) / 1024 / 1024;
      expect(scrollMemoryIncrease).toBeLessThan(120); // 120MB 滚动后内存增长
    }
  });

  test('should measure notification bundle size and resource loading', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 测试初始页面加载
    const response = await page.request('http://localhost:5173/notifications');
    const content = await response.text();
    
    // 估算bundle大小
    const estimatedBundleSize = content.length / 1024; // KB
    console.log(`估算Bundle大小: ${estimatedBundleSize}KB`);
    expect(estimatedBundleSize).toBeLessThan(600); // 600KB bundle大小阈值

    // 测试资源加载
    const resources = await page.evaluate(() => {
      const resources = {
        scripts: [] as string[],
        styles: [] as string[],
        images: [] as string[],
        websockets: [] as string[]
      };

      document.querySelectorAll('script[src]').forEach(script => {
        resources.scripts.push((script as HTMLScriptElement).src);
      });

      document.querySelectorAll('link[href]').forEach(link => {
        if (link.rel.includes('stylesheet')) {
          resources.styles.push((link as HTMLLinkElement).href);
        }
      });

      document.querySelectorAll('img[src]').forEach(img => {
        resources.images.push((img as HTMLImageElement).src);
      });

      return resources;
    });

    // 验证资源数量
    expect(resources.scripts.length).toBeLessThan(12);
    expect(resources.styles.length).toBeLessThan(6);
    expect(resources.images.length).toBeLessThan(15);

    // 测试资源加载时间
    const resourceLoadTimes = await page.evaluate(() => {
      const perf = window.performance as any;
      if (perf && perf.getEntriesByType) {
        const entries = perf.getEntriesByType('resource');
        return entries
          .filter(entry => entry.name.includes('/notifications'))
          .map(entry => ({
            name: entry.name,
            duration: entry.duration,
            size: entry.transferSize
          }));
      }
      return [];
    });

    const avgResourceLoadTime = resourceLoadTimes.reduce((sum, entry) => sum + entry.duration, 0) / resourceLoadTimes.length;
    console.log(`平均资源加载时间: ${avgResourceLoadTime}ms`);
    expect(avgResourceLoadTime).toBeLessThan(600); // 600ms 平均资源加载时间
  });

  test('should measure notification error handling performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    // 模拟API错误
    await page.route('**/api/notifications', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Server Error' })
      });
    });

    // 测试错误处理时间
    const startTime = performance.now();
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');
    const endTime = performance.now();

    const errorHandlingTime = endTime - startTime;
    console.log(`错误处理时间: ${errorHandlingTime}ms`);
    expect(errorHandlingTime).toBeLessThan(5000);

    // 验证错误UI显示
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('加载失败');

    // 测试错误恢复
    const recoveryStart = performance.now();
    await page.click('[data-testid="retry-button"]');
    await page.waitForLoadState('networkidle');
    const recoveryEnd = performance.now();

    const recoveryTime = recoveryEnd - recoveryStart;
    console.log(`恢复时间: ${recoveryTime}ms`);
    expect(recoveryTime).toBeLessThan(4000);
  });

  test('should measure notification responsive design performance', async ({ page }) => {
    // 测试桌面端性能
    await page.setViewportSize({ width: 1920, height: 1080 });
    
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    const desktopLoadTime = await page.evaluate(() => {
      const perf = window.performance as any;
      return perf.timing.loadEventEnd - perf.timing.navigationStart;
    });

    // 测试移动端性能
    await page.setViewportSize({ width: 375, height: 667 });
    
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    const mobileLoadTime = await page.evaluate(() => {
      const perf = window.performance as any;
      return perf.timing.loadEventEnd - perf.timing.navigationStart;
    });

    // 验证移动端性能
    expect(mobileLoadTime).toBeLessThan(desktopLoadTime * 1.5); // 移动端加载时间不应超过桌面端1.5倍

    // 测试移动端交互性能
    const mobileInteractionStart = performance.now();
    await page.click('.mobile-menu-toggle');
    const mobileInteractionEnd = performance.now();
    
    const mobileInteractionTime = mobileInteractionEnd - mobileInteractionStart;
    expect(mobileInteractionTime).toBeLessThan(300); // 300ms 移动端交互时间
  });

  test('should generate comprehensive notification performance report', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    // 创建性能测试套件
    const testResults = performanceUtils.createTestSuite([
      {
        name: 'notifications-load',
        fn: async () => {
          await page.goto('/notifications');
          await page.waitForLoadState('networkidle');
        }
      },
      {
        name: 'realtime-update',
        fn: async () => {
          await page.evaluate(() => {
            return new Promise(resolve => {
              setTimeout(() => {
                const event = new CustomEvent('notification-update');
                window.dispatchEvent(event);
                resolve(null);
              }, 50);
            });
          });
        }
      },
      {
        name: 'filter-performance',
        fn: async () => {
          await page.click('.type-filter button');
          await page.click('.category-filter button');
          await page.waitForLoadState('networkidle');
        }
      },
      {
        name: 'pagination-performance',
        fn: async () => {
          await page.click('[data-testid="page-2"]');
          await page.waitForLoadState('networkidle');
        }
      },
      {
        name: 'websocket-performance',
        fn: async () => {
          await page.evaluate(() => {
            const mockMessage = {
              type: 'notification',
              data: {
                id: 'test',
                title: '测试通知',
                message: '测试内容',
                timestamp: new Date().toISOString(),
                read: false
              }
            };
            
            if (window.mockWebSocket && window.mockWebSocket.onmessage) {
              window.mockWebSocket.onmessage({ data: JSON.stringify(mockMessage) });
            }
          });
        }
      }
    ]);

    const report = performanceUtils.generateReport(testResults);
    
    console.log('通知性能测试报告:', report);
    
    // 验证报告完整性
    expect(report.total).toBe(5);
    expect(report.passed).toBeGreaterThanOrEqual(4);
    expect(report.failed).toBeLessThanOrEqual(1);
    expect(report.successRate).toBeGreaterThan(80);
    expect(report.averageDuration).toBeLessThan(2000);
  });
});

test.describe('Notifications Performance Optimization Tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    // 注入性能优化工具
    await page.evaluate(() => {
      window.performanceOptimizer = {
        optimizeVirtualScrolling: () => {
          const container = document.querySelector('.notifications-container');
          if (container) {
            container.style.height = '600px';
            container.style.overflowY = 'auto';
            
            const itemHeight = 80;
            const visibleItems = Math.ceil(container.clientHeight / itemHeight);
            
            const renderVisibleItems = () => {
              const scrollTop = container.scrollTop;
              const startIndex = Math.floor(scrollTop / itemHeight);
              const endIndex = Math.min(startIndex + visibleItems + 5, window.mockNotifications.length);
              
              container.innerHTML = '';
              
              for (let i = startIndex; i < endIndex; i++) {
                const item = window.mockNotifications[i];
                const itemElement = document.createElement('div');
                itemElement.className = 'notification-item';
                itemElement.style.height = `${itemHeight}px`;
                itemElement.innerHTML = `
                  <div class="notification-header">
                    <span class="notification-type">${item.type}</span>
                    <span class="notification-time">${new Date(item.timestamp).toLocaleTimeString()}</span>
                  </div>
                  <div class="notification-title">${item.title}</div>
                  <div class="notification-message">${item.message}</div>
                `;
                container.appendChild(itemElement);
              }
            };
            
            container.addEventListener('scroll', renderVisibleItems);
            renderVisibleItems();
          }
        },
        
        optimizeWebSocketReconnection: () => {
          const reconnectAttempts = 0;
          const maxReconnectAttempts = 5;
          const reconnectDelay = 1000;
          
          const attemptReconnection = () => {
            if (reconnectAttempts < maxReconnectAttempts) {
              setTimeout(() => {
                console.log(`尝试重新连接 WebSocket... (尝试 ${reconnectAttempts + 1}/${maxReconnectAttempts})`);
                // 模拟重连逻辑
              }, reconnectDelay * Math.pow(2, reconnectAttempts));
            }
          };
          
          window.attemptWebSocketReconnection = attemptReconnection;
        },
        
        optimizeNotificationCaching: () => {
          const notificationCache = new Map();
          const maxCacheSize = 1000;
          
          window.cacheNotification = (notification: any) => {
            const cacheKey = notification.id;
            notificationCache.set(cacheKey, notification);
            
            // 限制缓存大小
            if (notificationCache.size > maxCacheSize) {
              const oldestKey = notificationCache.keys().next().value;
              notificationCache.delete(oldestKey);
            }
          };
          
          window.getNotificationFromCache = (id: string) => {
            return notificationCache.get(id);
          };
        },
        
        optimizeNotificationBatching: () => {
          let pendingNotifications: any[] = [];
          let batchTimer: NodeJS.Timeout;
          
          window.batchNotifications = (notifications: any[], delay: number = 100) => {
            pendingNotifications.push(...notifications);
            
            clearTimeout(batchTimer);
            batchTimer = setTimeout(() => {
              const batched = [...pendingNotifications];
              pendingNotifications = [];
              
              // 批量处理通知
              console.log(`批量处理 ${batched.length} 个通知`);
              return batched;
            }, delay);
          };
        }
      };
    });
  });

  test('should implement virtual scrolling for notification lists', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 创建大量数据
    await page.evaluate(() => {
      const largeData = [];
      for (let i = 0; i < 5000; i++) {
        largeData.push({
          id: `notification-${i}`,
          title: `通知 ${i}`,
          message: `这是通知 ${i} 的内容`,
          type: 'info',
          timestamp: new Date().toISOString(),
          read: false
        });
      }
      window.mockNotifications = largeData;
    });

    // 实现虚拟滚动
    await page.evaluate(() => {
      (window as any).performanceOptimizer.optimizeVirtualScrolling();
    });

    // 验证虚拟滚动功能
    await page.waitForTimeout(1000);
    
    const container = await page.$('.notifications-container');
    expect(container).toBeTruthy();

    // 滚动并验证渲染
    await container!.scrollTo({ top: 2000 });
    await page.waitForTimeout(100);

    const visibleItems = await page.$$('.notification-item');
    expect(visibleItems.length).toBeGreaterThan(0);
    expect(visibleItems.length).toBeLessThan(50); // 应该只渲染可见项目
  });

  test('should implement WebSocket reconnection optimization', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 实现WebSocket重连优化
    await page.evaluate(() => {
      (window as any).performanceOptimizer.optimizeWebSocketReconnection();
    });

    // 测试重连功能
    await page.evaluate(() => {
      (window as any).attemptWebSocketReconnection();
    });

    // 验证重连逻辑
    const reconnectLog = await page.evaluate(() => {
      return (window as any).reconnectLog || [];
    });

    console.log('WebSocket重连日志:', reconnectLog);
    expect(reconnectLog.length).toBeGreaterThan(0);
  });

  test('should implement notification caching optimization', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 实现缓存优化
    await page.evaluate(() => {
      (window as any).performanceOptimizer.optimizeNotificationCaching();
    });

    // 添加测试数据到缓存
    await page.evaluate(() => {
      const testNotifications = [
        { id: '1', title: '测试通知1', message: '内容1' },
        { id: '2', title: '测试通知2', message: '内容2' },
        { id: '3', title: '测试通知3', message: '内容3' }
      ];
      
      testNotifications.forEach(notification => {
        (window as any).cacheNotification(notification);
      });
    });

    // 测试缓存功能
    const cachedNotification = await page.evaluate(() => {
      return (window as any).getNotificationFromCache('2');
    });

    expect(cachedNotification).toBeTruthy();
    expect(cachedNotification.title).toBe('测试通知2');
  });

  test('should implement notification batching optimization', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 实现批量优化
    await page.evaluate(() => {
      (window as any).performanceOptimizer.optimizeNotificationBatching();
    });

    // 测试批量处理
    const testNotifications = [];
    for (let i = 0; i < 10; i++) {
      testNotifications.push({
        id: `batch-${i}`,
        title: `批量通知 ${i}`,
        message: `批量内容 ${i}`,
        timestamp: new Date().toISOString()
      });
    }

    await page.evaluate((notifications: any[]) => {
      return (window as any).batchNotifications(notifications, 100);
    }, testNotifications);

    // 等待批量处理完成
    await page.waitForTimeout(200);

    // 验证批量处理结果
    const batchLog = await page.evaluate(() => {
      return (window as any).batchLog || [];
    });

    console.log('批量处理日志:', batchLog);
    expect(batchLog.length).toBeGreaterThan(0);
  });

  test('should implement notification preloading', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 实现预加载
    await page.evaluate(() => {
      window.preloadNotifications = async (page: number, limit: number = 20) => {
        // 使用 prefetch API 预加载下一页
        const nextPage = page + 1;
        const link = document.createElement('link');
        link.rel = 'prefetch';
        link.href = `/api/notifications?page=${nextPage}&limit=${limit}`;
        document.head.appendChild(link);
        
        // 预加载更多数据
        const preloadData = [];
        const startIndex = (nextPage - 1) * limit;
        const endIndex = startIndex + limit * 2; // 预加载两页
        
        for (let i = startIndex; i < endIndex && i < window.mockNotifications.length; i++) {
          preloadData.push(window.mockNotifications[i]);
        }
        
        return preloadData;
      };
    });

    // 触发预加载
    await page.evaluate(() => {
      return (window as any).preloadNotifications(1, 20);
    });

    // 验证预加载链接
    const prefetchLinks = await page.$$('link[rel="prefetch"]');
    expect(prefetchLinks.length).toBeGreaterThan(0);

    // 测试预加载后的性能
    const startTime = performance.now();
    await page.click('[data-testid="next-page"]');
    await page.waitForLoadState('networkidle');
    const endTime = performance.now();

    const preloadTime = endTime - startTime;
    console.log(`预加载后分页时间: ${preloadTime}ms`);
    expect(preloadTime).toBeLessThan(800);
  });

  test('should implement notification debouncing and throttling', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 实现防抖和节流
    await page.evaluate(() => {
      const debounce = (func: Function, delay: number) => {
        let timeoutId: NodeJS.Timeout;
        return function(this: any, ...args: any[]) {
          clearTimeout(timeoutId);
          timeoutId = setTimeout(() => func.apply(this, args), delay);
        };
      };

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

      window.debouncedSearch = debounce((query: string) => {
        console.log('执行搜索:', query);
      }, 300);

      window.throttledUpdate = throttle((notifications: any[]) => {
        console.log('更新通知:', notifications.length);
      }, 200);

      window.searchCallCount = 0;
      window.updateCallCount = 0;

      window.originalSearch = (query: string) => {
        window.searchCallCount++;
        console.log('原始搜索:', query);
      };

      window.originalUpdate = (notifications: any[]) => {
        window.updateCallCount++;
        console.log('原始更新:', notifications.length);
      };
    });

    // 测试搜索防抖
    const searchInput = await page.$('.search-input input');
    if (searchInput) {
      for (let i = 0; i < 10; i++) {
        await searchInput.type(`test${i}`);
        await page.waitForTimeout(20);
      }
      await page.waitForTimeout(350);

      // 验证防抖效果
      const searchCallCount = await page.evaluate(() => {
        return (window as any).searchCallCount;
      });

      console.log(`搜索调用次数: ${searchCallCount}`);
      expect(searchCallCount).toBe(1);
    }

    // 测试更新节流
    for (let i = 0; i < 10; i++) {
      await page.evaluate(() => {
        (window as any).throttledUpdate([{ id: `test-${i}` }]);
      });
      await page.waitForTimeout(20);
    }
    await page.waitForTimeout(250);

    // 验证节流效果
    const updateCallCount = await page.evaluate(() => {
      return (window as any).updateCallCount;
    });

    console.log(`更新调用次数: ${updateCallCount}`);
    expect(updateCallCount).toBeLessThanOrEqual(5);
  });

  test('should implement notification lazy loading for images', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 添加懒加载图片
    await page.evaluate(() => {
      for (let i = 0; i < 15; i++) {
        const img = document.createElement('img');
        img.setAttribute('data-src', `/notification-image-${i}.jpg`);
        img.setAttribute('loading', 'lazy');
        img.className = 'notification-image';
        document.querySelector('.notification-images')?.appendChild(img);
      }
    });

    // 实现懒加载
    await page.evaluate(() => {
      const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            const img = entry.target as HTMLImageElement;
            img.src = img.dataset.src!;
            img.removeAttribute('data-src');
            observer.unobserve(img);
          }
        });
      });
      
      document.querySelectorAll('img[data-src]').forEach(img => {
        observer.observe(img);
      });
    });

    // 验证懒加载功能
    const lazyImages = await page.$$('img[data-src]');
    expect(lazyImages.length).toBeGreaterThan(0);

    // 滚动到图片位置
    const container = await page.$('.notification-images');
    if (container) {
      await container.scrollIntoView();
      await page.waitForTimeout(1000);

      // 验证部分图片已加载
      const loadedImages = await page.$$('img:not([data-src])');
      expect(loadedImages.length).toBeGreaterThan(0);
    }
  });

  test('should implement notification priority queue optimization', async ({ page }) => {
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');

    // 实现优先级队列
    await page.evaluate(() => {
      class NotificationPriorityQueue {
        private queue: any[] = [];
        private priorities = { high: 3, medium: 2, low: 1 };

        addNotification(notification: any) {
          const priority = this.priorities[notification.priority] || 1;
          notification.priority = priority;
          
          let inserted = false;
          for (let i = 0; i < this.queue.length; i++) {
            if (this.queue[i].priority < priority) {
              this.queue.splice(i, 0, notification);
              inserted = true;
              break;
            }
          }
          
          if (!inserted) {
            this.queue.push(notification);
          }
        }

        getNextNotification() {
          return this.queue.shift();
        }

        getQueueLength() {
          return this.queue.length;
        }

        clearQueue() {
          this.queue = [];
        }
      }

      window.notificationQueue = new NotificationPriorityQueue();
    });

    // 测试优先级队列
    const testNotifications = [
      { id: '1', priority: 'low', title: '低优先级通知' },
      { id: '2', priority: 'high', title: '高优先级通知' },
      { id: '3', priority: 'medium', title: '中优先级通知' },
      { id: '4', priority: 'high', title: '另一个高优先级通知' },
      { id: '5', priority: 'low', title: '另一个低优先级通知' }
    ];

    await page.evaluate((notifications: any[]) => {
      notifications.forEach(notification => {
        (window as any).notificationQueue.addNotification(notification);
      });
    }, testNotifications);

    // 验证优先级顺序
    const queueOrder = [];
    while (true) {
      const notification = await page.evaluate(() => {
        return (window as any).notificationQueue.getNextNotification();
      });
      if (!notification) break;
      queueOrder.push(notification.title);
    }

    console.log('通知优先级顺序:', queueOrder);
    expect(queueOrder[0]).toBe('高优先级通知');
    expect(queueOrder[1]).toBe('另一个高优先级通知');
    expect(queueOrder[2]).toBe('中优先级通知');
    expect(queueOrder[3]).toBe('低优先级通知');
    expect(queueOrder[4]).toBe('另一个低优先级通知');
  });
});