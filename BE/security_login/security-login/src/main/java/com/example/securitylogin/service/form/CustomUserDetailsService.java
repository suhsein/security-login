package com.example.securitylogin.service.form;

import com.example.securitylogin.dto.form.CustomUserDetails;
import com.example.securitylogin.entity.UserEntity;
import com.example.securitylogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            return new CustomUserDetails(userEntity);
        }
        throw new UsernameNotFoundException("User not found");
    }
}
