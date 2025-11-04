export interface Auth {
}


// DTO for /api/auth/login
export interface LoginRequest {
    usernameOrEmail: string;
    password: string;
}

// DTO for /api/auth/register
export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

// DTO for the response from login/register/refresh
export interface LoginResponse {
    accessToken: string;
    refreshToken: string;
}