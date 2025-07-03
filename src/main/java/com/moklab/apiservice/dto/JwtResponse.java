package com.moklab.apiservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * JWT 토큰 응답을 위한 DTO (Data Transfer Object) 클래스
 * 
 * 이 클래스는 로그인 성공 시 클라이언트에게 반환되는 응답 구조를 정의합니다:
 * - POST /api/auth/signin 엔드포인트의 응답 본문
 * - JWT 토큰과 기본 사용자 정보를 포함
 * 
 * 응답 예시:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlci...",
 *   "type": "Bearer",
 *   "username": "testuser",
 *   "email": "test@example.com"
 * }
 * 
 * 클라이언트 사용법:
 * 1. 응답에서 token 값을 추출
 * 2. localStorage나 sessionStorage에 저장
 * 3. 이후 API 요청 시 Authorization 헤더에 "Bearer {token}" 형태로 포함
 * 
 * 보안 고려사항:
 * - 토큰은 브라우저의 안전한 저장소에 보관
 * - XSS 공격 방지를 위해 HttpOnly 쿠키 사용 검토
 * - 토큰 만료 시간 확인 및 갱신 로직 구현 필요
 */
@Schema(description = "JWT 토큰 응답 DTO")
public class JwtResponse {
    
    
    /**
     * JWT 토큰 문자열
     * - 로그인 성공 시 JwtUtil에서 생성된 토큰
     * - 사용자 정보와 만료시간이 암호화되어 포함됨
     * - 클라이언트가 이 값을 저장하고 API 요청 시 사용
     */
    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    /**
     * 토큰 타입 (고정값: "Bearer")
     * - HTTP Authorization 헤더에서 사용되는 인증 방식
     * - "Bearer {token}" 형태로 헤더에 포함
     * - OAuth 2.0 RFC 6750 표준을 따름
     */
    @Schema(description = "토큰 타입", example = "Bearer")
    private String type = "Bearer";
    
    /**
     * 로그인한 사용자의 사용자명
     * - 클라이언트에서 현재 로그인 사용자 표시용
     * - UI에서 "안녕하세요, {username}님" 형태로 활용
     */
    @Schema(description = "사용자명", example = "testuser")
    private String username;
    
    /**
     * 로그인한 사용자의 이메일 주소
     * - 클라이언트에서 사용자 정보 표시용
     * - 프로필 화면이나 설정 화면에서 활용
     */
    @Schema(description = "이메일 주소", example = "test@example.com")
    private String email;

    /**
     * JWT 응답 객체 생성자
     * - 로그인 성공 시 AuthController에서 호출
     * - 필수 정보들을 모두 받아서 응답 객체 구성
     * 
     * @param accessToken 생성된 JWT 토큰 문자열
     * @param username 로그인한 사용자의 사용자명
     * @param email 로그인한 사용자의 이메일 주소
     */
    public JwtResponse(String accessToken, String username, String email) {
        this.token = accessToken;
        this.username = username;
        this.email = email;
        // type은 기본값 "Bearer"로 자동 설정됨
    }

    // === Getter/Setter 메서드들 ===
    // JSON 직렬화를 위해 필요 (Jackson 라이브러리에서 사용)

    /**
     * JWT 토큰 조회
     * @return JWT 토큰 문자열
     */
    public String getToken() {
        return token;
    }

    /**
     * JWT 토큰 설정
     * @param token 설정할 JWT 토큰
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 토큰 타입 조회
     * @return 토큰 타입 (기본값: "Bearer")
     */
    public String getType() {
        return type;
    }

    /**
     * 토큰 타입 설정
     * @param type 설정할 토큰 타입
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 사용자명 조회
     * @return 사용자명
     */
    public String getUsername() {
        return username;
    }

    /**
     * 사용자명 설정
     * @param username 설정할 사용자명
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 이메일 주소 조회
     * @return 이메일 주소
     */
    public String getEmail() {
        return email;
    }

    /**
     * 이메일 주소 설정
     * @param email 설정할 이메일 주소
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
