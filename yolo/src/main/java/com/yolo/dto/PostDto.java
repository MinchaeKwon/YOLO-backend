package com.yolo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
	private String content;
	private double latitude;
	private double longitude;
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Detail {
		private String nickname;
		private String authorImage;
		private String content;
		private List<String> imageUrl;
		private double latitude;
		private double longitude;
		private String createAt;
		private boolean isAuthor;
		private boolean isLiked;
		private int cntOfComment;
		private int cntOfLike;
	}
}
