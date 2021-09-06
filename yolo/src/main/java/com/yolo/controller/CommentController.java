package com.yolo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.CommentDto;
import com.yolo.entity.Account;
import com.yolo.response.ErrorResponse;
import com.yolo.response.Response;
import com.yolo.response.SuccessResponse;
import com.yolo.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

	@Autowired
	CommentService commtService;

	// 댓글 작성하기
	@PostMapping(value = "/community/{postId}/comment")
	public ResponseEntity<?> createRecipePost(@PathVariable("postId") Long postId, @ModelAttribute CommentDto info, @AuthenticationPrincipal Account account) {
		Long commentId;

		try {
			commentId = commtService.save(postId, info, account);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("댓글 작성 실패", "500"));
		}

		return ResponseEntity.ok().body(new SuccessResponse<Long>(commentId));
	}

	// 특정 댓글 삭제하기
	@DeleteMapping("/community/comment/{commentId}")
	public ResponseEntity<?> deleteRecipePost(@PathVariable("commentId") Long id) {
		boolean result;
		
		try {
			result = commtService.delete(id);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 댓글 정보가 없습니다.", "404"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("댓글 삭제 실패", "500"));
		}

		if (result) {
			return ResponseEntity.ok().body(new Response("댓글 삭제 성공"));	
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("댓글 삭제 실패", "500"));
		}
	}

	// 특정 게시글의 모든 댓글 가져오기
	@GetMapping("/community/{postId}/comment")
	public ResponseEntity<?> getAllPost(@PathVariable("postId") Long postId, @AuthenticationPrincipal Account account) {
		List<CommentDto.Detail> commentList;
		try {
			commentList = commtService.getAllComment(postId, account);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("댓글 목록을 가져오는 도중 오류가 발생했습니다.", "500"));
		}

		if (commentList.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("댓글 목록이 없습니다.", "404"));
		}

		return ResponseEntity.ok().body(new SuccessResponse<List<CommentDto.Detail>>(commentList));
	}

}
