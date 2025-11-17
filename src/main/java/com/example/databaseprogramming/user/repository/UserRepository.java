package com.example.databaseprogramming.user.repository;

import com.example.databaseprogramming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Security가 email(username)로 사용자를 찾을 때 사용할 메서드
    Optional<User> findByEmail(String email);

    // 회원가입 시 중복 체크용
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}