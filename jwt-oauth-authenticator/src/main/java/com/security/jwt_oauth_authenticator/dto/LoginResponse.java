package com.security.jwt_oauth_authenticator.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String refreshToken;

}
