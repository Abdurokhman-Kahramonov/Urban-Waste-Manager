package com.example.UrbanWasteManager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class JwtService {

    private final String SECRET_KEY = "super_secret_key_which_should_be_in_env_vars_but_is_here_for_demo_purposes_1234567890";
    private final long EXPIRATION_TIME = 86400000; // 1 day
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateToken(UserDetails userDetails) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", userDetails.getUsername());
            payload.put("roles", userDetails.getAuthorities().toString());
            payload.put("iat", new Date().getTime());
            payload.put("exp", new Date().getTime() + EXPIRATION_TIME);

            String headerEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsBytes(header));
            String payloadEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsBytes(payload));

            String signature = sign(headerEncoded + "." + payloadEncoded);

            return headerEncoded + "." + payloadEncoded + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);
            return (String) payload.get("sub");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String headerEncoded = parts[0];
            String payloadEncoded = parts[1];
            String signature = parts[2];

            String expectedSignature = sign(headerEncoded + "." + payloadEncoded);
            if (!expectedSignature.equals(signature)) return false;

            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadEncoded), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);
            
            String username = (String) payload.get("sub");
            long exp = ((Number) payload.get("exp")).longValue();

            return username.equals(userDetails.getUsername()) && new Date().getTime() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    private String sign(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
