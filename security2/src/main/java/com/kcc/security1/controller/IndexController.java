package com.kcc.security1.controller;

import com.kcc.security1.auth.PrincipalDetail;
import com.kcc.security1.model.User;
import com.kcc.security1.oauth.PricipalOauth2UserService;
import com.kcc.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
    // basic Security
    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping({"", "/"})
    public String index() {
        return "index";
    }

    @GetMapping("/common")
    public @ResponseBody String common() {
        return "common";
    }

    @GetMapping("/user")
    // Authentication으로 부터 PrincipalDetail정보를 가져옴(인증과 권한이 있는지 확인하기 위해).
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetail principalDetail) {
        System.out.println("principal detail: " + principalDetail.getUser());
        return "user";
    }

    //@Secured("ROLE_ADMIN") // 1번 방식(메소드 단위로 권한을 먹일때 사용) -  메소드 단위 방식
    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    //@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")  // 2번 방식(메소드 단위로 권한을 먹일때 사용) - 메소드 단위 방식
    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";  // String 했길때문에 jsp 이름임.
    }

    @GetMapping("/loginForm")
    public String login() {
        return "loginForm";
    }


    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public @ResponseBody String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");

        // 비밀번호 암호화
        String rawPassword = user.getPassword();  // 비밀번호를 가져옴.
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);  // 비밀번호를 인코딩 해줌.
        user.setPassword(encPassword); // 인코딩한 encPassword를 DB에 저장


        repository.save(user);
        return "join";
    }

    @GetMapping("/main")
    public String main() {
        return "main";
    }
}
