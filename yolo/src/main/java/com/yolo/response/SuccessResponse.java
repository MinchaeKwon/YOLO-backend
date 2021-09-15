package com.yolo.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SuccessResponse<T> {
	private String message;
	private int resultCode;
	private T result;
	
	public SuccessResponse(int resultCode, T result) {
		this.message = "success";
		this.resultCode = resultCode;
		this.result = result;
	}
}
