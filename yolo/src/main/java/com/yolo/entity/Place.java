package com.yolo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="place")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private long id;
	
	@Column(name="placeId")
	private Integer placeId;
	
	@Column(name="imageUrl")
	private String imageUrl;
	
	@Column(name="cityCode")
	private int cityCode;
	
	@Column(name="provinceName")
	private String provinceName;
	
	@Column(name="cityName")
	private String cityName;
	
	@Column(name="date")
	private String date;
	
	@Column(name="address")
	private String address;
	
	@Column(name="name")
	private String name;
	
	@Column(name="searchRanking")
	private int searchRanking;
	
	@Column(name="type")
	private int type;
	
}
