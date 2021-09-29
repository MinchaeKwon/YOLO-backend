package com.yolo.controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

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
	
	
}
