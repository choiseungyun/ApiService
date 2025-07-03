package com.moklab.apiservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 회원가입 요청을 위한 DTO (Data Transfer Object) 클래스
 * 
 * 이 클래스는 회원가입 API에서 사용됩니다:
 * - POST /api/auth/signup 엔드포인트의 요청 본문
 * - 새로운 사용자 계정 생성에 필요한 정보를 전달
 * 
 * 요청 예시:
 * {
 *   "username": "testuser",
 *   "password": "password123",
 *   "email": "test@example.com"
 * }
 * 
 * 검증 규칙:
 * - username: 고유해야 함, null/빈값 불허
 * - password: 적절한 강도 권장 (최소 8자 이상)
 * - email: 유효한 이메일 형식, 고유해야 함
 * 
 * 보안 고려사항:
 * - 비밀번호는 평문으로 전송되므로 HTTPS 필수
 * - 서버에서 BCrypt로 암호화하여 저장
 * - 중복 체크를 통한 무결성 보장
 */
@Schema(description = "회원가입 요청 DTO")
public class SignupRequest {
    
    
    /**
     * 회원가입할 사용자명
     * - 로그인 시 사용할 고유한 식별자
     * - 시스템 내에서 중복될 수 없음
     * - 영문, 숫자, 일부 특수문자 조합 권장
     */
    @Schema(description = "사용자명", example = "testuser", required = true)
    private String username;
    
    /**
     * 회원가입할 비밀번호
     * - 평문으로 전송되어 서버에서 BCrypt로 암호화
     * - 보안을 위해 적절한 복잡도 권장 (8자 이상, 영문+숫자+특수문자)
     * - HTTPS 환경에서만 전송되어야 함
     */
    @Schema(description = "비밀번호", example = "password123", required = true)
    private String password;
    
    /**
     * 회원가입할 이메일 주소
     * - 사용자 연락처 및 계정 복구용
     * - 유효한 이메일 형식이어야 함
     * - 시스템 내에서 중복될 수 없음
     * - 향후 이메일 인증 기능 추가 시 활용 가능
     */
    @Schema(description = "이메일 주소", example = "test@example.com", required = true)
    private String email;

    /**
     * 기본 생성자
     * - JSON 역직렬화를 위해 필요
     * - Spring Boot가 HTTP 요청을 Java 객체로 변환할 때 사용
     */
    public SignupRequest() {}

    /**
     * 모든 필드를 초기화하는 생성자
     * - 테스트 코드나 직접 객체 생성 시 편의를 위해 제공
     * 
     * @param username 사용자명
     * @param password 비밀번호 (평문)
     * @param email 이메일 주소
     */
    public SignupRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // === Getter/Setter 메서드들 ===
    // JSON 직렬화/역직렬화와 Spring 데이터 바인딩을 위해 필요

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
