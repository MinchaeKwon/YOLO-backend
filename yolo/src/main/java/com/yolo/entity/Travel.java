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
	
	@Column(name="contentId")
	private int contentId;
	
	@Column(name="congestion")
	private int congestion;
	
	@Column(name="address")
	private String address;
	
	@Column(name="imageUrl")
	private String imageUrl;
	
	@Column(name="title")
	private String title;
	
	@Column(name="contentTypeId")
	private String contentTypeId;
	
	@Builder
	public Travel(int contentId, int congestion, String address, String imageUrl, String title, String contentTypeId) {
		this.contentId = contentId;
		this.congestion = congestion;
		this.address = address;
		this.imageUrl = imageUrl;
		this.title = title;
		this.contentTypeId = contentTypeId;
	}
	
}
