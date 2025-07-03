package com.moklab.apiservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 보안 설정 클래스
 * 
 * 이 클래스는 애플리케이션의 보안 정책을 정의합니다:
 * 
 * 주요 기능:
 * 1. JWT 기반 인증 설정
 * 2. API 엔드포인트별 접근 권한 제어
 * 3. 비밀번호 암호화 방식 설정
 * 4. CSRF 보호 비활성화 (REST API용)
 * 5. 세션 관리 정책 설정 (Stateless)
 * 
 * 보안 아키텍처:
 * 1. 클라이언트가 JWT 토큰으로 요청
 * 2. JwtAuthenticationFilter에서 토큰 검증
 * 3. 유효한 토큰이면 SecurityContext에 인증 정보 설정
 * 4. 각 API 엔드포인트의 권한 확인
 * 5. 접근 허용/거부 결정
 * 
 * 접근 제어 정책:
 * - /api/auth/** : 로그인/회원가입 (인증 불필요)
 * - /api/public/** : 공개 API (인증 불필요)
 * - /swagger-ui/** : API 문서 (인증 불필요)
 * - 그 외 모든 API : JWT 토큰 인증 필수
 */
@Configuration  // Spring 설정 클래스임을 명시
@EnableWebSecurity  // Spring Security 웹 보안 활성화
@EnableMethodSecurity(prePostEnabled = true)  // 메서드 레벨 보안 활성화 (@PreAuthorize, @PostAuthorize 등 사용 가능)
public class SecurityConfig {

    
    /**
     * JWT 인증 실패 시 처리를 담당하는 핸들러
     * - 인증되지 않은 요청이 보호된 리소스에 접근할 때 호출
     * - 일반적으로 401 Unauthorized 응답을 생성
     * - JwtAuthenticationEntryPoint 클래스에서 구현
     */
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * 모든 HTTP 요청을 가로채서 JWT 토큰을 검증하는 필터
     * - 요청 헤더에서 Authorization: Bearer {token} 추출
     * - JWT 토큰의 유효성 검증 (서명, 만료시간 등)
     * - 유효한 토큰이면 SecurityContext에 인증 정보 설정
     * - JwtAuthenticationFilter 클래스에서 구현
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    
    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈 설정
     * 
     * BCryptPasswordEncoder 사용 이유:
     * 1. 단방향 해시 함수 - 암호화된 비밀번호를 원문으로 복원 불가능
     * 2. Salt 자동 생성 - 동일한 비밀번호라도 매번 다른 해시값 생성
     * 3. 적응형 비용 - 하드웨어 성능 향상에 따라 해시 복잡도 조정 가능
     * 4. 무지개 테이블 공격 방지 - Salt로 인해 사전 계산된 해시 테이블 무효화
     * 
     * 사용 시점:
     * - 회원가입 시: 평문 비밀번호를 암호화하여 데이터베이스 저장
     * - 로그인 시: 입력된 평문 비밀번호와 저장된 암호화 비밀번호 비교
     * 
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security의 인증 관리자 빈 설정
     * 
     * AuthenticationManager의 역할:
     * 1. 사용자 인증 요청 처리 (로그인 시)
     * 2. UserDetailsService를 호출하여 사용자 정보 조회
     * 3. PasswordEncoder로 비밀번호 검증
     * 4. 인증 성공/실패 결정
     * 5. 인증 성공 시 Authentication 객체 생성
     * 
     * 사용 시점:
     * - AuthController의 로그인 API에서 사용
     * - authenticationManager.authenticate() 메서드 호출
     * 
     * @param config Spring Security 인증 설정
     * @return AuthenticationManager 인스턴스
     * @throws Exception 설정 오류 시 발생
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    
    /**
     * Spring Security의 핵심 보안 필터 체인 설정
     * 
     * 이 메서드는 애플리케이션의 보안 정책을 정의하는 가장 중요한 설정입니다.
     * 모든 HTTP 요청이 이 필터 체인을 통과하며, 각 요청의 인증/인가가 결정됩니다.
     * 
     * 설정 순서가 중요하며, 다음과 같은 단계로 구성됩니다:
     * 1. CSRF 보호 설정
     * 2. 요청별 접근 권한 설정
     * 3. 예외 처리 설정
     * 4. 세션 관리 정책 설정
     * 5. JWT 필터 추가
     * 
     * @param http HttpSecurity 설정 객체
     * @return SecurityFilterChain 보안 필터 체인
     * @throws Exception 설정 오류 시 발생
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        // 1. CSRF (Cross-Site Request Forgery) 보호 비활성화
        // REST API는 상태가 없고(Stateless) JWT 토큰으로 인증하므로 CSRF 공격에 취약하지 않음
        // 또한 CSRF 토큰이 있으면 API 클라이언트(모바일 앱, SPA 등)에서 사용하기 복잡함
        http.csrf(csrf -> csrf.disable())
                
                // 2. HTTP 요청별 접근 권한 설정
                .authorizeHttpRequests(authz -> authz
                        // 인증 관련 API는 누구나 접근 가능 (로그인, 회원가입)
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // 공개 API는 누구나 접근 가능 (공지사항, 상품 목록 등)
                        .requestMatchers("/api/public/**").permitAll()
                        
                        // Swagger UI 관련 경로는 개발/테스트를 위해 접근 허용
                        // 운영환경에서는 보안을 위해 제거하거나 별도 인증 설정 권장
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        
                        // 위에서 명시하지 않은 모든 요청은 인증 필요
                        // JWT 토큰이 있고 유효해야만 접근 가능
                        .anyRequest().authenticated()
                )
                
                // 3. 인증 실패 시 예외 처리 설정
                // 인증되지 않은 사용자가 보호된 리소스에 접근할 때의 처리 방식
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                
                // 4. 세션 관리 정책을 Stateless로 설정
                // JWT 토큰 기반 인증에서는 서버에 세션을 저장하지 않음
                // 각 요청마다 토큰을 검증하여 인증 상태 확인
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 5. JWT 인증 필터를 Spring Security 필터 체인에 추가
        // UsernamePasswordAuthenticationFilter 이전에 실행되도록 설정
        // 모든 요청에서 JWT 토큰을 먼저 검증하고, 유효하면 SecurityContext에 인증 정보 설정
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 6. 설정된 보안 필터 체인 빌드 및 반환
        return http.build();
    }
}
