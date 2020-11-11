package pl.fintech.dragons.dragonslending.security;

class SecurityConstants {
    static final String SECRET = "SecretKeyToGenJWTs";
    static final long EXPIRATION_TIME = 3600000;
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = "X-Authorization";

    static final String SIGN_UP_URL = "/api/users/sign-up";
    static final String SIGN_IN_URL = "/api/login";
}
