package com.yolo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.dto.AccountDto;
import com.yolo.dto.AccountUpdateDto;
import com.yolo.entity.Account;
import com.yolo.repository.AccountRepository;
import com.yolo.response.SocialUserNotFoundException;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;

	// 시큐리티에서 지정한 서비스이기 때문에 이 메소드를 필수로 구현
	public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
		return accountRepository.findBySocialId(socialId).orElseThrow(() -> new UsernameNotFoundException((socialId)));
	}

	// 소셜 로그인에 사용
	public UserDetails loadUserBySocialIdAndType(String email, String type)
			throws UsernameNotFoundException, SocialUserNotFoundException {
		return accountRepository.findBySocialIdAndType(email, type)
				.orElseThrow(() -> new SocialUserNotFoundException((email)));
	}

	// 회원정보 저장
	@Transactional
	public Long save(AccountDto infoDto) {
		return accountRepository.save(Account.builder().socialId(infoDto.getSocialId()).type(infoDto.getType()).auth("ROLE_USER")
				.nickname(infoDto.getNickname()).imageUrl(infoDto.getImageUrl()).build()).getId();
	}

	// 닉네임 중복 확인
	public Boolean isExistNickname(String nickname) {
		return accountRepository.existsByNickname(nickname);
	}
	
	// 특정 사용자의 id로 사용자 정보 가져오기
	public AccountDto.Profile loadUserById(long id) throws UsernameNotFoundException {
		Account account = accountRepository.findById(id).orElseThrow();
		AccountDto.Profile result = new AccountDto.Profile(account.getSocialId(), account.getType(), account.getNickname(), account.getImageUrl());
		return result;
	}

	// 회원정보 수정
	public Long updateAccount(AccountUpdateDto infoDto, Account account) throws UsernameNotFoundException, SocialUserNotFoundException {
		Account updateAccount = (Account) loadUserBySocialIdAndType(account.getSocialId(), account.getType());
		
		updateAccount.update(infoDto);
		return accountRepository.save(updateAccount).getId();
	}

}
