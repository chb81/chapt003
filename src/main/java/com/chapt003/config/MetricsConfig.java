package com.chapt003.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Timer apiRequestTimer(MeterRegistry registry) {
        return Timer.builder("api.request.duration")
                .description("API请求耗时")
                .tag("type", "api")
                .register(registry);
    }

    @Bean
    public Counter userLoginCounter(MeterRegistry registry) {
        return Counter.builder("user.login.count")
                .description("用户登录次数")
                .register(registry);
    }

    @Bean
    public Counter userRegistrationCounter(MeterRegistry registry) {
        return Counter.builder("user.registration.count")
                .description("用户注册次数")
                .register(registry);
    }

    @Bean
    public Counter volunteerSubmitCounter(MeterRegistry registry) {
        return Counter.builder("volunteer.submit.count")
                .description("志愿提交次数")
                .register(registry);
    }

    @Bean
    public Counter schoolSearchCounter(MeterRegistry registry) {
        return Counter.builder("school.search.count")
                .description("学校搜索次数")
                .register(registry);
    }
}
