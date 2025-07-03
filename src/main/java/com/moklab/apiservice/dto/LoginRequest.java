package com.moklab.apiservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 요청을 위한 DTO (Data Transfer Object) 클래스
 * 
 * DTO의 역할:
 * 1. 클라이언트와 서버 간 데이터 전송을 위한 객체
 * 2. API 요청/응답의 구조를 명확하게 정의
 * 3. 엔티티와 분리하여 API 변경 시 영향도 최소화
 * 4. 입력 데이터 검증 및 Swagger 문서화 지원
 * 
 * 이 클래스는 로그인 API에서 사용됩니다:
 * - POST /api/auth/signin 엔드포인트의 요청 본문
 * - JSON 형태로 전송되는 데이터를 Java 객체로 변환
 * 
 * 요청 예시:
 * {
 *   "username": "testuser",
 *   "password": "password123"
 * }
 * 
 * 보안 고려사항:
 * - 비밀번호는 HTTPS를 통해서만 전송되어야 함
 * - 로그에 비밀번호가 출력되지 않도록 주의
 * - 클라이언트에서는 비밀번호를 메모리에 최소한으로 보관
 */
@Schema(description = "로그인 요청 DTO")  // Swagger 문서화용 스키마 설명
public class LoginRequest {
    
    
    /**
     * 로그인에 사용할 사용자명
     * - 회원가입 시 등록한 고유한 사용자 식별자
     * - 대소문자 구분하여 정확히 입력해야 함
     * - null이나 빈 문자열 불허
     */
    @Schema(description = "사용자명", example = "testuser", required = true)
    private String username;
    
    /**
     * 로그인에 사용할 비밀번호
     * - 회원가입 시 설정한 평문 비밀번호
     * - 서버에서 BCrypt로 암호화된 비밀번호와 비교 검증
     * - 보안상 HTTPS 환경에서만 전송되어야 함
     */
    @Schema(description = "비밀번호", example = "password123", required = true)
    private String password;

    /**
     * 기본 생성자
     * - JSON 역직렬화를 위해 필요 (Jackson 라이브러리 사용)
     * - Spring이 요청 JSON을 Java 객체로 변환할 때 사용
     */
    public LoginRequest() {}

    /**
     * 모든 필드를 초기화하는 생성자
     * - 테스트 코드나 직접 객체 생성 시 사용
     * 
     * @param username 사용자명
     * @param password 비밀번호
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // === Getter/Setter 메서드들 ===
    // Spring의 데이터 바인딩과 JSON 직렬화/역직렬화를 위해 필요

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
     * 비밀번호 조회
     * @return 비밀번호 (평문)
     */
    public String getPassword() {
        return password;
    }

    /**
     * 비밀번호 설정
     * @param password 설정할 비밀번호 (평문)
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
