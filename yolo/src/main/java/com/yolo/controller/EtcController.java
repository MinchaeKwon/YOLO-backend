package com.yolo.controller;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.HomeDto;
import com.yolo.dto.MagazineDto;
import com.yolo.dto.NoticeDto;
import com.yolo.dto.PlaceDto;
import com.yolo.entity.Account;
import com.yolo.response.ErrorResponse;
import com.yolo.response.MagazineRespone;
import com.yolo.response.Response;
import com.yolo.response.SuccessListResponse;
import com.yolo.response.SuccessResponse;
import com.yolo.service.EtcService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EtcController {
	
	@Autowired
	EtcService etcService;
	
	// 홈 정보 가져오기
	@GetMapping("/home")
	public ResponseEntity<?> getHomeInfo() {
		List<String> camping = null;
		List<PlaceDto> foodList = etcService.getPlace(0);
		List<PlaceDto> NotFoodList = etcService.getPlace(1);
		
		HomeDto home = new HomeDto(camping, foodList, NotFoodList);
		
		return ResponseEntity.ok().body(new SuccessResponse<HomeDto>(200, home));
	}
	
	// 공지사항 정보 가져오기
	@GetMapping("/notice")
	public ResponseEntity<?> getNotice() {
		List<NoticeDto> result = etcService.getNoticeList();
		
		return ResponseEntity.ok().body(new SuccessListResponse<List<NoticeDto>>(200, result.size(), result));
	}
	
	// 매거진 정보 가져오기
	@GetMapping(value = "/magazine")
	public ResponseEntity<?> getMagazineInfo(@AuthenticationPrincipal Account account) {
		List<MagazineDto> result;
		boolean subscribe;
		
		try {
			result = etcService.getMagazine(account);
			subscribe = etcService.isSubscribe(account);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("매거진 가져오기 실패", 500));
		}
		
		return ResponseEntity.ok().body(new MagazineRespone<List<MagazineDto>>(200, "'9월호'", result, subscribe));
	}
	
	// 매거진 구독하기
	@PostMapping(value = "/magazine")
	public ResponseEntity<?> subscribeMagazine(@AuthenticationPrincipal Account account) {
		boolean result;

		try {
			result = etcService.magazineSubscribe(account);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("매거진 구독 실패", 500));
		}

		if (result) {
			return ResponseEntity.ok().body(new Response("매거진 구독 성공", 200));			
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("구독중인 매거진입니다.", 400));
	}
	
	// 매거진 구독 취소하기
	@DeleteMapping(value = "/magazine")
	public ResponseEntity<?> subscribeCancel(@AuthenticationPrincipal Account account) {
		boolean result;
		
		try {
			result = etcService.cancelSubscribe(account);
		} catch(EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("일치하는 매거진 정보가 없습니다.", 404));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("매거진 구독 취소 실패", 500));
		}

		if (result) {
			return ResponseEntity.ok().body(new Response("매거진 구독 취소 성공", 200));	
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("매거진 구독 취소 실패", 500));
	}

}
