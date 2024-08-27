package com.kcc.security3.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter1 implements Filter {

    // 내가 동작하고 싶은 필터를 실행시키기 위해 사용
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //System.out.println("filter1............");

        // 요청/응답 객체 변환
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if(req.getMethod().equals("POST")){  // POST 메서드 검사
            String headerAuth = req.getHeader("Authorization");  // 사용자 인증 정보가 담겨 있는 헤더 'Authorization' 값을 가져 옴.
            System.out.println(headerAuth);

            // Authorization를 'kosa'라는 값을 보내면 로그인 가능
            if(headerAuth.equals("kosa")) {
                chain.doFilter(req, res); // 인증에 성공한 경우 필터 체인(chain)을 호출하여 다음 필터 또는 실제 서블릿 실행으로 흐름을 넘겨 줌.
            }else {
                // 인증에 실패한 경우 응답 객체(response)를 이용하여 "login fail..." 메시지를 클라이언트에게 전송
                PrintWriter out = res.getWriter();
                out.println("login fail...");
            }
        }

        // 메서드 마지막 부분에서 필터 체인(chain)을 호출, 이를 통해 다음 필터가 있을 경우 해당 필터로 흐름을 넘겨주고, 마지막 필터일 경우 실제 서블릿을 실행
        chain.doFilter(req, res);
    }
}
