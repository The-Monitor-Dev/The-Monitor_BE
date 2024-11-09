package the_monitor.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import the_monitor.application.service.AccountService;
import the_monitor.domain.model.Account;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AccountService accountService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String accessToken = null;

        // Retrieve accessToken from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
            }
        }

        if (accessToken != null) {
            String tokenStatus = jwtProvider.validateToken(accessToken);

            if ("VALID".equals(tokenStatus)) {
                // accessToken이 유효한 경우, 인증 정보 설정
                Authentication authentication = jwtProvider.getAuthenticationFromToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else if ("EXPIRED".equals(tokenStatus) && session != null) {
                // accessToken이 만료된 경우, refreshToken으로 새로운 accessToken 발급
                String refreshToken = (String) session.getAttribute("refreshToken");
                Authentication authentication = jwtProvider.refreshAccessToken(refreshToken, response);

                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } else if ("INVALID".equals(tokenStatus)) {
                // accessToken이 불일치하거나 손상된 경우, 인증 정보 설정 없이 요청 통과 (로그인 요청 등 처리)
                SecurityContextHolder.clearContext();
            }
        }

        // 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }
}