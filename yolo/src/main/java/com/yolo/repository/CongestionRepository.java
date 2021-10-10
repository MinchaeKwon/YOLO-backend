package com.yolo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Congestion;

@Repository
public interface CongestionRepository extends JpaRepository<Congestion, Long> {
	// 특정 날짜에 해당하는 관광지 가져오기
	Page<Congestion> findByDate(Pageable pageable, String date);
	
	// 특정 날짜, contentTypeId에 해당하는 관광지 가져오기
	Page<Congestion> findByDateAndContentTypeId(Pageable pageable, String date, Long contentTypeId);
}
