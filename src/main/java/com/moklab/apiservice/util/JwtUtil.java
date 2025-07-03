package com.moklab.apiservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT(JSON Web Token) 토큰 관리를 위한 유틸리티 클래스
 * 
 * JWT의 주요 개념:
 * 1. Header: 토큰 타입(JWT)과 서명 알고리즘 정보
 * 2. Payload: 사용자 정보와 만료시간 등의 클레임(Claims) 정보
 * 3. Signature: Header + Payload를 비밀키로 서명한 값
 * 
 * 이 클래스의 주요 기능:
 * 1. JWT 토큰 생성 (로그인 성공 시)
 * 2. JWT 토큰 검증 (API 요청 시)
 * 3. 토큰에서 사용자 정보 추출
 * 4. 토큰 만료 여부 확인
 * 
 * JWT 사용 이유:
 * - Stateless: 서버에 세션 저장 불필요
 * - 확장성: 여러 서버 간 토큰 공유 가능
 * - 모바일 친화적: 쿠키 대신 헤더로 전송
 */
@Component  // Spring Bean으로 등록하여 의존성 주입 가능
public class JwtUtil {

    /**
     * JWT 서명에 사용할 비밀키
     * application.properties의 jwt.secret 값을 주입받음
     * 
     * 보안 주의사항:
     * - 실제 운영환경에서는 환경변수나 외부 설정파일 사용 권장
     * - 충분히 긴 길이의 랜덤 문자열 사용 필요
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT 토큰의 만료 시간 (밀리초)
     * application.properties의 jwt.expiration 값을 주입받음
     * 기본값: 86400000ms = 24시간
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    
    /**
     * JWT 토큰에서 사용자명(username) 추출
     * 
     * @param token JWT 토큰 문자열
     * @return 토큰에 저장된 사용자명
     * 
     * JWT의 subject 클레임에서 사용자명을 추출합니다.
     * 로그인한 사용자를 식별하는데 사용됩니다.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * JWT 토큰에서 만료일시 추출
     * 
     * @param token JWT 토큰 문자열
     * @return 토큰의 만료일시
     * 
     * 토큰이 언제 만료되는지 확인하는데 사용됩니다.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * JWT 토큰에서 특정 클레임을 추출하는 제네릭 메서드
     * 
     * @param token JWT 토큰 문자열
     * @param claimsResolver 클레임에서 원하는 값을 추출하는 함수
     * @return 추출된 클레임 값
     * 
     * Function 인터페이스를 사용하여 다양한 클레임을 유연하게 추출할 수 있습니다.
     * 예: Claims::getSubject, Claims::getExpiration 등
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * JWT 토큰에서 모든 클레임을 추출 (private 메서드)
     * 
     * @param token JWT 토큰 문자열
     * @return Claims 객체 (토큰의 페이로드 정보)
     * 
     * 처리 과정:
     * 1. JWT 파서 생성
     * 2. 비밀키로 서명 검증
     * 3. 토큰 파싱하여 클레임 추출
     * 
     * 예외 발생 시:
     * - 서명이 올바르지 않으면 SignatureException
     * - 토큰이 만료되면 ExpiredJwtException
     * - 토큰 형식이 잘못되면 MalformedJwtException
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())  // 비밀키로 서명 검증
                .build()
                .parseSignedClaims(token)  // 토큰 파싱
                .getPayload();             // 페이로드(클레임) 반환
    }

    /**
     * 토큰이 만료되었는지 확인 (private 메서드)
     * 
     * @param token JWT 토큰 문자열
     * @return true: 만료됨, false: 아직 유효함
     * 
     * 현재 시간과 토큰의 만료 시간을 비교하여 판단합니다.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    
    /**
     * 사용자 정보로 JWT 토큰 생성 (로그인 성공 시 호출)
     * 
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 JWT 토큰 문자열
     * 
     * 로그인 성공 후 클라이언트에게 전달할 JWT 토큰을 생성합니다.
     * 클라이언트는 이 토큰을 저장하고 이후 API 요청시 헤더에 포함시켜 전송합니다.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 필요시 추가 클레임을 여기에 추가할 수 있음 (예: 권한, 이메일 등)
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 클레임과 주체(subject)로 JWT 토큰 생성 (private 메서드)
     * 
     * @param claims 토큰에 포함할 추가 정보
     * @param subject 토큰의 주체 (일반적으로 사용자명)
     * @return 생성된 JWT 토큰 문자열
     * 
     * JWT 구조:
     * 1. Header: {"alg": "HS256", "typ": "JWT"}
     * 2. Payload: {클레임들, "sub": subject, "iat": 발급시간, "exp": 만료시간}
     * 3. Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)                                                    // 사용자 정의 클레임 설정
                .subject(subject)                                                  // 주체(사용자명) 설정
                .issuedAt(new Date(System.currentTimeMillis()))                  // 토큰 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expiration))   // 토큰 만료 시간
                .signWith(getSignKey())                                           // 비밀키로 서명
                .compact();                                                       // 문자열로 압축
    }

    
    /**
     * JWT 서명에 사용할 비밀키 생성 (private 메서드)
     * 
     * @return SecretKey 객체
     * 
     * HMAC SHA-256 알고리즘을 사용하여 비밀키를 생성합니다.
     * - 설정 파일의 secret 문자열을 바이트 배열로 변환
     * - JJWT 라이브러리가 요구하는 SecretKey 형태로 변환
     * 
     * 보안 고려사항:
     * - secret은 충분히 길고 복잡해야 함 (최소 32바이트 권장)
     * - 운영환경에서는 환경변수나 외부 보안 저장소 사용 권장
     */
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * JWT 토큰의 유효성을 검증
     * 
     * @param token JWT 토큰 문자열
     * @param userDetails 검증할 사용자 정보
     * @return true: 유효한 토큰, false: 무효한 토큰
     * 
     * 검증 과정:
     * 1. 토큰에서 사용자명 추출
     * 2. 추출한 사용자명과 현재 사용자명 비교
     * 3. 토큰 만료 여부 확인
     * 
     * 두 조건을 모두 만족해야 유효한 토큰으로 판단:
     * - 토큰의 사용자명 == 현재 사용자명
     * - 토큰이 만료되지 않음
     * 
     * API 요청 시 인증 필터에서 이 메서드를 호출하여 토큰 검증합니다.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
