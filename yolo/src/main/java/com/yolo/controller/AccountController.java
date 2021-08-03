package com.yolo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.domain.Account;
import com.yolo.domain.AccountDto;
import com.yolo.domain.AccountUpdateDto;
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
@RequestMapping("/yolo")
public class AccountController {
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailService;

	@Autowired
	private AuthenticationManager authenticationManager;

	// 회원 가입
	@PostMapping(value = "/signup")
	public ResponseEntity<?> signup(@RequestBody AccountDto infoDto) {
		try {
			userDetailService.save(infoDto);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("회원가입을 하는 도중 오류가 발생했습니다.", "500"));
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new Response("회원가입을 성공적으로 완료했습니다."));
	}

	// 로그인
	@PostMapping(value = "/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest request) throws Exception {
		String token = "";
		final UserDetails userDetails;

		try {
			// 일반 로그인
			if (request.getType().equals("normal")) {
				System.out.println("type: " + request.getType());
				authenticate(request.getEmail(), request.getPassword());

				userDetails = userDetailService.authenticateByEmailAndPassword(
						request.getEmail(), request.getType(), request.getPassword());

				token = jwtTokenUtil.generateToken(userDetails);
			} else { // 소셜 로그인
				System.out.println("type: " + request.getType());
				userDetails = userDetailService.loadUserByEmailAndType(request.getEmail(), request.getType());

				token = jwtTokenUtil.generateToken(userDetails);
			}
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 회원정보가 없습니다.", "404"));
		} catch (SocialUserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("소셜 로그인 회원정보 없음", "404"));
		}
		
		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
	
	// 로그인한 사용자의 정보 가져오기 -> 필요한 정보만 return 해야 함
	@GetMapping(value = "/account/profile", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getMyAccount(@AuthenticationPrincipal Account account) {
		return ResponseEntity.ok().body(new SuccessResponse(account));
	}
	
	// 사용자 id로 정보 가져오기
	@GetMapping(value = "/account/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getMyAccountById(@PathVariable("id") Long accountId) {
		Account account;
		
		try {
			account = userDetailService.loadUserById(accountId);	
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("일치하는 회원정보가 없습니다.", "404"));
		}

		return ResponseEntity.ok().body(new SuccessResponse(account));
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
	
	// 이메일 중복 확인
	@GetMapping("/email/exist")
	public ResponseEntity<?> isExistEmail(@RequestParam("email") String email){
		if(userDetailService.isExistEmail(email)) {
			return ResponseEntity.ok().body(new Response("이미 가입된 회원입니다"));
		}
		else {
			return ResponseEntity.ok().body(new Response("사용 가능"));
		}
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
