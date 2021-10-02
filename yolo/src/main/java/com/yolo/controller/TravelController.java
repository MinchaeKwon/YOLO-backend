package com.yolo.controller;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.yolo.dto.MagazineDto;
import com.yolo.response.ErrorResponse;
import com.yolo.response.MagazineRespone;
import com.yolo.response.SuccessResponse;
import com.yolo.service.TravelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TravelController {
	
	@Autowired
	TravelService travelService;
	
	// 날짜 받아서 api에서 받아온 데이터와 혼잡도 데이터 합쳐서 전달하는 api
	@GetMapping(value = "/travel")
	public ResponseEntity<?> getTravelInfo(String date) throws IOException, SAXException, ParserConfigurationException {
		
		
		return ResponseEntity.ok().body(new SuccessResponse<String>(200, travelService.getAreaTourInfo()));
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
