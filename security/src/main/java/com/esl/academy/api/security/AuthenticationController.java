package com.esl.academy.api.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.esl.academy.api.security.AuthDto.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Login to the system using email and password")
    @PostMapping("login")
    public LoginResponseDto login(@RequestBody @Valid LoginDto dto) {
        return authenticationService.login(dto);
    }

}
