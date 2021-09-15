package com.yolo.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Response {
    private String message;
    private int resultCode;

    public Response(String message, int resultCode) {
        this.message = message;
        this.resultCode = resultCode;
    }

}