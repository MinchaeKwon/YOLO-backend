package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
	// 이미지 삭제
	@Modifying
    @Transactional
	@Query("DELETE FROM Image i WHERE i.id = :imageId")
	int deleteByImageId(@Param("imageId") Long imageId);
}
