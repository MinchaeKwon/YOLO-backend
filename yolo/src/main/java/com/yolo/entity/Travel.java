package com.yolo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="travel")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Travel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private long id;
	
	@Column(name="predictNum")
	private int predictNum;
	
	@Column(name="predictRate")
	private int predictRate;
	
	@Column(name="contentId")
	private int contentId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="congestion")
	private int congestion;
	
	@Column(name="divisionCode")
	private int divisionCode;
	
	@Builder
	public Travel(int predictNum, int predictRate, int contentId, String name, int congestion, int divisionCode) {
		this.predictNum = predictNum;
		this.predictRate = predictRate;
		this.contentId = contentId;
		this.name = name;
		this.congestion = congestion;
		this.divisionCode = divisionCode;
	}
	
}
