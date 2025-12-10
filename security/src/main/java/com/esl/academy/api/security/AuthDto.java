package com.esl.academy.api.security;

public record AuthDto() {

    public record LoginDto(
        String email,
        String password
    ) {}
}
