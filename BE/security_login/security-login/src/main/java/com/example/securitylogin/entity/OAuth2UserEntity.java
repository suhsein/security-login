package com.example.securitylogin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter @Setter
public class OAuth2UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username; // provider + provider id
    private String name; // 실제 이름
    private String email;
    private String role;

    @Builder
    public OAuth2UserEntity(String username, String email, String name, String role) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}
