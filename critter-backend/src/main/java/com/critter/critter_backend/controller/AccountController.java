package com.critter.critter_backend.controller;

import com.critter.critter_backend.entity.Account;
import com.critter.critter_backend.service.AccountService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 👈 리액트 연동용 CORS 허용 및 세션 쿠키 허용 설정!
public class AccountController {

    private final AccountService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Account account) {
        // 👈 중요: 이제 UserService가 문자열 3개를 받으므로, account 객체에서 꺼내서 줍니다!
        userService.register(account.getUserName(), account.getPassword(), account.getNickname());
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Map<String, String> loginReq, HttpSession session) {
        // 네 기존 코드 가독성이 좋아서 그대로 유지!
        Account account = userService.login(loginReq.get("userName"), loginReq.get("password"));
        
        // 💡 서블릿 세션에 유저 고유 PK 박아넣기 (추후 방명록/게시판 검증용)
        session.setAttribute("USER_ID", account.getUserId());
        
        return ResponseEntity.ok(account);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화로 로그아웃 처리
        return ResponseEntity.ok("로그아웃 성공!");
    }
}