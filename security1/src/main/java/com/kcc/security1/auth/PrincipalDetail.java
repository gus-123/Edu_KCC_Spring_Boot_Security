package com.kcc.security1.auth;

import com.kcc.security1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// PrincipalDetail = UserDetails
public class PrincipalDetail implements UserDetails {
    private User user;

    // User를 UserDetails에 넣음.
    public PrincipalDetail(User user) {
        this.user = user;
    }

    // user의 권한을 GrantedAuthority표현해라(ex. 권한이 1개면, 1개의 권한을 넣어준다. 즉, 갯수만큼 권한을 넣어줌.)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<GrantedAuthority>();
        collect.add(new GrantedAuthority() {
            @Override  //ROLE_MANGER, ROLE_ADMIN등 1개,1개를 만들면 GrantedAuthority의 갯수가 증가 함.
            public String getAuthority() {
                return user.getRole();  //Role을 Authority객체로 만들어서 return
            }
        });
        return collect;
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
