package com.example.securitylogin.service.form;

import com.example.securitylogin.dto.form.JoinDto;
import com.example.securitylogin.entity.UserEntity;
import com.example.securitylogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(JoinDto joinDto) {
        // join logic...
        Boolean isExist = userRepository.existsByUsername(joinDto.getUsername());

        if (isExist) {
            System.out.println("already exist user");
            return;
        }

        UserEntity userEntity = UserEntity
                .builder()
                .username(joinDto.getUsername())
                .password(bCryptPasswordEncoder.encode(joinDto.getPassword()))
                .role("ROLE_ADMIN")
                .build();

        userRepository.save(userEntity);
    }
}
