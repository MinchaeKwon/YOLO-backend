package com.yolo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Account;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Long> {
	// SocialId, 로그인 타입에 해당하는 회원이 있는지 확인
	Optional<Account> findBySocialIdAndType(String socialId, String type);
	
	// SocialId에 해당하는 회원이 있는지 확인
	Optional<Account> findBySocialId(String socialId);
	
	Boolean existsBySocialId(String socialId);
	
	// 닉네임 중복 확인
	Boolean existsByNickname(String nickname);
	
	// 회원정보 삭제
	int deleteBySocialIdAndType(String socialId, String type);
	
	List<Account> findByRegistrationToken(String registrationToken);
}
