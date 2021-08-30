package com.yolo.controller;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	@PostMapping("/community")
	public ResponseEntity<?> createRecipePost(@RequestBody PostDto info, @AuthenticationPrincipal Account account) {
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
		try {
			postService.deletePost(id);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 게시글 정보가 없습니다.", "404"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 삭제 실패", "500"));
		}
		
		return ResponseEntity.ok().body(new Response("레시피 삭제 성공"));
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

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("이미 좋아요한 레시피입니다.", "400"));
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

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 좋아요 취소 실패", "500"));
	}
	
}
