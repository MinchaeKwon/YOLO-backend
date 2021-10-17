package com.yolo.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yolo.dto.TripDto;
import com.yolo.entity.Congestion;
import com.yolo.entity.Tour;
import com.yolo.repository.CongestionRepository;
import com.yolo.repository.TourRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

	@Autowired
	CongestionRepository congestionRepo;
	
	@Autowired
	TourRepository tourRepo;
	
	private TripDto.Detail tripDetail;
	private ArrayList<String> detailImageUrl;
	
	private int ELE_SIZE = 20;
	
	// 날짜별 여행지 가져오기
	public List<TripDto> getDateTripInfo(int page, String sort, String date, Long contentTypeId) {
		
		// 해당 페이지에 20개씩 혼잡도 파일 가져오고(sort -> 혼잡도 높은순, 낮은순), for문 돌리면서 관광지 정보 가져온 다음 TripDto에 넣기
		
		List<TripDto> result = new ArrayList<>();
		Page<Congestion> conList = null;
		
		if (sort.equals("high")) {
			if (contentTypeId == null) {
				Pageable pageable = PageRequest.of(page - 1, ELE_SIZE, Sort.by("congestion").descending());
				conList = congestionRepo.findByDate(pageable, date);
			} else {
				Pageable pageable = PageRequest.of(page - 1, ELE_SIZE, Sort.by("congestion").descending());
				conList = congestionRepo.findByDateAndContentTypeId(pageable, date, contentTypeId);
			}
		} else if (sort.equals("low")) {
			if (contentTypeId == null) {
				Pageable pageable = PageRequest.of(page - 1, ELE_SIZE, Sort.by("congestion").ascending());
				conList = congestionRepo.findByDate(pageable, date);
			} else {
				Pageable pageable = PageRequest.of(page - 1, ELE_SIZE, Sort.by("congestion").ascending());
				conList = congestionRepo.findByDateAndContentTypeId(pageable, date, contentTypeId);
			}
		}
		
		for (Congestion c : conList) {
			int contentId = c.getContentId();
			
			Tour tour = tourRepo.findByContentId(contentId).orElseThrow();
			
			result.add(new TripDto(tour.getContentId(), tour.getContentTypeId(), tour.getTitle(), tour.getAddress(), 
					tour.getImageUrl(), tour.getThumbnail(), c.getCongestion() + 1));
		}

		return result;
	}

	// 관광지 상세정보 가져오기 -> 소개/이미지정보/공통정보조회 api 호출 (총 3개)
	public TripDto.Detail getDetail(Long contentId, Long contentTypeId) throws SAXException, IOException, ParserConfigurationException {	
		tripDetail = new TripDto.Detail();
		detailImageUrl = new ArrayList<>();
		
		getCommonInfo(contentId, contentTypeId);
		getIntroInfo(contentId, contentTypeId);
		getImageInfo(contentId);

		return tripDetail;
	}

	// 공통정보 api 호출
	public void getCommonInfo(Long contentId, Long contentTypeId) throws SAXException, IOException, ParserConfigurationException {
		StringBuilder urlBuilder = new StringBuilder(
				"http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon"); /* URL */

		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "="
				+ URLEncoder.encode(
						"8j34mk+s1/ndx0AkafC8kxGknHpk3HTehopMk9PIig4trbdhrG6PslyubpYwy4UWaU0GpUrcAwAvDsVWJkLi8g==",
						"UTF-8")); /* 공공데이터포털에서 발급받은 인증키 */
		urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("AND", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("YOLO", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("contentId", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(contentId), "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("contentTypeId", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(contentTypeId), "UTF-8"));
		urlBuilder.append(
				"&" + URLEncoder.encode("defaultYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("firstImageYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("areacodeYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("catcodeYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("addrinfoYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("mapinfoYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("overviewYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));

		Document documentInfo = null;
		documentInfo = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(urlBuilder.toString());
		documentInfo.getDocumentElement().normalize();
		
		Element root = documentInfo.getDocumentElement();
		NodeList nList = root.getElementsByTagName("items").item(0).getChildNodes();

		Node nNode = nList.item(0);
		Element eElement = (Element) nNode;

		tripDetail.setContentId(contentId);
		tripDetail.setContentTypeId(contentTypeId);
		
		tripDetail.setTitle(getTagValue("title", eElement));
		
		// html 코드 제거
		String homepage = getTagValue("homepage", eElement);
		if (homepage != null) {
			tripDetail.setHomepage(homepage.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", ""));	
		}
		
		String tel = getTagValue("tel", eElement);
		if (tel != null) {
			tripDetail.setTel(tel);
		}
		
		String address = getTagValue("addr1", eElement);
		if (address != null) {
			tripDetail.setAddress(address);	
		}
		
		// 줄바꿈 제거
		String overview = getTagValue("overview", eElement);
		if (overview != null) {
			tripDetail.setOverview(overview.replaceAll("\n", ""));	
		}
		
		String latitude = getTagValue("mapy", eElement);
		if (latitude != null) {
			tripDetail.setLatitude(Double.parseDouble(latitude));
		}
		
		String longitude = getTagValue("mapx", eElement);
		if (longitude != null) {
			tripDetail.setLongitude(Double.parseDouble(longitude));
		}
		
		String imageUrl = getTagValue("firstimage", eElement);
		if (imageUrl != null) {
			detailImageUrl.add(imageUrl);	
		}
	}

	// 소개정보 api 호출
	public void getIntroInfo(Long contentId, Long contentTypeId) throws SAXException, IOException, ParserConfigurationException {
		StringBuilder urlBuilder = new StringBuilder(
				"http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro");
		
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "="
				+ URLEncoder.encode(
						"8j34mk+s1/ndx0AkafC8kxGknHpk3HTehopMk9PIig4trbdhrG6PslyubpYwy4UWaU0GpUrcAwAvDsVWJkLi8g==",
						"UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("AND", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("YOLO", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("contentId", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(contentId), "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("contentTypeId", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(contentTypeId), "UTF-8"));

		Document documentInfo = null;
		documentInfo = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(urlBuilder.toString());
		documentInfo.getDocumentElement().normalize();
		
		Element root = documentInfo.getDocumentElement();
		NodeList nList = root.getElementsByTagName("items").item(0).getChildNodes();
		
		System.out.println(nList.getLength());

		Node nNode = nList.item(0);
		Element eElement = (Element) nNode;

		String parking = getTagValue("parking", eElement);
		if (parking != null) {
			tripDetail.setParking(parking);
		}
		
		String restdate = getTagValue("restdate", eElement);
		if (restdate != null) {
			tripDetail.setRestdate(restdate);
		}
		
		String usetime = getTagValue("usetime", eElement);
		if (usetime != null) {
			tripDetail.setUsetime(usetime);	
		}
	}

	// 이미지정보 api 호출
	public void getImageInfo(Long contentId) throws SAXException, IOException, ParserConfigurationException {
		StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage");
		
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "="
				+ URLEncoder.encode(
						"8j34mk+s1/ndx0AkafC8kxGknHpk3HTehopMk9PIig4trbdhrG6PslyubpYwy4UWaU0GpUrcAwAvDsVWJkLi8g==",
						"UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("2", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("AND", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("YOLO", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("contentId", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(contentId), "UTF-8")); 
		urlBuilder.append("&" + URLEncoder.encode("imageYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("subImageYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));

		Document documentInfo = null;
		
		documentInfo = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(urlBuilder.toString());
		documentInfo.getDocumentElement().normalize();
		
		Element root = documentInfo.getDocumentElement();
		NodeList nList = root.getElementsByTagName("items").item(0).getChildNodes();
		
		System.out.println(nList.getLength());

		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			Element eElement = (Element) nNode;

			String imageUrl = getTagValue("originimgurl", eElement);
			if (imageUrl != null) {
				detailImageUrl.add(imageUrl);
			}
		}
		
		tripDetail.setImageUrl(detailImageUrl);
	}

	// tag값 정보를 가져오는 메소드
	private static String getTagValue(String tag, Element eElement) {
		Node nValue = null;

		nValue = eElement.getElementsByTagName(tag).item(0);

		if (nValue == null) {
			return null;
		}

//		System.out.println("태그 정보 - " + nValue.getNodeName() + ": " + nValue.getTextContent());

		return nValue.getTextContent();
	}

}
