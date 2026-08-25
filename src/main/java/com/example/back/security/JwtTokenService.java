package com.example.back.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int MINIMUM_SECRET_BYTES = 32;
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final Duration lifetime;

    public JwtTokenService(
            ObjectMapper objectMapper,
            @Value("${app.security.jwt.secret:local-development-jwt-secret-change-before-deployment}") String secret,
            @Value("${app.security.jwt.lifetime:PT1H}") Duration lifetime) {
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.lifetime = lifetime;

        if (this.secret.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalArgumentException("JWT secret must contain at least 32 bytes");
        }
        if (lifetime.isZero() || lifetime.isNegative()) {
            throw new IllegalArgumentException("JWT lifetime must be positive");
        }
    }

    public String generate(String subject, Long accountId, String role) {
        Instant issuedAt = Instant.now();

        Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT");

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", subject);
        claims.put("accountId", accountId);
        claims.put("role", role);
        claims.put("iat", issuedAt.getEpochSecond());
        claims.put("exp", issuedAt.plus(lifetime).getEpochSecond());

        String encodedHeader = encodeJson(header);
        String encodedClaims = encodeJson(claims);
        String unsignedToken = encodedHeader + "." + encodedClaims;

        return unsignedToken + "." + sign(unsignedToken);
    }

    private String encodeJson(Object value) {
        try {
            return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (JacksonException exception) {
            throw new IllegalStateException("Unable to serialize JWT", exception);
        }
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(unsignedToken.getBytes(StandardCharsets.US_ASCII));
            return BASE64_URL_ENCODER.encodeToString(signature);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to sign JWT", exception);
        }
    }
}
