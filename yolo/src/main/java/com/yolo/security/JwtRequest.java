package com.yolo.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor //need default constructor for JSON Parsing
@AllArgsConstructor
public class JwtRequest {

    private String email;
    private String password;
    private String type;
}