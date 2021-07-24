package com.yolo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.domain.Account;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Long> {
	// 특정 이메일, 로그인 타입에 해당하는 회원이 있는지 확인
	Optional<Account> findByEmailAndType(String email, String type);
	
	// 특정 이메일에 해당하는 회원이 있는지 확인
	Optional<Account> findByEmail(String email);
	
	// 이메일 중복 확인
	Boolean existsByEmail(String email);
	
	// 닉네임 중복 확인
	Boolean existsByNickname(String nickname);
}
