package com.example.securitylogin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String refresh;
    private String expiration;

    @Builder
    public RefreshEntity(String username, String refresh, String expiration) {
        this.username = username;
        this.refresh = refresh;
        this.expiration = expiration;
    }
}
