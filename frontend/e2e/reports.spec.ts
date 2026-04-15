import { test, expect } from '@playwright/test';

test.describe('Reports E2E Tests', () => {
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
    
    await page.goto('/reports');
    await page.waitForLoadState('networkidle');
  });

  test('should load reports page', async ({ page }) => {
    await expect(page.locator('.reports-container')).toBeVisible();
    await expect(page.locator('.reports-header')).toBeVisible();
    await expect(page.locator('.reports-header h1')).toHaveText('报表管理');
  });

  test('should display report templates', async ({ page }) => {
    await expect(page.locator('.report-templates')).toBeVisible();
    await expect(page.locator('.template-card').first()).toBeVisible();
    
    // 检查模板卡片的基本信息
    const firstCard = page.locator('.template-card').first();
    await expect(firstCard.locator('.template-title')).toBeVisible();
    await expect(firstCard.locator('.template-description')).toBeVisible();
    await expect(firstCard.locator('.template-type')).toBeVisible();
  });

  test('should navigate to template details', async ({ page }) => {
    const firstCard = page.locator('.template-card').first();
    await firstCard.click();
    
    // 应该导航到详情页
    await expect(page).toHaveURL(/\/reports\/templates\/\d+/);
    await expect(page.locator('.template-detail')).toBeVisible();
  });

  test('should create new report', async ({ page }) => {
    await page.click('[data-testid="create-report"]');
    
    await expect(page.locator('.create-report-dialog')).toBeVisible();
    await expect(page.locator('.report-form')).toBeVisible();
  });

  test('should fill report form', async ({ page }) => {
    await page.click('[data-testid="create-report"]');
    
    // 填写报表表单
    await page.fill('[data-testid="report-title"]', '月度活动报表');
    await page.fill('[data-testid="report-description"]', '2024年1月活动统计报表');
    await page.selectOption('[data-testid="report-type"]', '活动统计');
    await page.selectOption('[data-testid="report-format"]', 'PDF');
    
    // 选择时间范围
    await page.click('[data-testid="date-range"]');
    await page.click('[data-testid="date-range-last-month"]');
    
    // 选择包含的内容
    await page.check('[data-testid="include-volunteers"]');
    await page.check('[data-testid="include-activities"]');
    await page.check('[data-testid="include-registrations"]');
    
    await page.click('[data-testid="save-report"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('报表创建成功');
  });

  test('should validate report form', async ({ page }) => {
    await page.click('[data-testid="create-report"]');
    
    // 不填写必填字段
    await page.click('[data-testid="save-report"]');
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('请填写报表标题');
  });

  test('should generate report', async ({ page }) => {
    // 选择一个模板
    const firstCard = page.locator('.template-card').first();
    await firstCard.click();
    
    await page.click('[data-testid="generate-report"]');
    
    await expect(page.locator('.generating-dialog')).toBeVisible();
    await expect(page.locator('.progress-bar')).toBeVisible();
    
    await page.waitForTimeout(3000);
    
    // 验证生成完成
    await expect(page.locator('.download-link')).toBeVisible();
    await expect(page.locator('.download-link')).toHaveAttribute('href', expect.stringContaining('.pdf'));
  });

  test('should download generated report', async ({ page }) => {
    const downloadPromise = page.waitForEvent('download');
    
    // 模拟已生成的报表
    await page.evaluate(() => {
      localStorage.setItem('generated-reports', JSON.stringify([{
        id: 'report-123',
        title: '测试报表',
        type: '活动统计',
        format: 'PDF',
        url: '/api/reports/download/report-123.pdf',
        generatedAt: new Date().toISOString()
      }]));
    });
    
    await page.reload();
    
    await page.click('[data-testid="download-report"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('测试报表');
  });

  test('should schedule report generation', async ({ page }) => {
    await page.click('[data-testid="schedule-report"]');
    
    await expect(page.locator('.schedule-dialog')).toBeVisible();
    await expect(page.locator('.schedule-form')).toBeVisible();
    
    // 填写调度表单
    await page.fill('[data-testid="schedule-title"]', '每日活动报表');
    await page.selectOption('[data-testid="schedule-type"]', '每日');
    await page.selectOption('[data-testid="schedule-time"]', '09:00');
    await page.selectOption('[data-testid="schedule-format"]', 'PDF');
    await page.check('[data-testid="schedule-email"]');
    await page.fill('[data-testid="schedule-emails"]', 'admin@example.com');
    
    await page.click('[data-testid="save-schedule"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('调度已保存');
  });

  test('should manage scheduled reports', async ({ page }) => {
    await page.click('[data-testid="scheduled-reports"]');
    
    await expect(page.locator('.scheduled-reports')).toBeVisible();
    await expect(page.locator('.schedule-item').first()).toBeVisible();
    
    // 编辑调度
    const firstSchedule = page.locator('.schedule-item').first();
    await firstSchedule.locator('.edit-button').click();
    
    await expect(page.locator('.edit-schedule-dialog')).toBeVisible();
    
    // 删除调度
    await firstSchedule.locator('.delete-button').click();
    
    await expect(page.locator('.confirm-dialog')).toBeVisible();
    await page.click('[data-testid="confirm-delete"]');
    
    // 验证删除成功
    const schedules = page.locator('.schedule-item');
    expect(await schedules.count()).toBe(0);
  });

  test('should view report history', async ({ page }) => {
    await page.click('[data-testid="report-history"]');
    
    await expect(page.locator('.report-history')).toBeVisible();
    await expect(page.locator('.history-item').first()).toBeVisible();
    
    // 检查历史记录项
    const firstHistory = page.locator('.history-item').first();
    await expect(firstHistory.locator('.history-title')).toBeVisible();
    await expect(firstHistory.locator('.history-date')).toBeVisible();
    await expect(firstHistory.locator('.history-status')).toBeVisible();
  });

  test('should filter reports by type', async ({ page }) => {
    await page.click('.filter-dropdown button');
    await page.click('[data-testid="filter-activity"]');
    
    await expect(page.locator('.filter-dropdown')).toContainText('活动统计');
    
    // 验证过滤后的结果
    const cards = page.locator('.template-card');
    await expect(cards.first()).toBeVisible();
    
    const cardTypes = await cards.evaluateAll(cards => 
      cards.map(card => card.querySelector('.template-type')?.textContent)
    );
    expect(cardTypes.every(type => type?.includes('活动'))).toBe(true);
  });

  test('should search reports', async ({ page }) => {
    const searchInput = page.locator('.search-input input');
    await searchInput.fill('月度报表');
    
    await searchInput.press('Enter');
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.loading-spinner')).not.toBeVisible({ timeout: 5000 });
    
    // 验证搜索结果
    const cards = page.locator('.template-card');
    await expect(cards.first()).toBeVisible();
  });

  test('should export report templates', async ({ page }) => {
    const downloadPromise = page.waitForEvent('download');
    await page.click('[data-testid="export-templates"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('report_templates_');
  });

  test('should handle report generation errors', async ({ page }) => {
    await page.route('**/api/reports/generate', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Generation failed' })
      });
    });
    
    await page.click('[data-testid="generate-report"]');
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('报表生成失败');
  });

  test('should show loading state during generation', async ({ page }) => {
    await page.click('[data-testid="generate-report"]');
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.report-content')).toBeHidden();
    
    await page.waitForTimeout(5000);
    await expect(page.locator('.loading-spinner')).not.toBeVisible();
  });

  test('should be responsive on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    
    await expect(page.locator('.reports-container')).toBeVisible();
    await expect(page.locator('.mobile-menu')).toBeVisible();
    
    // 移动端特定的交互
    await page.click('.mobile-menu-toggle');
    await expect(page.locator('.mobile-menu')).toBeVisible();
  });

  test('should show accessibility features', async ({ page }) => {
    await expect(page.locator('.skip-to-content')).toBeVisible();
    await expect(page.locator('[role="button"]')).toBeAccessible();
    
    // 测试键盘导航
    await page.keyboard.press('Tab');
    await expect(page.locator('.reports-container')).toBeFocused();
  });

  test('should show report statistics', async ({ page }) => {
    await page.click('[data-testid="report-stats"]');
    
    await expect(page.locator('.report-stats')).toBeVisible();
    await expect(page.locator('.stats-item').first()).toBeVisible();
    
    const firstItem = page.locator('.stats-item').first();
    await expect(firstItem).toContainText('15');
    await expect(firstItem).toContainText('总报表数');
  });
});

test.describe('Report Templates E2E Tests', () => {
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
    
    await page.goto('/reports/templates');
    await page.waitForLoadState('networkidle');
  });

  test('should load report templates page', async ({ page }) => {
    await expect(page.locator('.report-templates')).toBeVisible();
    await expect(page.locator('.templates-header')).toBeVisible();
    await expect(page.locator('.templates-header h1')).toHaveText('报表模板');
  });

  test('should create new template', async ({ page }) => {
    await page.click('[data-testid="create-template"]');
    
    await expect(page.locator('.create-template-dialog')).toBeVisible();
    await expect(page.locator('.template-form')).toBeVisible();
  });

  test('should fill template form', async ({ page }) => {
    await page.click('[data-testid="create-template"]');
    
    // 填写模板表单
    await page.fill('[data-testid="template-name"]', '志愿者活动统计模板');
    await page.fill('[data-testid="template-description"]', '用于统计志愿者活动参与情况的标准模板');
    await page.selectOption('[data-testid="template-type"]', '活动统计');
    await page.selectOption('[data-testid="template-format"]', 'PDF');
    
    // 添加模板字段
    await page.click('[data-testid="add-field"]');
    await page.fill('[data-testid="field-name"]', '志愿者总数');
    await page.selectOption('[data-testid="field-type"]', 'number');
    await page.click('[data-testid="save-field"]');
    
    await page.click('[data-testid="save-template"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('模板创建成功');
  });

  test('should edit template', async ({ page }) => {
    const firstCard = page.locator('.template-card').first();
    await firstCard.locator('.edit-button').click();
    
    await expect(page.locator('.edit-template-dialog')).toBeVisible();
    await expect(page.locator('.template-form')).toBeVisible();
    
    // 修改模板名称
    await page.fill('[data-testid="template-name"]', '修改后的模板名称');
    
    await page.click('[data-testid="save-template"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('模板更新成功');
  });

  test('should delete template', async ({ page }) => {
    const firstCard = page.locator('.template-card').first();
    const initialCount = await page.locator('.template-card').count();
    
    await firstCard.locator('.delete-button').click();
    
    await expect(page.locator('.confirm-dialog')).toBeVisible();
    await page.click('[data-testid="confirm-delete"]');
    
    const newCount = await page.locator('.template-card').count();
    expect(newCount).toBe(initialCount - 1);
  });

  test('should duplicate template', async ({ page }) => {
    const firstCard = page.locator('.template-card').first();
    await firstCard.locator('.duplicate-button').click();
    
    await expect(page.locator('.template-card').nth(1)).toBeVisible();
    await expect(page.locator('.template-card').nth(1).locator('.template-title')).toHaveText('副本');
  });

  test('should preview template', async ({ page }) => {
    const firstCard = page.locator('.template-card').first();
    await firstCard.locator('.preview-button').click();
    
    await expect(page.locator('.template-preview')).toBeVisible();
    await expect(page.locator('.preview-content')).toBeVisible();
  });

  test('should validate template form', async ({ page }) => {
    await page.click('[data-testid="create-template"]');
    
    // 不填写必填字段
    await page.click('[data-testid="save-template"]');
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('请填写模板名称');
  });

  test('should manage template fields', async ({ page }) => {
    await page.click('[data-testid="create-template"]');
    
    // 添加字段
    await page.click('[data-testid="add-field"]');
    await page.fill('[data-testid="field-name"]', '测试字段');
    await page.selectOption('[data-testid="field-type"]', 'text');
    await page.click('[data-testid="save-field"]');
    
    // 编辑字段
    await page.click('[data-testid="edit-field"]');
    await page.fill('[data-testid="field-name"]', '修改后的字段');
    await page.click('[data-testid="save-field"]');
    
    // 删除字段
    await page.click('[data-testid="delete-field"]');
    await page.click('[data-testid="confirm-delete"]');
    
    await expect(page.locator('.field-item').count()).toBe(0);
  });

  test('should export templates', async ({ page }) => {
    const downloadPromise = page.waitForEvent('download');
    await page.click('[data-testid="export-templates"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('report_templates_');
  });

  test('should handle template errors', async ({ page }) => {
    await page.route('**/api/templates', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Server Error' })
      });
    });
    
    await page.reload();
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('加载模板失败');
  });

  test('should show template loading state', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('templates-loading', 'true');
    });
    
    await page.reload();
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.template-list')).toBeHidden();
  });
});

test.describe('Scheduled Reports E2E Tests', () => {
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
    
    await page.goto('/reports/scheduled');
    await page.waitForLoadState('networkidle');
  });

  test('should load scheduled reports page', async ({ page }) => {
    await expect(page.locator('.scheduled-reports')).toBeVisible();
    await expect(page.locator('.scheduled-header')).toBeVisible();
    await expect(page.locator('.scheduled-header h1')).toHaveText('定时报表');
  });

  test('should create scheduled report', async ({ page }) => {
    await page.click('[data-testid="create-schedule"]');
    
    await expect(page.locator('.create-schedule-dialog')).toBeVisible();
    await expect(page.locator('.schedule-form')).toBeVisible();
  });

  test('should fill schedule form', async ({ page }) => {
    await page.click('[data-testid="create-schedule"]');
    
    // 填写调度表单
    await page.fill('[data-testid="schedule-name"]', '每日活动报表');
    await page.selectOption('[data-testid="schedule-type"]', '每日');
    await page.selectOption('[data-testid="schedule-time"]', '09:00');
    await page.selectOption('[data-testid="schedule-format"]', 'PDF');
    
    // 选择模板
    await page.click('[data-testid="select-template"]');
    await page.click('[data-testid="template-activity"]');
    
    // 设置通知
    await page.check('[data-testid="schedule-email"]');
    await page.fill('[data-testid="schedule-emails"]', 'admin@example.com');
    
    await page.click('[data-testid="save-schedule"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('调度创建成功');
  });

  test('should edit scheduled report', async ({ page }) => {
    const firstSchedule = page.locator('.schedule-item').first();
    await firstSchedule.locator('.edit-button').click();
    
    await expect(page.locator('.edit-schedule-dialog')).toBeVisible();
    await expect(page.locator('.schedule-form')).toBeVisible();
    
    // 修改调度时间
    await page.selectOption('[data-testid="schedule-time"]', '10:00');
    
    await page.click('[data-testid="save-schedule"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('调度更新成功');
  });

  test('should delete scheduled report', async ({ page }) => {
    const firstSchedule = page.locator('.schedule-item').first();
    const initialCount = await page.locator('.schedule-item').count();
    
    await firstSchedule.locator('.delete-button').click();
    
    await expect(page.locator('.confirm-dialog')).toBeVisible();
    await page.click('[data-testid="confirm-delete"]');
    
    const newCount = await page.locator('.schedule-item').count();
    expect(newCount).toBe(initialCount - 1);
  });

  test('should toggle schedule status', async ({ page }) => {
    const firstSchedule = page.locator('.schedule-item').first();
    const toggleButton = firstSchedule.locator('.toggle-button');
    
    await toggleButton.click();
    
    await expect(firstSchedule.locator('.status-indicator')).toHaveClass('disabled');
  });

  test('should view schedule history', async ({ page }) => {
    const firstSchedule = page.locator('.schedule-item').first();
    await firstSchedule.locator('.history-button').click();
    
    await expect(page.locator('.schedule-history')).toBeVisible();
    await expect(page.locator('.history-item').first()).toBeVisible();
  });

  test('should validate schedule form', async ({ page }) => {
    await page.click('[data-testid="create-schedule"]');
    
    // 不填写必填字段
    await page.click('[data-testid="save-schedule"]');
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('请填写调度名称');
  });

  test('should handle schedule errors', async ({ page }) => {
    await page.route('**/api/schedules', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Server Error' })
      });
    });
    
    await page.reload();
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('加载调度失败');
  });

  test('should show schedule loading state', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('schedules-loading', 'true');
    });
    
    await page.reload();
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.schedule-list')).toBeHidden();
  });

  test('should export scheduled reports', async ({ page }) => {
    const downloadPromise = page.waitForEvent('download');
    await page.click('[data-testid="export-schedules"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('scheduled_reports_');
  });
});