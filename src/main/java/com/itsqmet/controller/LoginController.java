package com.itsqmet.controller;

import com.itsqmet.entity.Usuario;

import com.itsqmet.repository.UsuarioRepository;
import com.itsqmet.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioReporitory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginUsuario) {
        // Delegamos TODA la lógica al servicio
        Map<String, String> authResponse = usuarioService.autenticar(loginUsuario);
        if (authResponse != null) {
            // Si las credenciales son correctas, devolvemos el JSON con el Token y el Rol
            return ResponseEntity.ok(authResponse);
        }
        // Si fallan, devolvemos un 401 (No autorizado) con un mensaje claro
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales incorrectas"));
    }
}
