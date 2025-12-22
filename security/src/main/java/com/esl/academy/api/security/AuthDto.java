package com.esl.academy.api.security;

import com.esl.academy.api.user.user.UserDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthDto() {

    public record LoginDto(@NotNull @Email String email,
                           @NotNull String password) {}

    public record LoginResponseDto(UserDto user,
                                   String token) {}

}
