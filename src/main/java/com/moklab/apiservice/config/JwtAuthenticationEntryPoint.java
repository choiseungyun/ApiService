package com.moklab.apiservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

/**
 * JWT 인증 실패 시 처리하는 핸들러 클래스
 * 
 * Spring Security에서 인증이 실패했을 때 실행되는 진입점(Entry Point)입니다.
 * 예를 들어, JWT 토큰이 없거나 잘못된 토큰을 제공했을 때 이 클래스가 동작합니다.
 * 
 * 실무 사용 예시:
 * - 클라이언트가 Authorization 헤더 없이 보호된 API에 접근
 * - 만료된 JWT 토큰으로 API 호출
 * - 잘못된 형식의 JWT 토큰 제공
 * 
 * @author API 개발팀
 * @since 1.0
 */
@Component // Spring이 이 클래스를 Bean으로 관리하도록 설정
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    /**
     * 직렬화를 위한 버전 ID
     * 클래스 구조가 변경되었을 때 호환성을 확인하는 용도입니다.
     */
    private static final long serialVersionUID = -7858869558953243875L;

    /**
     * 인증 실패 시 실행되는 메서드
     * 
     * 이 메서드는 Spring Security에서 자동으로 호출되며,
     * 인증되지 않은 사용자가 보호된 리소스에 접근하려 할 때 실행됩니다.
     * 
     * 동작 과정:
     * 1. 클라이언트가 JWT 토큰 없이 보호된 API 호출
     * 2. JwtAuthenticationFilter에서 인증 실패 감지
     * 3. Spring Security가 이 메서드 자동 호출
     * 4. HTTP 401 Unauthorized 응답 반환
     * 
     * @param request HTTP 요청 객체 (요청 정보 포함)
     * @param response HTTP 응답 객체 (응답을 작성할 객체)
     * @param authException 발생한 인증 예외 (인증 실패 원인 정보)
     * @throws IOException 입출력 예외가 발생할 수 있음
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {

        // HTTP 401 Unauthorized 상태 코드와 메시지를 응답으로 전송
        // 클라이언트는 이 응답을 받아 로그인 페이지로 리다이렉트하거나 에러 처리
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
