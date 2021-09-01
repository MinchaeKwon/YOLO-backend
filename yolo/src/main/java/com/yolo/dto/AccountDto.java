package com.yolo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
	private String socialId;
	private String type;
	private String nickname;
	private MultipartFile image;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Profile {
		private String socialId;
		private String type;
		private String nickname;
		private String imageUrl;
	}
}
