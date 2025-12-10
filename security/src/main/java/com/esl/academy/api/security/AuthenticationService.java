package com.esl.academy.api.security;

import com.esl.academy.api.security.configuration.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import static com.esl.academy.api.security.AuthDto.LoginDto;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String login(@Valid LoginDto dto) {
//        Authentication authentication = authenticationManager
//            .authenticate(new UsernamePasswordAuthenticationToken(
//                dto.email(),
//                dto.password())
//            );
//        if (authentication.isAuthenticated())
//            return jwtService.generateToken(userDto);
        return "Authentication failed";
    }
}
