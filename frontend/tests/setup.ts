import { test as setup, expect } from '@playwright/test';

setup.beforeEach(async ({ page }) => {
  // 清除本地存储
  await page.context().clearCookies();
  await page.evaluate(() => localStorage.clear());
});

// 全局测试辅助函数
export const authTest = async (page: any, email: string, password: string) => {
  await page.goto('/login');
  await page.fill('input[type="email"]', email);
  await page.fill('input[type="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForLoadState('networkidle');
};

export const mockApiResponse = async (page: any, url: string, response: any) => {
  await page.route(url, route => {
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(response)
    });
  });
};

export const mockApiError = async (page: any, url: string, status: number = 500) => {
  await page.route(url, route => {
    route.fulfill({
      status: status,
      contentType: 'application/json',
      body: JSON.stringify({ 
        code: status, 
        message: 'Test error' 
      })
    });
  });
};

// 用户数据生成器
export const generateUser = (overrides: any = {}) => {
  const defaultUser = {
    id: Math.floor(Math.random() * 1000) + 1,
    username: `testuser${Math.floor(Math.random() * 1000)}`,
    email: `test${Math.floor(Math.random() * 1000)}@example.com`,
    role: 'USER',
    status: 'ACTIVE',
    emailVerified: true,
    mobileVerified: false,
    createdAt: new Date().toISOString(),
    ...overrides
  };
  
  return defaultUser;
};

// 分页数据生成器
export const generatePageData = (items: any[], page: number, size: number) => {
  const start = page * size;
  const end = start + size;
  const content = items.slice(start, end);
  
  return {
    content,
    totalPages: Math.ceil(items.length / size),
    totalElements: items.length,
    number: page,
    size,
    first: page === 0,
    last: end >= items.length,
    empty: content.length === 0
  };
};