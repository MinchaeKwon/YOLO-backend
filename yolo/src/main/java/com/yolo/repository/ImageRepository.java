package com.yolo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yolo.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}
