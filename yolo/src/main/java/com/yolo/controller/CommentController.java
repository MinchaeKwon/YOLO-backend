package com.yolo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.CommentDto;
import com.yolo.entity.Account;
import com.yolo.entity.Post;
import com.yolo.firebase.FCMService;
import com.yolo.response.ErrorResponse;
import com.yolo.response.Response;
import com.yolo.response.SuccessListResponse;
import com.yolo.response.SuccessResponse;
import com.yolo.security.JwtTokenUtil;
import com.yolo.security.JwtUserDetailsService;
import com.yolo.service.CommentService;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

	@Autowired
	CommentService commtService;
	
	@Autowired
	private JwtUserDetailsService userDetailService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private FCMService fcmService;

	// 댓글 작성하기
	@PostMapping(value = "/community/{postId}/comment")
	public ResponseEntity<?> createComment(@PathVariable("postId") Long postId, @ModelAttribute CommentDto info, @AuthenticationPrincipal Account account) {
		Map<String, Object> result;
		CommentDto.Common commt;
		Post post;

		try {
			result = commtService.save(postId, info, account);
			
			commt = (CommentDto.Common) result.get("commentDto");
			post = (Post) result.get("post");
			
			// 게시글 작성자에게 푸시 알림 보내기
			if(post.getAccount().isCommentPush()) {
				fcmService.sendCommentToToken(post.getAccount().getRegistrationToken());
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 게시글 정보가 없습니다.", 404));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("댓글 작성 실패", 500));
		}

		return ResponseEntity.ok().body(new SuccessResponse<CommentDto.Common>(200, commt));
	}

	// 특정 댓글 삭제하기
	@DeleteMapping("/community/comment/{commentId}")
	public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long id) {
		boolean result;
		
		try {
			result = commtService.delete(id);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 댓글 정보가 없습니다.", 404));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("댓글 삭제 실패", 500));
		}

		if (result) {
			return ResponseEntity.ok().body(new Response("댓글 삭제 성공", 200));	
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("댓글 삭제 실패", 500));
		}
	}

	// 특정 게시글의 모든 댓글 가져오기 -> 최신순
	@GetMapping("/community/{postId}/comment")
	public ResponseEntity<?> getAllComment(@PathVariable("postId") Long postId, @AuthenticationPrincipal Account account, 
			@RequestHeader(value="Authorization", required=false) String token) {
		
		List<CommentDto.Detail> commentList = new ArrayList<>();
		List<CommentDto.Common> notLoginList = new ArrayList<>();
		
		try {
			// 로그인 O
			if (account != null) {
				token = token.substring(7, token.length());
				System.out.println("token: " + token);

				UserDetails userDetails = userDetailService.loadUserBySocialIdAndType(account.getSocialId(), account.getType());

				if (jwtTokenUtil.validateToken(token, userDetails)) {
					commentList = commtService.getAllComment(postId, account);
				}
				
				return ResponseEntity.ok().body(new SuccessListResponse<List<CommentDto.Detail>>(200, commentList.size(), commentList));
			}
			else { // 로그인 X
				// 헤더에 토큰이 null이 아닐 경우 토큰이 유효한지 확인
				if (token != null) {
					
					// 프론트에서 로그인 안했을때 자동으로 Autorization: null을 넘겨주는데 이때 null이 문자열이어서 이곳에서 확인해줘야함
//					if (token.equals("null")) {
//						notLoginPost = postService.getPostNotLogin(id);
//						return ResponseEntity.ok().body(new SuccessResponse<PostDto.DetailNotLogin>(notLoginPost));
//					}
					
					// 토큰이 유효한지 확인하기 위함
					token = token.substring(7);
					jwtTokenUtil.getUsernameFromToken(token);
				}
				
				notLoginList = commtService.getAllCommentNotLogin(postId);
				
				return ResponseEntity.ok().body(new SuccessListResponse<List<CommentDto.Common>>(200, notLoginList.size(), notLoginList));
			}
			
		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("토큰이 유효하지 않습니다.", 401));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("댓글 목록을 가져오는 도중 오류가 발생했습니다.", 500));
		}

	}

}
