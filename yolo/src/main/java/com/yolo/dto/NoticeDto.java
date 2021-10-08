package com.yolo.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
	private String title;
	private String content;
	private String date;
}
