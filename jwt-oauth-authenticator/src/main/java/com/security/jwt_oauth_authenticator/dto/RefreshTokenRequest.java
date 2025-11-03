package com.security.jwt_oauth_authenticator.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenRequest {

    private String refreshToken;

}
