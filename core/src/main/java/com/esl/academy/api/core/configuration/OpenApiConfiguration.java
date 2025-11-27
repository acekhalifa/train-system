package com.esl.academy.api.core.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class OpenApiConfiguration {

    @Value("${application.server.apiUrl}")
    private String apiUrl;

    @Bean
    public OpenAPI openAPI() {
        Contact contact = new Contact();
        contact.setEmail("yakubu@encentralsolutions.com");
        contact.setName("Yakubu Ibrahim");

        Info info = new Info()
                .title("TCMP API")
                .contact(contact)
                .version("1.0")
                .description("TCMP - Training and Certificate Management Portal API");

        Server server = new Server().url(apiUrl).description("Computed server url");

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }

}
