package com.kcc.security3.config;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.kcc.security3.Repository.UserRepository;
import com.kcc.security3.model.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // final을 쓰면 AllArgsConstructor 대신 이걸 사용한다. (즉, final 필드인 userRepository에 대한 생성자를 자동으로 생성)
public class PrincipalDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    // loadUserByUsername - Spring Security에서 사용자 정보를 가져오는 메서드
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername 호출 .....");
        // userRepository.findByUsername(username) 메서드를 사용하여 사용자 이름에 해당하는 사용자 정보를 가져 옴.
        User userEntity = userRepository.findByUsername(username);

        // 가져온 사용자 정보 (User userEntity)를 기반으로 PrincipalDetail 클래스의 객체를 생성하여 반환
        return new PrincipalDetail(userEntity);
    }
}
