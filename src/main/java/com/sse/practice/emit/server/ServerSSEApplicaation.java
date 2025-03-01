package com.sse.practice.emit.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Collections;

@SpringBootApplication(exclude = { RedisReactiveAutoConfiguration.class })
public class ServerSSEApplicaation {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServerSSEApplicaation.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        app.run(args);
    }

    @Bean
    public SecurityWebFilterChain sseServerSpringSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                .anyExchange()
                .permitAll();
        return http.build();
    }
}
