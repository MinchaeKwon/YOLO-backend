package com.yolo.dto;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripDto {
	private int contentId;
	private int conteTypeId;
	private String title;
	private String address;
	private String imageUrl;
	private String tumbnailUrl;
	private int congestion;
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Detail {
		private Long contentId;
		private Long contentTypeId;
		private String title;
		private String homepage;
		private String tel;
		private String address;
		private String overview;
		private ArrayList<String> imageUrl;
		private double latitude;
		private double longitude;
		private String parking;
		private String restdate;
		private String usetime;
	}
	
}
