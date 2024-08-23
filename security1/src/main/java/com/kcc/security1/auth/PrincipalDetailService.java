package com.kcc.security1.auth;

import com.kcc.security1.model.User;
import com.kcc.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
// 인증을 처리하기 위한 프로세스 서비스
public class PrincipalDetailService implements UserDetailsService {
    // 1. 로그인 버튼을 누르면 loadUserByUsername 호출
    // 2. loginForm의 username이 넘어옴.
    // 3. usernamef를 통해 데이터 베이스에서 해당 사용자 정보를 조회해서 일치하면
    // 4. userEntity != null이 아니면 Spring Security가 요구하는 UserDetails 객체로 변환하여 반환
    // 5. 사용잦 정보를 찾을 수 없으면 예외를 발생시킴.

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //userEntity를 가져왔다는건 로그인을 성공했다는 의미
        User userEntity = userRepository.findByUsername(username);
        // UserDetailsService가 끝나고 Authentication(UserDetails을 리턴하면 만들어짐)이 SecurityContext에 만들어짐.
        if (userEntity != null) {
            return new PrincipalDetail(userEntity);  // 인증한 userEntity를 UserDetails로 리턴
        }

        return null;
    }
}
