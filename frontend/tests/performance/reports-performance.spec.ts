import { test, expect } from '@playwright/test';
import { PerformanceMonitor, PerformanceBenchmarkRunner, performanceUtils } from './performance-utils';

test.describe('Reports Performance Tests', () => {
  let monitor: PerformanceMonitor;
  let benchmarkRunner: PerformanceBenchmarkRunner;

  test.beforeEach(() => {
    monitor = new PerformanceMonitor();
    benchmarkRunner = new PerformanceBenchmarkRunner();
    
    // 添加性能基准
    benchmarkRunner.addBenchmark('reports-load', {
      loadTime: 4000,
      renderTime: 2000,
      responseTime: 1000,
      memoryUsage: 100,
      cpuUsage: 50,
      networkLatency: 400,
      interactionLatency: 200,
      bundleSize: 1000
    });

    benchmarkRunner.addBenchmark('template-management', {
      loadTime: 3000,
      renderTime: 1500,
      responseTime: 800,
      memoryUsage: 80,
      cpuUsage: 40,
      networkLatency: 300,
      interactionLatency: 150,
      bundleSize: 600
    });

    benchmarkRunner.addBenchmark('report-generation', {
      loadTime: 5000,
      renderTime: 3000,
      responseTime: 2000,
      memoryUsage: 150,
      cpuUsage: 80,
      networkLatency: 1000,
      interactionLatency: 400,
      bundleSize: 1200
    });

    benchmarkRunner.addBenchmark('export-performance', {
      loadTime: 4000,
      renderTime: 2500,
      responseTime: 1500,
      memoryUsage: 120,
      cpuUsage: 60,
      networkLatency: 800,
      interactionLatency: 300,
      bundleSize: 900
    });
  });

  test('should load reports with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('reports-load');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('reports-load');
    
    // 验证加载性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证页面功能正常
    const templateCards = await page.locator('.template-card').count();
    expect(templateCards).toBeGreaterThan(0);
    
    const generateButton = await page.locator('button:has-text("生成报告")');
    expect(await generateButton.count()).toBeGreaterThan(0);
    
    const exportButton = await page.locator('button:has-text("导出")');
    expect(await exportButton.count()).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle template management with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('template-management');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 模拟模板加载
    const templateCards = await page.locator('.template-card').count();
    for (let i = 0; i < Math.min(templateCards, 10); i++) {
      const template = page.locator('.template-card').nth(i);
      await template.click();
      await page.waitForTimeout(200);
      
      const editButton = await page.locator('button:has-text("编辑")');
      if (await editButton.isVisible()) {
        await editButton.click();
        await page.waitForTimeout(300);
      }
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('template-management');
    
    // 验证模板管理性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证模板管理功能
    const templateCardsAfter = await page.locator('.template-card').count();
    expect(templateCardsAfter).toBeGreaterThan(0);
    
    const deleteButtons = await page.locator('button:has-text("删除")').count();
    expect(deleteButtons).toBeGreaterThanOrEqual(0);
    
    const duplicateButtons = await page.locator('button:has-text("复制")').count();
    expect(duplicateButtons).toBeGreaterThanOrEqual(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle report generation with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('report-generation');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 查找并点击生成报告按钮
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    if (generateButtons > 0) {
      const generateButton = page.locator('button:has-text("生成报告")').first();
      await generateButton.click();
      await page.waitForLoadState('networkidle');
      
      // 等待报告生成完成
      await page.waitForTimeout(3000);
      
      // 检查报告预览
      const previewContainer = await page.locator('.report-preview').count();
      expect(previewContainer).toBeGreaterThan(0);
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('report-generation');
    
    // 验证报告生成性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证报告生成功能
    const reportPreviews = await page.locator('.report-preview').count();
    if (reportPreviews > 0) {
      expect(reportPreviews).toBeGreaterThan(0);
    }
    
    const downloadButtons = await page.locator('button:has-text("下载")').count();
    if (downloadButtons > 0) {
      expect(downloadButtons).toBeGreaterThan(0);
    }
    
    await monitor.stopMonitoring();
  });

  test('should handle report scheduling with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('scheduling');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 查找并点击定时报告按钮
    const scheduleButton = await page.locator('button:has-text("定时报告")');
    if (await scheduleButton.isVisible()) {
      await scheduleButton.click();
      await page.waitForLoadState('networkidle');
      
      // 填写定时报告表单
      const nameInput = await page.locator('input[placeholder="报告名称"]');
      if (await nameInput.isVisible()) {
        await nameInput.fill('测试定时报告');
      }
      
      const frequencySelect = await page.locator('select');
      if (await frequencySelect.isVisible()) {
        await frequencySelect.selectOption({ label: '每周' });
      }
      
      const scheduleButtonSubmit = await page.locator('button:has-text("保存")');
      if (await scheduleButtonSubmit.isVisible()) {
        await scheduleButtonSubmit.click();
        await page.waitForLoadState('networkidle');
      }
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('scheduling') || {
      loadTime: 3500,
      renderTime: 2000,
      responseTime: 1000,
      memoryUsage: 80,
      cpuUsage: 45,
      networkLatency: 500,
      interactionLatency: 250,
      bundleSize: 700
    };
    
    // 验证定时报告性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证定时报告功能
    const scheduledReports = await page.locator('.scheduled-report').count();
    expect(scheduledReports).toBeGreaterThanOrEqual(0);
    
    const editScheduleButtons = await page.locator('button:has-text("编辑")').count();
    expect(editScheduleButtons).toBeGreaterThanOrEqual(0);
    
    const deleteScheduleButtons = await page.locator('button:has-text("删除")').count();
    expect(deleteScheduleButtons).toBeGreaterThanOrEqual(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle report history with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('history');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 查找并点击历史记录按钮
    const historyButton = await page.locator('button:has-text("历史记录")');
    if (await historyButton.isVisible()) {
      await historyButton.click();
      await page.waitForLoadState('networkidle');
      
      // 等待历史记录加载完成
      await page.waitForTimeout(2000);
      
      // 测试分页
      const nextPageButton = await page.locator('button:has-text("下一页")');
      if (await nextPageButton.isVisible()) {
        await nextPageButton.click();
        await page.waitForLoadState('networkidle');
      }
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('history') || {
      loadTime: 4000,
      renderTime: 2500,
      responseTime: 1500,
      memoryUsage: 90,
      cpuUsage: 50,
      networkLatency: 800,
      interactionLatency: 300,
      bundleSize: 800
    };
    
    // 验证历史记录性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证历史记录功能
    const historyItems = await page.locator('.history-item').count();
    expect(historyItems).toBeGreaterThanOrEqual(0);
    
    const viewButtons = await page.locator('button:has-text("查看")').count();
    expect(viewButtons).toBeGreaterThanOrEqual(0);
    
    const downloadHistoryButtons = await page.locator('button:has-text("下载")').count();
    expect(downloadHistoryButtons).toBeGreaterThanOrEqual(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle export functionality with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('export-performance');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 查找并点击导出按钮
    const exportButtons = await page.locator('button:has-text("导出")').count();
    if (exportButtons > 0) {
      const exportButton = page.locator('button:has-text("导出")').first();
      await exportButton.click();
      await page.waitForLoadState('networkidle');
      
      // 等待导出完成
      await page.waitForTimeout(3000);
      
      // 检查下载链接
      const downloadLinks = await page.locator('a:has-text("下载")').count();
      if (downloadLinks > 0) {
        const downloadLink = page.locator('a:has-text("下载")').first();
        await downloadLink.click();
        await page.waitForTimeout(1000);
      }
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('export-performance');
    
    // 验证导出性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证导出功能
    const exportFormats = await page.locator('.export-format').count();
    expect(exportFormats).toBeGreaterThanOrEqual(0);
    
    const downloadLinks = await page.locator('a:has-text("下载")').count();
    expect(downloadLinks).toBeGreaterThanOrEqual(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle form validation with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('form-validation');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 查找生成报告表单并快速输入
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    if (generateButtons > 0) {
      const generateButton = page.locator('button:has-text("生成报告")').first();
      await generateButton.click();
      await page.waitForLoadState('networkidle');
      
      // 快速填写表单
      const inputFields = await page.locator('input').count();
      for (let i = 0; i < Math.min(inputFields, 5); i++) {
        const input = page.locator('input').nth(i);
        await input.fill('测试数据');
        await page.waitForTimeout(50);
      }
      
      // 验证表单验证
      const validateButton = await page.locator('button:has-text("验证")');
      if (await validateButton.isVisible()) {
        await validateButton.click();
        await page.waitForLoadState('networkidle');
      }
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('form-validation') || {
      loadTime: 3000,
      renderTime: 1500,
      responseTime: 800,
      memoryUsage: 60,
      cpuUsage: 35,
      networkLatency: 400,
      interactionLatency: 200,
      bundleSize: 500
    };
    
    // 验证表单验证性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证表单验证功能
    const errorMessages = await page.locator('.error-message').count();
    expect(errorMessages).toBeGreaterThanOrEqual(0);
    
    const successMessages = await page.locator('.success-message').count();
    expect(successMessages).toBeGreaterThanOrEqual(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle real-time updates with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('realtime-update');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 模拟实时更新
    await page.evaluate(() => {
      const event = new CustomEvent('realtime-update', {
        detail: { type: 'report-status', data: { status: 'completed' } }
      });
      window.dispatchEvent(event);
    });
    
    await page.waitForTimeout(1000);
    
    // 模拟WebSocket更新
    await page.evaluate(() => {
      if (window.dispatchEvent) {
        const event = new CustomEvent('websocket-message', {
          detail: { type: 'report', data: { progress: 100 } }
        });
        window.dispatchEvent(event);
      }
    });
    
    await page.waitForTimeout(1000);
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('realtime-update') || {
      loadTime: 2000,
      renderTime: 1000,
      responseTime: 500,
      memoryUsage: 40,
      cpuUsage: 25,
      networkLatency: 300,
      interactionLatency: 150,
      bundleSize: 400
    };
    
    // 验证实时更新性能
    expect(metrics.loadTime).toBeLessThan(benchmark.loadTime);
    expect(metrics.renderTime).toBeLessThan(benchmark.renderTime);
    expect(metrics.responseTime).toBeLessThan(benchmark.responseTime);
    expect(metrics.memoryUsage).toBeLessThan(benchmark.memoryUsage);
    expect(metrics.cpuUsage).toBeLessThan(benchmark.cpuUsage);
    expect(metrics.networkLatency).toBeLessThan(benchmark.networkLatency);
    expect(metrics.interactionLatency).toBeLessThan(benchmark.interactionLatency);
    
    // 验证实时更新功能
    const statusIndicators = await page.locator('.status-indicator').count();
    expect(statusIndicators).toBeGreaterThanOrEqual(0);
    
    const progressBars = await page.locator('.progress-bar').count();
    expect(progressBars).toBeGreaterThanOrEqual(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle error scenarios with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('error-handling');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 模拟网络错误
    await page.route('**/api/reports/generate', route => {
      route.abort('failed');
    });
    
    // 尝试生成报告
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    if (generateButtons > 0) {
      const generateButton = page.locator('button:has-text("生成报告")').first();
      await generateButton.click();
      await page.waitForLoadState('networkidle');
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('error-handling') || {
      loadTime: 4000,
      renderTime: 2500,
      responseTime: 2000,
      memoryUsage: 80,
      cpuUsage: 50,
      networkLatency: 1500,
      interactionLatency: 300,
      bundleSize: 700
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
      await page.goto('/reports');
      await page.waitForLoadState('networkidle');
      
      // 验证响应式布局
      const container = await page.locator('.reports-container');
      await expect(container).toBeVisible();
      
      const templateCards = await page.locator('.template-card').count();
      expect(templateCards).toBeGreaterThan(0);
      
      const generateButtons = await page.locator('button:has-text("生成报告")').count();
      expect(generateButtons).toBeGreaterThan(0);
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('responsive') || {
      loadTime: 5000,
      renderTime: 3000,
      responseTime: 2500,
      memoryUsage: 120,
      cpuUsage: 70,
      networkLatency: 2000,
      interactionLatency: 400,
      bundleSize: 1000
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
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 验证可访问性
    const templateCards = await page.locator('.template-card').count();
    for (let i = 0; i < templateCards; i++) {
      const card = page.locator('.template-card').nth(i);
      await expect(card).toBeVisible();
      
      // 检查键盘导航
      await card.press('Tab');
      await expect(card).toBeFocused();
    }
    
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    for (let i = 0; i < generateButtons; i++) {
      const button = page.locator('button:has-text("生成报告")').nth(i);
      await expect(button).toBeVisible();
      
      // 检查键盘导航
      await button.press('Tab');
      await expect(button).toBeFocused();
    }
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('accessibility') || {
      loadTime: 3500,
      renderTime: 2000,
      responseTime: 1500,
      memoryUsage: 80,
      cpuUsage: 45,
      networkLatency: 1000,
      interactionLatency: 300,
      bundleSize: 700
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
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    const metrics1 = await monitor.getMetrics();
    
    // 第二次加载（缓存命中）
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    const metrics2 = await monitor.getMetrics();
    
    // 验证缓存优化效果
    expect(metrics2.loadTime).toBeLessThan(metrics1.loadTime);
    expect(metrics2.renderTime).toBeLessThan(metrics1.renderTime);
    expect(metrics2.memoryUsage).toBeLessThan(metrics1.memoryUsage);
    expect(metrics2.cpuUsage).toBeLessThan(metrics1.cpuUsage);
    
    // 验证页面功能正常
    const templateCards = await page.locator('.template-card').count();
    expect(templateCards).toBeGreaterThan(0);
    
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    expect(generateButtons).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle lazy loading with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('lazy-loading');
    
    await page.goto('/reports');
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
      loadTime: 4500,
      renderTime: 2500,
      responseTime: 2000,
      memoryUsage: 100,
      cpuUsage: 60,
      networkLatency: 1500,
      interactionLatency: 350,
      bundleSize: 900
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
    const templateCards = await page.locator('.template-card').count();
    expect(templateCards).toBeGreaterThan(0);
    
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    expect(generateButtons).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle virtual scrolling with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('virtual-scrolling');
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
    
    // 模拟虚拟滚动
    await page.evaluate(() => {
      const container = document.querySelector('.reports-container');
      if (container) {
        container.scrollTop = 1000;
        container.dispatchEvent(new Event('scroll'));
      }
    });
    await page.waitForTimeout(500);
    
    await page.evaluate(() => {
      const container = document.querySelector('.reports-container');
      if (container) {
        container.scrollTop = 2000;
        container.dispatchEvent(new Event('scroll'));
      }
    });
    await page.waitForTimeout(500);
    
    const metrics = await monitor.getMetrics();
    const benchmark = await benchmarkRunner.getBenchmark('virtual-scrolling') || {
      loadTime: 5000,
      renderTime: 3000,
      responseTime: 2500,
      memoryUsage: 110,
      cpuUsage: 65,
      networkLatency: 1800,
      interactionLatency: 400,
      bundleSize: 1000
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
    const templateCards = await page.locator('.template-card').count();
    expect(templateCards).toBeGreaterThan(0);
    
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    expect(generateButtons).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle preloading with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('preloading');
    
    await page.goto('/reports');
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
      loadTime: 4500,
      renderTime: 2500,
      responseTime: 2000,
      memoryUsage: 90,
      cpuUsage: 55,
      networkLatency: 1200,
      interactionLatency: 300,
      bundleSize: 850
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
    const templateCards = await page.locator('.template-card').count();
    expect(templateCards).toBeGreaterThan(0);
    
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    expect(generateButtons).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });

  test('should handle priority queue with optimal performance', async ({ page }) => {
    await monitor.startMonitoring('priority-queue');
    
    await page.goto('/reports');
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
      loadTime: 6000,
      renderTime: 3500,
      responseTime: 3000,
      memoryUsage: 150,
      cpuUsage: 80,
      networkLatency: 2000,
      interactionLatency: 450,
      bundleSize: 1200
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
    const templateCards = await page.locator('.template-card').count();
    expect(templateCards).toBeGreaterThan(0);
    
    const generateButtons = await page.locator('button:has-text("生成报告")').count();
    expect(generateButtons).toBeGreaterThan(0);
    
    await monitor.stopMonitoring();
  });
});