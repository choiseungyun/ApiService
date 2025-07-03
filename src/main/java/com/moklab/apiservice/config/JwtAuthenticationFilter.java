package com.moklab.apiservice.config;

import com.moklab.apiservice.service.UserDetailsServiceImpl;
import com.moklab.apiservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰 인증을 처리하는 필터 클래스
 * 
 * 이 필터는 모든 HTTP 요청에 대해 JWT 토큰을 검증합니다.
 * Spring Security의 필터 체인에서 UsernamePasswordAuthenticationFilter 이전에 실행됩니다.
 * 
 * 필터 동작 과정:
 * 1. HTTP 요청에서 Authorization 헤더 추출
 * 2. "Bearer " 접두사 확인 및 JWT 토큰 파싱
 * 3. JWT 토큰에서 사용자명 추출
 * 4. 데이터베이스에서 사용자 정보 조회
 * 5. 토큰 유효성 검증 (서명, 만료시간)
 * 6. 유효한 토큰이면 SecurityContext에 인증 정보 설정
 * 7. 다음 필터로 요청 전달
 * 
 * OncePerRequestFilter 상속 이유:
 * - 하나의 요청당 한 번만 실행되도록 보장
 * - 내부 redirect나 forward 시 중복 실행 방지
 * 
 * @author API 개발팀
 * @since 1.0
 */
@Component // Spring Bean으로 등록
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT 토큰은 "Bearer " 로 시작
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (Exception e) {
                logger.error("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // 토큰을 가져왔다면 검증
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 토큰이 유효하다면 스프링 시큐리티에 인증 설정
            if (jwtUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
