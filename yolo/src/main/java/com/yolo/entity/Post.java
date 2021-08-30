package com.yolo.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Table(name="community_post")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	private long id;
	
	@Column(name="content")
	private String content;
	
	@Column(name="imageUrl")
	private String imageUrl;
	
	@Column(name="latitude")
	private double latitude;
	
	@Column(name="longitude")
	private double longitude;
	
	@Column(name="createAt")
	@CreationTimestamp
	private LocalDateTime createAt;
	
	@Column(name="updateAt")
	@UpdateTimestamp
	private LocalDateTime updateAt;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="post", orphanRemoval=true)
	private List<LikePost> recommend;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="post", orphanRemoval=true)
	private List<Comment> comment;
	
	@Builder
	public Post(String content, String imageUrl, double latitude, double longitude , Account account) {
		this.content = content;
		this.imageUrl = imageUrl;
		this.latitude = latitude;
		this.longitude = longitude;
		this.account = account;
	}
}
