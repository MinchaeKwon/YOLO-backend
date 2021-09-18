package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Travel;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {

}
