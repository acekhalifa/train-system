package com.esl.academy.api.invitation;

import com.esl.academy.api.appconfig.AppConfigService;
import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.core.exceptions.AuthenticationException;
import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.user.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class InvitationTokenService {
    private final SecretKey secretKey;
    private final long invitationExpirationDays;
    private final String frontendBaseUrl;
    private final AppConfigService appConfigService;

    public InvitationTokenService(
        @Value("${application.jwt.secret}") String secret,
        @Value("${application.server.privateAppUrl}") String frontendBaseUrl,
        AppConfigService appConfigService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.appConfigService = appConfigService;
        this.invitationExpirationDays =
            Long.parseLong(appConfigService.getAppConfigById(AppConfigId.INVITATION_EXPIRY_DAYS).getValue());
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public String generateInvitationToken(InvitationDataDTO.InvitationTokenData data) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (invitationExpirationDays * 24 * 60 * 60 * 1000));
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", data.userId().toString());
        claims.put("email", data.email());
        claims.put("name", data.fullName());
        claims.put("userType", data.userType().name());

        if (data.trackId() != null) {
            claims.put("trackId", data.trackId().toString());
        }
        return Jwts.builder()
            .claims(claims)
            .subject(data.email())
            .issuedAt(now)
            .expiration(expiration)
            .id(UUID.randomUUID().toString())
            .signWith(secretKey)
            .compact();
    }

    public InvitationDataDTO.InvitationTokenData validateAndExtractToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            // Extract and validate required fields
            String userIdStr = claims.get("userId", String.class);
            String email = claims.get("email", String.class);
            String name = claims.get("name", String.class);
            String userType = claims.get("userType", String.class);

            if (userIdStr == null || email == null || name == null || userType == null) {
                throw new NotFoundException("Missing required claims in token");
            }
            UUID userId = UUID.fromString(userIdStr);
            String trackIdStr = claims.get("trackId", String.class);
            UUID trackId = trackIdStr != null ? UUID.fromString(trackIdStr) : null;
            return new InvitationDataDTO.InvitationTokenData(
                userId,
                email,
                name,
                UserType.valueOf(userType),
                trackId
            );
        } catch (ExpiredJwtException e) {
            log.error("Invitation token has expired: {}", e.getMessage());
            throw new AuthenticationException("Invitation link has expired");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new BadRequestException("Invalid invitation token format");
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new BadRequestException("Malformed invitation token");
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new AuthenticationException("Invalid invitation token signature");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new BadRequestException("Empty invitation token");
        }
    }

    public String generateInvitationUrl(String token) {
        return String.format("%s/setup/%s", frontendBaseUrl, token);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public Date getTokenExpiration(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getExpiration();
    }
}
