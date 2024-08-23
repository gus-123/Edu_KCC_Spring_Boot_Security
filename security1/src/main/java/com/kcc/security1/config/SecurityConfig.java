package com.kcc.security1.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // SecurityConfig(3.0) 필터를 활성화 시키는 역할
//@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)  // @Secured, @PreAuthorize 어노테이션을 활성화 시켜줌. - 메소드 단위 방식
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
            "/",
            "/common/**",
            "/WEB-INF/views/**",
            "/join",
            "/loginForm",
            "/joinForm",
            "/join",
            "/h2-console/**"
    };

    // Spring IoC 컨테이너에 BCryptPasswordEncoder라는 이름의 빈을 등록
    //  패스워드 인코딩 및 검증에 사용될 BCrypt 해시 알고리즘을 제공
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    1. SecurityFilterChain 생성
     */

    /*
    @Bean // 메소드 단위 방식
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);  // restAPI는 이것만 처리해도됨.(BASIC)이라 이렇게 한것임.
        http.authorizeHttpRequests(authorize ->
                authorize.requestMatchers(WHITE_LIST).permitAll()  // 이것만 허용
                        .requestMatchers(PathRequest.toH2Console()).permitAll()  // H2에 들어가기위해 허용 해줌.
                        .anyRequest().authenticated()  // 다른것은 인증을 요구할것임.
                ).csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))  // H2 DB 문제점을 위해 적어줌.
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .formLogin(form -> form.loginPage("/loginForm") //인증을 안하고 들어오면 '/loginForm'로 이동 & loginForm으로 login 지정
                .loginProcessingUrl("/login")  // 사용자가 로그인 폼을 제출하면 "/login" URL로 요청이 전달
                .defaultSuccessUrl("/main"));  // 인증이 성공하면 "/main" URL로 리다이렉트


        return http.build();
    }
    */


    @Bean // URL 단위 방식
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(WHITE_LIST).permitAll()  // 이것만 허용
                                // 권한 설정(SecurityContextHolder를 통해 인증과 권한을 파악)
                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers(PathRequest.toH2Console()).permitAll()  // H2에 들어가기위해 허용 해줌.
                                .anyRequest().authenticated()  // 다른것은 인증을 요구할것임.
                ).csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))  // H2 DB 문제점을 위해 적어줌.
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(form -> form.loginPage("/loginForm") //인증을 안하고 들어오면 '/loginForm'로 이동
                        .loginProcessingUrl("/login")  // 사용자가 로그인 폼을 제출하면 "/login" URL로 요청이 전달
                        .defaultSuccessUrl("/main"));  // 인증이 성공하면 "/main" URL로 리다이렉트


        return http.build();
    }

}
