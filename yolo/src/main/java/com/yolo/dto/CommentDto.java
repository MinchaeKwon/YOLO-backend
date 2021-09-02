package com.yolo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
	String content;
	MultipartFile image;
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Detail {
		String content;
		String imageUrl;
		String nickname;
		String authorImage;
		String createAt;
		boolean isAuthor;
	}
}
