package com.critter.critter_backend.config;

import com.critter.critter_backend.domain.CritterType;
import com.critter.critter_backend.domain.EcosystemTheme;
import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.entity.CritterTemplate;
import com.critter.critter_backend.repository.AccountRepository;
import com.critter.critter_backend.repository.CritterTemplateRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
// final로 선언된 녀석들을 자동으로 파라미터로 받는 생성자 생성
public class DataInitializer {

    private final AccountRepository accountRepository;
    private final CritterTemplateRepository critterTemplateRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (accountRepository.findByUserName("test").isEmpty()) {
                accountRepository.save(Account.builder()
                        .userName("test")
                        .password("1234")
                        .nickname("수달이")
                        .build());
                
                accountRepository.save(Account.builder()
                        .userName("testing")
                        .password("1234")
                        .nickname("펭귄이")
                        .build());
                
                System.out.println("테스트 계정 생성 완료: test, testing");
            }

            // 2. 상점 데이터(CritterTemplate) 생성
            if (critterTemplateRepository.count() == 0) {
                critterTemplateRepository.save(new CritterTemplate(null, "문어", CritterType.OCTOPUS, EcosystemTheme.OCEAN, 20L));
                critterTemplateRepository.save(new CritterTemplate(null, "거북이", CritterType.TURTLE, EcosystemTheme.OCEAN, 25L));
                critterTemplateRepository.save(new CritterTemplate(null, "펭귄", CritterType.PENGUIN, EcosystemTheme.OCEAN, 30L));
                critterTemplateRepository.save(new CritterTemplate(null, "다람쥐", CritterType.SQUIRREL, EcosystemTheme.FOREST, 20L));
                critterTemplateRepository.save(new CritterTemplate(null, "여우", CritterType.FOX, EcosystemTheme.FOREST, 25L));
                critterTemplateRepository.save(new CritterTemplate(null, "랫서판다", CritterType.REDPANDA, EcosystemTheme.FOREST, 30L));
                critterTemplateRepository.save(new CritterTemplate(null, "토끼", CritterType.RABBIT, EcosystemTheme.GRASSLAND, 20L));
                critterTemplateRepository.save(new CritterTemplate(null, "강아지", CritterType.DOG, EcosystemTheme.GRASSLAND, 25L));
                critterTemplateRepository.save(new CritterTemplate(null, "고양이", CritterType.CAT, EcosystemTheme.GRASSLAND, 30L));
                System.out.println("상점 데이터 생성 완료");
            }
        };
    }
}