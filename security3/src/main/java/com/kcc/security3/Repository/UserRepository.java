package com.kcc.security3.Repository;

import com.kcc.security3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// JpaRepository<원하는 Entity 클래스 이름, Primary key의 데이터 타입>
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);
}
