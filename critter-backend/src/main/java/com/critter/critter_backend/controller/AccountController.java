package com.critter.critter_backend.controller;

import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.service.AccountService;
import com.critter.critter_backend.service.PointService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
// 리액트 연동용 CORS 허용 및 세션 쿠키 허용 설정
public class AccountController {

    private final AccountService userService;
    private final PointService pointService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Account account) {
        userService.register(account.getUserName(), account.getPassword(), account.getNickname());
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Map<String, String> loginReq, HttpSession session) {
        Account account = userService.login(loginReq.get("userName"), loginReq.get("password"));
        session.setAttribute("USER_ID", account.getUserId());
        System.out.println("로그인 성공! 세션 ID: " + session.getId() + ", 유저 ID: " + account.getUserId());
        return ResponseEntity.ok(account);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        System.out.println("세션 삭제");
        return ResponseEntity.ok("로그아웃 성공!");
    }

    @GetMapping("/me")
    public ResponseEntity<Account> getUserInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            return ResponseEntity.ok(null); 
        }

        Account account = userService.getUserById(userId);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/visit/{roomId}")
    public ResponseEntity<Boolean> visitRoom(@PathVariable Long roomId, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        boolean isEarned = pointService.processVisit(userId, roomId);
        return ResponseEntity.ok(isEarned);
    }
}