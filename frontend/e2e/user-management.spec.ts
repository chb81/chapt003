import { test, expect } from '@playwright/test';

test.describe('User Management E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    // 每个测试前导航到主页
    await page.goto('/');
  });

  test('should load the application', async ({ page }) => {
    await expect(page.locator('#app')).toBeVisible();
  });

  test('should navigate to user list', async ({ page }) => {
    // 模拟导航到用户列表页
    await page.goto('/users');
    await expect(page).toHaveURL('/users');
  });

  test('should display user list table', async ({ page }) => {
    await page.goto('/users');
    
    // 等待表格加载
    await expect(page.locator('table')).toBeVisible();
    
    // 检查表格列是否存在
    await expect(page.locator('th').filter({ hasText: '用户名' })).toBeVisible();
    await expect(page.locator('th').filter({ hasText: '邮箱' })).toBeVisible();
    await expect(page.locator('th').filter({ hasText: '角色' })).toBeVisible();
    await expect(page.locator('th').filter({ hasText: '状态' })).toBeVisible();
  });

  test('should search users', async ({ page }) => {
    await page.goto('/users');
    
    // 填写搜索框
    await page.fill('input[placeholder*="搜索"]', 'test');
    
    // 点击搜索按钮
    await page.click('button:has-text("搜索")');
    
    // 等待结果
    await page.waitForTimeout(1000);
  });

  test('should filter by role', async ({ page }) => {
    await page.goto('/users');
    
    // 点击角色下拉菜单
    await page.click('.role-filter');
    
    // 选择ADMIN角色
    await page.click('text=管理员');
    
    // 等待结果
    await page.waitForTimeout(1000);
  });

  test('should navigate to user detail', async ({ page }) => {
    await page.goto('/users');
    
    // 点击第一个用户
    await page.click('tbody tr:first-child td:first-child');
    
    // 等待导航到详情页
    await expect(page).toHaveURL(/\/users\/\d+/);
    
    // 检查详情页内容
    await expect(page.locator('.user-detail')).toBeVisible();
  });

  test('should display user tabs', async ({ page }) => {
    await page.goto('/users/1');
    
    // 检查标签页是否存在
    await expect(page.locator('text=基本信息')).toBeVisible();
    await expect(page.locator('text=登录历史')).toBeVisible();
    await expect(page.locator('text=操作日志')).toBeVisible();
  });

  test('should switch between tabs', async ({ page }) => {
    await page.goto('/users/1');
    
    // 点击登录历史标签
    await page.click('text=登录历史');
    await expect(page.locator('.login-history')).toBeVisible();
    
    // 点击操作日志标签
    await page.click('text=操作日志');
    await expect(page.locator('.audit-logs')).toBeVisible();
  });

  test('should handle API errors gracefully', async ({ page }) => {
    // 模拟API错误
    await page.route('**/api/admin/users', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ code: 500, message: 'Internal Server Error' })
      });
    });

    await page.goto('/users');
    
    // 等待错误提示
    await expect(page.locator('.el-message--error')).toBeVisible();
  });
});

test.describe('Authentication E2E Tests', () => {
  test('should redirect to login when not authenticated', async ({ page }) => {
    // 清除localStorage
    await page.evaluate(() => localStorage.clear());
    
    // 尝试访问受保护的页面
    await page.goto('/users');
    
    // 应该重定向到登录页
    await expect(page).toHaveURL('/login');
  });

  test('should login with valid credentials', async ({ page }) => {
    await page.goto('/login');
    
    // 填写登录表单
    await page.fill('input[type="email"]', 'test@example.com');
    await page.fill('input[type="password"]', 'password123');
    
    // 点击登录按钮
    await page.click('button[type="submit"]');
    
    // 等待重定向到主页
    await expect(page).toHaveURL('/');
  });

  test('should show error for invalid credentials', async ({ page }) => {
    await page.goto('/login');
    
    // 填写错误的登录信息
    await page.fill('input[type="email"]', 'wrong@example.com');
    await page.fill('input[type="password"]', 'wrongpassword');
    
    // 点击登录按钮
    await page.click('button[type="submit"]');
    
    // 等待错误提示
    await expect(page.locator('.el-message--error')).toBeVisible();
  });

  test('should logout successfully', async ({ page }) => {
    // 先登录
    await page.evaluate(() => {
      localStorage.setItem('token', 'mock-token');
    });
    
    await page.goto('/users');
    
    // 点击退出按钮
    await page.click('.logout-button');
    
    // 等待重定向到登录页
    await expect(page).toHaveURL('/login');
    
    // 检查token是否被清除
    const token = await page.evaluate(() => localStorage.getItem('token'));
    expect(token).toBeNull();
  });
});

test.describe('Responsive Design E2E Tests', () => {
  test('should work on mobile devices', async ({ page }) => {
    // 设置移动设备视口
    await page.setViewportSize({ width: 375, height: 667 });
    
    await page.goto('/users');
    
    // 检查移动端菜单按钮是否存在
    await expect(page.locator('.mobile-menu-button')).toBeVisible();
  });

  test('should work on tablet devices', async ({ page }) => {
    // 设置平板设备视口
    await page.setViewportSize({ width: 768, height: 1024 });
    
    await page.goto('/users');
    
    // 检查表格是否适配
    await expect(page.locator('table')).toBeVisible();
  });

  test('should work on desktop', async ({ page }) => {
    // 设置桌面视口
    await page.setViewportSize({ width: 1920, height: 1080 });
    
    await page.goto('/users');
    
    // 检查所有元素是否可见
    await expect(page.locator('table')).toBeVisible();
    await expect(page.locator('.search-bar')).toBeVisible();
    await expect(page.locator('.filters')).toBeVisible();
  });
});