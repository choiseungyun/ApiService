<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Spring Boot API Service Project Instructions

이 프로젝트는 Spring Boot를 사용한 REST API 서비스입니다.

## 프로젝트 구조

- **Entity**: JPA 엔티티 클래스들 (`com.moklab.apiservice.entity`)
- **Repository**: 데이터 접근 계층 (`com.moklab.apiservice.repository`)
- **Service**: 비즈니스 로직 계층 (`com.moklab.apiservice.service`)
- **Controller**: REST API 컨트롤러 (`com.moklab.apiservice.controller`)
- **Config**: 설정 클래스들 (`com.moklab.apiservice.config`)
- **DTO**: 데이터 전송 객체 (`com.moklab.apiservice.dto`)
- **Util**: 유틸리티 클래스들 (`com.moklab.apiservice.util`)

## 기술 스택

- Spring Boot 3.5.3
- Spring Security (JWT 인증)
- Spring Data JPA
- MariaDB
- Maven

## 코딩 가이드라인

- RESTful API 설계 원칙을 따르세요
- JWT 토큰 기반 인증을 사용하세요
- 적절한 HTTP 상태 코드를 반환하세요
- 예외 처리를 적절히 구현하세요
- API 문서화를 위한 주석을 작성하세요
