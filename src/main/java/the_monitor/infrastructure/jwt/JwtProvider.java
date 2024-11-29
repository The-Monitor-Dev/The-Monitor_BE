package the_monitor.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import the_monitor.application.service.AccountService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.Account;
import the_monitor.infrastructure.security.CustomUserDetails;

@Component
public class JwtProvider {

    private final Key key;
    private final Long ACCESS_TOKEN_EXPIRE_TIME;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;


    public JwtProvider(@Value("${jwt.secret_key}") String secretKey,
                       @Value("${jwt.access_token_expire}") Long accessTokenExpire,
                       @Value("${jwt.refresh_token_expire}") Long refreshTokenExpire,
                       @Lazy AccountService accountService) {

        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTokenExpire;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTokenExpire;


    }

    public String generateAccessToken(Account account) {

        Date expiredAt = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .claim("account_id", account.getId())
                .claim("email", account.getEmail())
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateRefreshToken(Account account) {

        Date expiredAt = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .claim("account_id", account.getId())
                .claim("email", account.getEmail())
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public Long getAccountId(String token) {

        Claims claims = getClaimsFromToken(token);
        return claims.get("account_id", Long.class);

    }

    public void setAccessTokenInCookie(String accessToken, HttpServletResponse response) {

        // accessToken을 쿠키에 설정
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);   // 클라이언트 측 접근 방지
        accessTokenCookie.setSecure(true);     // HTTPS에서만 전송
        accessTokenCookie.setPath("/");        // 전체 경로에서 접근 가능
        response.addCookie(accessTokenCookie); // 쿠키 설정 추가

        response.addHeader("Set-Cookie", String.format(
                "%s=%s; Path=%s; HttpOnly; Secure; SameSite=None",
                accessTokenCookie.getName(),
                accessTokenCookie.getValue(),
                accessTokenCookie.getPath()
        ));

    }

    public void storeRefreshTokenInSession(Account account, HttpSession session) {
        String refreshToken = generateRefreshToken(account);
        session.setAttribute("refreshToken", refreshToken);
    }

    public String validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return "VALID";
        } catch (ExpiredJwtException e) {
            return "EXPIRED";
        } catch (SignatureException | MalformedJwtException e) {
            return "INVALID";
        }
    }

    public Claims getClaimsFromToken(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ApiException(ErrorStatus._JWT_EXPIRED);
        }

    }

    public Authentication getAuthenticationFromToken(String token) {

        Claims claims = getClaimsFromToken(token);
        Long accountId = claims.get("account_id", Long.class);
        String email = claims.get("email", String.class);

        CustomUserDetails userDetails = new CustomUserDetails(accountId, email, new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());

    }


    public Authentication refreshAccessToken(String refreshToken, HttpServletResponse response, Account account) {

        if ("VALID".equals(validateToken(refreshToken))) {

            // 새 accessToken 생성
            String newAccessToken = generateAccessToken(account);
            setAccessTokenInCookie(newAccessToken, response);

            // 인증 객체 생성 및 반환
            return getAuthenticationFromToken(newAccessToken);

        }

        return null;

    }

}