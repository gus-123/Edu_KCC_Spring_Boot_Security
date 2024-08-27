package com.kcc.security3.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcc.security3.config.PrincipalDetail;
import com.kcc.security3.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

// loginForm을 동작하지 않을 때는 Filter를 통해서 로그인 역할을 수행해준다.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    
    //  로그인 시도시 attemptAuthentication 호출
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication 호출");
        //1. request통해 username, password 받기
        try{
            ObjectMapper mapper = new ObjectMapper(); //json 데이더  파싱
            // User 객체에 데이터를 받을수 있음.
            User user = mapper.readValue(request.getInputStream(), User.class);
            System.out.println(user);

            // UsernamePasswordAuthenticationToken 만듦
            UsernamePasswordAuthenticationToken authenticationToken  =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            //2. PrincipalDetailService loadUserByUsername() 호출
            // authenticationToken을 통해 usrnamedmf 호출해 id와 pw 받음. - authenticationd안에 userdetail안에 user가 존재 함.
            Authentication authentication = authenticationManager.authenticate(authenticationToken );
            //3. PricincipalDetail 구하기
            PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();
            System.out.println("principalDetail: " + principalDetail);

            return authentication;
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
    
    // 인증이 완료 되고 로그인을 성공하면
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("로그인 성공");
        //JWT Token 생성, 전송
        PrincipalDetail principalDetail = (PrincipalDetail) authResult.getPrincipal();  // 인증 정보를 얻어옴.

        String jwtToken = JWT.create()
                // 공식적 추가
                .withSubject("kosaToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000*10)))
                // 사용자에 의해 추가
                .withClaim("id", principalDetail.getUser().getId())
                .withClaim("username", principalDetail.getUser().getUsername())
                // 서명 추가
                .sign(Algorithm.HMAC512("kcc"));  // jwtToken이 만들어짐.

        response.addHeader("Authorization", "Bearer " + jwtToken);  // 사용자에게 jwtToken을 줌.
    }
}
