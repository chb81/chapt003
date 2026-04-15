import { test, expect } from '@playwright/test';
import { PerformanceMonitor, PerformanceBenchmarkRunner, performanceUtils } from './performance-utils';

test.describe('Recommendations Performance Tests', () => {
  let monitor: PerformanceMonitor;
  let benchmarkRunner: PerformanceBenchmarkRunner;

  test.beforeEach(() => {
    monitor = new PerformanceMonitor();
    benchmarkRunner = new PerformanceBenchmarkRunner();
    
    // 添加性能基准
    benchmarkRunner.addBenchmark('recommendations-load', {
      loadTime: 2000,
      renderTime: 1000,
      responseTime: 500,
      memoryUsage: 50,
      cpuUsage: 30,
      networkLatency: 200,
      interactionLatency: 100,
      bundleSize: 500
    });

    benchmarkRunner.addBenchmark('filter-performance', {
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
    console.log('推荐性能报告:', report);
  });

  test('should measure recommendations loading performance', async ({ page }) => {
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

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 记录页面加载完成
    monitor.mark('loadComplete');

    // 运行基准测试
    await benchmarkRunner.runBenchmark('recommendations-load', async () => {
      await page.goto('/recommendations');
      await page.waitForLoadState('networkidle');
    });

    // 验证关键性能指标
    const metrics = monitor.getMetrics();
    expect(metrics.loadTime).toBeLessThan(3000);
    expect(metrics.renderTime).toBeLessThan(2000);
    expect(metrics.responseTime).toBeLessThan(1000);
  });

  test('should measure filtering performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建大量测试数据
    await page.evaluate(() => {
      const mockData = [];
      for (let i = 0; i < 1000; i++) {
        mockData.push({
          id: i,
          title: `推荐项目 ${i}`,
          description: `这是推荐项目 ${i} 的详细描述`,
          category: ['技术', '教育', '医疗', '文化'][Math.floor(Math.random() * 4)],
          status: ['active', 'completed', 'pending'][Math.floor(Math.random() * 3)],
          priority: Math.floor(Math.random() * 5) + 1,
          matchScore: Math.random() * 100,
          tags: ['AI', '机器学习', '大数据', '云计算', '区块链'].slice(0, Math.floor(Math.random() * 3) + 1)
        });
      }
      window.mockRecommendations = mockData;
    });

    // 测试筛选器响应时间
    const filterTests = [
      { name: 'category-filter', action: () => page.click('.category-filter button') },
      { name: 'status-filter', action: () => page.click('.status-filter button') },
      { name: 'priority-filter', action: () => page.click('.priority-filter button') },
      { name: 'search-input', action: async () => {
          const searchInput = await page.$('.search-input input');
          if (searchInput) {
            await searchInput.fill('技术');
            await searchInput.press('Enter');
          }
        }
      },
      { name: 'tag-filter', action: () => page.click('.tag-filter button') }
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

    // 运行基准测试
    await benchmarkRunner.runBenchmark('filter-performance', async () => {
      await page.click('.category-filter button');
      await page.click('.status-filter button');
      await page.waitForLoadState('networkidle');
    });
  });

  test('should measure sorting performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建排序测试数据
    await page.evaluate(() => {
      const mockData = [];
      for (let i = 0; i < 500; i++) {
        mockData.push({
          id: i,
          title: `推荐项目 ${i}`,
          matchScore: Math.random() * 100,
          priority: Math.floor(Math.random() * 5) + 1,
          createdAt: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString(),
          updateCount: Math.floor(Math.random() * 100)
        });
      }
      window.mockRecommendations = mockData;
    });

    // 测试各种排序方式
    const sortOptions = [
      { name: 'match-score-desc', action: () => page.click('[data-testid="sort-match-score-desc"]') },
      { name: 'match-score-asc', action: () => page.click('[data-testid="sort-match-score-asc"]') },
      { name: 'priority-desc', action: () => page.click('[data-testid="sort-priority-desc"]') },
      { name: 'priority-asc', action: () => page.click('[data-testid="sort-priority-asc"]') },
      { name: 'update-count-desc', action: () => page.click('[data-testid="sort-update-count-desc"]') }
    ];

    for (const sortOption of sortOptions) {
      const startTime = performance.now();
      await sortOption.action();
      await page.waitForLoadState('networkidle');
      const endTime = performance.now();
      
      const sortTime = endTime - startTime;
      console.log(`${sortOption.name} 排序时间: ${sortTime}ms`);
      expect(sortTime).toBeLessThan(800);
    }
  });

  test('should measure pagination performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建大量分页数据
    await page.evaluate(() => {
      const mockData = [];
      for (let i = 0; i < 10000; i++) {
        mockData.push({
          id: i,
          title: `推荐项目 ${i}`,
          description: `这是推荐项目 ${i} 的详细描述`,
          category: ['技术', '教育', '医疗', '文化'][Math.floor(Math.random() * 4)],
          status: ['active', 'completed', 'pending'][Math.floor(Math.random() * 3)],
          priority: Math.floor(Math.random() * 5) + 1,
          matchScore: Math.random() * 100
        });
      }
      window.mockRecommendations = mockData;
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

  test('should measure recommendation details loading', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建详细数据
    await page.evaluate(() => {
      const mockDetail = {
        id: 1,
        title: '人工智能在教育中的应用',
        description: '这是一个关于人工智能在教育领域应用的详细项目描述，包含大量的文本内容来测试加载性能。',
        fullDescription: '这是一个非常详细的项目描述，包含大量的文本内容、技术细节、实施方案、预期效果等内容。'.repeat(100),
        category: '技术',
        status: 'active',
        priority: 5,
        matchScore: 95.5,
        tags: ['AI', '教育', '机器学习', '深度学习'],
        requirements: [
          '具备机器学习基础',
          '有一定的教育经验',
          '熟悉Python编程',
          '了解深度学习框架'
        ],
        benefits: [
          '推动教育智能化发展',
          '提高教学效率',
          '个性化学习体验',
          '数据驱动的教育决策'
        ],
        timeline: {
          start: '2024-01-01',
          end: '2024-12-31',
          milestones: ['需求分析', '系统设计', '开发实现', '测试验证', '部署上线']
        },
        budget: 500000,
        resources: ['硬件设备', '软件工具', '人力资源', '培训材料'],
        riskAssessment: {
          technical: '中等',
          schedule: '低',
          budget: '低',
          resource: '中等'
        },
        stakeholders: [
          { name: '技术部门', role: '负责技术实现' },
          { name: '教育部门', role: '提供教育专业知识' },
          { name: '项目管理', role: '协调项目进度' }
        ],
        deliverables: [
          'AI教学系统',
          '个性化推荐引擎',
          '学习分析平台',
          '教师培训材料'
        ]
      };
      window.mockRecommendationDetail = mockDetail;
    });

    // 测试详情页加载
    const startTime = performance.now();
    await page.click('.recommendation-item');
    await page.waitForLoadState('networkidle');
    const endTime = performance.now();

    const detailLoadTime = endTime - startTime;
    console.log(`详情页加载时间: ${detailLoadTime}ms`);
    expect(detailLoadTime).toBeLessThan(2000);

    // 验证详情页内容
    await expect(page.locator('.recommendation-detail')).toBeVisible();
    await expect(page.locator('.detail-title')).toHaveText('人工智能在教育中的应用');
    await expect(page.locator('.detail-description')).toBeVisible();
    await expect(page.locator('.detail-tags')).toBeVisible();
  });

  test('should measure recommendation interaction performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 测试各种交互操作
    const interactions = [
      { name: 'like-button', action: () => page.click('.like-button') },
      { name: 'bookmark-button', action: () => page.click('.bookmark-button') },
      { name: 'share-button', action: () => page.click('.share-button') },
      { name: 'comment-button', action: () => page.click('.comment-button') },
      { name: 'settings-button', action: () => page.click('.settings-button') }
    ];

    for (const interaction of interactions) {
      const startTime = performance.now();
      await interaction.action();
      await page.waitForTimeout(100);
      const endTime = performance.now();
      
      const interactionTime = endTime - startTime;
      console.log(`${interaction.name} 交互时间: ${interactionTime}ms`);
      expect(interactionTime).toBeLessThan(200);
    }
  });

  test('should measure recommendation analytics performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建分析数据
    await page.evaluate(() => {
      const mockAnalytics = {
        totalRecommendations: 1000,
        activeRecommendations: 750,
        completedRecommendations: 200,
        pendingRecommendations: 50,
        matchScoreDistribution: {
          '90-100': 300,
          '80-89': 400,
          '70-79': 200,
          '60-69': 80,
          'below-60': 20
        },
        categoryDistribution: {
          '技术': 400,
          '教育': 300,
          '医疗': 200,
          '文化': 100
        },
        monthlyTrends: Array.from({ length: 12 }, (_, i) => ({
          month: i + 1,
          created: Math.floor(Math.random() * 100) + 50,
          completed: Math.floor(Math.random() * 80) + 20
        })),
        performanceMetrics: {
          averageMatchScore: 82.5,
          completionRate: 0.75,
          averageProcessingTime: 2.5,
          satisfactionScore: 4.2
        }
      };
      window.mockAnalytics = mockAnalytics;
    });

    // 测试分析页面加载
    const startTime = performance.now();
    await page.click('[data-testid="analytics-tab"]');
    await page.waitForLoadState('networkidle');
    const endTime = performance.now();

    const analyticsLoadTime = endTime - startTime;
    console.log(`分析页面加载时间: ${analyticsLoadTime}ms`);
    expect(analyticsLoadTime).toBeLessThan(2000);

    // 测试图表渲染
    await expect(page.locator('.analytics-charts')).toBeVisible();
    await expect(page.locator('.performance-metrics')).toBeVisible();
  });

  test('should measure recommendation settings performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 测试设置页面加载
    const startTime = performance.now();
    await page.click('[data-testid="settings-tab"]');
    await page.waitForLoadState('networkidle');
    const endTime = performance.now();

    const settingsLoadTime = endTime - startTime;
    console.log(`设置页面加载时间: ${settingsLoadTime}ms`);
    expect(settingsLoadTime).toBeLessThan(1500);

    // 测试设置表单交互
    const formInputs = [
      { name: 'recommendation-threshold', selector: '[data-testid="threshold-input"]' },
      { name: 'max-results', selector: '[data-testid="max-results-input"]' },
      { name: 'update-frequency', selector: '[data-testid="frequency-select"]' }
    ];

    for (const input of formInputs) {
      const startTime = performance.now();
      const element = await page.$(input.selector);
      if (element) {
        await element.fill('10');
        await element.press('Enter');
      }
      const endTime = performance.now();
      
      const inputTime = endTime - startTime;
      console.log(`${input.name} 输入响应时间: ${inputTime}ms`);
      expect(inputTime).toBeLessThan(300);
    }
  });

  test('should measure recommendation search performance', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建搜索数据
    await page.evaluate(() => {
      const mockData = [];
      const categories = ['技术', '教育', '医疗', '文化'];
      const keywords = ['人工智能', '大数据', '云计算', '物联网', '区块链', '机器学习'];
      
      for (let i = 0; i < 2000; i++) {
        const randomCategory = categories[Math.floor(Math.random() * categories.length)];
        const randomKeyword = keywords[Math.floor(Math.random() * keywords.length)];
        
        mockData.push({
          id: i,
          title: `${randomCategory}领域的${randomKeyword}应用`,
          description: `这是一个关于${randomKeyword}在${randomCategory}领域应用的推荐项目`,
          category: randomCategory,
          tags: [randomKeyword, randomCategory],
          priority: Math.floor(Math.random() * 5) + 1,
          matchScore: Math.random() * 100
        });
      }
      window.mockRecommendations = mockData;
    });

    // 测试搜索功能
    const searchInputs = [
      '人工智能',
      '大数据',
      '云计算',
      '教育',
      '技术'
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

  test('should measure memory usage with large datasets', async ({ page }) => {
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

    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建大量数据
    await page.evaluate(() => {
      const largeData = [];
      for (let i = 0; i < 5000; i++) {
        largeData.push({
          id: i,
          title: `推荐项目 ${i}`,
          description: `这是推荐项目 ${i} 的详细描述，包含大量的文本内容来测试内存使用情况。`.repeat(10),
          category: ['技术', '教育', '医疗', '文化'][Math.floor(Math.random() * 4)],
          status: ['active', 'completed', 'pending'][Math.floor(Math.random() * 3)],
          priority: Math.floor(Math.random() * 5) + 1,
          matchScore: Math.random() * 100,
          tags: ['AI', '机器学习', '大数据', '云计算', '区块链'].slice(0, Math.floor(Math.random() * 3) + 1),
          metadata: {
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            version: Math.random().toString(36).substring(7),
            checksum: Math.random().toString(36).substring(7)
          }
        });
      }
      window.largeRecommendations = largeData;
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
    expect(memoryIncrease).toBeLessThan(100); // 100MB 内存增长阈值

    // 测试滚动时的内存使用
    const container = await page.$('.recommendations-container');
    if (container) {
      await container.scrollBy(0, 1000);
      await page.waitForTimeout(1000);

      let scrollMemory = 0;
      await page.evaluate(() => {
        if ('memory' in performance) {
          scrollMemory = (performance as any).memory.usedJSHeapSize;
        }
      });

      const scrollMemoryIncrease = (scrollMemory - initialMemory) / 1024 / 1024;
      expect(scrollMemoryIncrease).toBeLessThan(150); // 150MB 滚动后内存增长
    }
  });

  test('should measure bundle size and resource loading', async ({ page }) => {
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 测试初始页面加载
    const response = await page.request('http://localhost:5173/recommendations');
    const content = await response.text();
    
    // 估算bundle大小
    const estimatedBundleSize = content.length / 1024; // KB
    console.log(`估算Bundle大小: ${estimatedBundleSize}KB`);
    expect(estimatedBundleSize).toBeLessThan(800); // 800KB bundle大小阈值

    // 测试资源加载
    const resources = await page.evaluate(() => {
      const resources = {
        scripts: [] as string[],
        styles: [] as string[],
        images: [] as string[],
        fonts: [] as string[]
      };

      document.querySelectorAll('script[src]').forEach(script => {
        resources.scripts.push((script as HTMLScriptElement).src);
      });

      document.querySelectorAll('link[href]').forEach(link => {
        if (link.rel.includes('stylesheet')) {
          resources.styles.push((link as HTMLLinkElement).href);
        }
        if (link.rel === 'preload' && link.as === 'font') {
          resources.fonts.push((link as HTMLLinkElement).href);
        }
      });

      document.querySelectorAll('img[src]').forEach(img => {
        resources.images.push((img as HTMLImageElement).src);
      });

      return resources;
    });

    // 验证资源数量
    expect(resources.scripts.length).toBeLessThan(15);
    expect(resources.styles.length).toBeLessThan(8);
    expect(resources.images.length).toBeLessThan(20);
    expect(resources.fonts.length).toBeLessThan(5);

    // 测试资源加载时间
    const resourceLoadTimes = await page.evaluate(() => {
      const perf = window.performance as any;
      if (perf && perf.getEntriesByType) {
        const entries = perf.getEntriesByType('resource');
        return entries
          .filter(entry => entry.name.includes('/recommendations'))
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
    expect(avgResourceLoadTime).toBeLessThan(800); // 800ms 平均资源加载时间
  });

  test('should measure error handling performance', async ({ page }) => {
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
    await page.route('**/api/recommendations', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Server Error' })
      });
    });

    // 测试错误处理时间
    const startTime = performance.now();
    await page.goto('/recommendations');
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

  test('should generate comprehensive performance report', async ({ page }) => {
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
        name: 'recommendations-load',
        fn: async () => {
          await page.goto('/recommendations');
          await page.waitForLoadState('networkidle');
        }
      },
      {
        name: 'filter-performance',
        fn: async () => {
          await page.click('.category-filter button');
          await page.click('.status-filter button');
          await page.waitForLoadState('networkidle');
        }
      },
      {
        name: 'search-performance',
        fn: async () => {
          const searchInput = await page.$('.search-input input');
          if (searchInput) {
            await searchInput.fill('技术');
            await searchInput.press('Enter');
            await page.waitForLoadState('networkidle');
          }
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
        name: 'analytics-performance',
        fn: async () => {
          await page.click('[data-testid="analytics-tab"]');
          await page.waitForLoadState('networkidle');
        }
      }
    ]);

    const report = performanceUtils.generateReport(testResults);
    
    console.log('推荐性能测试报告:', report);
    
    // 验证报告完整性
    expect(report.total).toBe(5);
    expect(report.passed).toBeGreaterThanOrEqual(4);
    expect(report.failed).toBeLessThanOrEqual(1);
    expect(report.successRate).toBeGreaterThan(80);
    expect(report.averageDuration).toBeLessThan(2000);
  });
});

test.describe('Recommendations Performance Optimization Tests', () => {
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
          const container = document.querySelector('.recommendations-container');
          if (container) {
            container.style.height = '600px';
            container.style.overflowY = 'auto';
            
            const itemHeight = 80;
            const visibleItems = Math.ceil(container.clientHeight / itemHeight);
            
            const renderVisibleItems = () => {
              const scrollTop = container.scrollTop;
              const startIndex = Math.floor(scrollTop / itemHeight);
              const endIndex = Math.min(startIndex + visibleItems + 5, window.mockRecommendations.length);
              
              container.innerHTML = '';
              
              for (let i = startIndex; i < endIndex; i++) {
                const item = window.mockRecommendations[i];
                const itemElement = document.createElement('div');
                itemElement.className = 'recommendation-item';
                itemElement.style.height = `${itemHeight}px`;
                itemElement.innerHTML = `
                  <div class="item-header">
                    <h3>${item.title}</h3>
                    <span class="match-score">${item.matchScore.toFixed(1)}</span>
                  </div>
                  <div class="item-description">${item.description}</div>
                  <div class="item-tags">${item.tags.join(', ')}</div>
                `;
                container.appendChild(itemElement);
              }
            };
            
            container.addEventListener('scroll', renderVisibleItems);
            renderVisibleItems();
          }
        },
        
        optimizeSearchDebouncing: () => {
          const debounce = (func: Function, delay: number) => {
            let timeoutId: NodeJS.Timeout;
            return function(this: any, ...args: any[]) {
              clearTimeout(timeoutId);
              timeoutId = setTimeout(() => func.apply(this, args), delay);
            };
          };

          const searchInput = document.querySelector('.search-input input');
          if (searchInput) {
            searchInput.addEventListener('input', debounce((e: Event) => {
              const query = (e.target as HTMLInputElement).value;
              if (query.length > 2) {
                console.log('执行搜索:', query);
              }
            }, 300));
          }
        },
        
        optimizeImageLoading: () => {
          const images = document.querySelectorAll('img[data-src]');
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
          
          images.forEach(img => observer.observe(img));
        }
      };
    });
  });

  test('should implement virtual scrolling for large recommendation lists', async ({ page }) => {
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建大量数据
    await page.evaluate(() => {
      const largeData = [];
      for (let i = 0; i < 10000; i++) {
        largeData.push({
          id: i,
          title: `推荐项目 ${i}`,
          description: `这是推荐项目 ${i} 的描述`,
          matchScore: Math.random() * 100,
          category: ['技术', '教育', '医疗', '文化'][Math.floor(Math.random() * 4)]
        });
      }
      window.mockRecommendations = largeData;
    });

    // 实现虚拟滚动
    await page.evaluate(() => {
      (window as any).performanceOptimizer.optimizeVirtualScrolling();
    });

    // 验证虚拟滚动功能
    await page.waitForTimeout(1000);
    
    const container = await page.$('.recommendations-container');
    expect(container).toBeTruthy();

    // 滚动并验证渲染
    await container!.scrollTo({ top: 2000 });
    await page.waitForTimeout(100);

    const visibleItems = await page.$$('.recommendation-item');
    expect(visibleItems.length).toBeGreaterThan(0);
    expect(visibleItems.length).toBeLessThan(50); // 应该只渲染可见项目

    // 滚动到不同位置
    await container!.scrollTo({ top: 5000 });
    await page.waitForTimeout(100);

    const newVisibleItems = await page.$$('.recommendation-item');
    expect(newVisibleItems.length).toBeGreaterThan(0);
  });

  test('should implement search debouncing', async ({ page }) => {
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 实现搜索防抖
    await page.evaluate(() => {
      (window as any).performanceOptimizer.optimizeSearchDebouncing();
    });

    // 添加搜索输入
    await page.evaluate(() => {
      const searchInput = document.createElement('input');
      searchInput.className = 'search-input';
      searchInput.placeholder = '搜索推荐...';
      document.querySelector('.search-container')?.appendChild(searchInput);
    });

    // 测试防抖功能
    const searchInput = await page.$('.search-input input');
    expect(searchInput).toBeTruthy();

    // 快速输入多个字符
    for (let i = 0; i < 10; i++) {
      await searchInput!.type('test');
      await page.waitForTimeout(20);
    }

    await page.waitForTimeout(350); // 等待防抖时间

    // 验证防抖效果 - 应该只执行一次搜索
    const searchCallCount = await page.evaluate(() => {
      return (window as any).searchCallCount || 0;
    });

    console.log(`搜索调用次数: ${searchCallCount}`);
    expect(searchCallCount).toBe(1);
  });

  test('should implement image lazy loading', async ({ page }) => {
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 添加懒加载图片
    await page.evaluate(() => {
      for (let i = 0; i < 20; i++) {
        const img = document.createElement('img');
        img.setAttribute('data-src', `/recommendation-image-${i}.jpg`);
        img.setAttribute('loading', 'lazy');
        img.className = 'recommendation-image';
        document.querySelector('.image-container')?.appendChild(img);
      }
    });

    // 实现图片懒加载
    await page.evaluate(() => {
      (window as any).performanceOptimizer.optimizeImageLoading();
    });

    // 验证懒加载功能
    const lazyImages = await page.$$('img[data-src]');
    expect(lazyImages.length).toBeGreaterThan(0);

    // 滚动到图片位置
    const container = await page.$('.image-container');
    if (container) {
      await container.scrollIntoView();
      await page.waitForTimeout(1000);

      // 验证部分图片已加载
      const loadedImages = await page.$$('img:not([data-src])');
      expect(loadedImages.length).toBeGreaterThan(0);
    }
  });

  test('should implement recommendation caching', async ({ page }) => {
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 实现缓存
    await page.evaluate(() => {
      const recommendationCache = new Map();
      
      window.getCachedRecommendations = async (filters: any) => {
        const cacheKey = JSON.stringify(filters);
        if (recommendationCache.has(cacheKey)) {
          return recommendationCache.get(cacheKey);
        }
        
        const response = await fetch('/api/recommendations?' + new URLSearchParams(filters));
        const data = await response.json();
        recommendationCache.set(cacheKey, data);
        return data;
      };

      window.clearCache = () => {
        recommendationCache.clear();
      };
    });

    // 测试缓存功能
    const startTime = performance.now();
    await page.evaluate(() => {
      return (window as any).getCachedRecommendations({ category: '技术', limit: 10 });
    });
    const firstCallTime = performance.now() - startTime;

    // 第二次调用应该更快（从缓存获取）
    const startTime2 = performance.now();
    await page.evaluate(() => {
      return (window as any).getCachedRecommendations({ category: '技术', limit: 10 });
    });
    const secondCallTime = performance.now() - startTime2;

    console.log(`首次调用时间: ${firstCallTime}ms`);
    console.log(`缓存调用时间: ${secondCallTime}ms`);

    // 验证缓存效果
    expect(secondCallTime).toBeLessThan(firstCallTime * 0.5); // 缓存调用应显著更快
  });

  test('should implement recommendation preloading', async ({ page }) => {
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 实现预加载
    await page.evaluate(() => {
      window.preloadRecommendations = async (filters: any) => {
        // 使用 prefetch 或 preload API
        const link = document.createElement('link');
        link.rel = 'prefetch';
        link.href = `/api/recommendations?` + new URLSearchParams(filters);
        document.head.appendChild(link);
        
        // 也可以预加载下一页数据
        const nextFilters = { ...filters, page: (filters.page || 1) + 1 };
        const nextLink = document.createElement('link');
        nextLink.rel = 'prefetch';
        nextLink.href = `/api/recommendations?` + new URLSearchParams(nextFilters);
        document.head.appendChild(nextLink);
      };
    });

    // 触发预加载
    await page.evaluate(() => {
      return (window as any).preloadRecommendations({ category: '技术', limit: 10 });
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
    expect(preloadTime).toBeLessThan(1000); // 预加载后应更快
  });

  test('should implement recommendation pagination optimization', async ({ page }) => {
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 实现分页优化
    await page.evaluate(() => {
      window.paginatedData = [];
      const pageSize = 20;
      
      window.loadPage = async (page: number) => {
        const start = (page - 1) * pageSize;
        const end = start + pageSize;
        const pageData = window.mockRecommendations.slice(start, end);
        
        return new Promise((resolve) => {
          setTimeout(() => {
            window.paginatedData = pageData;
            resolve(pageData);
          }, 100);
        });
      };
    });

    // 测试分页加载性能
    const pageLoadTimes = [];
    
    for (let page = 1; page <= 10; page++) {
      const startTime = performance.now();
      await page.evaluate((pageNum: number) => {
        return (window as any).loadPage(pageNum);
      }, page);
      const endTime = performance.now();
      
      const loadTime = endTime - startTime;
      pageLoadTimes.push(loadTime);
      console.log(`第${page}页加载时间: ${loadTime}ms`);
      expect(loadTime).toBeLessThan(500);
    }

    // 验证一致性
    const avgLoadTime = pageLoadTimes.reduce((sum, time) => sum + time, 0) / pageLoadTimes.length;
    expect(avgLoadTime).toBeLessThan(300);
  });

  test('should implement recommendation filtering optimization', async ({ page }) => {
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');

    // 创建过滤优化数据
    await page.evaluate(() => {
      window.mockRecommendations = [];
      const categories = ['技术', '教育', '医疗', '文化'];
      const statuses = ['active', 'completed', 'pending'];
      
      for (let i = 0; i < 1000; i++) {
        window.mockRecommendations.push({
          id: i,
          title: `推荐项目 ${i}`,
          description: `这是推荐项目 ${i} 的描述`,
          category: categories[Math.floor(Math.random() * categories.length)],
          status: statuses[Math.floor(Math.random() * statuses.length)],
          priority: Math.floor(Math.random() * 5) + 1,
          matchScore: Math.random() * 100,
          tags: ['AI', '机器学习', '大数据', '云计算', '区块链'].slice(0, Math.floor(Math.random() * 3) + 1)
        });
      }

      // 创建索引
      window.categoryIndex = new Map();
      window.statusIndex = new Map();
      window.tagIndex = new Map();
      
      window.mockRecommendations.forEach(item => {
        if (!window.categoryIndex.has(item.category)) {
          window.categoryIndex.set(item.category, []);
        }
        window.categoryIndex.get(item.category).push(item);
        
        if (!window.statusIndex.has(item.status)) {
          window.statusIndex.set(item.status, []);
        }
        window.statusIndex.get(item.status).push(item);
        
        item.tags.forEach(tag => {
          if (!window.tagIndex.has(tag)) {
            window.tagIndex.set(tag, []);
          }
          window.tagIndex.get(tag).push(item);
        });
      });
    });

    // 实现索引过滤
    await page.evaluate(() => {
      window.filterByIndex = (field: string, value: string) => {
        const index = window[`${field}Index`];
        if (index && index.has(value)) {
          return index.get(value);
        }
        return [];
      };

      window.multiFilter = (filters: any) => {
        let result = window.mockRecommendations;
        
        if (filters.category) {
          const categoryData = window.filterByIndex('category', filters.category);
          result = result.filter(item => categoryData.includes(item));
        }
        
        if (filters.status) {
          const statusData = window.filterByIndex('status', filters.status);
          result = result.filter(item => statusData.includes(item));
        }
        
        if (filters.priority) {
          result = result.filter(item => item.priority === filters.priority);
        }
        
        if (filters.minMatchScore) {
          result = result.filter(item => item.matchScore >= filters.minMatchScore);
        }
        
        return result;
      };
    });

    // 测试过滤性能
    const filterTests = [
      { name: 'category-filter', filters: { category: '技术' } },
      { name: 'status-filter', filters: { status: 'active' } },
      { name: 'priority-filter', filters: { priority: 5 } },
      { name: 'score-filter', filters: { minMatchScore: 80 } },
      { name: 'multi-filter', filters: { category: '技术', status: 'active', priority: 5 } }
    ];

    for (const filterTest of filterTests) {
      const startTime = performance.now();
      await page.evaluate((filters: any) => {
        return (window as any).multiFilter(filters);
      }, filterTest.filters);
      const endTime = performance.now();
      
      const filterTime = endTime - startTime;
      console.log(`${filterTest.name} 过滤时间: ${filterTime}ms`);
      expect(filterTime).toBeLessThan(100);
    }
  });
});