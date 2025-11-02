package com.security.jwt_oauth_authenticator.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String usernameOrEmail;
    private String password;

}
