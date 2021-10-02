package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Congestion;

@Repository
public interface CongestionRepository extends JpaRepository<Congestion, Long> {

}
