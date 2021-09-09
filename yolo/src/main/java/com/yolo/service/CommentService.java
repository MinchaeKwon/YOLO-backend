package com.yolo.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.dto.CommentDto;
import com.yolo.entity.Account;
import com.yolo.entity.Comment;
import com.yolo.entity.Image;
import com.yolo.entity.Post;
import com.yolo.repository.CommentRepository;
import com.yolo.repository.ImageRepository;
import com.yolo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	@Autowired
	CommentRepository commtRepo;
	
	@Autowired
	private PostRepository postRepo;
	
	@Autowired
	ImageRepository imageRepo;
	
	private final S3Service s3Service;
	
	// 댓글 작성
	@Transactional
	public Long save(Long postId, CommentDto dto, Account account) throws IOException {
		Post post = postRepo.findById(postId).get();
		
		Comment comment = commtRepo.save(Comment.builder().content(dto.getContent()).account(account).post(post).build());
		
		// 이미지 S3에 업로드 후 image 테이블에 url 저장
		// 이미지 파일이 있을 경우에만 업로드
		if (dto.getImage() != null) {
			System.out.println("댓글 들어온 이미지: " + dto.getImage().getOriginalFilename());
			
			String imageUrl = s3Service.upload(dto.getImage(), "images");
			imageRepo.save(Image.builder().imageUrl(imageUrl).comment(comment).build());
		}
		
		return comment.getId();
	}
	
	// 댓글 삭제
	@Transactional
	public boolean delete(Long commentId) {
		Comment comment = commtRepo.findById(commentId).orElseThrow(EntityNotFoundException::new);
		Image commtImage = comment.getImage();
		
		commtRepo.deleteById(commentId);
		
		boolean result = true;
		
		if (commtImage != null) {
			result = s3Service.delete(commtImage.getImageUrl());
		}
		
		return result;
	}
	
	// 특정 게시글의 모든 댓글 가져오기 -> 최신순
	public List<CommentDto.Detail> getAllComment(Long postId, Account account) {
		List<Comment> commentList = commtRepo.findByPostIdOrderById(postId);
		List<CommentDto.Detail> result = new ArrayList<>();
		
		for (Comment c : commentList) {
			Account commtAccount = c.getAccount();
			
			boolean isAuthor = false;
			if (account.getId() == commtAccount.getId()) {
				isAuthor = true;
			}
			
			String createAt = c.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
			
			Image commentImage = c.getImage();
			String commentImageUrl = null;
			
			if (commentImage != null) {
				commentImageUrl = commentImage.getImageUrl();
			}
			
			Image accountImage = commtAccount.getImage();
			String accountImageUrl = null;
			
			if (accountImage != null) {
				accountImageUrl = accountImage.getImageUrl();
			}
			
			result.add(new CommentDto.Detail(c.getId(), commtAccount.getNickname(), accountImageUrl, 
					c.getContent(), commentImageUrl, createAt, isAuthor));
		}
		
		return result;
	}
	
}
