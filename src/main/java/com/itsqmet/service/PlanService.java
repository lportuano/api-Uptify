package com.itsqmet.service;

import com.itsqmet.entity.Plan;
import com.itsqmet.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    // Para que el frontend pueda mostrar los 3 planes en el <select>
    public List<Plan> listarPlanes() {
        return planRepository.findAll();
    }

    public Plan buscarPorNombre(String nombre) {
        return planRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
    }
}