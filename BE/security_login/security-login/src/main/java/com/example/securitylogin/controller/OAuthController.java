package com.example.securitylogin.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2 로그인 시 바로 응답을 할 수 없기 때문에 리다이렉트 하여 쿠키로 토큰을 보냄 (액세스+리프레시)
 * httpOnly 쿠키 저장 시 xss 공격은 막을 수 있지만, csrf 공격에 취약하다
 * -> 백엔드 서버로 재요청하여 헤더에 담아서 보냄. 프론트엔드는 로컬스토리지에 액세스 토큰 저장
 */
@RestController
public class OAuthController {
    @PostMapping("/oauth2-jwt-header")
    public String oauth2Redirect(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String access = null;
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("access")){
                access = cookie.getValue();
            }
        }
        if(access == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "bad";
        }
        // 클라이언트의 access 토큰 쿠키를 만료
        response.addCookie(createCookie("access", null));
        response.addHeader("access", access);
        response.setStatus(HttpServletResponse.SC_OK);
        return "success";
    }
    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
