package com.amaris.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@Profile(value = "dev")
public class SwaggerConfig {
    public static final String LOCAL_SERVER_URL = "http://localhost:";
    
    @Value("${server.port}")
    private int port;
    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public OpenAPI openAPI() {
        final Server localServer = new Server();
        localServer.setDescription("Local server for develop");
        localServer.setUrl(LOCAL_SERVER_URL.concat(String.valueOf(port)));

        final OpenAPI openAPI = new OpenAPI();
        openAPI.info(
                new Info()
                    .title(appName)
                    .description("Rest Api for manage task of employees")
                    .version("1.0.0")
                    .license(new License().name("Apache License Version 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
        );
        
        return openAPI;
    }
}