package com.yolo.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
	private String content;
	private String placeName;
	private double latitude;
	private double longitude;
	private List<MultipartFile> images;
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Detail {
		private Long postId;
		private String nickname;
		private String authorImage;
		private String content;
		private List<String> imageUrl;
		private String placeName;
		private double latitude;
		private double longitude;
		private String createAt;
		private boolean isAuthor;
		private boolean isLiked;
		private int cntOfLike;
		private int cntOfComment;
	}
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NotLogin {
		private Long postId;
		private String nickname;
		private String authorImage;
		private String content;
		private List<String> imageUrl;
		private String placeName;
		private double latitude;
		private double longitude;
		private String createAt;
		private int cntOfLike;
		private int cntOfComment;
	}
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class My {
		private Long postId;
		private String nickname;
		private String authorImage;
		private String content;
		private List<String> imageUrl;
		private String placeName;
		private double latitude;
		private double longitude;
		private String createAt;
		private int cntOfLike;
		private int cntOfComment;
	}
}
