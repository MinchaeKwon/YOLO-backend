package com.yolo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.MagazineDto;
import com.yolo.dto.TripDto;
import com.yolo.response.ErrorResponse;
import com.yolo.response.MagazineRespone;
import com.yolo.response.SuccessListResponse;
import com.yolo.response.SuccessResponse;
import com.yolo.service.TripService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TripController {
	
	@Autowired
	TripService travelService;
	
	// 날짜별 여행지 가져오기 -> 지역기반관광정보 api에서 받아온 데이터와 혼잡도 데이터 합쳐서 전달
	@GetMapping(value = "/trip")
	public ResponseEntity<?> getDateTripInfo(String date, @RequestParam(value="contentTypeId", required=false) Long contentTypeId, 
			@RequestParam("page") int page, @RequestParam("sort") String sort) {
		
		List<TripDto> result = null;
		
		try {
			result = travelService.getDateTripInfo(date, contentTypeId, page, sort);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("관광지 목록 가져오기 실패", 500));
		}
		
		return ResponseEntity.ok().body(new SuccessListResponse<List<TripDto>>(200, result.size(), result));
	}
	
	// 관광지 상세정보 가져오기
	@GetMapping(value = "/trip/detail")
	public ResponseEntity<?> getDetailInfo(@RequestParam("contentId") Long contentId, @RequestParam("contentTypeId") Long contentTypeId) {
		TripDto.Detail result = null;
		
		try {
			result = travelService.getDetail(contentId, contentTypeId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("상세정보 가져오기 실패", 500));
		}
		
		return ResponseEntity.ok().body(new SuccessResponse<TripDto.Detail>(200, result));
	}
	
	// 매거진 정보 가져오기
	@GetMapping(value = "/magazine")
	public ResponseEntity<?> getMagazineInfo(String date) {
		List<MagazineDto> result;
		
		try {
			result = travelService.getMagazine();
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("매진 가져오기 실패", 500));
		}
		
		return ResponseEntity.ok().body(new MagazineRespone<List<MagazineDto>>(200, "'9월호'", result));
	}
	
}
