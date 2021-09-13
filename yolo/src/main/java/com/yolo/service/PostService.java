package com.yolo.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.yolo.dto.PostDto;
import com.yolo.entity.Account;
import com.yolo.entity.Comment;
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
	
	private int ELE_SIZE = 20;
	
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
				imageRepo.save(Image.builder().imageUrl(imageUrl).post(post).build()).getId();
			}
		}
		
		return post.getId();
	}
	
	// 모든 게시글 가져오기 -> 로그인 O
	@Transactional
	public List<PostDto.Detail> getPostWithPaging(Account account, int page, String sort) {
		Page<Post> postList = null;
		
		if (sort.equals("createAt")) {
			Pageable pageable = PageRequest.of(page - 1, ELE_SIZE, Sort.by("id").descending());
			postList = postRepo.findAll(pageable);
		}
		else if (sort.equals("liked")) {
			Pageable pageable = PageRequest.of(page - 1, ELE_SIZE);
			postList = postRepo.findAlOrderByLiked(pageable);
		}
		
		List<PostDto.Detail> result = new ArrayList<>();
		
		if (postList != null) {
			for (Post post : postList) {
				int cntOfRecommend = post.getLiked().size(); // 게시글 좋아요 개수
				int cntOfComment = post.getComment().size(); // 댓글 개수
				
				Account post_account = post.getAccount();
				boolean isAuthor = false;
				
				// 게시글 작성자인지 확인
				if (account.getId() == post_account.getId()) {
					isAuthor = true;
				}
				
				Image accountImage = post_account.getImage();
				String accountImageUrl = null;
				
				if (accountImage != null) {
					accountImageUrl = accountImage.getImageUrl();
				}
				
				List<String> postImage = new ArrayList<>();
				
				for (Image image : post.getImages()) {
					postImage.add(image.getImageUrl());
				}
				
				boolean isLiked = likedRepo.existsByPostAndAccount(post, account); // 게시글 좋아요 했는지 확인
				String createAt = post.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
				
				result.add(new PostDto.Detail(post.getId(), post_account.getNickname(), accountImageUrl, 
						post.getContent(), postImage, post.getLatitude(), post.getLongitude(), 
						createAt, isAuthor, isLiked, cntOfRecommend, cntOfComment));
			}	
		}
		
		return result;
	}
	
	// 모든 게시글 가져오기 -> 로그인 X
	@Transactional
	public List<PostDto.NotLogin> getPostWithPagingNotLogin(int page, String sort) {
		Page<Post> postList = null;

		if (sort.equals("createAt")) {
			Pageable pageable = PageRequest.of(page - 1, ELE_SIZE, Sort.by("id").descending());
			postList = postRepo.findAll(pageable);
		}
		else if (sort.equals("liked")) {
			Pageable pageable = PageRequest.of(page - 1, ELE_SIZE);
			postList = postRepo.findAlOrderByLiked(pageable);
		}

		List<PostDto.NotLogin> result = new ArrayList<>();

		if (postList != null) {
			for (Post post : postList) {
				int cntOfRecommend = post.getLiked().size(); // 게시글 좋아요 개수
				int cntOfComment = post.getComment().size(); // 댓글 개수

				Account post_account = post.getAccount();

				Image accountImage = post_account.getImage();
				String accountImageUrl = null;

				if (accountImage != null) {
					accountImageUrl = accountImage.getImageUrl();
				}

				List<String> postImage = new ArrayList<>();

				for (Image image : post.getImages()) {
					postImage.add(image.getImageUrl());
				}
				
				String createAt = post.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

				result.add(new PostDto.NotLogin(post.getId(), post_account.getNickname(), accountImageUrl,
						post.getContent(), postImage, post.getLatitude(), post.getLongitude(), createAt, cntOfRecommend, cntOfComment));
			}
		}

		return result;
	}
	
	// 특정 게시글 삭제하기
	@Transactional
	public boolean deletePost(Long post_id) {
		Post post = postRepo.findById(post_id).orElseThrow(EntityNotFoundException::new);
		List<Image> postImage = post.getImages();
		List<Comment> comments = post.getComment();
		
		postRepo.deleteById(post_id);
		
		// S3에 업로드된 사진도 삭제
		boolean result = true;
		
		if (postImage.size() != 0) {
			for (Image image : postImage) {
				result = s3Service.delete(image.getImageUrl());
			}
		}
		
		for (Comment c : comments) {
			result = s3Service.delete(c.getImage().getImageUrl());
		}
		
		return result;
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
