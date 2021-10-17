package com.yolo.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MagazineRespone<T> {
	private String message;
	private int resultCode;
	private String title;
	private T magazine;
	private boolean subscribe;
	
	public MagazineRespone(int resultCode, String title, T magazine, boolean subscribe) {
		this.message = "success";
		this.resultCode = resultCode;
		this.title = title;
		this.magazine = magazine;
		this.subscribe = subscribe;
	}
}
