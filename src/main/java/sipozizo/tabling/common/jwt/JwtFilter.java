package sipozizo.tabling.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sipozizo.tabling.security.CustomUserDetailService;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
        String authorization = request.getHeader("Authorization");

//        기존 코드 문제점: ① SecurityConfig에서 설정된 예외 URI 로직을 한 번 더 처리함. ② JwtFilter부분에 해당 로직을 처리하려면, 바로 건너띄지 못함
//        그래서 로직을 이중 if문을 통해 구성했습니다.
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.extractUserId(token);
                UserDetails userDetails = customUserDetailService.loadUserByUserId(userId);

                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            } else {
                // 토큰 검증에 실패한 경우 로그만 남기고, 인증 정보는 설정하지 않음
                log.warn("Invalid JWT token.");
            }
        }

        filterChain.doFilter(request, response);


    }
}
