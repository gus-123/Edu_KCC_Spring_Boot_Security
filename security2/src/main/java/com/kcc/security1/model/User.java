package com.kcc.security1.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user2")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;  // 사용자의 고유 ID (소셜 로그인 제공자명_providerId) 형식으로 생성
    private String password;
    private String email;  // 사용자 이메일 (OAuth2 정보에서 추출)
    private String role;  //ROLE_USER, ROLE_ADMIN - 사용자 권한 (여기서는 기본적으로 "ROLE_USER" 설정)

    // 구글 회원 정보 저장 용도
    private String provider;  // 소셜 로그인 제공자 (e.g., google)
    private String providerId;  // 소셜 로그인 제공자에서 부여한 고유 ID (숫자)

    @CreationTimestamp
    private Timestamp createdDate;

    @Builder
    public User(String username, String password, String email, String role, String provider, String providerId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
}
