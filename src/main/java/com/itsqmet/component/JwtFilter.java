package com.itsqmet.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            // 1. Buscamos el encabezado "Authorization"
            String authHeader = request.getHeader("Authorization");

            // 2. Si trae un token que empieza con "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // Extraemos el token puro

                try {
                    // 3. Validamos el token usando Auth0
                    var decodedJWT = jwtUtil.validarYDecodificarToken(token);
                    String email = decodedJWT.getSubject();
                    String rol = decodedJWT.getClaim("rol").asString();

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 4. Si el token es válido, le decimos a Spring que este usuario está autorizado
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                email, null, Collections.singletonList(new SimpleGrantedAuthority(rol))
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (Exception e) {
                    // Token inválido o expirado
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
            // 5. Continuar con la petición
            filterChain.doFilter(request, response);
        }
    }
