package com.moklab.apiservice.controller;

import com.moklab.apiservice.dto.JwtResponse;
import com.moklab.apiservice.dto.LoginRequest;
import com.moklab.apiservice.dto.SignupRequest;
import com.moklab.apiservice.entity.User;
import com.moklab.apiservice.repository.UserRepository;
import com.moklab.apiservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 인증(로그인/회원가입) 관련 REST API 컨트롤러
 * 
 * 이 컨트롤러는 다음과 같은 기능을 제공합니다:
 * 
 * 1. 사용자 회원가입 (POST /api/auth/signup)
 *    - 새로운 사용자 계정 생성
 *    - 사용자명/이메일 중복 체크
 *    - 비밀번호 암호화 저장
 * 
 * 2. 사용자 로그인 (POST /api/auth/signin)
 *    - 사용자명/비밀번호 검증
 *    - JWT 토큰 생성 및 반환
 *    - 클라이언트 인증 정보 제공
 * 
 * 보안 설정:
 * - CORS 허용: 모든 도메인에서 접근 가능 (*주의: 운영환경에서는 특정 도메인만 허용 권장)
 * - 인증 불필요: /api/auth/** 경로는 SecurityConfig에서 permitAll() 설정
 * 
 * Swagger 문서화:
 * - @Tag: API 그룹핑 및 설명
 * - @Operation: 각 API 메서드 설명
 * - @ApiResponses: 응답 코드별 설명
 */
@RestController  // JSON 응답을 자동으로 처리하는 컨트롤러
@RequestMapping("/api/auth")  // 기본 URL 경로 설정
@CrossOrigin(origins = "*", maxAge = 3600)  // CORS 정책 설정 (모든 도메인 허용)
@Tag(name = "Authentication", description = "사용자 인증 API")  // Swagger 문서화용 태그
public class AuthController {

    /**
     * Spring Security의 인증 관리자
     * - 사용자 인증(로그인) 처리를 담당
     * - UserDetailsService와 PasswordEncoder를 사용하여 인증 수행
     */
    @Autowired
    AuthenticationManager authenticationManager;

    
    /**
     * 사용자 데이터 접근을 위한 Repository
     * - 사용자 조회, 저장, 중복 체크 등에 사용
     */
    @Autowired
    UserRepository userRepository;

    /**
     * 비밀번호 암호화를 위한 인코더
     * - BCryptPasswordEncoder 사용 (SecurityConfig에서 빈 등록)
     * - 회원가입 시 평문 비밀번호를 암호화하여 저장
     */
    @Autowired
    PasswordEncoder encoder;

    /**
     * JWT 토큰 생성 및 검증을 위한 유틸리티
     * - 로그인 성공 시 JWT 토큰 생성
     * - API 요청 시 토큰 검증
     */
    @Autowired
    JwtUtil jwtUtil;

    
    /**
     * 사용자 로그인 API
     * 
     * 로그인 처리 과정:
     * 1. 클라이언트로부터 사용자명과 비밀번호를 받음
     * 2. Spring Security의 AuthenticationManager로 인증 처리
     * 3. UserDetailsService에서 사용자 정보 조회
     * 4. PasswordEncoder로 비밀번호 검증
     * 5. 인증 성공 시 JWT 토큰 생성
     * 6. 사용자 정보와 함께 토큰 반환
     * 
     * @param loginRequest 로그인 요청 정보 (사용자명, 비밀번호)
     * @return ResponseEntity JWT 토큰과 사용자 정보 또는 에러 메시지
     * 
     * 성공 응답 (200):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "type": "Bearer",
     *   "username": "testuser",
     *   "email": "test@example.com"
     * }
     * 
     * 실패 응답 (401): 인증 실패 (잘못된 사용자명 또는 비밀번호)
     */
    @PostMapping("/signin")  // POST /api/auth/signin
    @Operation(summary = "사용자 로그인", description = "사용자명과 비밀번호로 로그인하여 JWT 토큰을 받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 1. 사용자명과 비밀번호로 인증 토큰 생성
        // UsernamePasswordAuthenticationToken: Spring Security의 인증 토큰
        // AuthenticationManager가 이 토큰을 사용하여 실제 인증 수행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. 인증 성공 시 Principal에서 사용자 정보 추출
        // Principal: 인증된 사용자의 정보를 담고 있는 객체
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // 3. 인증된 사용자 정보로 JWT 토큰 생성
        String jwt = jwtUtil.generateToken(userDetails);

        // 4. 추가 사용자 정보 조회 (응답에 포함하기 위해)
        User user = userRepository.findByUsername(userDetails.getUsername()).get();

        // 5. JWT 토큰과 사용자 기본 정보를 포함한 응답 생성
        return ResponseEntity.ok(new JwtResponse(jwt, user.getUsername(), user.getEmail()));
    }

    
    /**
     * 사용자 회원가입 API
     * 
     * 회원가입 처리 과정:
     * 1. 클라이언트로부터 회원가입 정보를 받음
     * 2. 사용자명 중복 체크
     * 3. 이메일 중복 체크
     * 4. 비밀번호 암호화 (BCrypt)
     * 5. 새로운 사용자 계정 생성 및 저장
     * 6. 성공 메시지 반환
     * 
     * @param signUpRequest 회원가입 요청 정보 (사용자명, 비밀번호, 이메일)
     * @return ResponseEntity 성공 메시지 또는 에러 메시지
     * 
     * 성공 응답 (200): "User registered successfully!"
     * 실패 응답 (400): 
     * - "Error: Username is already taken!" (사용자명 중복)
     * - "Error: Email is already in use!" (이메일 중복)
     * 
     * 보안 고려사항:
     * - 비밀번호는 BCrypt로 암호화하여 저장 (평문 저장 절대 금지)
     * - 사용자명과 이메일은 고유성 보장 (UNIQUE 제약조건)
     * - 기본 권한은 USER로 설정 (관리자 권한은 별도 처리)
     */
    @PostMapping("/signup")  // POST /api/auth/signup
    @Operation(summary = "사용자 회원가입", description = "새로운 사용자 계정을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "사용자명 또는 이메일이 이미 사용 중")
    })
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        
        // 1. 사용자명 중복 체크
        // 동일한 사용자명이 이미 존재하는지 확인
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()  // HTTP 400 Bad Request
                    .body("Error: Username is already taken!");
        }

        // 2. 이메일 중복 체크
        // 동일한 이메일이 이미 등록되어 있는지 확인
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()  // HTTP 400 Bad Request
                    .body("Error: Email is already in use!");
        }

        // 3. 새로운 사용자 계정 생성
        // 중요: 비밀번호는 반드시 암호화하여 저장!
        User user = new User(
                signUpRequest.getUsername(),           // 사용자명
                encoder.encode(signUpRequest.getPassword()),  // 암호화된 비밀번호
                signUpRequest.getEmail()               // 이메일
        );
        // 기본 권한(USER)과 활성화 상태(true)는 User 생성자에서 자동 설정

        // 4. 데이터베이스에 사용자 정보 저장
        // MyBatis의 insert 쿼리 실행, 자동 생성된 ID가 user.id에 설정됨
        userRepository.save(user);

        // 5. 성공 응답 반환
        return ResponseEntity.ok("User registered successfully!");
    }
}
