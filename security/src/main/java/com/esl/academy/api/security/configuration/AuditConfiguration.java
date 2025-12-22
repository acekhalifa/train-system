package com.esl.academy.api.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAware<>() {

            @NonNull
            @Override
            public Optional<String> getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated() ||
                        authentication instanceof AnonymousAuthenticationToken) {
                    return Optional.of("{\"userId\":\"system\"}");
                }

                final var user = (CustomUserDetails) authentication.getPrincipal();
                return Optional.of(user.getUser().stringify());
            }
        };
    }

}
