package com.example.back.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTests {

    private static final String SECRET = "a-test-secret-that-is-at-least-thirty-two-bytes";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generatesASignedJwtContainingTheSessionClaims() throws Exception {
        JwtTokenService tokenService = new JwtTokenService(objectMapper, SECRET, Duration.ofHours(1));

        String token = tokenService.generate("user@example.com", 42L, "USER");
        String[] parts = token.split("\\.");

        assertThat(parts).hasSize(3);

        JsonNode header = decode(parts[0]);
        JsonNode claims = decode(parts[1]);
        assertThat(header.get("alg").asText()).isEqualTo("HS256");
        assertThat(header.get("typ").asText()).isEqualTo("JWT");
        assertThat(claims.get("sub").asText()).isEqualTo("user@example.com");
        assertThat(claims.get("accountId").asLong()).isEqualTo(42L);
        assertThat(claims.get("role").asText()).isEqualTo("USER");
        assertThat(claims.get("exp").asLong() - claims.get("iat").asLong()).isEqualTo(3600L);
        assertThat(parts[2]).isEqualTo(sign(parts[0] + "." + parts[1]));
    }

    private JsonNode decode(String part) throws Exception {
        byte[] json = Base64.getUrlDecoder().decode(part);
        return objectMapper.readTree(json);
    }

    private String sign(String unsignedToken) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signature = mac.doFinal(unsignedToken.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
    }
}
