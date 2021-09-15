package com.yolo.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SuccessListResponse<T> {
	private String message;
	private int resultCode;
	private int count;
	private T result;
	
	public SuccessListResponse(int resultCode, int count, T result) {
		this.message = "success";
		this.resultCode = resultCode;
		this.count = count;
		this.result = result;
	}
}
