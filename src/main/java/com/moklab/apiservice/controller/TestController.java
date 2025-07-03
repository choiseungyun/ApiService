package com.moklab.apiservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 테스트용 REST API 컨트롤러
 * 
 * 이 컨트롤러는 JWT 인증과 권한 체계를 테스트하기 위한 API 엔드포인트들을 제공합니다.
 * 다양한 보안 레벨의 API를 통해 인증과 인가가 올바르게 동작하는지 확인할 수 있습니다.
 * 
 * 제공되는 엔드포인트:
 * 1. /api/public/hello - 인증 불필요한 공개 API
 * 2. /api/user/profile - USER 또는 ADMIN 권한 필요
 * 3. /api/admin/dashboard - ADMIN 권한만 허용
 * 
 * 테스트 시나리오:
 * 1. 토큰 없이 public API 접근 → 성공
 * 2. 토큰 없이 인증 필요 API 접근 → 401 Unauthorized
 * 3. 유효한 토큰으로 권한에 맞는 API 접근 → 성공
 * 4. 권한이 부족한 토큰으로 API 접근 → 403 Forbidden
 * 
 * 실무 활용 예시:
 * - 신규 개발자의 인증/인가 시스템 이해를 위한 학습용
 * - CI/CD 파이프라인에서 API 보안 검증용
 * - 프론트엔드 개발 시 인증 플로우 테스트용
 * - QA 팀의 보안 테스트 시나리오 기반 제공
 * 
 * @author 개발팀
 * @version 1.0
 * @since 2024-01-01
 * @see AuthController 실제 인증 처리 컨트롤러
 * @see SecurityConfig 보안 설정 클래스
 */
@RestController  // REST API 컨트롤러임을 나타내는 어노테이션
@RequestMapping("/api")  // 모든 요청의 기본 경로를 "/api"로 설정
@CrossOrigin(origins = "*", maxAge = 3600)  // CORS 설정: 모든 origin에서 접근 허용, 1시간 캐시
@Tag(name = "Test API", description = "테스트용 API 엔드포인트")  // Swagger 문서에서 API 그룹화
public class TestController {

    /**
     * 공개 API 엔드포인트 - 인증 불필요
     * 
     * 이 API는 JWT 토큰 없이도 접근 가능한 공개 엔드포인트입니다.
     * SecurityConfig에서 "/api/public/**" 경로를 permitAll()로 설정했기 때문에
     * Spring Security의 인증 검사를 거치지 않습니다.
     * 
     * 사용 사례:
     * - 서비스 상태 확인 (Health Check)
     * - 공개 정보 조회 (공지사항, FAQ 등)
     * - 초기 화면 데이터 (로그인 전 접근 가능한 데이터)
     * 
     * 테스트 방법:
     * curl -X GET http://localhost:8080/api/public/hello
     * 
     * @return 환영 메시지와 타임스탬프를 포함한 응답 맵
     */
    @GetMapping("/public/hello")  // GET 메서드로 "/api/public/hello" 경로 매핑
    @Operation(summary = "공개 엔드포인트", description = "인증 없이 접근 가능한 공개 API입니다.")  // Swagger 문서용 API 설명
    @ApiResponse(responseCode = "200", description = "성공")  // Swagger 응답 코드 문서화
    public Map<String, String> publicEndpoint() {
        // 응답 데이터 구성
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from public endpoint!");  // 환영 메시지
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));  // 현재 시간 (Unix 타임스탬프)
        return response;  // JSON 형태로 자동 변환되어 응답
    }

    /**
     * 사용자 전용 API 엔드포인트 - USER 또는 ADMIN 권한 필요
     * 
     * 이 API는 유효한 JWT 토큰과 USER 또는 ADMIN 권한이 필요합니다.
     * @PreAuthorize 어노테이션을 통해 메서드 레벨에서 권한을 검사합니다.
     * 
     * 권한 검사 과정:
     * 1. JwtAuthenticationFilter에서 토큰 유효성 검증
     * 2. UserDetailsServiceImpl에서 사용자 정보 로드
     * 3. @PreAuthorize에서 SpEL 표현식으로 권한 검사
     * 4. 권한이 있으면 메서드 실행, 없으면 403 Forbidden 반환
     * 
     * SpEL 표현식 설명:
     * - hasRole('USER'): ROLE_USER 권한 보유 여부 확인
     * - hasRole('ADMIN'): ROLE_ADMIN 권한 보유 여부 확인
     * - or 연산자: 둘 중 하나라도 만족하면 통과
     * 
     * 테스트 방법:
     * curl -X GET http://localhost:8080/api/user/profile \
     *      -H "Authorization: Bearer {JWT_TOKEN}"
     * 
     * @return 사용자 프로필 정보와 타임스탬프를 포함한 응답 맵
     */
    @GetMapping("/user/profile")  // GET 메서드로 "/api/user/profile" 경로 매핑
    @Operation(summary = "사용자 프로필", description = "로그인한 사용자의 프로필 정보를 조회합니다.")  // Swagger API 설명
    @SecurityRequirement(name = "Bearer Authentication")  // Swagger에서 이 API가 JWT 토큰 필요함을 표시
    @ApiResponses(value = {  // 가능한 응답 코드들을 Swagger에 문서화
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")  // USER 또는 ADMIN 권한 필요
    public Map<String, String> userAccess() {
        // 응답 데이터 구성
        Map<String, String> response = new HashMap<>();
        response.put("message", "User Content");  // 사용자 전용 메시지
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));  // 현재 시간
        return response;
    }

    /**
     * 관리자 전용 API 엔드포인트 - ADMIN 권한만 허용
     * 
     * 이 API는 오직 ADMIN 권한을 가진 사용자만 접근할 수 있습니다.
     * 가장 높은 보안 레벨의 API로, 관리자 전용 기능들을 제공합니다.
     * 
     * 사용 사례:
     * - 시스템 관리자 대시보드
     * - 사용자 관리 (생성, 수정, 삭제)
     * - 시스템 설정 변경
     * - 감사 로그 조회
     * 
     * 보안 고려사항:
     * - ADMIN 권한은 신중하게 부여해야 함
     * - 관리자 행위는 별도 로깅 필요
     * - 민감한 작업은 추가 인증 단계 고려
     * 
     * 권한 부족 시 응답:
     * - USER 권한만 있는 경우: 403 Forbidden
     * - 토큰이 없는 경우: 401 Unauthorized
     * - 토큰이 유효하지 않은 경우: 401 Unauthorized
     * 
     * 테스트 방법:
     * curl -X GET http://localhost:8080/api/admin/dashboard \
     *      -H "Authorization: Bearer {ADMIN_JWT_TOKEN}"
     * 
     * @return 관리자 대시보드 정보와 타임스탬프를 포함한 응답 맵
     */
    @GetMapping("/admin/dashboard")  // GET 메서드로 "/api/admin/dashboard" 경로 매핑
    @Operation(summary = "관리자 대시보드", description = "관리자 권한이 필요한 대시보드 정보를 조회합니다.")  // Swagger API 설명
    @SecurityRequirement(name = "Bearer Authentication")  // Swagger에서 JWT 토큰 필요함을 표시
    @ApiResponses(value = {  // 가능한 응답 코드들을 Swagger에 문서화
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")  // 권한 부족 시 403 에러
    })
    @PreAuthorize("hasRole('ADMIN')")  // ADMIN 권한만 허용
    public Map<String, String> adminAccess() {
        // 응답 데이터 구성
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin Dashboard");  // 관리자 전용 메시지
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));  // 현재 시간
        return response;
    }
    
    /**
     * API 테스트 가이드
     * 
     * 1. 공개 API 테스트:
     *    GET /api/public/hello
     *    → 토큰 없이도 접근 가능, 200 OK 응답
     * 
     * 2. 사용자 API 테스트:
     *    POST /api/auth/signup (회원가입)
     *    POST /api/auth/login (로그인, JWT 토큰 획득)
     *    GET /api/user/profile (토큰 포함하여 요청)
     *    → 유효한 토큰이면 200 OK, 없으면 401 Unauthorized
     * 
     * 3. 관리자 API 테스트:
     *    관리자 계정으로 로그인하여 ADMIN 토큰 획득
     *    GET /api/admin/dashboard (ADMIN 토큰 포함하여 요청)
     *    → ADMIN 권한이면 200 OK, USER 권한이면 403 Forbidden
     * 
     * 4. Swagger UI에서 테스트:
     *    http://localhost:8080/swagger-ui.html 접속
     *    Authorize 버튼으로 JWT 토큰 입력
     *    각 API 직접 테스트 가능
     */
}
