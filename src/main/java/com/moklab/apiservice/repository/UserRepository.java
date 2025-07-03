package com.moklab.apiservice.repository;

import com.moklab.apiservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 데이터 접근을 위한 MyBatis 매퍼 인터페이스
 * 
 * 이 인터페이스는 Data Access Layer(데이터 접근 계층)의 역할을 담당합니다:
 * 
 * 1. Repository 패턴 구현
 *    - 비즈니스 로직과 데이터 접근 로직을 분리
 *    - 데이터베이스 종류가 바뀌어도 비즈니스 로직은 변경되지 않음
 * 
 * 2. MyBatis 매퍼 인터페이스
 *    - @Mapper 어노테이션으로 MyBatis가 자동으로 구현체 생성
 *    - UserMapper.xml 파일과 연결되어 SQL 쿼리 실행
 * 
 * 3. Spring 빈 등록
 *    - @Repository 어노테이션으로 Spring 컨테이너에 빈으로 등록
 *    - 의존성 주입을 통해 Service 계층에서 사용
 * 
 * 4. 예외 변환
 *    - @Repository가 데이터베이스 예외를 Spring의 DataAccessException으로 변환
 */
@Repository  // Spring의 데이터 접근 계층 컴포넌트로 등록
@Mapper      // MyBatis 매퍼 인터페이스임을 명시
public interface UserRepository {
    
    /**
     * 사용자명으로 사용자를 조회합니다.
     * 
     * 주요 용도:
     * - 로그인 시 사용자 인증
     * - 사용자 정보 조회
     * 
     * @param username 조회할 사용자명
     * @return Optional<User> 사용자 정보 (없으면 Optional.empty())
     * 
     * Optional 사용 이유:
     * - null 체크를 강제하여 NullPointerException 방지
     * - 값이 있는지 명확하게 표현
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 이메일로 사용자를 조회합니다.
     * 
     * 주요 용도:
     * - 이메일 중복 체크
     * - 비밀번호 찾기 기능
     * 
     * @param email 조회할 이메일 주소
     * @return Optional<User> 사용자 정보 (없으면 Optional.empty())
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 사용자명이 이미 존재하는지 확인합니다.
     * 
     * 주요 용도:
     * - 회원가입 시 사용자명 중복 체크
     * - 사용자명 변경 시 중복 확인
     * 
     * @param username 확인할 사용자명
     * @return boolean true: 존재함, false: 존재하지 않음
     */
    boolean existsByUsername(String username);
    
    /**
     * 이메일이 이미 존재하는지 확인합니다.
     * 
     * 주요 용도:
     * - 회원가입 시 이메일 중복 체크
     * - 이메일 변경 시 중복 확인
     * 
     * @param email 확인할 이메일 주소
     * @return boolean true: 존재함, false: 존재하지 않음
     */
    boolean existsByEmail(String email);
    
    /**
     * 새로운 사용자를 데이터베이스에 저장합니다.
     * 
     * 주요 용도:
     * - 회원가입 처리
     * - 관리자가 사용자 추가
     * 
     * @param user 저장할 사용자 정보
     * 
     * 주의사항:
     * - user.getId()는 null이어야 함 (자동 생성)
     * - 비밀번호는 반드시 암호화된 상태여야 함
     * - 저장 후 user 객체의 id 필드에 생성된 ID가 자동 설정됨
     */
    void save(User user);
    
    /**
     * ID로 사용자를 조회합니다.
     * 
     * 주요 용도:
     * - 특정 사용자 상세 정보 조회
     * - 관리자의 사용자 관리 기능
     * 
     * @param id 조회할 사용자 ID
     * @return Optional<User> 사용자 정보 (없으면 Optional.empty())
     */
    Optional<User> findById(Long id);
    
    /**
     * 기존 사용자 정보를 업데이트합니다.
     * 
     * 주요 용도:
     * - 프로필 정보 수정
     * - 비밀번호 변경
     * - 관리자의 사용자 정보 수정
     * 
     * @param user 업데이트할 사용자 정보 (id 필드 필수)
     * 
     * 주의사항:
     * - user.getId()가 반드시 존재해야 함
     * - 존재하지 않는 ID로 업데이트 시 아무 변경 없음
     */
    void update(User user);
    
    /**
     * 사용자를 삭제합니다.
     * 
     * 주요 용도:
     * - 회원 탈퇴 처리
     * - 관리자의 사용자 삭제
     * 
     * @param id 삭제할 사용자 ID
     * 
     * 주의사항:
     * - 물리적 삭제로 복구 불가능
     * - 실제 서비스에서는 enabled=false로 논리적 삭제 권장
     */
    void deleteById(Long id);
}
