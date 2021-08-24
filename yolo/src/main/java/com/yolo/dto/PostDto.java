package com.yolo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
	private String content;
	private String imageUrl;
	private double latitude;
	private double longitude;
	
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Detail {
		private String authorNickname;
		private String authorImage;
		private String content;
		private String imageUrl;
		private double latitude;
		private double longitude;
		private String createAt;
		private boolean isAuthor;
		private boolean isRecommended;
		private int cntOfComment;
		private int cntOfRecommend;
	}
}
