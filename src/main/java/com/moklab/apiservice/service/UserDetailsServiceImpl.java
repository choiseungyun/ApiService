package com.moklab.apiservice.service;

import com.moklab.apiservice.entity.User;
import com.moklab.apiservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security 사용자 인증을 위한 UserDetailsService 구현 클래스
 * 
 * 이 클래스는 Spring Security의 핵심 인증 과정에서 사용됩니다:
 * 
 * 1. 인증 과정에서의 역할:
 *    - 사용자가 로그인을 시도할 때 호출됨
 *    - 사용자명으로 데이터베이스에서 사용자 정보를 조회
 *    - 조회된 사용자 정보를 Spring Security가 인식할 수 있는 UserDetails 형태로 반환
 * 
 * 2. Spring Security 통합:
 *    - AuthenticationManager가 자동으로 이 서비스를 호출
 *    - 반환된 UserDetails로 비밀번호 검증 및 권한 확인 수행
 *    - JWT 토큰 생성 시에도 사용자 정보 제공
 * 
 * 3. 예외 처리:
 *    - 사용자를 찾을 수 없으면 UsernameNotFoundException 발생
 *    - Spring Security가 이 예외를 처리하여 인증 실패 응답 생성
 */
@Service  // Spring의 비즈니스 로직 계층 컴포넌트로 등록
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 사용자 데이터 접근을 위한 Repository
     * @Autowired: Spring이 자동으로 UserRepository 구현체를 주입
     */
    @Autowired
    private UserRepository userRepository;
    /**
     * 사용자명으로 사용자 정보를 로드하는 메서드
     * 
     * 이 메서드는 Spring Security의 인증 과정에서 자동으로 호출됩니다:
     * 
     * 1. 호출 시점:
     *    - 사용자가 로그인을 시도할 때
     *    - JWT 토큰 검증 시 사용자 정보가 필요할 때
     *    - @PreAuthorize 등의 권한 체크 시
     * 
     * 2. 처리 과정:
     *    - 데이터베이스에서 사용자명으로 사용자 검색
     *    - 사용자가 존재하면 User 객체 반환 (UserDetails 구현체)
     *    - 사용자가 없으면 UsernameNotFoundException 발생
     * 
     * 3. 반환값 활용:
     *    - Spring Security가 반환된 UserDetails로 비밀번호 검증
     *    - 사용자의 권한 정보(getAuthorities())로 접근 권한 확인
     *    - 계정 상태(isEnabled(), isAccountNonLocked() 등) 확인
     * 
     * @param username 로그인 시 입력한 사용자명
     * @return UserDetails 사용자 정보와 권한을 담은 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 데이터베이스에서 사용자명으로 사용자 조회
        User user = userRepository.findByUsername(username)
                // Optional이 비어있으면 (사용자가 없으면) 예외 발생
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // User 객체는 UserDetails를 구현하므로 그대로 반환
        // Spring Security가 이 객체를 사용하여 인증/인가 처리
        return user;
    }
}
