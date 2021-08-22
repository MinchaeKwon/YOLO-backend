package com.yolo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountDto {
	private String email;
	private String type;
	private String password;
	private String auth;
	private String nickname;
	
	@Getter
	public static class Profile {
		private String email;
		private String type;
		private String nickname;
	}
}
