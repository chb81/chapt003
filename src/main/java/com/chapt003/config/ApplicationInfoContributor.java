package com.chapt003.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class ApplicationInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        builder.withDetail("application", new java.util.HashMap<String, Object>() {{
            put("name", "中考志愿填报系统");
            put("version", "1.0.0");
            put("javaVersion", System.getProperty("java.version"));
            put("springBootVersion", "2.7.18");
        }});

        builder.withDetail("runtime", new java.util.HashMap<String, Object>() {{
            put("uptime", formatUptime(runtimeBean.getUptime()));
            put("startTime", LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(runtimeBean.getStartTime()),
                    ZoneId.systemDefault()).toString());
            put("processors", Runtime.getRuntime().availableProcessors());
        }});
    }

    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%dh %dm %ds", hours, minutes, secs);
    }
}
