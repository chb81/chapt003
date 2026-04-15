import { test, expect } from '@playwright/test';

test.describe('Dashboard E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    // 设置认证状态
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        email: 'admin@example.com',
        name: 'Admin User',
        role: 'ADMIN'
      }));
    });
    
    await page.goto('/');
    await page.waitForLoadState('networkidle');
  });

  test('should load dashboard successfully', async ({ page }) => {
    await expect(page.locator('.dashboard')).toBeVisible();
    await expect(page.locator('.dashboard-header h1')).toHaveText('数据概览');
  });

  test('should display overview statistics', async ({ page }) => {
    const stats = page.locator('.overview-stat');
    await expect(stats.nth(0)).toBeVisible();
    await expect(stats.nth(0)).toContainText('150');
    await expect(stats.nth(0)).toContainText('总志愿者');
    
    await expect(stats.nth(1)).toBeVisible();
    await expect(stats.nth(1)).toContainText('25');
    await expect(stats.nth(1)).toContainText('进行中活动');
  });

  test('should render activity chart', async ({ page }) => {
    await expect(page.locator('#activity-chart')).toBeVisible();
    
    // 检查图表是否包含正确的数据
    await page.waitForTimeout(2000);
    const chartContent = await page.locator('#activity-chart').textContent();
    expect(chartContent).toContain('活动统计');
  });

  test('should render user distribution chart', async ({ page }) => {
    await expect(page.locator('#user-distribution-chart')).toBeVisible();
    
    await page.waitForTimeout(2000);
    const chartContent = await page.locator('#user-distribution-chart').textContent();
    expect(chartContent).toContain('用户分布');
  });

  test('should render performance metrics', async ({ page }) => {
    await expect(page.locator('.performance-metrics')).toBeVisible();
    
    const metrics = page.locator('.metric-item');
    await expect(metrics.first()).toBeVisible();
  });

  test('should handle date range filter', async ({ page }) => {
    await page.click('.date-filter button');
    await page.click('[data-testid="last-7-days"]');
    
    await expect(page.locator('.date-filter')).toHaveText('最近7天');
  });

  test('should export dashboard data', async ({ page }) => {
    // Mock download
    const downloadPromise = page.waitForEvent('download');
    await page.click('[data-testid="export-dashboard"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('dashboard_');
  });

  test('should refresh dashboard data', async ({ page }) => {
    const refreshButton = page.locator('[data-testid="refresh-dashboard"]');
    await refreshButton.click();
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.loading-spinner')).not.toBeVisible({ timeout: 10000 });
  });

  test('should display real-time updates', async ({ page }) => {
    // 模拟实时数据更新
    await page.route('**/api/dashboard/stats', route => {
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          volunteers: 155,
          activities: 27,
          registrations: 892,
          pending: 12
        })
      });
    });
    
    // 触发更新
    await page.evaluate(() => {
      const event = new Event('dashboard:update');
      window.dispatchEvent(event);
    });
    
    await page.waitForTimeout(1000);
    
    // 验证数据已更新
    await expect(page.locator('.overview-stat').nth(0)).toContainText('155');
    await expect(page.locator('.overview-stat').nth(1)).toContainText('27');
  });

  test('should handle errors gracefully', async ({ page }) => {
    // Mock API error
    await page.route('**/api/dashboard/stats', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Internal Server Error' })
      });
    });
    
    await page.reload();
    await page.waitForLoadState('networkidle');
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('加载数据失败');
  });

  test('should be responsive on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    
    await expect(page.locator('.dashboard')).toBeVisible();
    await expect(page.locator('.mobile-stats')).toBeVisible();
    
    // 移动端特定的交互
    await page.click('.mobile-menu-toggle');
    await expect(page.locator('.mobile-menu')).toBeVisible();
  });

  test('should switch between different chart types', async ({ page }) => {
    await page.click('[data-testid="chart-type-selector"]');
    await page.click('[data-testid="line-chart"]');
    
    await expect(page.locator('#activity-chart')).toBeVisible();
    await expect(page.locator('.chart-type-selector')).toContainText('折线图');
  });

  test('should show loading states during data fetch', async ({ page }) => {
    const startTime = Date.now();
    await page.click('[data-testid="refresh-dashboard"]');
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.dashboard-content')).toBeHidden();
    
    // 等待加载完成
    await expect(page.locator('.loading-spinner')).not.toBeVisible({ timeout: 10000 });
    
    const endTime = Date.now();
    expect(endTime - startTime).toBeGreaterThan(2000);
  });

  test('should maintain scroll position on refresh', async ({ page }) => {
    await page.evaluate(() => {
      window.scrollTo(0, 500);
    });
    
    const scrollPosition = await page.evaluate(() => window.scrollY);
    expect(scrollPosition).toBeGreaterThan(0);
    
    await page.click('[data-testid="refresh-dashboard"]');
    await page.waitForLoadState('networkidle');
    
    // 验证滚动位置被恢复
    const newScrollPosition = await page.evaluate(() => window.scrollY);
    expect(newScrollPosition).toBeGreaterThan(0);
  });

  test('should handle offline mode', async ({ page }) => {
    // 离线测试
    await page.route('**/api/dashboard/stats', route => {
      route.abort();
    });
    
    await page.reload();
    await page.waitForLoadState('networkidle');
    
    await expect(page.locator('.offline-indicator')).toBeVisible();
    await expect(page.locator('.offline-message')).toBeVisible();
  });

  test('should show accessibility features', async ({ page }) => {
    await expect(page.locator('.skip-to-content')).toBeVisible();
    await expect(page.locator('[role="button"]')).toBeAccessible();
    
    // 测试键盘导航
    await page.keyboard.press('Tab');
    await expect(page.locator('.dashboard-header')).toBeFocused();
  });
});

test.describe('Dashboard Analytics Tests', () => {
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
    
    await page.goto('/dashboard');
    await page.waitForLoadState('networkidle');
  });

  test('should load analytics data', async ({ page }) => {
    await expect(page.locator('.analytics-section')).toBeVisible();
    
    const analyticsCards = page.locator('.analytics-card');
    await expect(analyticsCards.first()).toBeVisible();
    await expect(analyticsCards.first()).toContainText('分析数据');
  });

  test('should generate analytics report', async ({ page }) => {
    const downloadPromise = page.waitForEvent('download');
    await page.click('[data-testid="generate-report"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('analytics_report_');
  });

  test('should export chart data', async ({ page }) => {
    await page.click('[data-testid="export-chart"]');
    await page.click('[data-testid="export-as-png"]');
    
    // 检查下载开始
    await expect(page.locator('.export-notification')).toBeVisible();
  });

  test('should handle analytics filters', async ({ page }) => {
    await page.click('.analytics-filter button');
    await page.click('[data-testid="filter-week"]');
    
    await expect(page.locator('.analytics-filter')).toContainText('本周');
  });

  test('should show loading spinner for analytics', async ({ page }) => {
    await page.click('[data-testid="load-analytics"]');
    
    await expect(page.locator('.analytics-loading')).toBeVisible();
    await expect(page.locator('.analytics-content')).toBeHidden();
    
    await page.waitForTimeout(3000);
    await expect(page.locator('.analytics-loading')).not.toBeVisible();
  });

  test('should handle analytics errors', async ({ page }) => {
    await page.route('**/api/analytics', route => {
      route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Bad Request' })
      });
    });
    
    await page.click('[data-testid="load-analytics"]');
    
    await expect(page.locator('.analytics-error')).toBeVisible();
    await expect(page.locator('.analytics-error')).toContainText('加载数据失败');
  });
});