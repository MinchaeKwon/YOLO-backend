package com.yolo.security;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yolo.dto.AccountDto;
import com.yolo.dto.AccountUpdateDto;
import com.yolo.dto.PostDto;
import com.yolo.entity.Account;
import com.yolo.entity.Image;
import com.yolo.entity.Post;
import com.yolo.entity.WithdrewAccount;
import com.yolo.repository.AccountRepository;
import com.yolo.repository.ImageRepository;
import com.yolo.repository.PostRepository;
import com.yolo.repository.WithdrewAccountRepository;
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
	
	@Autowired
	private PostRepository postRepo;
	
	@Autowired
	private WithdrewAccountRepository withdrewRepo;
	
	private final S3Service s3Service;
	
	private int ELE_SIZE = 10;

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
	
	// 사용자의 id로 사용자 정보 가져오기
	public AccountDto.Profile loadUserById(long id) throws UsernameNotFoundException {
		Account account = accountRepository.findById(id).orElseThrow();
		
		Image image = account.getImage();
		String imageUrl = null;
		
		if (image != null) {
			imageUrl = image.getImageUrl();
		}
		
		AccountDto.Profile result = new AccountDto.Profile(account.getId(), account.getSocialId(), 
				account.getType(), account.getNickname(), imageUrl);
		
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
				System.out.println("사용자 프로필 사진 존재");
				
				// S3에 업로드된 이미지 먼저 삭제
				boolean result = s3Service.delete(image.getImageUrl());
				
				// 이미지가 제대로 삭제되었을 경우
				if (result) {
					String imageUrl = s3Service.upload(infoDto.getImage(), "images");
					
					image.updateImage(imageUrl);
					imageRepo.save(image);	
				}	
				
			} 
			else {
				System.out.println("사용자 프로필 사진 없음");
				
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
	public boolean deleteImage(Account account) {
		Image image = account.getImage();
		System.out.println("삭제할 이미지 url: " + image.getImageUrl() + ", id: " + image.getId());
		
		boolean delete = s3Service.delete(image.getImageUrl());
		int result = 0;
		
		if (delete) {
			result = imageRepo.deleteByImageId(image.getId());
		}
		
		if (result == 1) {
			return true;
		}
		
		return false;
	}
	
	// 사용자가 작성한 게시글 가져오기
	public List<PostDto.My> getMyPost(Account account, int page) {
		Pageable pageable = PageRequest.of(page - 1, ELE_SIZE, Sort.by("id").descending());
		Page<Post> postList = postRepo.findByAccount(account, pageable);
		
		List<PostDto.My> result = new ArrayList<>();
		
		for (Post post : postList) {
			int cntOfRecommend = post.getLiked().size(); // 게시글 좋아요 개수
			int cntOfComment = post.getComment().size(); // 댓글 개수
			
			Account post_account = post.getAccount();
			
			String createAt = post.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
			
			Image accountImage = post_account.getImage();
			String accountImageUrl = null;
			
			if (accountImage != null) {
				accountImageUrl = accountImage.getImageUrl();
			}
			
			List<String> postImage = new ArrayList<>();
			
			for (Image image : post.getImages()) {
				postImage.add(image.getImageUrl());
			}
			
			result.add(new PostDto.My(post.getId(), post_account.getNickname(), accountImageUrl, 
					post.getContent(), postImage, post.getPlaceName(), post.getLatitude(), post.getLongitude(), 
					createAt, cntOfRecommend, cntOfComment));
		}
		
		return result;
	}
	
	// 사용자 토큰 수정 -> FCM에 사용
	@Transactional
	public void modifyRegistrationToken(String token, Account account) {
		account.setRegistrationToken(token);
		accountRepository.save(account);
	}
	
	// 회원 탈퇴
	@Transactional
	public void deletedSave(String socialId, String type) {
		// 회원 테이블에서 회원 정보 삭제
		int delete = accountRepository.deleteBySocialIdAndType(socialId, type);
		
		if (delete == 1) {
			// 탈퇴 회원 테이블에 저장
			withdrewRepo.save(WithdrewAccount.builder().socialId(socialId).type(type).build());
		}
		
		return;
	}
	
	// 닉네임 중복 확인 -> 사용 X
	public Boolean isExistNickname(String nickname) {
		return accountRepository.existsByNickname(nickname);
	}

}
