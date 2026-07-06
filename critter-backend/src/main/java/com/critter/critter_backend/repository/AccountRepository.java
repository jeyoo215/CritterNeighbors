package com.critter.critter_backend.repository;

import com.critter.critter_backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // 로그인 시 아이디로 유저를 찾아야
    Optional<Account> findByUserName(String userName);

    // 포인트 추가...
    @Modifying
    @Query("UPDATE Account a SET a.point = a.point + :amount WHERE a.userId = :userId")
    void addPoint(@Param("userId") Long userId, @Param("amount") Long amount);
}