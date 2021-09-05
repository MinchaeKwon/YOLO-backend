package com.yolo.dto;

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
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Profile {
		private Long accountId;
		private String socialId;
		private String type;
		private String nickname;
		private String imageUrl;
	}
}
