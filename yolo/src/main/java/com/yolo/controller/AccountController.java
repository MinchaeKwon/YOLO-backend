package com.yolo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yolo.dto.AccountDto;
import com.yolo.dto.AccountUpdateDto;
import com.yolo.entity.Account;
import com.yolo.response.ErrorResponse;
import com.yolo.response.Response;
import com.yolo.response.SocialUserNotFoundException;
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

	// 회원 가입 -> form data로 받도록 수정
	@PostMapping(value = "/signup")
	public ResponseEntity<?> signup(@RequestBody AccountDto info, MultipartFile file) {
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
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest request) throws Exception {
		String token = "";
		final UserDetails userDetails;

		try {
			
			System.out.println("type: " + request.getType());
			userDetails = userDetailService.loadUserBySocialIdAndType(request.getSocialId(), request.getType());

			token = jwtTokenUtil.generateToken(userDetails);
			
		} catch (SocialUserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 회원정보가 없습니다.", "404"));
		}
		
		return ResponseEntity.ok(new JwtResponse(token));
	}
	
	// 로그인한 사용자의 정보 가져오기
	@GetMapping(value = "/account/profile")
	public ResponseEntity<?> getMyAccount(@AuthenticationPrincipal Account account) {
		AccountDto.Profile profile = new AccountDto.Profile(account.getSocialId(), account.getType(), account.getNickname(), account.getImageUrl());
		return ResponseEntity.ok().body(new SuccessResponse<AccountDto.Profile>(profile));
	}
	
	// 사용자 id로 정보 가져오기
	@GetMapping(value = "/account/{id}")
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
	@PutMapping("account/profile")
	public ResponseEntity<?> updateAccount(@RequestBody AccountUpdateDto infoDto, @AuthenticationPrincipal Account account) {
		try {
			userDetailService.updateAccount(infoDto, account);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("회원정보 수정 실패", "500"));
		}
		
		return ResponseEntity.ok().body(new Response("회원정보 수정 성공"));
	}
	
	// 닉네임 중복 확인
	@GetMapping("/nickname/exist")
	public ResponseEntity<?> isExistName(@RequestParam("nickname") String nickname){		
		if(userDetailService.isExistNickname(nickname)) {
			return ResponseEntity.ok().body(new Response("이미 존재하는 닉네임입니다"));
		}
		else {
			return ResponseEntity.ok().body(new Response("사용 가능"));
		}
	}
	
	
}
