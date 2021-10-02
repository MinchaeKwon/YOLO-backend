package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Magazine;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long> {

}
