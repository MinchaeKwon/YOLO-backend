package com.yolo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
	private String email;
	private String type;
	private String password;
	private String auth;
	private String nickname;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Profile {
		private String email;
		private String type;
		private String nickname;
	}
}
