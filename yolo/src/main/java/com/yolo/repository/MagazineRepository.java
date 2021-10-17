package com.yolo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Magazine;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long> {
	List<Magazine> findByMonth(int month);
}
