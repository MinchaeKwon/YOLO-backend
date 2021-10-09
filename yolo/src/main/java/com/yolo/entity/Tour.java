package com.yolo.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name="tour")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tour {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private long id;
	
	@Column(name="contentId")
	private int contentId;
	
	@Column(name="contentTypeId")
	private int contentTypeId;
	
	@Column(name="title")
	private String title;
	
	@Column(name="address")
	private String address;
	
	@Column(name="imageUrl")
	private String imageUrl;
	
	@Column(name="thumbnail")
	private String thumbnail;
}
