package com.yolo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yolo.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	List<Place> findAllByTypeOrderById(int type);
}
