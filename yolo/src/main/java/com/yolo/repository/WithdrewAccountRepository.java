package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.WithdrewAccount;

@Repository
public interface WithdrewAccountRepository extends JpaRepository<WithdrewAccount, Long> {
	boolean existsBySocialIdAndType(String socialId, String type);
}
