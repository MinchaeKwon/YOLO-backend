package com.yolo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountUpdateDto {
	private String nickname;
	private MultipartFile image;
}
