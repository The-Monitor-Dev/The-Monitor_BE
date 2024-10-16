package the_monitor.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.Account;

@Component
public class JwtProvider {

    private final Key key;
    private final Long ACCESS_TOKEN_EXPIRE_TIME;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;

    // JWT Provider 생성자
    public JwtProvider(@Value("${jwt.secret_key}") String secretKey,
                       @Value("${jwt.access_token_expire}") Long accessTokenExpire,
                       @Value("${jwt.refresh_token_expire}") Long refreshTokenExpire) {

        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);  // 안전한 256비트 비밀 키 생성
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTokenExpire;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTokenExpire;

    }

    public void setAddCookieToken(Account account, HttpServletResponse response) {

        // AccessToken과 RefreshToken 생성
        String accessToken = generateAccessToken(account);
        String refreshToken = generateRefreshToken(account);

        int accessTokenExpireTime = Math.toIntExact(ACCESS_TOKEN_EXPIRE_TIME / 1000);
        int refreshTokenExpireTime = Math.toIntExact(REFRESH_TOKEN_EXPIRE_TIME / 1000);

        // 쿠키에 AccessToken 저장
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true); // HttpOnly 설정
        accessTokenCookie.setSecure(true);   // HTTPS에서만 전송
        accessTokenCookie.setPath("/");      // 모든 경로에서 쿠키 접근 가능
        accessTokenCookie.setMaxAge(accessTokenExpireTime); // 만료 시간 설정

        // 쿠키에 RefreshToken 저장
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(refreshTokenExpireTime);

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

    }

    /**
     * JWT 토큰 생성 (회원가입 또는 로그인 후 호출)
     */
    public String generateAccessToken(Account account) {

        Date expiredAt = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .claim("account_id", account.getId())  // User의 ID를 포함
                .claim("email", account.getEmail())  // User의 이메일 포함
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateRefreshToken(Account account) {

        Date expiredAt = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .claim("account_id", account.getId())  // User의 ID를 포함
                .claim("email", account.getEmail())  // User의 이메일 포함
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    /**
     * JWT 토큰에서 User ID 추출
     */
    public Long getAccountId(String token) {
        return parseClaims(token).get("account_id", Long.class);
    }

    /**
     * JWT 토큰에서 Claims(토큰 정보)를 파싱
     */
    public Claims parseClaims(String token) {

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

    /**
     * JWT 토큰 유효성 검증
     */
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

    /**
     * JWT 토큰을 기반으로 Authentication 객체 생성 (User 정보 포함)
     */
    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);

        // JWT에서 클레임을 가져옵니다.
        Long accountId = claims.get("account_id", Long.class);
        String email = claims.get("email", String.class);

        // 사용자 정보 기반으로 Authentication 객체 생성
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(email, "", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());

    }

    /**
     * SecurityContext에 인증 객체 설정
     */
    private void setContextHolder(Authentication authentication) {

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    /**
     * JWT 토큰의 만료 시간을 가져옵니다.
     */
    public Long getExpiration(String token) {

        Date expiration = Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;

    }

}