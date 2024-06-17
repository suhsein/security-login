package com.example.securitylogin.loginhandler;

import com.example.securitylogin.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * OAuth2 로그인 성공 후 JWT 발급
 */
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // create JWT
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // access
        String access = jwtUtil.createJwt("access", username, role, 60 * 10 * 1000L);
        // refresh
        String refresh = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        response.addCookie(createCookie("access", access, 60 * 10 * 1000));
        response.addCookie(createCookie("refresh", refresh, 24 * 60 * 60 * 1000));

        // redirect param 인코딩 후 전달
        String encodedUsername = URLEncoder.encode(username, "UTF-8");
        response.sendRedirect("http://localhost:3000/login?username=" + encodedUsername);
    }
    private Cookie createCookie(String key, String value, Integer expiredMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiredMs);
        return cookie;
    }
}
