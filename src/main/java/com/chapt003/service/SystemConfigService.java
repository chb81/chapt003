package com.chapt003.service;

import com.chapt003.dto.SystemConfigRequest;
import com.chapt003.dto.SystemConfigResponse;
import com.chapt003.entity.SystemConfig;
import com.chapt003.exception.BusinessException;
import com.chapt003.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository repository;

    @Transactional(readOnly = true)
    @Cacheable(value = "systemConfig", key = "'all'")
    public List<SystemConfigResponse> getAll() {
        return repository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "systemConfig", key = "#configKey")
    public SystemConfigResponse getByKey(String configKey) {
        SystemConfig config = repository.findByConfigKey(configKey)
                .orElseThrow(() -> new BusinessException(404, "配置项不存在: " + configKey));
        return convertToResponse(config);
    }

    @Transactional
    @CacheEvict(value = "systemConfig", allEntries = true)
    public SystemConfigResponse createOrUpdate(SystemConfigRequest request) {
        SystemConfig config = repository.findByConfigKey(request.getConfigKey())
                .orElse(SystemConfig.builder()
                        .configKey(request.getConfigKey())
                        .build());
        config.setConfigValue(request.getConfigValue());
        if (request.getDescription() != null) {
            config.setDescription(request.getDescription());
        }
        config = repository.save(config);
        return convertToResponse(config);
    }

    @Transactional
    @CacheEvict(value = "systemConfig", allEntries = true)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(404, "配置项不存在");
        }
        repository.deleteById(id);
    }

    private SystemConfigResponse convertToResponse(SystemConfig config) {
        return SystemConfigResponse.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .description(config.getDescription())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
