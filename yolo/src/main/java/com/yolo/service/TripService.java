package com.yolo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.yolo.entity.Magazine;
import com.yolo.repository.MagazineRepository;
import com.yolo.repository.CongestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

	@Autowired
	CongestionRepository congestionRepo;
	
	@Autowired
	MagazineRepository magazineRepo;

	// 탭2 관련 정보 가져오기
	public List<MagazineDto> getMagazine() {
		List<Magazine> magazineList = magazineRepo.findAll();
		List<MagazineDto> result = new ArrayList<>();
		
		for (Magazine m : magazineList) {
			result.add(new MagazineDto(m.getLink(), m.getThumbnail()));
		}
		
		return result;
	}
	
	// open api 이용해서 관광 데이터 가져오기
	// contenttypeid, addr1, addr2, firstimage1, firstimage2, title

	// 날짜별 여행지 가져오기
	public String getDateTripInfo(String date, Long contentTypeId, int page, String sort) throws IOException, SAXException, ParserConfigurationException {
		System.out.println(page + ", " + sort);
		
		StringBuilder urlBuilder = new StringBuilder(
				"http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList"); /* URL */
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "="
				+ URLEncoder.encode(
						"8j34mk+s1/ndx0AkafC8kxGknHpk3HTehopMk9PIig4trbdhrG6PslyubpYwy4UWaU0GpUrcAwAvDsVWJkLi8g==",
						"UTF-8")); /* 공공데이터포털에서 발급받은 인증키 */
		urlBuilder.append(
				"&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(page), "UTF-8")); /* 현재 페이지 번호 */
		urlBuilder.append(
				"&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("20", "UTF-8")); /* 한 페이지 결과 수 */
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

		List<Map<String, String>> list = new ArrayList<>();

		for (int i = 0; i < nList.getLength(); i++) {
			Map<String, String> map = new HashMap<>();
			
			Node nNode = nList.item(i);
			Element eElement = (Element) nNode;

			map.put("addr1", getTagValue("addr1", eElement));
			map.put("addr2", getTagValue("addr2", eElement));
			map.put("contentId", getTagValue("contentid", eElement));
			map.put("contenttypeId", getTagValue("contenttypeid", eElement));
			map.put("imageUrl", getTagValue("firstimage", eElement));
			map.put("thumbnaiUrl", getTagValue("firstimage2", eElement));
//			map.put("mapX", getTagValue("mapx", eElement));
//			map.put("mapY", getTagValue("mapy", eElement));
//			map.put("phone", getTagValue("tel", eElement));
			map.put("title", getTagValue("title", eElement));

			list.add(map);
		}

		return test(urlBuilder.toString());
	}
	
	// 관광지 상세정보 가져오기 -> 소개/반복/이미지정보/공통정보조회 api 호출 (총 4개)
	// 여행코스, 숙박 타입의 경우 [반복정보 조회] api 호출 필요
	public String getDetail(Long contentId, Long contentTypeId) {
		
		
		return null;
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
		
//		System.out.println(nValue.getNodeName() + ": " + nValue.getTextContent());
		
		return nValue.getNodeValue();
	}

	private String test(String testUrl) throws IOException {
		URL url = new URL(testUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

//		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;

		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}

		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}

		rd.close();
		conn.disconnect();

		System.out.println(sb.toString());

		return sb.toString();
	}

}
