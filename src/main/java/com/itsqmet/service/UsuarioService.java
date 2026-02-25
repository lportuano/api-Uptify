package com.itsqmet.service;

import com.itsqmet.entity.Usuario;
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
    private PasswordEncoder passwordEncoder;

    //leer
    public List<Usuario> mostrarUsuarios() {
        return usuarioRepository.findAll();
    }

    //buscar id
    public Optional<Usuario> buscarById(Long id) {
        return usuarioRepository.findById(id);
    }

    //guardar
    public Usuario guardarUsuario(Usuario usuario) {
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        usuario.setRol(Rol.ROLE_USUARIO);
        return usuarioRepository.save(usuario);
    }

    //actualizar
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario usuarioExistente = buscarById((id))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setRol(usuario.getRol());

        if (usuario.getPassword() != null && !usuario.getPassword().equals(usuario.getPassword())) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuarioExistente);
    }

    //eliminar
    public void eliminarUsuario(Long id) {
        Usuario usuarioEliminar = buscarById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));
        usuarioRepository.delete(usuarioEliminar);
    }

    //autenticacion
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        //metodo builder
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(usuario.getRol().name())
                .build();
    }
}
