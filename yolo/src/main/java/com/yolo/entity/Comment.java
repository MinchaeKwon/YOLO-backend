package com.yolo.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Table(name="community_comment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	private long id;
	
	@Column(name="content")
	private String content;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy="comment", orphanRemoval=true)
	private Image image;
	
	@Column(name="createAt")
	@CreationTimestamp
	private LocalDateTime createAt;
	
	@Column(name="updateAt")
	@UpdateTimestamp
	private LocalDateTime updateAt;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	@ManyToOne
	@JoinColumn(name="post_id")
	private Post post;
	
	@Builder
	public Comment(String content, Account account, Post post) {
		this.content = content;
		this.account = account;
		this.post = post;
	}
}
