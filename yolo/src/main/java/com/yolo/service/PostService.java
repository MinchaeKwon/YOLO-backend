package com.yolo.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.yolo.dto.PostDto;
import com.yolo.entity.Account;
import com.yolo.entity.Image;
import com.yolo.entity.Liked;
import com.yolo.entity.Post;
import com.yolo.repository.CommentRepository;
import com.yolo.repository.ImageRepository;
import com.yolo.repository.LikedRepository;
import com.yolo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	@Autowired
	PostRepository postRepo;
	
	@Autowired
	LikedRepository likedRepo;
	
	@Autowired
	CommentRepository commtRepo;
	
	@Autowired
	ImageRepository imageRepo;
	
	private final S3Service s3Service;
	
	// 게시글 작성하기
	@Transactional
	public Long savePost(PostDto info, Account account) throws IOException {
		Post post = postRepo.save(Post.builder().content(info.getContent())
				.latitude(info.getLatitude()).longitude(info.getLongitude()).account(account).build());
		
		
		// 이미지 S3에 업로드 후 image 테이블에 url 저장
		// 이미지 파일이 있을 경우에만 업로드
		if (info.getImages() != null) {
			for (MultipartFile image : info.getImages()) {
				System.out.println("게시글 들어온 이미지: " + image.getOriginalFilename());
				
				String imageUrl = s3Service.upload(image, "images");
				imageRepo.save(Image.builder().imageUrl(imageUrl).post(post).build());
			}
		}
		
		return post.getId();
	}
	
	// 모든 게시글 가져오기 -> 로그인된 경우
	public List<PostDto.Detail> getAllPost(Account account) {
		List<Post> postList = postRepo.findAll();
		List<PostDto.Detail> result = new ArrayList<>();
		
		for (Post post : postList) {
			int cntOfRecommend = post.getRecommend().size(); // 댓글 개수
			int cntOfComment = post.getComment().size(); // 게시글 좋아요 개수
			
			Account post_account = post.getAccount();
			boolean isAuthor = false;
			
			// 게시글 작성자인지 확인
			if (account.getId() == post_account.getId()) {
				isAuthor = true;
			}
			
			boolean isLiked = likedRepo.existsByPostAndAccount(post, account); // 게시글 좋아요 했는지 확인
			String createAt = post.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
			
			// 사용자와 게시글 이미지 가져와야함
			String accountImage = post.getAccount().getImage().getImageUrl();
			List<String> postImageList = new ArrayList<>();
			
			for (Image image : post.getImages()) {
				postImageList.add(image.getImageUrl());
			}
			
			result.add(new PostDto.Detail(post_account.getNickname(), accountImage, 
					post.getContent(), postImageList, post.getLatitude(), post.getLongitude(), 
					createAt, isAuthor, isLiked, cntOfRecommend, cntOfComment));
		}
		
		return result;
	}
	
	// 특정 게시글 삭제하기
	@Transactional
	public void deletePost(Long post_id) {
		postRepo.deleteById(post_id);
	}
	
	// 게시글 좋아요
	@Transactional
	public boolean addLike(Long post_id, Account account) {
		Post post = postRepo.findById(post_id).orElseThrow(EntityNotFoundException::new);

		boolean isExist = likedRepo.existsByPostAndAccount(post, account);

		if (isExist) {
			return false;
		}

		Liked like = Liked.builder().post(post).account(account).build();
		likedRepo.save(like);

		return true;
	}
	
	// 게시글 좋아요 취소
	@Transactional
	public boolean deleteLike(Long post_id, Account account) {
		Post post = postRepo.findById(post_id).orElseThrow(EntityNotFoundException::new);

		int delete = likedRepo.deleteByPostAndAccount(post, account);

		if (delete == 1) {
			return true;
		} else {
			return false;
		}
	}
	
}
