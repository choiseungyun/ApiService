# ApiService - Spring Boot API 서비스

JWT 인증과 MariaDB를 사용하는 REST API 서비스입니다.

## 🚀 주요 기능

- JWT 토큰 기반 인증
- Spring Security를 통한 보안
- MariaDB 데이터베이스 연동
- RESTful API 설계
- 사용자 회원가입 및 로그인

## 🛠 기술 스택

- **Framework**: Spring Boot 3.5.3
- **Security**: Spring Security + JWT
- **Database**: MariaDB
- **Data Access**: MyBatis (XML Mapper)
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 사전 요구사항

- Java 17 이상
- MariaDB 서버
- Maven 3.6 이상

## 🔧 설치 및 실행

### 1. 프로젝트 클론

```bash
git clone <repository-url>
cd ApiService
```

### 2. MariaDB 설정

1. MariaDB에 `MOKLAB` 데이터베이스 생성
2. 사용자 `moklab` 생성 및 권한 부여

```sql
CREATE DATABASE MOKLAB;
CREATE USER 'moklab'@'localhost' IDENTIFIED BY 'moklab12!@';
GRANT ALL PRIVILEGES ON MOKLAB.* TO 'moklab'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 데이터베이스 스키마 생성

애플리케이션 실행 시 자동으로 테이블이 생성됩니다. 또는 수동으로 실행:

```sql
-- 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 4. 애플리케이션 실행

```bash
./mvnw spring-boot:run
```

또는

```bash
mvn spring-boot:run
```

애플리케이션은 `http://localhost:8080`에서 실행됩니다.

## 🔌 API 엔드포인트

### 인증 API

- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/signin` - 로그인

### 테스트 API

- `GET /api/public/hello` - 공개 엔드포인트
- `GET /api/user/profile` - 사용자 전용 (로그인 필요)
- `GET /api/admin/dashboard` - 관리자 전용 (ADMIN 권한 필요)

## 📖 API 사용 예제

### 회원가입

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com"
  }'
```

### 로그인

```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 인증이 필요한 API 호출

```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## 📖 API 문서화

### Swagger UI

애플리케이션 실행 후 다음 URL에서 대화형 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API 문서 (JSON)**: http://localhost:8080/v3/api-docs

### 주요 기능

- 모든 API 엔드포인트 자동 문서화
- 실시간 API 테스트 인터페이스
- JWT 인증 지원 (Authorization 헤더에 `Bearer {token}` 형식)
- 요청/응답 스키마 자동 생성

## 📂 프로젝트 구조

```
src/
├── main/
│   ├── java/com/moklab/apiservice/
│   │   ├── config/          # 설정 클래스들
│   │   │   ├── MyBatisConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── JwtAuthenticationEntryPoint.java
│   │   ├── controller/      # REST 컨트롤러
│   │   │   ├── AuthController.java
│   │   │   └── TestController.java
│   │   ├── dto/            # 데이터 전송 객체
│   │   │   ├── LoginRequest.java
│   │   │   ├── SignupRequest.java
│   │   │   └── JwtResponse.java
│   │   ├── entity/         # 엔티티 클래스
│   │   │   ├── User.java
│   │   │   └── Role.java
│   │   ├── repository/     # 데이터 접근 계층 (MyBatis Mapper)
│   │   │   └── UserRepository.java
│   │   ├── service/        # 비즈니스 로직
│   │   │   └── UserDetailsServiceImpl.java
│   │   ├── util/           # 유틸리티 클래스
│   │   │   └── JwtUtil.java
│   │   └── ApiServiceApplication.java
│   └── resources/
│       ├── mapper/         # MyBatis XML 매퍼
│       │   └── UserMapper.xml
│       ├── sql/           # 데이터베이스 스키마
│       │   └── schema.sql
│       └── application.properties
```

## ⚙️ 설정

주요 설정은 `application.properties`에서 관리됩니다:

```properties
# MariaDB 설정
spring.datasource.url=jdbc:mariadb://localhost:3306/apiservice
spring.datasource.username=moklab
spring.datasource.password=moklab12!@

# JWT 설정
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000

# 서버 설정
server.port=8080
```

## 🔒 보안

- 모든 API는 CSRF 보호가 비활성화되어 있습니다 (REST API용)
- JWT 토큰은 24시간 후 만료됩니다
- 비밀번호는 BCrypt로 암호화됩니다
- `/api/auth/**`와 `/api/public/**` 경로는 인증 없이 접근 가능합니다

## 🤝 기여

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
