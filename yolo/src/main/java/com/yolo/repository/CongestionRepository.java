package com.yolo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Congestion;

@Repository
public interface CongestionRepository extends JpaRepository<Congestion, Long> {
	// 혼잡도 높은순
	@Query("SELECT c FROM Congestion c WHERE c.date=:date ORDER BY c.congestion DESC, c.id DESC")
	Page<Congestion> findByDaeOrderByCongestionDesc(@Param("date") String date, Pageable pageable);
	
	// 혼잡도 낮은순
	@Query("SELECT c FROM Congestion c WHERE c.date=:date ORDER BY c.congestion ASC, c.id DESC")
	Page<Congestion> findByDaeOrderByCongestionAsc(@Param("date") String date, Pageable pageable);
}
