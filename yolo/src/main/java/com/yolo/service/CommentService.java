package com.yolo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.dto.CommentDto;
import com.yolo.entity.Account;
import com.yolo.entity.Comment;
import com.yolo.entity.Post;
import com.yolo.repository.CommentRepository;
import com.yolo.repository.PostRepository;

@Service
public class CommentService {

	@Autowired
	CommentRepository commtRepo;
	
	@Autowired
	private PostRepository postRepo;
	
	// 댓글 작성
	@Transactional
	public Long save(Long postId, CommentDto dto, Account account) {
		Post post = postRepo.findById(postId).get();
		
		// 이미지 S3에 업로드 후 image 테이블에 url 저장해야함
		
		return commtRepo.save(Comment.builder().content(dto.getContent()).account(account).post(post).build()).getId();
	}
	
	// 댓글 삭제
	@Transactional
	public void delete(Long commentId) {
		commtRepo.deleteById(commentId);
	}
	
	// 특정 게시글의 모든 댓글 가져오기
	
	
}
