package com.moklab.apiservice.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 설정 클래스
 * 
 * MyBatis는 SQL 매핑 프레임워크로, SQL과 Java 객체 간의 매핑을 처리합니다.
 * 이 클래스는 MyBatis와 Spring의 통합을 설정하여 Mapper 인터페이스들을 자동으로 등록합니다.
 * 
 * 주요 역할:
 * 1. Mapper 인터페이스 스캔 및 자동 등록
 * 2. Spring Context와 MyBatis SqlSession 통합
 * 3. XML 매퍼 파일과 Mapper 인터페이스 연결
 * 
 * 실무에서의 활용:
 * - SQL과 Java 코드의 분리로 유지보수성 향상
 * - 동적 SQL 작성 가능
 * - 복잡한 조인 쿼리나 저장 프로시저 사용 시 유리
 * - 기존 레거시 DB 시스템과의 연동에 적합
 * 
 * MyBatis vs JPA 비교:
 * - MyBatis: SQL 직접 제어, 복잡한 쿼리에 유리, 학습곡선 낮음
 * - JPA: 객체 지향적, 간단한 CRUD에 유리, 표준화됨
 * 
 * @author 개발팀
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration  // Spring 설정 클래스임을 나타내는 어노테이션
@MapperScan("com.moklab.apiservice.repository")  // Mapper 인터페이스를 스캔할 패키지 지정
public class MyBatisConfig {
    
    /**
     * MyBatis 설정 클래스
     * 
     * @MapperScan 어노테이션 상세 설명:
     * - basePackages: "com.moklab.apiservice.repository"
     *   → 이 패키지에서 @Mapper 어노테이션이 붙은 인터페이스들을 자동으로 찾아 Spring Bean으로 등록
     * 
     * - 스프링 부트의 AutoConfiguration이 다음 설정들을 자동으로 처리:
     *   1. SqlSessionFactory 생성 (application.properties의 설정 참조)
     *   2. SqlSessionTemplate 생성 (MyBatis와 Spring 트랜잭션 통합)
     *   3. DataSource 연결 (MariaDB 커넥션 풀)
     *   4. XML 매퍼 파일 로딩 (mapper-locations 설정 기반)
     * 
     * 동작 원리:
     * 1. 애플리케이션 시작 시 @MapperScan이 지정된 패키지를 스캔
     * 2. Mapper 인터페이스들을 찾아 프록시 객체 생성
     * 3. 각 메서드를 XML 매퍼 파일의 SQL과 매핑
     * 4. Spring 컨테이너에 Bean으로 등록하여 의존성 주입 가능하게 함
     * 
     * 예시:
     * UserRepository 인터페이스 → UserMapper.xml과 매핑
     * @Autowired UserRepository userRepository; // 자동 주입 가능
     * 
     * 트러블슈팅 팁:
     * - Mapper 인터페이스가 Bean으로 등록되지 않는다면 패키지 경로 확인
     * - XML 파일을 찾지 못한다면 application.properties의 mapper-locations 확인
     * - SQL 실행 오류 시 MyBatis 로그 레벨을 DEBUG로 설정하여 실제 실행 쿼리 확인
     */
}
