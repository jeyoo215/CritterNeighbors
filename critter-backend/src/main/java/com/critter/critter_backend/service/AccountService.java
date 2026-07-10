package com.critter.critter_backend.service;

import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.exception.account.BadCredentialsException;
import com.critter.critter_backend.exception.account.DuplicateUsernameException;
import com.critter.critter_backend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Account getUserById(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. ID: " + userId));
    }
    
    // 회원가입
    @Transactional
    public Account register(String userName, String password, String nickname) {
        if (accountRepository.findByUserName(userName).isPresent()) {
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }
        
        String encodedPassword = passwordEncoder.encode(password);

        Account account = Account.builder()
                .userName(userName)
                .password(encodedPassword)
                .nickname(nickname)
                .build();
        
        account.setPoint(100L);

        return accountRepository.save(account);
    }

    // 로그인 및 검증
    @Transactional(readOnly = true)
    public Account login(String userName, String password) {
        Account account = accountRepository.findByUserName(userName)
                .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return account;
    }

    @Transactional(readOnly = true)
    public String getNickname(Long userId) {
        Account account = getUserById(userId);
        return account.getNickname();
    }
}