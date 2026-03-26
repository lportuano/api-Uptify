package com.itsqmet.service;

import com.itsqmet.component.JwtUtil;
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
import org.springframework.transaction.annotation.Transactional; // Importante para procedimientos

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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

        Plan planBase = planRepository.findByNombre("Gratuito").orElse(null);
        usuario.setPlan(planBase);

        return usuarioRepository.save(usuario);
    }

    // Actualizar datos generales
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario usuarioExistente = buscarById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setRol(usuario.getRol());

        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

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

    public Map<String, Object> autenticar(Usuario loginUsuario) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail(loginUsuario.getEmail());

        if (usuarioEncontrado.isPresent()) {
            Usuario usuario = usuarioEncontrado.get();
            if (passwordEncoder.matches(loginUsuario.getPassword(), usuario.getPassword())) {
                String nombrePlan = (usuario.getPlan() != null) ? usuario.getPlan().getNombre() : "Gratuito";
                String token = jwtUtil.generarToken(usuario.getEmail(), nombrePlan);

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("email", usuario.getEmail());
                response.put("id", usuario.getId());
                response.put("plan", nombrePlan);
                response.put("rol", usuario.getRol().name());

                return response;
            }
        }
        return null;
    }

    /**
     * MÉTODO PARA EL PROCEDIMIENTO ALMACENADO
     * Usamos @Transactional porque los procedimientos suelen modificar datos.
     */
    @Transactional
    public void reportarErrorBaseDatos(Integer usuarioId, String descripcion, String modulo) {
        usuarioRepository.registrarError(usuarioId, descripcion, modulo);
    }
}