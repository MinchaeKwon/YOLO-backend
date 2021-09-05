package com.yolo.controller;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.PostDto;
import com.yolo.entity.Account;
import com.yolo.response.ErrorResponse;
import com.yolo.response.Response;
import com.yolo.response.SuccessResponse;
import com.yolo.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {

	@Autowired
	PostService postService;
	
	// 게시글 작성하기
	@PostMapping(value = "/community", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createRecipePost(@ModelAttribute PostDto info, @AuthenticationPrincipal Account account) {
		Long postId;

		try {
			postId = postService.savePost(info, account);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 작성 실패", "500"));
		}

		return ResponseEntity.ok().body(new SuccessResponse<Long>(postId));
	}
	
	// 특정 게시글 삭제하기
	@DeleteMapping("/community/{id}")
	public ResponseEntity<?> deleteRecipePost(@PathVariable("id") Long id) {
		boolean result;
		
		try {
			result = postService.deletePost(id);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 게시글 정보가 없습니다.", "404"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 삭제 실패", "500"));
		}
		
		if (result) {
			return ResponseEntity.ok().body(new Response("게시글 삭제 성공"));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 삭제 실패", "500"));
		}
	}
	
	// 모든 게시글 가져오기
	@GetMapping("/community")
	public ResponseEntity<?> getAllPost(@AuthenticationPrincipal Account account) {
		List<PostDto.Detail> postList;
		try {
			postList = postService.getAllPost(account);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 목록을 가져오는 도중 오류가 발생했습니다.", "500"));
		}
		
		if (postList.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("게시글 목록이 없습니다.", "404"));
		}
		
		
		return ResponseEntity.ok().body(new SuccessResponse<List<PostDto.Detail>>(postList));
	}
	
	// 게시글 좋아요
	@PostMapping("/community/like/{id}")
	public ResponseEntity<?> addLiked(@PathVariable("id") Long id, @AuthenticationPrincipal Account account) {
		boolean result;

		try {
			result = postService.addLike(id, account);
			
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 게시글 정보가 없습니다.", "404"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 좋아요 도중 오류가 발생했습니다.", "500"));
		}

		if (result) {
			return ResponseEntity.ok().body(new Response("게시글 좋아요 성공"));			
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("이미 좋아요한 게시글입니다.", "400"));
	}

	// 게시글 좋아요 취소
	@DeleteMapping("/community/like/{id}")
	public ResponseEntity<?> deleteLiked(@PathVariable("id") Long id, @AuthenticationPrincipal Account account) {
		boolean result;
		
		try {
			result = postService.deleteLike(id, account);
			
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 게시글 정보가 없습니다.", "404"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 좋아요 취소 실패", "500"));
		}

		if (result) {
			return ResponseEntity.ok().body(new Response("게시글 좋아요 취소 성공"));			
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("좋아요 하지 않은 게시글입니다.", "400"));
	}
	
}
