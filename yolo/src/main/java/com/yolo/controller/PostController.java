package com.yolo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.PostDto;
import com.yolo.entity.Account;
import com.yolo.firebase.FCMService;
import com.yolo.response.ErrorResponse;
import com.yolo.response.Response;
import com.yolo.response.SuccessListResponse;
import com.yolo.response.SuccessResponse;
import com.yolo.security.JwtTokenUtil;
import com.yolo.security.JwtUserDetailsService;
import com.yolo.service.PostService;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {

	@Autowired
	PostService postService;
	
	@Autowired
	private JwtUserDetailsService userDetailService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private FCMService fcmService;
	
	// 게시글 작성하기
	@PostMapping(value = "/community")
	public ResponseEntity<?> createPost(@ModelAttribute PostDto info, @AuthenticationPrincipal Account account) {
		Long postId;

		try {
			postId = postService.savePost(info, account);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 작성 실패", 500));
		}

		return ResponseEntity.ok().body(new SuccessResponse<Long>(200, postId));
	}
	
	// 특정 게시글 삭제하기
	@DeleteMapping("/community/{id}")
	public ResponseEntity<?> deletePost(@PathVariable("id") Long id) {
		boolean result;
		
		try {
			result = postService.deletePost(id);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 게시글 정보가 없습니다.", 404));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 삭제 실패", 500));
		}
		
		if (result) {
			return ResponseEntity.ok().body(new Response("게시글 삭제 성공", 200));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 삭제 실패", 500));
		}
	}
	
	// 모든 게시글 가져오기 (최신순, 인기순)
	@GetMapping("/community")
	public ResponseEntity<?> getAllPost(@AuthenticationPrincipal Account account, 
			@RequestHeader(value="Authorization", required=false) String token,
			@RequestParam("page") int page, @RequestParam("sort") String sort) {
		
		List<PostDto.Detail> postList = new ArrayList<>();
		List<PostDto.NotLogin> notLoginPostList = new ArrayList<>();
		
		try {
			// 로그인 O
			if (account != null) {
				token = token.substring(7);
				System.out.println("token: " + token);
				
				UserDetails userDetails = userDetailService.loadUserBySocialIdAndType(account.getSocialId(), account.getType());
				
				if (jwtTokenUtil.validateToken(token, userDetails)) {
					postList = postService.getPostWithPaging(account, page, sort);
				}

				return ResponseEntity.ok().body(new SuccessListResponse<List<PostDto.Detail>>(200, postList.size(), postList));
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
				
				notLoginPostList = postService.getPostWithPagingNotLogin(page, sort);
				
				return ResponseEntity.ok().body(new SuccessListResponse<List<PostDto.NotLogin>>(200, notLoginPostList.size(), notLoginPostList));
			}
			
		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("토큰이 유효하지 않습니다.", 401));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 목록을 가져오는 도중 오류가 발생했습니다.", 500));
		}
	}
	
	// 게시글 좋아요
	@PostMapping("/community/like/{id}")
	public ResponseEntity<?> addLiked(@PathVariable("id") Long id, @AuthenticationPrincipal Account account) {
		Map<String, Object> result;

		try {
			result = postService.addLike(id, account);
			Account postAccount = (Account) result.get("postAccount");
			
			// 게시글 작성자에게 좋아요 알림 보내기
			if (postAccount.isPush()) {
				fcmService.sendLikedPush(postAccount.getRegistrationToken(), account.getNickname());
			}
			
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 게시글 정보가 없습니다.", 404));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 좋아요 도중 오류가 발생했습니다.", 500));
		}
		
		boolean liked = (boolean) result.get("liked");

		if (liked) {
			return ResponseEntity.ok().body(new Response("게시글 좋아요 성공", 200));			
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("이미 좋아요한 게시글입니다.", 400));
	}

	// 게시글 좋아요 취소
	@DeleteMapping("/community/like/{id}")
	public ResponseEntity<?> deleteLiked(@PathVariable("id") Long id, @AuthenticationPrincipal Account account) {
		boolean result;
		
		try {
			result = postService.deleteLike(id, account);
			
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 게시글 정보가 없습니다.", 404));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("게시글 좋아요 취소 실패", 500));
		}

		if (result) {
			return ResponseEntity.ok().body(new Response("게시글 좋아요 취소 성공", 200));			
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("좋아요 하지 않은 게시글입니다.", 400));
	}
	
}
