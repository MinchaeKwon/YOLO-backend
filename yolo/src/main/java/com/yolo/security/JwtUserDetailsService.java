package com.yolo.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.dto.AccountDto;
import com.yolo.dto.AccountUpdateDto;
import com.yolo.entity.Account;
import com.yolo.entity.Image;
import com.yolo.repository.AccountRepository;
import com.yolo.repository.ImageRepository;
import com.yolo.response.SocialUserNotFoundException;
import com.yolo.service.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ImageRepository imageRepo;
	
	private final S3Service s3Service;

	// 시큐리티에서 지정한 서비스이기 때문에 이 메소드를 필수로 구현
	public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
		return accountRepository.findBySocialId(socialId).orElseThrow(() -> new UsernameNotFoundException((socialId)));
	}

	// 소셜 로그인에 사용
	public UserDetails loadUserBySocialIdAndType(String socialId, String type)
			throws UsernameNotFoundException, SocialUserNotFoundException {
		return accountRepository.findBySocialIdAndType(socialId, type)
				.orElseThrow(() -> new SocialUserNotFoundException((socialId)));
	}

	// 회원정보 저장
	@Transactional
	public Long save(AccountDto infoDto) throws IOException {
		Long accountId = accountRepository.save(Account.builder().socialId(infoDto.getSocialId()).type(infoDto.getType()).auth("ROLE_USER")
				.nickname(infoDto.getNickname()).build()).getId();
		
		return accountId;
	}

	// 닉네임 중복 확인 -> 사용 X
	public Boolean isExistNickname(String nickname) {
		return accountRepository.existsByNickname(nickname);
	}
	
	// 사용자의 id로 사용자 정보 가져오기
	public AccountDto.Profile loadUserById(long id) throws UsernameNotFoundException {
		Account account = accountRepository.findById(id).orElseThrow();
		
		Image image = account.getImage();
		String imageUrl = null;
		
		if (image != null) {
			imageUrl = image.getImageUrl();
		}
		
		AccountDto.Profile result = new AccountDto.Profile(account.getSocialId(), account.getType(), account.getNickname(), imageUrl);
		
		return result;
	}

	// 회원정보 수정
	@Transactional
	public Long updateAccount(AccountUpdateDto infoDto, Account account) throws SocialUserNotFoundException, IOException {
		// 이미지가 없는 경우에는 이미지 수정 X -> 닉네임만 수정
		if (infoDto.getImage() != null) {
			// 기존에 S3에 올라간 사용자 이미지 삭제 후 다시 저장
			// 수정할 이미지 파일 S3에 업로드 후에 사용자 이미지 url 수정
			
			Image image = account.getImage(); // 사용자의 기존 이미지
			
			// 사용자 이미지가 null이 아닐 경우 기존 이미지 삭제하고 수정
			if (image != null) {
				boolean result = s3Service.delete(image.getImageUrl());
				
				// 이미지가 제대로 삭제되었을 경우
				if (result) {
					String imageUrl = s3Service.upload(infoDto.getImage(), "images");
					
					image.updateImage(imageUrl);
					imageRepo.save(image);	
				}
			} 
			else {
				// 사용자 이미지가 null인 경우 새롭게 삽입
				// 이미지 파일 S3에 업로드 후 url을 테이블에 저장
				System.out.println("들어온 이미지: " + infoDto.getImage().getOriginalFilename());
				String imageUrl = s3Service.upload(infoDto.getImage(), "images");
				imageRepo.save(Image.builder().imageUrl(imageUrl).account(account).build());
			}
			
		}
		
		Account updateAccount = (Account) loadUserBySocialIdAndType(account.getSocialId(), account.getType());
		
		updateAccount.update(infoDto);
		return accountRepository.save(updateAccount).getId();
	}
	
	// 회원정보에서 이미지만 삭제
	@Transactional
	public boolean deleteImage(String imageUrl) {
		// S3에 업로드된 이미지도 함께 삭제
		boolean result = s3Service.delete(imageUrl);
		
		int delete = 0;
		
		// S3에 있는 이미지가 삭제되었을 경우에만 DB에서도 삭제
		if (result) {
			delete = imageRepo.deleteByImageUrl(imageUrl);
		}
		
		if (delete > 0) {
			return true;
		} else {
			return false;
		}
	}

}
