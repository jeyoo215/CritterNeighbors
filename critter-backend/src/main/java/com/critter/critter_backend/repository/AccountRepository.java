package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // 로그인 시 아이디로 유저를 찾아야
    Optional<Account> findByUserName(String userName);
}