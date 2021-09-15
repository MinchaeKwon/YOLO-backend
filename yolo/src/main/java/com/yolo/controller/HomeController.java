package com.yolo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.HomeDto;
import com.yolo.dto.PlaceDto;
import com.yolo.response.SuccessResponse;
import com.yolo.service.HomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HomeController {
	
	@Autowired
	HomeService homeService;
	
	@GetMapping("/home")
	public ResponseEntity<?> getHomeInfo() {
		List<String> camping = null;
		List<PlaceDto> foodList = homeService.getPlace(0);
		List<PlaceDto> NotFoodList = homeService.getPlace(1);
		
		HomeDto home = new HomeDto(camping, foodList, NotFoodList);
		
		return ResponseEntity.ok().body(new SuccessResponse<HomeDto>(200, home));
	}

}
