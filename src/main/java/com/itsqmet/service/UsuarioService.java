package com.itsqmet.service;

import com.itsqmet.entity.Plan;
import com.itsqmet.entity.Usuario;
import com.itsqmet.repository.PlanRepository;
import com.itsqmet.repository.UsuarioRepository;
import com.itsqmet.role.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> mostrarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Guardar (Registro)
    public Usuario guardarUsuario(Usuario usuario) {
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        usuario.setRol(Rol.ROLE_USUARIO);

        // 2. Asignación automática del plan gratuito al registrarse
        Plan planBase = planRepository.findByNombre("Gratuito")
                .orElse(null); // Si no existe, queda nulo hasta que corras el SQL
        usuario.setPlan(planBase);

        return usuarioRepository.save(usuario);
    }

    // Actualizar
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario usuarioExistente = buscarById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setRol(usuario.getRol());

        // FIX: Antes comparabas el password consigo mismo (!password.equals(password))
        // Ahora verificamos si el usuario envió un nuevo password (no nulo y no vacío)
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    // NUEVO: Método para cambiar solo el plan (usado por el formulario Premium)
    public Usuario actualizarSuscripcion(Long id, String nombrePlan) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Plan nuevoPlan = planRepository.findByNombre(nombrePlan)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + nombrePlan));

        usuario.setPlan(nuevoPlan);
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        Usuario usuarioEliminar = buscarById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));
        usuarioRepository.delete(usuarioEliminar);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(usuario.getRol().name())
                .build();
    }
}