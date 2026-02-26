package com.itsqmet.controller;

import com.itsqmet.entity.Plan;
import com.itsqmet.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/planes")
@CrossOrigin(origins = "*")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping
    public List<Plan> obtenerPlanes() {
        return planService.listarPlanes();
    }
}