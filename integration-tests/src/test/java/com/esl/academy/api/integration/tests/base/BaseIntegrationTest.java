package com.esl.academy.api.integration.tests.base;

import com.esl.academy.api.TCMPApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = TCMPApplication.class)
@Import(TestcontainersConfiguration.class)
public abstract class BaseIntegrationTest {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @DynamicPropertySource
    static void postgresProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.placeholderReplacement", () -> "false");
        registry.add("spring.flyway.locations", () -> "filesystem:../service-gateway/**/db/migration/default,filesystem:../integration-tests/**/db/migration/test");
        registry.add("spring.main.allow-circular-references", () -> true);
        registry.add("spring.threads.virtual.enabled", () -> true);

        registry.add("application.server.apiUrl", () -> "http://localhost:9000");
        registry.add("application.server.publicAppUrl", () -> "http://localhost:4200");
        registry.add("application.server.privateAppUrl", () -> "http://localhost:4200");

        registry.add("application.cors.liveOrigins", () -> "http://localhost:4200");
        registry.add("application.cors.devOrigins", () -> "http://localhost:4200");

        registry.add("application.jwt.access.secret", () -> "91727bb427fab55f0404411bf7df91bfd422d354bf8d5cbb8e6ec40367eb8baf");
        registry.add("application.jwt.access.expiration", () -> "60");
        registry.add("application.jwt.refresh.expiration", () -> "7");

        registry.add("application.storage.path", () -> "~/");
    }

    protected void forceFlush() {
        entityManager.flush();
        entityManager.clear();
    }

    protected void setAuthenticatedUser(String usernameOrEmail) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}