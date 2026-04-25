package com.chapt003.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    public ApplicationHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public Health health() {
        Health.Builder builder = Health.up();
        try {
            redisConnectionFactory.getConnection().ping();
            builder.withDetail("redis", "连接正常");
        } catch (Exception e) {
            builder.down().withDetail("redis", "连接失败: " + e.getMessage());
        }

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsagePercent = (double) usedMemory / totalMemory * 100;

        builder.withDetail("memoryUsedMB", usedMemory / 1024 / 1024)
               .withDetail("memoryTotalMB", totalMemory / 1024 / 1024)
               .withDetail("memoryUsagePercent", String.format("%.1f%%", memoryUsagePercent));

        if (memoryUsagePercent > 90) {
            builder.status("WARNING").withDetail("memoryWarning", "内存使用率超过90%");
        }

        return builder.build();
    }
}
