package com.kcc.security3.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kcc.security3.Repository.UserRepository;
import com.kcc.security3.config.PrincipalDetail;
import com.kcc.security3.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

/*
1. 사용자가 입력한 사용자명이 존재하면 데이터베이스에서 해당 사용자 정보를 조회
2. 조회된 정보를 기반으로 인증 객체를 생성하고 보안 컨텍스트에 설정
3. 이후의 요청 처리 과정에서 인증된 사용자로 인식되어 권한이 부여
*/

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {  // BasicAuthenticationFilter를 통해 권한 check
    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManagerManager, UserRepository userRepository) {
        super(authenticationManagerManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //super.doFilterInternal(request, response, chain);

        System.out.println("jwt 필터 호출"); // 호출이 되는지 안되는지 확인 용도

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtToken: " + jwtHeader);

        //JWT 토큰을 검증을 해서 정상적인 사용자인지 확인
        if(jwtHeader == null || !jwtHeader.startsWith("Bearer")) {  // 제대로 된 토큰이 아닐시
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = request.getHeader("Authorization")
                .replace("Bearer ", "");
        String username = JWT.require(Algorithm.HMAC512("kcc"))   // 정상적인 토큰("kcc")이 아니면 username을 못 뽑음.
                .build().verify(jwtToken).getClaim("username").asString();
        System.out.println("username: " + username);

        // 사용자의 입력된 사용자명을 기반으로 인증 프로세스를 수행
        if(username != null) {
            // userRepository는 사용자 엔티티를 관리하는 리포지토리
            User userEntity = userRepository.findByUsername(username);  // 입력된 사용자명을 사용하여 데이터베이스에서 사용자 엔티티를 조회
            // PrincipalDetail은 인증된 사용자의 정보를 담는 객체로, Spring Security에서 사용
            PrincipalDetail principalDetail = new PrincipalDetail(userEntity);  // 조회된 사용자 엔티티를 기반으로 PrincipalDetail 객체를 생성
            /*
            UsernamePasswordAuthenticationToken은 사용자명과 비밀번호를 기반으로 하는 인증 토큰.
            principalDetail은 인증된 사용자의 정보를 담은 객체.
            null은 비밀번호를 나타내는데, 이 경우 인증은 사용자명만으로 이루어짐.
            principalDetail.getAuthorities()는 사용자의 권한을 나타내는 Collection을 반환.
             */
            Authentication authentication
                    = new UsernamePasswordAuthenticationToken(principalDetail, null, principalDetail.getAuthorities()); // 인증 객체를 생성
            SecurityContextHolder.getContext().setAuthentication(authentication);  // 생성된 인증 객체를 보안 컨텍스트에 설정, 이를 통해 이후의 요청 처리 과정에서 인증된 사용자로 인식

            // 다음 필터로 요청을 전달하여 계속해서 필터 체인을 실행
            chain.doFilter(request, response);
        }
    }
}
