package com.yolo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {
	private String date;
	private Integer placeId;
	private String imageUrl;
	private int ranking;
	private String name;
	private String address;
}
