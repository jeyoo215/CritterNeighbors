package com.critter.critter_backend.config;

import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.repository.AccountRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(AccountRepository accountRepository) {
        // 💡 이 메서드 자체가 CommandLineRunner 객체를 return 해야 함!
        return args -> {
            // 이미 데이터가 있는지 확인하고, 없으면 생성!
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
                
                System.out.println("✅ 테스트 계정 생성 완료: test, testing");
            }
        };
    }
}