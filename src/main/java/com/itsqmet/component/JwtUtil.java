package com.itsqmet.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta para firmar el token
    private final String SECRET_KEY = "ItsqmetDesarrolloSoftware2026!";
    // El algoritmo que usaremos (HMAC256)
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    public String generarToken(String email, String rol) {
        return JWT.create()
                .withSubject(email)
                .withClaim("rol", rol)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .sign(algorithm);
    }

    public DecodedJWT validarYDecodificarToken(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }
}
