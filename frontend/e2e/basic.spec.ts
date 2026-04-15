import { test, expect } from '@playwright/test';

test('basic page load test', async ({ page }) => {
  await page.goto('/');
  await expect(page.locator('#app')).toBeVisible();
});

test('navigation test', async ({ page }) => {
  await page.goto('/');
  await page.click('text=用户管理');
  await expect(page).toHaveURL(/\/users/);
});

test('API health check', async ({ page }) => {
  await page.goto('/');
  
  // 测试API健康检查
  const response = await page.request.get('/api/health');
  expect(response.status()).toBe(200);
});