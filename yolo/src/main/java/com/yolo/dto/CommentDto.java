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
	private String content;
	private MultipartFile image;
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Detail {
		private Long commentId;
		private String nickname;
		private String authorImage;
		private String content;
		private String imageUrl;
		private String createAt;
		private boolean isAuthor;
	}
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Common {
		private Long commentId;
		private String nickname;
		private String authorImage;
		private String content;
		private String imageUrl;
		private String createAt;
	}
}
