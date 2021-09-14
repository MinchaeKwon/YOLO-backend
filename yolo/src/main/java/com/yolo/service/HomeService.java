package com.yolo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.dto.PlaceDto;
import com.yolo.entity.Place;
import com.yolo.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeService {
	@Autowired
	PlaceRepository placeRepo;
	
	// 식당 9개 가져오기
	public List<PlaceDto> getFoodPlace() {
		List<Place> placeList = placeRepo.findAllByTypeOrderById(0);
		List<PlaceDto> result = new ArrayList<>();
		
		for (Place p : placeList) {
			result.add(new PlaceDto(p.getSearchRanking(), p.getName(), p.getAddress()));
		}
		
		return result;
	}
	
	// 장소 9개 가져오기
	public List<PlaceDto> getNotFoodPlace() {
		List<Place> placeList = placeRepo.findAllByTypeOrderById(1);
		List<PlaceDto> result = new ArrayList<>();
		
		for (Place p : placeList) {
			result.add(new PlaceDto(p.getSearchRanking(), p.getName(), p.getAddress()));
		}
		
		return result;
	}

}
