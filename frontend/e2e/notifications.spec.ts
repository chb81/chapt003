import { test, expect } from '@playwright/test';

test.describe('Notifications E2E Tests', () => {
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
    
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');
  });

  test('should load notifications panel', async ({ page }) => {
    await expect(page.locator('.notifications-panel')).toBeVisible();
    await expect(page.locator('.panel-header')).toBeVisible();
    await expect(page.locator('.panel-header h2')).toHaveText('通知中心');
  });

  test('should display notification items', async ({ page }) => {
    await expect(page.locator('.notification-list')).toBeVisible();
    await expect(page.locator('.notification-item').first()).toBeVisible();
    
    // 检查通知项的基本信息
    const firstItem = page.locator('.notification-item').first();
    await expect(firstItem.locator('.notification-title')).toBeVisible();
    await expect(firstItem.locator('.notification-message')).toBeVisible();
    await expect(firstItem.locator('.notification-time')).toBeVisible();
  });

  test('should filter notifications by type', async ({ page }) => {
    await page.click('.filter-tabs button');
    await page.click('[data-testid="filter-system"]');
    
    await expect(page.locator('.filter-tabs')).toContainText('系统');
    
    // 验证过滤后的结果
    const items = page.locator('.notification-item');
    await expect(items.first()).toBeVisible();
    
    // 检查是否都是系统类型
    const itemTypes = await items.evaluateAll(items => 
      items.map(item => item.querySelector('.notification-type')?.textContent)
    );
    expect(itemTypes.every(type => type?.includes('系统'))).toBe(true);
  });

  test('should mark notification as read', async ({ page }) => {
    const firstItem = page.locator('.notification-item').first();
    await expect(firstItem.locator('.unread-indicator')).toBeVisible();
    
    await firstItem.click();
    
    await expect(firstItem.locator('.unread-indicator')).not.toBeVisible();
    await expect(firstItem.locator('.read-indicator')).toBeVisible();
  });

  test('should delete notification', async ({ page }) => {
    const firstItem = page.locator('.notification-item').first();
    const initialCount = await page.locator('.notification-item').count();
    
    await firstItem.locator('.delete-button').click();
    
    await expect(page.locator('.confirm-dialog')).toBeVisible();
    await page.click('[data-testid="confirm-delete"]');
    
    const newCount = await page.locator('.notification-item').count();
    expect(newCount).toBe(initialCount - 1);
  });

  test('should mark all notifications as read', async ({ page }) => {
    await page.click('.mark-all-read-button');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('所有通知已标记为已读');
  });

  test('should clear all notifications', async ({ page }) => {
    await page.click('.clear-all-button');
    
    await expect(page.locator('.confirm-dialog')).toBeVisible();
    await page.click('[data-testid="confirm-clear"]');
    
    await expect(page.locator('.notification-list')).toBeVisible();
    const items = page.locator('.notification-item');
    expect(await items.count()).toBe(0);
  });

  test('should load more notifications on scroll', async ({ page }) => {
    const initialCount = await page.locator('.notification-item').count();
    
    // 滚动到底部
    await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.loading-spinner')).not.toBeVisible({ timeout: 5000 });
    
    const newCount = await page.locator('.notification-item').count();
    expect(newCount).toBeGreaterThan(initialCount);
  });

  test('should show notification details', async ({ page }) => {
    const firstItem = page.locator('.notification-item').first();
    await firstItem.click();
    
    await expect(page.locator('.notification-detail')).toBeVisible();
    await expect(page.locator('.detail-title')).toBeVisible();
    await expect(page.locator('.detail-content')).toBeVisible();
  });

  test('should handle real-time notifications', async ({ page }) => {
    // 模拟WebSocket消息
    await page.evaluate(() => {
      const event = new CustomEvent('new-notification', {
        detail: {
          id: 'new-notification-123',
          title: '新通知',
          message: '测试实时通知',
          type: 'system',
          priority: 'high',
          timestamp: new Date().toISOString(),
          read: false
        }
      });
      window.dispatchEvent(event);
    });
    
    await page.waitForTimeout(1000);
    
    await expect(page.locator('.notification-item').last()).toBeVisible();
    await expect(page.locator('.notification-item').last().locator('.notification-title')).toHaveText('新通知');
  });

  test('should show notification preferences', async ({ page }) => {
    await page.click('.settings-button');
    await page.click('[data-testid="open-settings"]');
    
    await expect(page.locator('.notification-settings')).toBeVisible();
    await expect(page.locator('.preference-form')).toBeVisible();
  });

  test('should save notification preferences', async ({ page }) => {
    await page.click('.settings-button');
    await page.click('[data-testid="open-settings"]');
    
    // 填写设置表单
    await page.check('[data-testid="pref-email"]');
    await page.check('[data-testid="pref-push"]');
    await page.fill('[data-testid="pref-frequency"]', '实时');
    
    await page.click('[data-testid="save-settings"]');
    
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('设置已保存');
  });

  test('should handle notification errors', async ({ page }) => {
    await page.route('**/api/notifications', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Server Error' })
      });
    });
    
    await page.reload();
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('加载通知失败');
  });

  test('should show loading state for notifications', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('notifications-loading', 'true');
    });
    
    await page.reload();
    
    await expect(page.locator('.loading-spinner')).toBeVisible();
    await expect(page.locator('.notification-list')).toBeHidden();
  });

  test('should show empty state when no notifications', async ({ page }) => {
    await page.evaluate(() => {
      localStorage.setItem('notifications-empty', 'true');
    });
    
    await page.reload();
    
    await expect(page.locator('.empty-state')).toBeVisible();
    await expect(page.locator('.empty-state')).toContainText('暂无通知');
  });

  test('should be responsive on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    
    await expect(page.locator('.notifications-panel')).toBeVisible();
    await expect(page.locator('.mobile-filters')).toBeVisible();
    
    // 移动端特定的交互
    await page.click('.mobile-menu-toggle');
    await expect(page.locator('.mobile-menu')).toBeVisible();
  });

  test('should handle offline mode', async ({ page }) => {
    await page.route('**/api/notifications', route => {
      route.abort();
    });
    
    await page.reload();
    
    await expect(page.locator('.offline-indicator')).toBeVisible();
    await expect(page.locator('.offline-message')).toBeVisible();
  });

  test('should show accessibility features', async ({ page }) => {
    await expect(page.locator('.skip-to-content')).toBeVisible();
    await expect(page.locator('[role="button"]')).toBeAccessible();
    
    // 测试键盘导航
    await page.keyboard.press('Tab');
    await expect(page.locator('.notifications-panel')).toBeFocused();
  });

  test('should export notifications data', async ({ page }) => {
    const downloadPromise = page.waitForEvent('download');
    await page.click('[data-testid="export-notifications"]');
    
    const download = await downloadPromise;
    expect(download.suggestedFilename()).toContain('notifications_');
  });

  test('should show notification statistics', async ({ page }) => {
    await page.click('.stats-button');
    await page.click('[data-testid="view-stats"]');
    
    await expect(page.locator('.notification-stats')).toBeVisible();
    await expect(page.locator('.stats-overview')).toBeVisible();
  });
});

test.describe('WebSocket Notification Tests', () => {
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
    
    await page.goto('/notifications');
    await page.waitForLoadState('networkidle');
  });

  test('should connect to WebSocket', async ({ page }) => {
    // 模拟WebSocket连接
    await page.evaluate(() => {
      window.mockWebSocket = {
        readyState: 1,
        send: vi.fn(),
        close: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn()
      };
      global.WebSocket = vi.fn(() => window.mockWebSocket);
    });
    
    await page.reload();
    
    await expect(page.locator('.connection-status')).toHaveText('已连接');
  });

  test('should handle WebSocket message', async ({ page }) => {
    // 模拟WebSocket消息
    await page.evaluate(() => {
      const event = new CustomEvent('websocket-message', {
        detail: {
          type: 'new_notification',
          data: {
            id: 'websocket-notification-123',
            title: 'WebSocket通知',
            message: '通过WebSocket接收的通知',
            type: 'system',
            priority: 'medium',
            timestamp: new Date().toISOString(),
            read: false
          }
        }
      });
      window.dispatchEvent(event);
    });
    
    await page.waitForTimeout(1000);
    
    await expect(page.locator('.notification-item').last()).toBeVisible();
    await expect(page.locator('.notification-item').last().locator('.notification-title')).toHaveText('WebSocket通知');
  });

  test('should handle WebSocket reconnect', async ({ page }) => {
    // 模拟断开连接
    await page.evaluate(() => {
      if (window.mockWebSocket) {
        window.mockWebSocket.readyState = 3;
      }
    });
    
    // 模拟重连
    await page.evaluate(() => {
      const event = new CustomEvent('websocket-reconnect', {
        detail: { success: true }
      });
      window.dispatchEvent(event);
    });
    
    await page.waitForTimeout(1000);
    
    await expect(page.locator('.connection-status')).toHaveText('已连接');
  });

  test('should handle WebSocket error', async ({ page }) => {
    // 模拟WebSocket错误
    await page.evaluate(() => {
      const event = new CustomEvent('websocket-error', {
        detail: { error: 'Connection failed' }
      });
      window.dispatchEvent(event);
    });
    
    await page.waitForTimeout(1000);
    
    await expect(page.locator('.connection-status')).toHaveText('连接失败');
    await expect(page.locator('.error-message')).toBeVisible();
  });

  test('should handle WebSocket close', async ({ page }) => {
    // 模拟WebSocket关闭
    await page.evaluate(() => {
      const event = new CustomEvent('websocket-close', {
        detail: { code: 1000 }
      });
      window.dispatchEvent(event);
    });
    
    await page.waitForTimeout(1000);
    
    await expect(page.locator('.connection-status')).toHaveText('已断开');
  });

  test('should auto-reconnect on connection loss', async ({ page }) => {
    // 模拟连接断开
    await page.evaluate(() => {
      if (window.mockWebSocket) {
        window.mockWebSocket.readyState = 3;
      }
    });
    
    // 模拟重连成功
    await page.evaluate(() => {
      setTimeout(() => {
        const event = new CustomEvent('websocket-reconnect', {
          detail: { success: true }
        });
        window.dispatchEvent(event);
      }, 2000);
    });
    
    await page.waitForTimeout(3000);
    
    await expect(page.locator('.connection-status')).toHaveText('已连接');
  });

  test('should handle connection timeout', async ({ page }) => {
    // 模拟连接超时
    await page.evaluate(() => {
      const event = new CustomEvent('websocket-timeout', {
        detail: { timeout: 5000 }
      });
      window.dispatchEvent(event);
    });
    
    await page.waitForTimeout(1000);
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('连接超时');
  });

  test('should show connection status indicator', async ({ page }) => {
    await expect(page.locator('.connection-status')).toBeVisible();
    await expect(page.locator('.connection-indicator')).toBeVisible();
  });

  test('should handle multiple concurrent messages', async ({ page }) => {
    // 模拟多个并发消息
    for (let i = 0; i < 5; i++) {
      await page.evaluate((index) => {
        const event = new CustomEvent('websocket-message', {
          detail: {
            type: 'new_notification',
            data: {
              id: `notification-${index}`,
              title: `通知${index}`,
              message: `测试消息${index}`,
              type: 'system',
              priority: 'medium',
              timestamp: new Date().toISOString(),
              read: false
            }
          }
        });
        window.dispatchEvent(event);
      }, i);
    }
    
    await page.waitForTimeout(1000);
    
    const items = page.locator('.notification-item');
    expect(await items.count()).toBeGreaterThanOrEqual(5);
  });
});