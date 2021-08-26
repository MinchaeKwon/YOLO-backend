package com.yolo.service;

import java.time.format.DateTimeFormatter;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.dto.PostDto;
import com.yolo.entity.Account;
import com.yolo.entity.Post;
import com.yolo.repository.CommentRepository;
import com.yolo.repository.PostRepository;
import com.yolo.repository.RecommendPostRepository;

@Service
public class PostService {
	@Autowired
	PostRepository postRepo;
	
	@Autowired
	RecommendPostRepository recommendPostRepo;
	
	@Autowired
	CommentRepository commtRepo;
	
	// 게시글 작성하기
	@Transactional
	public Long savePost(PostDto info, Account account) {
		return postRepo.save(Post.builder().content(info.getContent()).imageUrl(info.getImageUrl())
				.latitude(info.getLatitude()).longitude(info.getLongitude()).account(account).build()).getId();
	}
	
	
	// 특정 게시글 상세보기 -> 로그인된 경우
	public PostDto.Detail getPostById(Long post_id, Account account) {
		Post post = postRepo.findById(post_id).orElseThrow(EntityNotFoundException::new);
		int cntOfRecommend = post.getRecommend().size(); // 댓글 개수
		int cntOfComment = post.getComment().size(); // 게시글 추천 개수
		
		Account post_account = post.getAccount();
		boolean isAuthor = false;
		
		// 게시글 작성자인지 확인
		if (account.getId() == post_account.getId()) {
			isAuthor = true;
		}
		
		boolean isRecommended = recommendPostRepo.existsByPostAndAccount(post, account); // 게시글 추천했는지 확인
		String createAt = post.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
		
		PostDto.Detail result = new PostDto.Detail(post_account.getNickname(), post_account.getImageUrl(), 
				post.getContent(), post.getImageUrl(), post.getLatitude(), post.getLongitude(), 
				createAt, isAuthor, isRecommended, cntOfRecommend, cntOfComment);
		
		
		return result;
	}
	
	
	// 특정 게시글 삭제하기
	@Transactional
	public void deletePost(Long post_id) {
		postRepo.deleteById(post_id);
	}
	
}