package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
	
}
