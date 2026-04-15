import { test, expect } from '@playwright/test';

test.describe('Recommendations E2E Tests', () => {
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
    
    await page.goto('/recommendations');
    await page.waitForLoadState('networkidle');
  });

  test('should load recommendation panel', async ({ page }) => {
    await expect(page.locator('.recommendation-panel')).toBeVisible();
    await expect(page.locator('.panel-header')).toBeVisible();
    await expect(page.locator('.panel-header .header-title')).toHaveText('智能推荐');
  });

  test('should display recommendation items', async ({ page }) => {
    await expect(page.locator('.recommendation-list')).toBeVisible();
    await expect(page.locator('.recommendation-item').first()).toBeVisible();
    
    // 检查推荐项的基本信息
    const firstItem = page.locator('.recommendation-item').first();
    await expect(firstItem.locator('.activity-title')).toBeVisible();
    await expect(firstItem.locator('.activity-type')).toBeVisible();
    await expect(firstItem.locator('.activity-location')).toBeVisible();
  });

  test('should filter recommendations by type', async ({ page }) => {
    await page.click('.filter-type button');
    await page.click('[data-testid="filter-education"]');
    
    await expect(page.locator('.filter-type')).toContainText('教育');
    
    // 验证过滤后的结果
    const items = page.locator('.recommendation-item');
    await expect(items.first()).toBeVisible();
    
    // 检查是否都是教育类型
    const itemTypes = await items.evaluateAll(items => 
      items.map(item => item.querySelector('.activity-type')?.textContent)
    );
    expect(itemTypes.every(type => type?.includes('教育'))).toBe(true);
  });

  test('should search recommendations', async ({ page }) => {
    const searchInput = page.locator('.search-input input');
    await searchInput.fill('志愿者培训');
    
    await searchInput.press('Enter');
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.loading-spinner')).not.toBeVisible({ timeout: 5000 });
    
    // 验证搜索结果
    const items = page.locator('.recommendation-item');
    await expect(items.first()).toBeVisible();
  });

  test('should sort recommendations', async ({ page }) => {
    await page.click('.sort-options button');
    await page.click('[data-testid="sort-relevance"]');
    
    await expect(page.locator('.sort-options')).toContainText('相关性');
    
    // 验证排序结果
    const items = page.locator('.recommendation-item');
    const firstItem = items.first();
    await expect(firstItem.locator('.relevance-score')).toBeVisible();
  });

  test('should like a recommendation', async ({ page }) => {
    const likeButton = page.locator('.recommendation-item').first().locator('.like-button');
    await likeButton.click();
    
    await expect(likeButton.locator('.like-icon')).toHaveClass('liked');
    await expect(page.locator('.like-count')).toBeVisible();
  });

  test('should view recommendation details', async ({ page }) => {
    const firstItem = page.locator('.recommendation-item').first();
    await firstItem.click();
    
    // 应该导航到详情页
    await expect(page).toHaveURL(/\/recommendations\/\d+/);
    await expect(page.locator('.recommendation-detail')).toBeVisible();
  });

  test('should handle recommendation loading states', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('recommendations-loading', 'true');
    });
    
    await page.reload();
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.recommendation-list')).toBeHidden();
    
    await page.evaluate(() => {
      localStorage.removeItem('recommendations-loading');
    });
    
    await page.reload();
    await expect(page.locator('.loading-spinner')).not.toBeVisible();
  });

  test('should show error when no recommendations', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('recommendations-empty', 'true');
    });
    
    await page.reload();
    
    await expect(page.locator('.empty-state')).toBeVisible();
    await expect(page.locator('.empty-state')).toContainText('暂无推荐活动');
  });

  test('should refresh recommendations', async ({ page }) => {
    const refreshButton = page.locator('[data-testid="refresh-recommendations"]');
    await refreshButton.click();
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.loading-spinner')).not.toBeVisible({ timeout: 5000 });
  });

  test('should load recommendation settings', async ({ page }) => {
    await page.click('.settings-button');
    await page.click('[data-testid="open-settings"]');
    
    await expect(page.locator('.recommendation-settings')).toBeVisible();
    await expect(page.locator('.settings-form')).toBeVisible();
  });

  test('should save recommendation preferences', async ({ page }) => {
    await page.click('.settings-button');
    await page.click('[data-testid="open-settings"]');
    
    // 填写设置表单
    await page.check('[data-testid="activity-types-education"]');
    await page.check('[data-testid="activity-types-environmental"]');
    await page.fill('[data-testid="location-preference"]', '北京');
    
    await page.click('[data-testid="save-settings"]');
    
    await expect(page.locator('.settings-success')).toBeVisible();
    await expect(page.locator('.settings-success')).toContainText('设置已保存');
  });

  test('should reset recommendation settings', async ({ page }) => {
    await page.click('.settings-button');
    await page.click('[data-testid="open-settings"]');
    
    await page.click('[data-testid="reset-settings"]');
    
    await expect(page.locator('.confirm-dialog')).toBeVisible();
    await page.click('[data-testid="confirm-reset"]');
    
    await expect(page.locator('.settings-form')).toBeHidden();
  });

  test('should show recommendation analytics', async ({ page }) => {
    await page.click('.analytics-button');
    await page.click('[data-testid="view-analytics"]');
    
    await expect(page.locator('.recommendation-analytics')).toBeVisible();
    await expect(page.locator('.analytics-overview')).toBeVisible();
  });

  test('should export recommendation data', async ({ page }) => {
    const downloadPromise = page.waitForEvent('download');
    await page.click('[data-testid="export-recommendations"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('recommendations_');
  });

  test('should handle recommendation errors', async ({ page }) => {
    await page.route('**/api/recommendations', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Server Error' })
      });
    });
    
    await page.reload();
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('加载推荐失败');
  });

  test('should be responsive on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    
    await expect(page.locator('.recommendation-panel')).toBeVisible();
    await expect(page.locator('.mobile-filters')).toBeVisible();
    
    // 移动端特定的交互
    await page.click('.mobile-menu-toggle');
    await expect(page.locator('.mobile-menu')).toBeVisible();
  });

  test('should handle infinite scroll', async ({ page }) => {
    // 滚动到底部
    await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.loading-spinner')).not.toBeVisible({ timeout: 5000 });
    
    // 验证新加载的项目
    const items = page.locator('.recommendation-item');
    expect(await items.count()).toBeGreaterThan(5);
  });

  test('should show accessibility features', async ({ page }) => {
    await expect(page.locator('.skip-to-content')).toBeVisible();
    await expect(page.locator('[role="button"]')).toBeAccessible();
    
    // 测试键盘导航
    await page.keyboard.press('Tab');
    await expect(page.locator('.recommendation-panel')).toBeFocused();
  });
});

test.describe('Recommendation Settings E2E Tests', () => {
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
    
    await page.goto('/recommendations/settings');
    await page.waitForLoadState('networkidle');
  });

  test('should load recommendation settings page', async ({ page }) => {
    await expect(page.locator('.recommendation-settings')).toBeVisible();
    await expect(page.locator('.settings-header')).toBeVisible();
    await expect(page.locator('.settings-header h2')).toHaveText('推荐设置');
  });

  test('should display current preferences', async ({ page }) => {
    await expect(page.locator('.current-preferences')).toBeVisible();
    await expect(page.locator('.preference-section')).toBeVisible();
  });

  test('should validate form fields', async ({ page }) => {
    // 清空必填字段
    await page.fill('[data-testid="user-interests"]', '');
    
    await page.click('[data-testid="save-settings"]');
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('请至少选择一个兴趣');
  });

  test('should save settings successfully', async ({ page }) => {
    // 填写设置表单
    await page.check('[data-testid="activity-types-education"]');
    await page.check('[data-testid="activity-types-environmental"]');
    await page.fill('[data-testid="location-preference"]', '北京');
    await page.fill('[data-testid="time-preference"]', '周末');
    await page.fill('[data-testid="interests"]', '教育,环保');
    
    await page.click('[data-testid="save-settings"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('设置已保存');
  });

  test('should reset settings to defaults', async ({ page }) => {
    await page.click('[data-testid="reset-settings"]');
    
    await expect(page.locator('.confirm-dialog')).toBeVisible();
    await page.click('[data-testid="confirm-reset"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('设置已重置');
  });

  test('should load user preferences', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('user-preferences', JSON.stringify({
        activityTypes: ['education', 'environmental'],
        locations: ['北京'],
        timeSlots: ['周末'],
        interests: ['教育', '环保']
      }));
    });
    
    await page.reload();
    
    await expect(page.locator('[data-testid="activity-types-education"]')).toBeChecked();
    await expect(page.locator('[data-testid="activity-types-environmental"]')).toBeChecked();
    await expect(page.locator('[data-testid="location-preference"]')).toHaveValue('北京');
  });

  test('should handle settings loading errors', async ({ page }) => {
    await page.route('**/api/user/preferences', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Server Error' })
      });
    });
    
    await page.reload();
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('加载设置失败');
  });

  test('should show loading state when saving', async ({ page }) => {
    await page.click('[data-testid="save-settings"]');
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.save-button')).toBeDisabled();
    
    await page.waitForTimeout(3000);
    await expect(page.locator('.loading-spinner')).not.toBeVisible();
    await expect(page.locator('.save-button')).not.toBeDisabled();
  });
});

test.describe('Recommendation Analytics E2E Tests', () => {
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
    
    await page.goto('/recommendations/analytics');
    await page.waitForLoadState('networkidle');
  });

  test('should load analytics page', async ({ page }) => {
    await expect(page.locator('.recommendation-analytics')).toBeVisible();
    await expect(page.locator('.analytics-header')).toBeVisible();
    await expect(page.locator('.analytics-header h2')).toHaveText('推荐分析');
  });

  test('should display overview statistics', async ({ page }) => {
    await expect(page.locator('.overview-stats')).toBeVisible();
    await expect(page.locator('.overview-item').first()).toBeVisible();
    
    const firstItem = page.locator('.overview-item').first();
    await expect(firstItem).toContainText('150');
    await expect(firstItem).toContainText('总推荐数');
  });

  test('should render performance charts', async ({ page }) => {
    await expect(page.locator('#performance-chart')).toBeVisible();
    await expect(page.locator('#acceptance-rate-chart')).toBeVisible();
    
    await page.waitForTimeout(2000);
    const chartContent = await page.locator('#performance-chart').textContent();
    expect(chartContent).toContain('性能统计');
  });

  test('should filter analytics by time range', async ({ page }) => {
    await page.click('.time-filter button');
    await page.click('[data-testid="filter-last-30-days"]');
    
    await expect(page.locator('.time-filter')).toContainText('最近30天');
  });

  test('should export analytics report', async ({ page }) => {
    const downloadPromise = page.waitForEvent('download');
    await page.click('[data-testid="export-analytics"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('analytics_report_');
  });

  test('should show analytics details', async ({ page }) => {
    await page.click('[data-testid="view-details"]');
    
    await expect(page.locator('.analytics-details')).toBeVisible();
    await expect(page.locator('.detail-metrics')).toBeVisible();
  });

  test('should handle analytics errors', async ({ page }) => {
    await page.route('**/api/analytics', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Server Error' })
      });
    });
    
    await page.reload();
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('加载数据失败');
  });

  test('should show loading state for analytics', async ({ page }) => {
    await page.click('[data-testid="refresh-analytics"]');
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.analytics-content')).toBeHidden();
    
    await page.waitForTimeout(3000);
    await expect(page.locator('.loading-spinner')).not.toBeVisible();
  });
});