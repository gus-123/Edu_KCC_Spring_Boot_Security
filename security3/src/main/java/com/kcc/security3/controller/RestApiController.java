package com.kcc.security3.controller;

import com.kcc.security3.Repository.UserRepository;
import com.kcc.security3.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// Rest Api를 사용할때 는 JWT를 사용 하기 때문에 RestController를 사용한다.
@RestController
@RequiredArgsConstructor
public class RestApiController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    // 테스트 용도
    @GetMapping("/home")
    public String home() {
        return "<h1>HOME</h1>";
    }

    // 회원 가입 처리 용도
    @PostMapping("/join")
    public String join(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        return "회원가입완료";

    }

    // manager 권한이 있을시 들어갈수 있는 url
    @GetMapping("/manager/manager")
    public String manager() {
        return "manager";
    }

    // admin 권한이 있을시 들어갈 수 있는 url
    @GetMapping("/admin/admin")
    public String admin() {
        return "admin";
    }

    // Role이 user이면 들어갈수 있는 url
    @GetMapping("/user")
    public String user() {
        return "user";
    }
}
