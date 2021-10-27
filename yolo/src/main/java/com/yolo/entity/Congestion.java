package com.yolo.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="tour_place_congestion")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Congestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private long id;
	
	@Column(name="contentId")
	private int contentId;
	
	@Column(name="contentTypeId")
	private Long contentTypeId;
	
	@Column(name="date")
	private String date;
	
	@Column(name="congestion")
	private int congestion;
	
}
