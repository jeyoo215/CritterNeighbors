package com.critter.critter_backend.service;

import java.util.HashMap; // 추가
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.exception.ResourceNotFoundException;
import com.critter.critter_backend.repository.ShopCritterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopCritterService {

    private final ShopCritterRepository shopCritterRepository;

    public List<Map<String, Object>> getAllShopCritter() {
        return shopCritterRepository.findAll().stream()
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

    public Long getCritterPrice(CritterType type) {
        return shopCritterRepository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 크리터입니다."))
                .getPrice();
    }
}