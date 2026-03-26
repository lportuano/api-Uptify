package com.itsqmet.repository;

import com.itsqmet.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    // --- NUEVO: Llamada al procedimiento de la base de datos ---
    // En UsuarioRepository.java
    @Transactional
    @Modifying
    @Procedure(procedureName = "sp_registrar_error")
    void registrarError(
            @Param("p_usuario_id") Integer p_usuario_id,
            @Param("p_descripcion") String p_descripcion,
            @Param("p_modulo") String p_modulo
    );
}