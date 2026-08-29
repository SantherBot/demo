package com.example.demo.auth;

import com.example.demo.common.EstadoRegistro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio de Usuario.
 * JpaRepository ya trae: save(), findById(), findAll(), etc.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByUsernameAndState(String username, EstadoRegistro state);

    boolean existsByUsername(String username);
}