package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad.
 * Deja pasar todas las peticiones sin login y desactiva CSRF,
 * para poder probar el CRUD con Postman/navegador sin trabas.

 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                       // permite POST/PUT/DELETE sin token
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()                       // todo abierto
                );
        return http.build();
    }
}