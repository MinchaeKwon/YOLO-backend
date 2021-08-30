package com.yolo.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name="like_post", uniqueConstraints={@UniqueConstraint(columnNames={"post_id", "account_id"})})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikePost {
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
	public LikePost(Post post, Account account) {
		this.post = post;
		this.account = account;
	}
}
