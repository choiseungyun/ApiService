package com.moklab.apiservice.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 사용자 엔티티 클래스
 * 
 * 이 클래스는 다음과 같은 역할을 수행합니다:
 * 1. 데이터베이스의 users 테이블과 매핑되는 Java 객체 (POJO - Plain Old Java Object)
 * 2. Spring Security의 UserDetails 인터페이스 구현으로 인증/인가 정보 제공
 * 3. 사용자의 기본 정보(사용자명, 비밀번호, 이메일, 권한 등)를 저장
 * 
 * UserDetails 인터페이스를 구현함으로써:
 * - Spring Security가 이 객체를 사용자 정보로 인식
 * - 로그인 인증 시 자동으로 사용자 정보와 권한을 처리
 * - JWT 토큰 생성 시 사용자 정보 제공
 */
public class User implements UserDetails {
    
    // === 데이터베이스 테이블과 매핑되는 필드들 ===
    
    /**
     * 사용자 고유 ID (기본 키)
     * - 데이터베이스에서 AUTO_INCREMENT로 자동 생성
     * - 각 사용자를 유일하게 식별하는 값
     */
    private Long id;
    
    /**
     * 사용자명 (로그인 ID)
     * - 로그인 시 사용하는 고유한 사용자 식별자
     * - 데이터베이스에서 UNIQUE 제약조건 설정
     */
    private String username;
    
    /**
     * 비밀번호
     * - BCrypt로 암호화되어 저장
     * - 평문으로 저장하면 보안상 매우 위험함
     */
    private String password;
    
    /**
     * 이메일 주소
     * - 사용자 연락처 및 비밀번호 찾기 등에 사용
     * - 데이터베이스에서 UNIQUE 제약조건 설정
     */
    private String email;
    
    /**
     * 사용자 권한 (역할)
     * - USER: 일반 사용자 권한
     * - ADMIN: 관리자 권한
     * - 기본값은 USER로 설정
     */
    private Role role = Role.USER;
    
    /**
     * 계정 활성화 여부
     * - true: 활성화된 계정 (로그인 가능)
     * - false: 비활성화된 계정 (로그인 불가)
     * - 계정 정지나 탈퇴 시 false로 설정하여 논리적 삭제 구현
     */
    private boolean enabled = true;
    
    // === 생성자들 ===
    
    /**
     * 기본 생성자
     * - MyBatis가 객체 생성 시 사용
     * - 매개변수 없는 생성자가 반드시 필요함
     */
    public User() {}
    
    /**
     * 사용자 정보를 받아 객체를 생성하는 생성자
     * - 회원가입 시 사용자 정보로 User 객체 생성에 사용
     * 
     * @param username 사용자명
     * @param password 비밀번호 (암호화된 상태로 전달되어야 함)
     * @param email 이메일 주소
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    // === Spring Security UserDetails 인터페이스 구현 메서드들 ===
    // 이 메서드들은 Spring Security가 사용자 인증/인가를 처리할 때 호출됩니다.
    
    /**
     * 사용자의 권한(역할) 목록을 반환
     * 
     * Spring Security에서 사용자의 권한을 확인할 때 호출됩니다.
     * - "ROLE_" 접두사를 붙여서 Spring Security 규칙에 맞게 권한 생성
     * - 예: Role.USER → "ROLE_USER", Role.ADMIN → "ROLE_ADMIN"
     * 
     * @return 사용자 권한 목록 (GrantedAuthority 컬렉션)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    /**
     * 계정이 만료되지 않았는지 확인
     * 
     * @return true: 계정이 만료되지 않음 (현재 구현에서는 항상 true)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    /**
     * 계정이 잠겨있지 않은지 확인
     * 
     * @return true: 계정이 잠겨있지 않음 (현재 구현에서는 항상 true)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    /**
     * 자격 증명(비밀번호)이 만료되지 않았는지 확인
     * 
     * @return true: 비밀번호가 만료되지 않음 (현재 구현에서는 항상 true)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    /**
     * 계정이 활성화되어 있는지 확인
     * 
     * @return enabled 필드 값 (true: 활성화, false: 비활성화)
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    // === Getter와 Setter 메서드들 ===
    // 객체의 필드에 접근하고 수정하기 위한 메서드들
    // MyBatis와 Spring이 리플렉션을 통해 이 메서드들을 사용합니다.
    
    /**
     * 사용자 ID 조회
     * @return 사용자의 고유 ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * 사용자 ID 설정
     * @param id 설정할 사용자 ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 사용자명 조회 (UserDetails 인터페이스 구현)
     * Spring Security가 로그인 인증 시 사용하는 메서드
     * @return 사용자명
     */
    @Override
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
     * 비밀번호 조회 (UserDetails 인터페이스 구현)
     * Spring Security가 로그인 인증 시 사용하는 메서드
     * @return 암호화된 비밀번호
     */
    @Override
    public String getPassword() {
        return password;
    }
    
    /**
     * 비밀번호 설정
     * @param password 설정할 비밀번호 (반드시 암호화된 상태여야 함)
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * 이메일 주소 조회
     * @return 사용자의 이메일 주소
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
    
    /**
     * 사용자 권한(역할) 조회
     * @return 사용자의 권한 (USER 또는 ADMIN)
     */
    public Role getRole() {
        return role;
    }
    
    /**
     * 사용자 권한(역할) 설정
     * @param role 설정할 권한 (USER 또는 ADMIN)
     */
    public void setRole(Role role) {
        this.role = role;
    }
    
    /**
     * 계정 활성화 상태 설정
     * @param enabled true: 활성화, false: 비활성화
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
