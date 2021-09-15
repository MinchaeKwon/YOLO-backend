package com.yolo.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorResponse {
	private String errorMessage;
	private int resultCode;
	
	public ErrorResponse(String errorMessage, int errorCode) {
		this.errorMessage = errorMessage;
		this.resultCode = errorCode;
	}
}
