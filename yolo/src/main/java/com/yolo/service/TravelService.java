package com.yolo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.repository.TravelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelService {

	@Autowired
	TravelRepository travelRepo;
	
	// open api 이용해서 관광 데이터 가져오기
	
	// 관광 데이터와 혼잡도 데이터 결합 해야 함
	
}
