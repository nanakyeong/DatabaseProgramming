package com.example.databaseprogramming.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users") // 'user'는 H2 등에서 예약어일 수 있으므로 'users'를 권장
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    private LocalDate birthdate;

    // TODO: Role Enum을 만들 수 있지만, 간단하게 문자열로 처리
    private String role = "ROLE_USER";

    @Builder
    public User(String email, String password, String name, String nickname, LocalDate birthdate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.birthdate = birthdate;
    }

    // --- UserDetails 구현 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getUsername() {
        return this.email; // 우리는 email을 ID(username)로 사용
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // true: 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // true: 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true: 자격증명 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true; // true: 활성화됨
    }
}