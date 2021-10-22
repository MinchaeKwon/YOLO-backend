package com.yolo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.dto.MagazineDto;
import com.yolo.dto.NoticeDto;
import com.yolo.dto.PlaceDto;
import com.yolo.entity.Account;
import com.yolo.entity.Magazine;
import com.yolo.entity.MagazineSubscribe;
import com.yolo.entity.Notice;
import com.yolo.entity.Place;
import com.yolo.repository.MagazineSubscribeRepository;
import com.yolo.repository.MagazineRepository;
import com.yolo.repository.NoticeRepository;
import com.yolo.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EtcService {
	@Autowired
	PlaceRepository placeRepo;
	
	@Autowired
	NoticeRepository notiRepo;
	
	@Autowired
	MagazineRepository magazineRepo;
	
	@Autowired
	MagazineSubscribeRepository magazineSubRepo;
	
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
	
	// 공지사항 리스트 가져오기
	public List<NoticeDto> getNoticeList() {
		List<Notice> noticeList = notiRepo.findAll();
		List<NoticeDto> result = new ArrayList<>();
		
		for (Notice n : noticeList) {
			result.add(new NoticeDto(n.getId(), n.getTitle(), n.getContent(), n.getDate()));
		}

		return result;
	}
	
	// 탭2 관련 정보 가져오기 -> 매거진 정보
	public List<MagazineDto> getMagazine(Account account) {
		List<Magazine> magazineList = magazineRepo.findByMonth(10); // 10월호 매거진 가져오기
		List<MagazineDto> result = new ArrayList<>();

		for (Magazine m : magazineList) {
			result.add(new MagazineDto(m.getLink(), m.getThumbnail()));
		}

		return result;
	}
	
	// 사용자가 매거진을 구독했는지 확인
	public boolean isSubscribe(Account account) {
		return magazineSubRepo.existsByAccount(account);
	}

	// 매거진 구독하기
	@Transactional
	public boolean magazineSubscribe(Account account) {
		if (isSubscribe(account)) {
			return false;
		}
		
		magazineSubRepo.save(MagazineSubscribe.builder().account(account).build());
		return true;
	}

	// 매거진 구독 취소하기
	@Transactional
	public boolean cancelSubscribe(Account account) {
		int delete = magazineSubRepo.deleteByAccount(account);
		
		if (delete == 1) {
			return true;
		}
		
		return false;
	}

}
