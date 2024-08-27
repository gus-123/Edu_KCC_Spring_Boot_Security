package com.kcc.security3.config;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import com.kcc.security3.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
// Authority를 만들기 위해 필요
public class PrincipalDetail implements UserDetails {
    private User user;

    public PrincipalDetail(User user) {
        this.user = user;
    }

    // 사용자에게 부여된 모든 권한의 목록을 반환
    // 각 권한은 GrantedAuthority 인터페이스를 구현하는 객체이며, 권한의 이름을 반환하는 메서드를 가지고 있음
    // EX) 사용자가 "ROLE_ADMIN"과 "ROLE_USER"라는 두 개의 역할을 가지고 있다면, 이 코드는 "ROLE_ADMIN"과 "ROLE_USER"라는 두 개의 권한을 포함하는 목록을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoleList().forEach(r -> {
            authorities.add(() -> r);
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
