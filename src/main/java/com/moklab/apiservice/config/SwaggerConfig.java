package com.moklab.apiservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 설정 클래스
 * 
 * Swagger는 REST API를 문서화하고 테스트할 수 있는 도구입니다.
 * OpenAPI 3.0 명세를 기반으로 API 문서를 자동 생성하며, 개발자와 클라이언트 간의 소통을 원활하게 합니다.
 * 
 * 주요 기능:
 * 1. API 문서 자동 생성 (http://localhost:8080/swagger-ui.html)
 * 2. API 테스트 인터페이스 제공
 * 3. JWT 인증이 필요한 API에 대한 보안 설정
 * 4. Request/Response 스키마 정의
 * 
 * 실무에서의 활용:
 * - 프론트엔드 개발자와의 API 명세 공유
 * - QA 팀의 API 테스트 도구
 * - 외부 파트너사에게 API 문서 제공
 * - 개발 중 API 동작 확인 및 디버깅
 * 
 * 보안 주의사항:
 * - 운영 환경에서는 Swagger UI 접근을 제한하거나 비활성화
 * - 민감한 정보(DB 스키마, 내부 로직)가 노출되지 않도록 주의
 * - API 문서에 예시 데이터 사용 시 실제 개인정보 사용 금지
 * 
 * @author 개발팀
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration  // Spring 설정 클래스로 등록
public class SwaggerConfig {

    /**
     * OpenAPI 객체를 생성하여 Swagger 문서의 기본 정보와 보안 설정을 구성합니다.
     * 
     * OpenAPI 구성 요소:
     * 1. info: API 기본 정보 (제목, 설명, 버전, 라이선스)
     * 2. security: JWT 인증 요구사항 설정
     * 3. components: 재사용 가능한 컴포넌트 정의 (보안 스키마)
     * 
     * @return OpenAPI 설정 객체
     */
    @Bean  // Spring 컨테이너에 Bean으로 등록
    public OpenAPI openAPI() {
        return new OpenAPI()
                // API 기본 정보 설정
                .info(new Info()
                        .title("ApiService API")  // API 문서 제목
                        .description("Spring Boot API Service with JWT Authentication")  // API 설명
                        .version("v1.0.0")  // API 버전
                        .license(new License()
                                .name("MIT License")  // 라이선스 이름
                                .url("https://opensource.org/licenses/MIT")))  // 라이선스 URL
                
                // 전역 보안 요구사항 설정
                // 모든 API에 JWT 토큰이 필요함을 명시
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))  // 보안 스키마 이름과 매칭
                
                // 재사용 가능한 컴포넌트 정의
                .components(new Components()
                        // JWT 보안 스키마 정의
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    /**
     * JWT Bearer 토큰 인증을 위한 보안 스키마를 생성합니다.
     * 
     * 이 설정으로 Swagger UI에서 'Authorize' 버튼을 통해 JWT 토큰을 입력할 수 있습니다.
     * 
     * 사용 방법:
     * 1. Swagger UI에서 'Authorize' 버튼 클릭
     * 2. JWT 토큰을 입력 (Bearer 접두사 없이 토큰만 입력)
     * 3. 'Authorize' 클릭하면 이후 모든 API 호출에 토큰이 자동 포함
     * 
     * 보안 스키마 설정:
     * - type: HTTP 인증 방식 사용
     * - scheme: "bearer" 스키마 사용 (RFC 6750 표준)
     * - bearerFormat: "JWT" 형식임을 명시
     * 
     * 실제 요청 시 헤더 형태:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     * 
     * @return JWT Bearer 토큰 보안 스키마
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)    // HTTP 인증 방식 사용
                .bearerFormat("JWT")               // Bearer 토큰이 JWT 형식임을 명시
                .scheme("bearer");                 // Bearer 스키마 사용 (RFC 6750 표준)
    }
    
    /**
     * Swagger UI 접근 URL 정보:
     * - Swagger UI: http://localhost:8080/swagger-ui.html
     * - API 문서 JSON: http://localhost:8080/v3/api-docs
     * - API 문서 YAML: http://localhost:8080/v3/api-docs.yaml
     * 
     * 개발 팁:
     * 1. @Operation 어노테이션으로 개별 API 설명 추가 가능
     * 2. @Parameter 어노테이션으로 파라미터 설명 추가 가능
     * 3. @Schema 어노테이션으로 DTO 필드 설명 추가 가능
     * 4. @Tag 어노테이션으로 API 그룹화 가능
     * 
     * 예시:
     * @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인")
     * @Parameter(name = "email", description = "사용자 이메일", required = true)
     * @Tag(name = "인증", description = "사용자 인증 관련 API")
     * 
     * 운영 환경 설정:
     * - application-prod.properties에서 springdoc.swagger-ui.enabled=false 설정
     * - 또는 SecurityConfig에서 "/swagger-ui/**" 경로 접근 제한
     */
}
