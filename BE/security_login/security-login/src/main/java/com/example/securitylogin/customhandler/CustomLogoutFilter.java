package com.example.securitylogin.customhandler;

import com.example.securitylogin.jwt.JWTUtil;
import com.example.securitylogin.repository.RefreshRepository;
import com.example.securitylogin.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;

/**
 * 로그아웃 필터
 * refresh 토큰 만료
 */
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        // uri check
        if (!requestURI.matches("^\\/logout$")) {
            chain.doFilter(request, response);
            return;
        }
        // method check
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        // refresh token validation
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        refresh = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("refresh"))
                .findFirst().get().getValue();

        System.out.println("refresh = " + refresh);

        // refresh token null
        if(refresh == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String category = jwtUtil.getCategory(refresh);

        // not refresh token
        if(!category.equals("refresh")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);

        // not exist in DB
        if(!isExist){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // logout
        refreshRepository.deleteByRefresh(refresh);

        Cookie cookie = CookieUtil.createCookie("refresh", null, 0);
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
