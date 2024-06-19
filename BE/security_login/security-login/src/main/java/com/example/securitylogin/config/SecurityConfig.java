package com.example.securitylogin.config;

import com.example.securitylogin.jwt.JWTFilter;
import com.example.securitylogin.jwt.JWTUtil;
import com.example.securitylogin.loginhandler.CustomFailureHandler;
import com.example.securitylogin.repository.OAuth2UserRepository;
import com.example.securitylogin.service.oauth2.CustomOAuth2UserService;
import com.example.securitylogin.loginhandler.CustomFormSuccessHandler;
import com.example.securitylogin.loginhandler.CustomOAuth2SuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;
import java.util.Collections;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserRepository oAuth2UserRepository;
    private final JWTUtil jwtUtil;
    private final CustomFailureHandler customFailureHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // disable
        http
                .httpBasic((basic) -> basic.disable())
                .csrf((csrf) -> csrf.disable());

        // form
        http
                .formLogin((form) -> form.loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(new CustomFormSuccessHandler(jwtUtil))
                        .failureHandler(customFailureHandler)
                        .permitAll());

        // logout
        http
                .logout((auth) -> auth
                        .logoutSuccessUrl("/")
                        .permitAll());

        // oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint((userinfo) -> userinfo
                                .userService(new CustomOAuth2UserService(oAuth2UserRepository)))
                        .successHandler(new CustomOAuth2SuccessHandler(jwtUtil))
                        .failureHandler(customFailureHandler)
                        .permitAll());

        // cors
        http
                .cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000/"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("access"));

                        return configuration;
                    }
                }));

        // authorization
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/login", "/join", "/logout", "/oauth2-jwt-header").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        // 인가되지 않은 사용자에 대한 exception -> 프론트엔드로 코드 응답
        http.exceptionHandling((exception) ->
                exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("authenticationEntryPoint");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }));

        // jwt filter
        http
                .addFilterAfter(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // session stateless
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
