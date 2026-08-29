package com.example.demo.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de autenticacion.
 * Ruta base: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // POST /api/auth/register -> 201 Created
    @PostMapping("/register")
    public ResponseEntity<AuthService.RegisterResponse> register(@RequestBody AuthRequest request) {
        AuthService.RegisterResponse creado = service.register(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // POST /api/auth/login -> 200 OK
    @PostMapping("/login")
    public AuthService.LoginResponse login(@RequestBody AuthRequest request) {
        return service.login(request.username(), request.password());
    }

    public record AuthRequest(String username, String password) {
    }
}