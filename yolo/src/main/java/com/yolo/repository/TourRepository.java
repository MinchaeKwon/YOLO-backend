package com.yolo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
	// contentId에 해당하는 관광지 가져오기
	Optional<Tour> findByContentId(int contentId);
}
