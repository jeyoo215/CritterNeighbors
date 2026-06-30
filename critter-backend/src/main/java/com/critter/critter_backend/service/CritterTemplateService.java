package com.critter.critter_backend.service;

import java.util.HashMap; // 추가
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.repository.CritterTemplateRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CritterTemplateService {

    private final CritterTemplateRepository critterTemplateRepository;

    public List<Map<String, Object>> getAllCritterTemplates() {
        return critterTemplateRepository.findAll().stream()
            .map(template -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", template.getId());
                map.put("name", template.getName());
                map.put("type", template.getType().name());
                map.put("theme", template.getTheme().name());
                map.put("price", template.getPrice());
                return map;
            })
            .collect(Collectors.toList());
    }

    public Long getTemplatePrice(CritterType type) {
        return critterTemplateRepository.findByType(type)
                .orElseThrow(() -> new RuntimeException("해당 크리처 정보를 찾을 수 없습니다."))
                .getPrice();
    }
}