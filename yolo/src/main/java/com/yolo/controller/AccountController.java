package com.yolo.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.dto.AccountDto;
import com.yolo.dto.AccountUpdateDto;
import com.yolo.dto.PostDto;
import com.yolo.entity.Account;
import com.yolo.entity.Image;
import com.yolo.response.ErrorResponse;
import com.yolo.response.Response;
import com.yolo.response.SocialUserNotFoundException;
import com.yolo.response.SuccessListResponse;
import com.yolo.response.SuccessResponse;
import com.yolo.security.JwtRequest;
import com.yolo.security.JwtResponse;
import com.yolo.security.JwtTokenUtil;
import com.yolo.security.JwtUserDetailsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailService;

	// 회원정보 저장 -> socialId, type, nickname
	@PostMapping(value = "/signup")
	public ResponseEntity<?> signup(@ModelAttribute AccountDto info) {
		try {
			userDetailService.save(info);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("회원가입을 하는 도중 오류가 발생했습니다.", "500"));
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(new Response("회원가입을 성공적으로 완료했습니다."));
	}

	// 소셜 로그인
	@PostMapping(value = "/login")
	public ResponseEntity<?> createAuthenticationToken(@ModelAttribute JwtRequest request) throws Exception {
		String token = "";
		final UserDetails userDetails;

		try {
			System.out.println("type: " + request.getType());
			userDetails = userDetailService.loadUserBySocialIdAndType(request.getSocialId(), request.getType());

			token = jwtTokenUtil.generateToken(userDetails);

		} catch (SocialUserNotFoundException e) {
			// 문자열로 보내지말고 int로 1234 이런식으로 보내기
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 회원정보가 없습니다.", "404"));
		}

		return ResponseEntity.ok(new JwtResponse(token));
	}

	// 로그인한 사용자의 정보 가져오기
	@GetMapping("/account/profile")
	public ResponseEntity<?> getMyAccount(@AuthenticationPrincipal Account account) {
		Image image = account.getImage();
		String imageUrl = null;

		if (image != null) {
			imageUrl = image.getImageUrl();
		}

		AccountDto.Profile profile = new AccountDto.Profile(account.getId(), account.getSocialId(), account.getType(),
				account.getNickname(), imageUrl);
		return ResponseEntity.ok().body(new SuccessResponse<AccountDto.Profile>(profile));
	}

	// 사용자 id로 정보 가져오기
	@GetMapping("/account/{id}")
	public ResponseEntity<?> getMyAccountById(@PathVariable("id") Long accountId) {
		AccountDto.Profile account;

		try {
			account = userDetailService.loadUserById(accountId);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 회원정보가 없습니다.", "404"));
		}

		return ResponseEntity.ok().body(new SuccessResponse<AccountDto.Profile>(account));
	}

	// 회원정보 수정
	@PutMapping(value = "account/profile")
	public ResponseEntity<?> updateAccount(@ModelAttribute AccountUpdateDto infoDto, @AuthenticationPrincipal Account account) {
		try {
			userDetailService.updateAccount(infoDto, account);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("회원정보 수정 실패", "500"));
		}

		return ResponseEntity.ok().body(new Response("회원정보 수정 성공"));
	}

	// 사용자 프로필 사진 삭제하기
	@DeleteMapping("account/profile/image")
	public ResponseEntity<?> deleteMyImage(@AuthenticationPrincipal Account account) {
		boolean result;

		try {
			result = userDetailService.deleteImage(account);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("이미지 삭제 실패", "500"));
		}

		if (result) {
			return ResponseEntity.ok().body(new Response("이미지 삭제 성공"));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("이미지 삭제 실패", "500"));
		}

	}
	
	// 사용자가 커뮤니티에 작성한 게시글 목록 가져오기 -> 최신순
	@GetMapping("account/post")
	public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal Account account, @RequestParam("page") int page) {
		List<PostDto.My> postList = new ArrayList<>();

		try {
			postList = userDetailService.getMyPost(account, page);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("이미지 삭제 실패", "500"));
		}

		if (postList.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("게시글 목록이 없습니다.", "404"));
		}
		
		return ResponseEntity.ok().body(new SuccessListResponse<List<PostDto.My>>(postList.size(), postList));
	}

	// 닉네임 중복 확인 -> 사용 X
	@GetMapping("/nickname/exist")
	public ResponseEntity<?> isExistName(@RequestParam("nickname") String nickname) {
		if (userDetailService.isExistNickname(nickname)) {
			return ResponseEntity.ok().body(new Response("이미 존재하는 닉네임입니다."));
		} else {
			return ResponseEntity.ok().body(new Response("사용 가능한 닉네임입니다."));
		}
	}

}
