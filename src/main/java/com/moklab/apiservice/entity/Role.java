package com.moklab.apiservice.entity;

/**
 * 사용자 권한(역할)을 정의하는 열거형(Enum)
 * 
 * 이 enum은 시스템 내에서 사용자의 권한 수준을 나타냅니다:
 * 
 * 1. USER: 일반 사용자 권한
 *    - 기본적인 서비스 이용 가능
 *    - 자신의 프로필 조회/수정
 *    - 일반적인 API 접근 권한
 * 
 * 2. ADMIN: 관리자 권한  
 *    - 모든 사용자 정보 조회/수정
 *    - 시스템 관리 기능 접근
 *    - 관리자 전용 API 접근 권한
 * 
 * Spring Security에서 이 값들을 "ROLE_USER", "ROLE_ADMIN" 형태로 사용합니다.
 * 
 * @PreAuthorize("hasRole('USER')") : USER 권한 이상 필요
 * @PreAuthorize("hasRole('ADMIN')") : ADMIN 권한 필요
 */
public enum Role {
    /**
     * 일반 사용자 권한
     * - 회원가입 시 기본적으로 할당되는 권한
     * - 기본적인 서비스 이용 가능
     */
    USER,
    
    /**
     * 관리자 권한
     * - 시스템 관리 기능 접근 가능
     * - 다른 사용자 정보 관리 가능
     */
    ADMIN
}
