package com.yolo.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="trip_congestion")
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
	
	@Column(name="name")
	private String name;
	
	@Column(name="predictNum")
	private int predictNum;
	
	@Column(name="congestion")
	private int congestion;
	
}
