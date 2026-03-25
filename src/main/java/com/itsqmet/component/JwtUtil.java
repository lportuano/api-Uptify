package com.itsqmet.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta: Debe ser la misma en el filtro de validación
    private final String SECRET_KEY = "ItsqmetDesarrolloSoftware2026!";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    /**
     * Genera un token que incluye el email y el ROL/PLAN del usuario.
     * @param email Identificador del usuario (Subject).
     * @param rol El rol o plan actual (ej: ROLE_PREMIUM, ROLE_USUARIO).
     * @return String del token firmado.
     */
    public String generarToken(String email, String rol) {
        try {
            return JWT.create()
                    .withSubject(email)
                    .withClaim("role", rol) // Guardamos el rol con la clave "role"
                    .withIssuedAt(new Date())
                    // Expiración: 24 horas (ajustable según necesites)
                    .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error al crear el token JWT", exception);
        }
    }

    /**
     * Valida y decodifica el token recibido del Frontend.
     * @param token El token JWT enviado en el header Authorization.
     * @return Objeto DecodedJWT con la información del usuario.
     */
    public DecodedJWT validarYDecodificarToken(String token) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token inválido o expirado", exception);
        }
    }

    /**
     * Extrae el email (Subject) del token sin validarlo completamente
     * (útil para filtros de seguridad rápidos).
     */
    public String obtenerEmail(String token) {
        return JWT.decode(token).getSubject();
    }
}