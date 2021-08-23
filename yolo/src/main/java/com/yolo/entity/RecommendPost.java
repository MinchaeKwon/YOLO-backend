package com.yolo.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="community_post")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendPost {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	private long id;
	
	@ManyToOne
	@JoinColumn(name="post_id")
	private Post post;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	@Builder
	public RecommendPost(Post post, Account account) {
		this.post = post;
		this.account = account;
	}
}
