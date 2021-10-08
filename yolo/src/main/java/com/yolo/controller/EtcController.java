package com.yolo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.HomeDto;
import com.yolo.dto.NoticeDto;
import com.yolo.dto.PlaceDto;
import com.yolo.response.SuccessListResponse;
import com.yolo.response.SuccessResponse;
import com.yolo.service.EtcService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EtcController {
	
	@Autowired
	EtcService etcService;
	
	@GetMapping("/home")
	public ResponseEntity<?> getHomeInfo() {
		List<String> camping = null;
		List<PlaceDto> foodList = etcService.getPlace(0);
		List<PlaceDto> NotFoodList = etcService.getPlace(1);
		
		HomeDto home = new HomeDto(camping, foodList, NotFoodList);
		
		return ResponseEntity.ok().body(new SuccessResponse<HomeDto>(200, home));
	}
	
	@GetMapping("/notice")
	public ResponseEntity<?> getNotice() {
		List<NoticeDto> result = etcService.getNoticeList();
		
		return ResponseEntity.ok().body(new SuccessListResponse<List<NoticeDto>>(200, result.size(), result));
	}

}
