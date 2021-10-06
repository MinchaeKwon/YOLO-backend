package com.yolo.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yolo.dto.MagazineDto;
import com.yolo.dto.TripDto;
import com.yolo.entity.Magazine;
import com.yolo.repository.CongestionRepository;
import com.yolo.repository.MagazineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

	@Autowired
	CongestionRepository congestionRepo;

	@Autowired
	MagazineRepository magazineRepo;
	
	private List<TripDto> tripList;
	
	private TripDto.Detail tripDetail;
	private ArrayList<String> detailImageUrl;

	// 탭2 관련 정보 가져오기
	public List<MagazineDto> getMagazine() {
		List<Magazine> magazineList = magazineRepo.findAll();
		List<MagazineDto> result = new ArrayList<>();

		for (Magazine m : magazineList) {
			result.add(new MagazineDto(m.getLink(), m.getThumbnail()));
		}

		return result;
	}
	
	// 날짜별 여행지 가져오기
	public List<TripDto> getDateTripInfo(String date, Long contentTypeId, int page, String sort)
			throws IOException, SAXException, ParserConfigurationException {
		System.out.println(page + ", " + sort);

		StringBuilder urlBuilder = new StringBuilder(
				"http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList"); /* URL */
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "="
				+ URLEncoder.encode(
						"8j34mk+s1/ndx0AkafC8kxGknHpk3HTehopMk9PIig4trbdhrG6PslyubpYwy4UWaU0GpUrcAwAvDsVWJkLi8g==",
						"UTF-8")); /* 공공데이터포털에서 발급받은 인증키 */
		urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "="
				+ URLEncoder.encode(String.valueOf(page), "UTF-8")); /* 현재 페이지 번호 */
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
				+ URLEncoder.encode("20", "UTF-8")); /* 한 페이지 결과 수 */
		urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "="
				+ URLEncoder.encode("YOLO", "UTF-8")); /* 서비스명=어플명 */
		urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "="
				+ URLEncoder.encode("AND", "UTF-8")); /* IOS (아이폰), AND (안드로이드),WIN (원도우폰), ETC */
//	        urlBuilder.append("&" + URLEncoder.encode("arrange","UTF-8") + "=" + URLEncoder.encode(sort, "UTF-8")); /*(A=제목순, B=조회순, C=수정일순, D=생성일순) , 대표이미지가 반드시 있는 정렬 (O=제목순, P=조회순, Q=수정일순, R=생성일순)*/
//	        urlBuilder.append("&" + URLEncoder.encode("contentTypeId","UTF-8") + "=" + URLEncoder.encode("15", "UTF-8")); /*관광타입(관광지, 숙박 등) ID*/
//	        urlBuilder.append("&" + URLEncoder.encode("listYN","UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8")); /*목록 구분 (Y=목록, N=개수)*/

		Document documentInfo = null;
		documentInfo = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(urlBuilder.toString());
		documentInfo.getDocumentElement().normalize();

		Element root = documentInfo.getDocumentElement();
		NodeList nList = root.getElementsByTagName("items").item(0).getChildNodes();
		System.out.println(nList.getLength());

		tripList = new ArrayList<>();
		
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			Element eElement = (Element) nNode;
			
			TripDto trip = new TripDto();
			
			trip.setContentId(Long.parseLong(getTagValue("contentid", eElement)));
			trip.setContentId(contentTypeId);
			trip.setAddress(getTagValue("addr1", eElement));
			trip.setTitle(getTagValue("title", eElement));
			trip.setImageUrl(getTagValue("firstimage", eElement));
			trip.setTumbnailUrl(getTagValue("firstimage2", eElement));

			tripList.add(trip);
		}

		return tripList;
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
		System.out.println(nList.getLength());

		Node nNode = nList.item(0);
		Element eElement = (Element) nNode;

		tripDetail.setContentId(contentId);
		tripDetail.setContentTypeId(contentTypeId);
		
		tripDetail.setTitle(getTagValue("title", eElement));
		
		// html 코드 제거
		tripDetail.setHomepage(getTagValue("homepage", eElement).replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", ""));
		tripDetail.setTel(getTagValue("tel", eElement));
		tripDetail.setAddress(getTagValue("addr1", eElement));
		
		// 줄바꿈 제거
		tripDetail.setOverview(getTagValue("overview", eElement).replaceAll("\n", ""));
		
		tripDetail.setLatitude(Double.parseDouble(getTagValue("mapy", eElement)));
		tripDetail.setLongitude(Double.parseDouble(getTagValue("mapx", eElement)));
		
		detailImageUrl.add(getTagValue("firstimage", eElement));
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

		tripDetail.setParking(getTagValue("parking", eElement));
		tripDetail.setRestdate(getTagValue("restdate", eElement));
		tripDetail.setUsetime(getTagValue("usetime", eElement));
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

			detailImageUrl.add(getTagValue("originimgurl", eElement));
		}
		
		tripDetail.setImageUrl(detailImageUrl);
	}

	// tag값 정보를 가져오는 메소드
	private static String getTagValue(String tag, Element eElement) {
		Node nValue = null;

		try {
			nValue = eElement.getElementsByTagName(tag).item(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (nValue == null) {
			return null;
		}

//		System.out.println("태그 정보 - " + nValue.getNodeName() + ": " + nValue.getTextContent());

		return nValue.getTextContent();
	}

}
