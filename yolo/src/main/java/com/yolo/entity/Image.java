package com.yolo.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="image")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	private long id;
	
	@Column(name="imageUrl")
	private String imageUrl;
	
	@OneToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	@ManyToOne
	@JoinColumn(name="post_id")
	private Post post;
	
	@OneToOne
	@JoinColumn(name="comment_id")
	private Comment comment;
	
	@Builder
	public Image(String imageUrl, Account account, Post post, Comment comment) {
		this.imageUrl = imageUrl;
		this.account = account;
		this.post = post;
		this.comment = comment;
	}
	
	public void updateImage(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
