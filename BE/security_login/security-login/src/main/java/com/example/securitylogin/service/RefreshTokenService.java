package com.example.securitylogin.service;

import com.example.securitylogin.entity.RefreshEntity;
import com.example.securitylogin.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;
    @Transactional
    public void saveRefresh(String username, Integer expireS, String refresh) {
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration(new Date(System.currentTimeMillis() + expireS * 1000L).toString())
                .build();

        refreshRepository.save(refreshEntity);
    }
}
