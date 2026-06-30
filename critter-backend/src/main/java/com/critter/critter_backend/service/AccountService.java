package com.critter.critter_backend.service;

import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.exception.BadCredentialsException;
import com.critter.critter_backend.exception.DuplicateUsernameException;
import com.critter.critter_backend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    /*
     * 회원가입
     */
    @Transactional
    public Account register(String userName, String password, String nickname) {
        // 🚨 아이디 중복 체크 예외 처리 (네 AccountRepository에 메서드명이 findByUserName 인지 findByUsername 인지 체크!)
        if (accountRepository.findByUserName(userName).isPresent()) {
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }
        
        // 컨트롤러에서 받아온 날것의 데이터를 엔티티로 조립해서 저장해줌
        Account account = Account.builder()
                .userName(userName)
                .password(password)
                .nickname(nickname)
                .build();
        
        account.setPoint(100L);

        return accountRepository.save(account);
    }

    /*
     * 로그인 및 검증
     */
    @Transactional(readOnly = true)
    public Account login(String userName, String password) {
        Account account = accountRepository.findByUserName(userName)
                .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 일치 체크 예외 처리
        if (!account.getPassword().equals(password)) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return account;
    }
}