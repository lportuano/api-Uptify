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

    @GetMapping
    public List<Usuario> getUsuarios() {
        return usuarioService.mostrarUsuarios();
    }

    @PostMapping("/registrarUsuario")
    public Usuario postUsuario(@RequestBody Usuario usuario) {
        return usuarioService.guardarUsuario(usuario);
    }

    // --- CORRECCIÓN AQUÍ: Recibimos el ID de quien edita desde el Header ---
    @PutMapping("/{id}")
    public Usuario putUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuario,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long idEditor) {

        // Si no viene el header, podemos usar el mismo ID del usuario o un 0
        Long autorId = (idEditor != null) ? idEditor : id;
        return usuarioService.actualizarUsuario(id, usuario, autorId);
    }

    // --- CORRECCIÓN AQUÍ: Recibimos el ID de quien elimina ---
    @DeleteMapping("/{id}")
    public void deleteUsuario(
            @PathVariable Long id,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long idEditor) {

        Long autorId = (idEditor != null) ? idEditor : 0L;
        usuarioService.eliminarUsuario(id, autorId);
    }

    @GetMapping("/{id}")
    public Usuario getUsuarioById(@PathVariable Long id) {
        return usuarioService.buscarById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

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

    @PostMapping("/reportar-error")
    public ResponseEntity<?> reportarError(@RequestBody Map<String, Object> payload) {
        try {
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