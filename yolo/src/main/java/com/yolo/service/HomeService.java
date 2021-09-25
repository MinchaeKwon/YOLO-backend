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
	
	// 식당, 장소 9개 가져오기
	public List<PlaceDto> getPlace(int type) {
		List<Place> placeList;
		
		if (type == 0) {
			placeList = placeRepo.findAllByTypeOrderById(0);
		} else {
			placeList = placeRepo.findAllByTypeOrderById(1);
		}
		
		List<PlaceDto> result = new ArrayList<>();
		
		for (Place p : placeList) {
			result.add(new PlaceDto(p.getDate(), p.getPlaceId(), p.getImageUrl(), p.getSearchRanking(), p.getName(), p.getAddress()));
		}
		
		return result;
	}

}
