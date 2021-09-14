package com.yolo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeDto {
	List<String> camping;
	List<PlaceDto> foodPlace;
	List<PlaceDto> NonFoodPlace;
}
