package com.yolo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.domain.Account;
import com.yolo.domain.AccountDto;
import com.yolo.domain.AccountUpdateDto;
import com.yolo.repository.AccountRepository;
import com.yolo.response.SocialUserNotFoundException;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AccountRepository accountRepository;

	// 시큐리티에서 지정한 서비스이기 때문에 이 메소드를 필수로 구현
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException((email)));
	}

	// 로그인 검증
	public UserDetails authenticateByEmailAndPassword(String email, String type, String password) {
		Account account = accountRepository.findByEmailAndType(email, type)
				.orElseThrow(() -> new UsernameNotFoundException(email));

		if (!passwordEncoder.matches(password, account.getPassword())) {
			throw new BadCredentialsException("Password not matched");
		}

		return account;
	}

	// 소셜 로그인에 사용
	public UserDetails loadUserByEmailAndType(String email, String type)
			throws UsernameNotFoundException, SocialUserNotFoundException {
		return accountRepository.findByEmailAndType(email, type)
				.orElseThrow(() -> new SocialUserNotFoundException((email)));
	}

	// 회원정보 저장
	@Transactional
	public Long save(AccountDto infoDto) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		infoDto.setPassword(encoder.encode(infoDto.getPassword()));

		return accountRepository.save(Account.builder().email(infoDto.getEmail()).type(infoDto.getType())
				.password(infoDto.getPassword()).auth(infoDto.getAuth()).nickname(infoDto.getNickname())
				.phonenumber(infoDto.getPhonenumber()).build()).getAccountId();
	}

	// 이메일 중복 확인
	public Boolean isExistEmail(String email) {
		return accountRepository.existsByEmail(email);
	}

	// 닉네임 중복 확인
	public Boolean isExistNickname(String nickname) {
		return accountRepository.existsByNickname(nickname);
	}
	
	// 특정 사용자의 accountId로 사용자 정보 가져오기
	public Account loadUserById(long id) throws UsernameNotFoundException {
		return accountRepository.findById(id).orElseThrow();
	}

	// 회원정보 수정
	public Long updateAccount(AccountUpdateDto infoDto, Account account) throws UsernameNotFoundException, SocialUserNotFoundException {
		Account updateAccount = (Account) loadUserByEmailAndType(account.getEmail(), account.getType());
		
		updateAccount.update(infoDto);
		return accountRepository.save(updateAccount).getAccountId();
	}

}
