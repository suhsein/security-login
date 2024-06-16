package com.example.securitylogin.repository;

import com.example.securitylogin.entity.OAuth2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2UserRepository extends JpaRepository<OAuth2UserEntity, Long> {
    OAuth2UserEntity findByUsername(String username);
}
