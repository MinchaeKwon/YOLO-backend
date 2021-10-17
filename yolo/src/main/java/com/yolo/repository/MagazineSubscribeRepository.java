package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.entity.Account;
import com.yolo.entity.MagazineSubscribe;

@Repository
public interface MagazineSubscribeRepository extends JpaRepository<MagazineSubscribe, Long> {
	// 매거진 구독하기
	boolean existsByAccount(Account account);
	
	// 매거진 구독 취소하기
	@Modifying
    @Transactional
	@Query("DELETE FROM MagazineSubscribe ms WHERE ms.account = :account")
	int deleteByAccount(@Param("account") Account account);
}
