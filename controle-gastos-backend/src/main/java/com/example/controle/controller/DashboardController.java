package com.example.controle.controller;

import com.example.controle.model.dto.DashboardDTO;
import com.example.controle.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Dashboard", description = "Estatísticas e resumos de gastos")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(
        summary = "Obter dashboard do mês",
        description = "Retorna estatísticas completas do mês: total, comparação com mês anterior, " +
                     "gastos por categoria, maiores gastos e gastos por dia"
    )
    public ResponseEntity<DashboardDTO> getDashboard(
            @Parameter(description = "Mês de referência no formato YYYY-MM (ex: 2024-01). " +
                                    "Se não informado, usa o mês atual")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        
        YearMonth mesReferencia = mes != null ? mes : YearMonth.now();
        DashboardDTO dashboard = dashboardService.getDashboard(mesReferencia);
        return ResponseEntity.ok(dashboard);
    }
}
