package com.esl.academy.api.security;

import com.esl.academy.api.app_config.AppConfigService;
import com.esl.academy.api.core.exceptions.AuthenticationException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.core.services.JwtService;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.esl.academy.api.core.constants.AppConfigId.ACCOUNT_ACTIVATION_DELAY_AFTER_MAX_LOGIN_ATTEMPTS;
import static com.esl.academy.api.core.constants.AppConfigId.MAX_LOGIN_ATTEMPTS;
import static com.esl.academy.api.security.AuthDto.*;
import static com.esl.academy.api.user.UserStatus.ACTIVE;
import static java.lang.Integer.parseInt;
import static java.lang.Short.parseShort;
import static java.time.OffsetDateTime.now;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AppConfigService appConfigService;

    public LoginResponseDto login(@Valid LoginDto dto) {
        final var user = userService.getUserByEmail(dto.email())
            .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (user.getStatus() != ACTIVE) {
            throw new AuthenticationException("Account is not active");
        }

        var loginAttempts = user.getLoginAttempts();
        ++loginAttempts;

        final var maxLoginAttempts = parseShort(appConfigService.getAppConfigById(MAX_LOGIN_ATTEMPTS).orElseThrow().value());
        if (loginAttempts >= maxLoginAttempts) {
            final var currentDate = now();
            var lastLoginDate = currentDate;
            if (user.getLastLoginDate() != null) {
                lastLoginDate = user.getLastLoginDate();
            }

            final var activationDelay = parseInt(appConfigService.getAppConfigById(ACCOUNT_ACTIVATION_DELAY_AFTER_MAX_LOGIN_ATTEMPTS).orElseThrow().value());
            var pTime = currentDate.toEpochSecond()
                - activationDelay
                - lastLoginDate.toEpochSecond();
            if (pTime < 0) {
                pTime = pTime * -1;
                throw new AuthenticationException(String.format("You have exceeded your login attempts, wait for %02d minutes %02d seconds to login again", pTime / 60, pTime % 60));
            }
        }

        try {
            final var authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
            final var authentication = authenticationManager.authenticate(authenticationToken);

            if (!authentication.isAuthenticated()) {
                throw new AuthenticationException("Authentication failed");
            }
        } catch (Exception e) {
            user.setLoginAttempts(loginAttempts);
            userService.upsertUser(user);

            throw new BadCredentialsException("Invalid details", e);
        }

        final var token = jwtService.generateToken(user.getUserId().toString(), user.getEmail());

        user.setLoginAttempts((short) 0);
        user.setLastLoginDate(now());
        final var userDto = userService.upsertUser(user);

        return new LoginResponseDto(userDto, token);
    }

    public User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        return userService.getUserByEmail(email).orElseThrow(()-> new NotFoundException("Authenticated user not found"));
    }
}
