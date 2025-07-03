package com.moklab.apiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot API 서비스의 메인 애플리케이션 클래스
 * 
 * 이 클래스는 전체 Spring Boot 애플리케이션의 진입점(Entry Point)입니다.
 * main 메서드가 실행되면서 Spring 컨테이너가 초기화되고 웹 서버가 시작됩니다.
 * 
 * @SpringBootApplication 어노테이션의 역할:
 * 1. @Configuration: 이 클래스가 Spring 설정 클래스임을 나타냄
 * 2. @EnableAutoConfiguration: Spring Boot의 자동 설정 기능 활성화
 * 3. @ComponentScan: 현재 패키지 하위의 모든 컴포넌트를 자동 스캔
 * 
 * 자동 설정되는 주요 기능들:
 * - 웹 서버 (Tomcat) 설정 및 시작
 * - 데이터베이스 연결 설정 (MariaDB)
 * - MyBatis 자동 설정
 * - Spring Security 자동 설정
 * - JSON 변환기 (Jackson) 설정
 * - Swagger/OpenAPI 설정
 * 
 * 애플리케이션 시작 과정:
 * 1. main 메서드 실행
 * 2. SpringApplication.run() 호출
 * 3. Spring 컨텍스트 생성 및 초기화
 * 4. 자동 설정 클래스들 로딩
 * 5. Bean 등록 및 의존성 주입
 * 6. 웹 서버 시작 (기본 포트: 8080)
 * 7. 애플리케이션 Ready 상태
 * 
 * 실행 방법:
 * 1. IDE에서 직접 실행: Run 버튼 클릭
 * 2. Maven 명령어: ./mvnw spring-boot:run
 * 3. JAR 파일 실행: java -jar target/apiservice-0.0.1-SNAPSHOT.jar
 * 
 * 개발 환경 설정:
 * - application.properties: 데이터베이스, JWT 등 기본 설정
 * - application-dev.properties: 개발 환경 전용 설정 (선택사항)
 * - application-prod.properties: 운영 환경 전용 설정 (선택사항)
 * 
 * 접속 URL:
 * - 애플리케이션: http://localhost:8080
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API 문서: http://localhost:8080/v3/api-docs
 * 
 * 트러블슈팅:
 * - 포트 충돌: application.properties에서 server.port 변경
 * - DB 연결 실패: 데이터베이스 서버 및 설정 확인
 * - Bean 등록 실패: @ComponentScan 범위 및 어노테이션 확인
 * 
 * @author 개발팀
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication  // Spring Boot의 핵심 어노테이션 - 자동 설정과 컴포넌트 스캔 활성화
public class ApiServiceApplication {

	/**
	 * 애플리케이션의 시작점(Entry Point)
	 * 
	 * 이 메서드는 JVM에 의해 호출되는 프로그램의 시작점입니다.
	 * SpringApplication.run()을 통해 Spring Boot 애플리케이션을 시작합니다.
	 * 
	 * SpringApplication.run() 매개변수:
	 * 1. ApiServiceApplication.class: 설정 클래스 (현재 클래스)
	 * 2. args: 명령행 인수 (프로그램 실행 시 전달받는 파라미터)
	 * 
	 * 명령행 인수 예시:
	 * java -jar app.jar --server.port=9090 --spring.profiles.active=dev
	 * → args 배열에 ["--server.port=9090", "--spring.profiles.active=dev"] 전달
	 * 
	 * 주요 명령행 옵션:
	 * --server.port=포트번호: 웹 서버 포트 변경
	 * --spring.profiles.active=프로필: 활성 프로필 설정
	 * --spring.config.location=경로: 설정 파일 위치 지정
	 * 
	 * 실행 흐름:
	 * 1. SpringApplication 인스턴스 생성
	 * 2. 애플리케이션 컨텍스트 준비
	 * 3. 환경 설정 로딩 (application.properties 등)
	 * 4. Bean 정의 스캔 및 등록
	 * 5. 자동 설정 적용
	 * 6. 웹 서버 시작
	 * 7. 애플리케이션 이벤트 발행 (ApplicationReadyEvent 등)
	 * 
	 * @param args 명령행 인수 배열
	 */
	public static void main(String[] args) {
		// Spring Boot 애플리케이션 시작
		// 이 한 줄로 전체 웹 애플리케이션이 실행됩니다!
		SpringApplication.run(ApiServiceApplication.class, args);
		
		// 애플리케이션이 성공적으로 시작되면 콘솔에 다음과 같은 메시지가 출력됩니다:
		// "Started ApiServiceApplication in X.XXX seconds"
		// 이후 웹 서버가 요청을 받을 준비가 완료됩니다.
	}
	
	/**
	 * 개발 참고사항
	 * 
	 * 1. 패키지 구조:
	 *    com.moklab.apiservice (메인 패키지)
	 *    ├── config/          - 설정 클래스들
	 *    ├── controller/      - REST API 컨트롤러들
	 *    ├── dto/            - 데이터 전송 객체들
	 *    ├── entity/         - JPA 엔티티들
	 *    ├── repository/     - 데이터 접근 계층
	 *    ├── service/        - 비즈니스 로직 계층
	 *    └── util/           - 유틸리티 클래스들
	 * 
	 * 2. 컴포넌트 스캔 범위:
	 *    @SpringBootApplication이 있는 패키지부터 하위 패키지까지 자동 스캔
	 *    따라서 모든 @Component, @Service, @Repository, @Controller 등이 자동 등록
	 * 
	 * 3. 설정 우선순위:
	 *    명령행 인수 > application-{profile}.properties > application.properties > 기본값
	 * 
	 * 4. 로깅 확인:
	 *    애플리케이션 시작 시 로그를 통해 어떤 설정들이 적용되었는지 확인 가능
	 *    DEBUG 레벨로 설정하면 더 자세한 정보 확인 가능
	 * 
	 * 5. 개발 팁:
	 *    - 코드 변경 시 자동 재시작: spring-boot-devtools 의존성 추가
	 *    - 프로필별 설정: application-dev.properties, application-prod.properties 활용
	 *    - 외부 설정: OS 환경변수나 외부 설정 파일 사용 가능
	 */
}
