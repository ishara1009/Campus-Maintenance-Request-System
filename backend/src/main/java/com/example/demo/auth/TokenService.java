package com.example.demo.auth;

import com.example.demo.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class TokenService {
    private final AuthTokenRepository tokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long tokenHours;

    public TokenService(AuthTokenRepository tokenRepository,
                        @Value("${app.auth.token-hours:24}") long tokenHours) {
        this.tokenRepository = tokenRepository;
        this.tokenHours = tokenHours;
    }

    public String createToken(User user) {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        tokenRepository.save(new AuthToken(hash(rawToken), user,
                Instant.now().plus(tokenHours, ChronoUnit.HOURS)));
        return rawToken;
    }

    public String hash(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
