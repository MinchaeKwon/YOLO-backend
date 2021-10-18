package com.yolo.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
	private Long noticeId;
	private String title;
	private String content;
	private String date;
}
