package com.yolo.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Response {
    private String message;

    public Response(String message) {
        this.message = message;
    }

}