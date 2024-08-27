package com.kcc.security3.filter;


import jakarta.servlet.*;

import java.io.IOException;

public class MyFilter2 implements Filter {

    // 내가 동작하고 싶은 필터를 실행시키기 위해 사용
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("filter2............");

        chain.doFilter(request, response);
    }
}
