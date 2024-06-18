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

        response.addCookie(createCookie("access", access, 60 * 10));
        response.addCookie(createCookie("refresh", refresh, 24 * 60 * 60));

        // redirect query param 인코딩 후 전달
        // 이후에 JWT 를 읽어서 데이터를 가져올 수도 있지만, JWT 파싱 비용이 많이 들기 때문에
        // 처음 JWT 발급할 때 이름을 함께 넘긴 후, 로컬 스토리지에 저장한다.
        String encodedUsername = URLEncoder.encode(username, "UTF-8");
        response.sendRedirect("http://localhost:3000/oauth-redirect?username=" + encodedUsername);
    }
    private Cookie createCookie(String key, String value, Integer expiredS) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiredS);
        return cookie;
    }
}
