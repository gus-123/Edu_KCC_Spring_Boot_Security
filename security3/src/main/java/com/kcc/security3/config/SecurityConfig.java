package com.kcc.security3.config;

import com.kcc.security3.Repository.UserRepository;
import com.kcc.security3.filter.JwtAuthenticationFilter;
import com.kcc.security3.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration  // 설정파일의 역할을 하기 위해 넣어줌.(즉, 이 클래스가 설정 파일임을 나타냄)
@EnableWebSecurity  // Spring Security 기능을 활성화 되도록 넣어줌.
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CorsFilter corsFilter;

    private static final String[] WHITE_LIST = {  // 인증(로그인) 없이 접근 가능한 경로들을 배열로 정의
            "/join",  // 회원가입 페이지
            "/h2-console/**"  // H2 Console 관련 모든 경로
    };

    @Bean
    public BCryptPasswordEncoder encoderPwd() {  // 암호 해시를 위한 BCryptPasswordEncoder 빈(bean)을 생성
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Spring Security 에서 기본 제공하는 로그인 방식들을 비활성화하고, 커스텀 JwtAuthenticationFilter 를 통해 JWT 토큰을 이용한 인증 방식을 구현
    // 특정 경로에는 인증 없이 접근을 허용하도록 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, UserRepository userRepository) throws Exception {
        // JwtAuthentication 호출하기 위해 AuthenticationManager 객체 얻기
//        AuthenticationManager authenticationManager =
//                // http.getSharedObject(AuthenticationManager.class) 를 이용하여 이미 생성된 AuthenticationManager 객체를 가져옴.
//                http.getSharedObject(AuthenticationManager.class);

        // Security에 원하는 시점에 필터 적용하는 방법 1 (원하는 시점에 커스텀 필터 MyFilter1 을 기본 인증 필터 BasicAuthenticationFilter 앞에 적용하는 방법)
        //http.addFilterBefore(new MyFilter1(), BasicAuthenticationFilter.class);
        http.csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 기능을 비활성화
                //  H2 Console 사용을 위한 설정으로 같은 도메인에서만 프레임 사용을 허용
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(formLogin -> formLogin.disable())  // 기본적인 폼 로그인 방식을 비활성
                .httpBasic(httpBasic -> httpBasic.disable())  // 기본적인 HTTP Basic 인증 방식을 비활성화
                /*
                .addFilter의 전체 흐름
                1) JwtAuthenticationFilter: 사용자가 로그인 요청을 보내면 이 필터에서 먼저 처리됩니다. 인증이 성공하면 JWT 토큰이 생성되어 응답
                2) 다른 요청: 이후 다른 요청이 들어오면 JwtAuthorizationFilter에서 먼저 처리됩니다. 요청 헤더에 포함된 JWT 토큰을 검증하고, 유효한 토큰인 경우 인증된 사용자 정보를 설정
                3) corsFilter: 모든 요청에 대해 실행되며, CORS 관련 설정을 처리
                 */
                .addFilter(new JwtAuthenticationFilter(authenticationManager))  // 커스텀 JwtAuthenticationFilter 필터를 추가(앞서 얻은 AuthenticationManager 객체를 사용)
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository)) // 커스텀 JwtAuthorizationFilter 필터를 추가(앞서 얻은 AuthenticationManager, userRepository 객체를 사용)
                .addFilter(corsFilter)  // CORS 관련 설정을 처리
                .authorizeHttpRequests(authorize ->  // 요청에 대한 권한 설정
                        authorize.requestMatchers(WHITE_LIST).permitAll()  //  WHITE_LIST 에 등록된 경로들은 인증 없이 접근을 허용
                                // 권한 파악
                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers(PathRequest.toH2Console()).permitAll()  // H2 Console 접근도 인증 없이 허용
                                .anyRequest().authenticated()  // 그 외 모든 요청은 인증(로그인)이 필요
                ).sessionManagement(sessionManagement -> // 세션 관릿 설정
                        // 세션을 사용하지 않고 상태 유지를 토큰 (JWT)에 의존하는 방식으로 설정
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
