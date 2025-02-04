package sipozizo.tabling.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sipozizo.tabling.domain.user.enums.UserRole;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private static final String PREFIX = "Bearer ";
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key) // 비밀 키를 사용하여 서명 검증
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(Long id, UserRole role) {
        Date date = new Date();

        String token = Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("role", role) // 사용자 권한 (역할)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간 설정
                .setIssuedAt(date) // 발급 시간 설정
                .signWith(key, signatureAlgorithm) // 비밀 키와 알고리즘으로 서명
                .compact(); // JWT 토큰 생성

        log.info(token);
        return PREFIX + token;
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractAllClaims(token).getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 비밀 키 설정
                    .build() // 파서 빌더 빌드
                    .parseClaimsJws(token); // 토큰 파싱 및 검증
            return true; // 토큰이 유효한 경우
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", e);
        }
        return false;
    }
}
