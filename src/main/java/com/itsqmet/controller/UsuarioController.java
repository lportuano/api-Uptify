package com.itsqmet.controller;

import com.itsqmet.entity.Usuario;
import com.itsqmet.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // --- CRUD BÁSICO ---

    @GetMapping
    public List<Usuario> getUsuarios() {
        return usuarioService.mostrarUsuarios();
    }

    @PostMapping("/registrarUsuario")
    public Usuario postUsuario(@RequestBody Usuario usuario) {
        return usuarioService.guardarUsuario(usuario);
    }

    @PutMapping("/{id}")
    public Usuario putUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }

    @GetMapping("/{id}")
    public Usuario getUsuarioById(@PathVariable Long id) {
        return usuarioService.buscarById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // --- LÓGICA DE NEGOCIO (SUSCRIPCIONES) ---

    @PutMapping("/{id}/plan")
    public ResponseEntity<?> actualizarSuscripcion(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String nombrePlan = payload.get("nombrePlan");
            Usuario actualizado = usuarioService.actualizarSuscripcion(id, nombrePlan);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cambiar plan: " + e.getMessage());
        }
    }

    // --- LLAMADA AL PROCEDIMIENTO ALMACENADO (STORED PROCEDURE) ---

    @PostMapping("/reportar-error")
    public ResponseEntity<?> reportarError(@RequestBody Map<String, Object> payload) {
        try {
            // Convertimos el ID que viene de la web a Integer
            Integer usuarioId = Integer.parseInt(payload.get("usuarioId").toString());
            String descripcion = (String) payload.get("descripcion");
            String modulo = (String) payload.get("modulo");

            usuarioService.reportarErrorBaseDatos(usuarioId, descripcion, modulo);

            return ResponseEntity.ok(Map.of("mensaje", "Reporte procesado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}