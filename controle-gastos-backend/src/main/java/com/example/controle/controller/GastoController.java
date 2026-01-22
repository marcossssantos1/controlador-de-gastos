package com.example.controle.controller;

import com.example.controle.model.dto.GastoFilterDTO;
import com.example.controle.model.dto.GastoRequestDTO;
import com.example.controle.model.dto.GastoResponseDTO;
import com.example.controle.service.GastoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gastos")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Gastos", description = "Gerenciamento de gastos pessoais")
public class GastoController {

    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os gastos", description = "Lista gastos do usuário com paginação")
    public ResponseEntity<Page<GastoResponseDTO>> listarTodos(
            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "dataGasto") String sortBy,
            
            @Parameter(description = "Direção da ordenação (ASC ou DESC)")
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<GastoResponseDTO> gastos = gastoService.listarTodos(pageable);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/filtrar")
    @Operation(summary = "Filtrar gastos", description = "Filtra gastos com múltiplos critérios")
    public ResponseEntity<Page<GastoResponseDTO>> filtrar(
            @Parameter(description = "Descrição (busca parcial)")
            @RequestParam(required = false) String descricao,
            
            @Parameter(description = "ID da categoria")
            @RequestParam(required = false) Long categoriaId,
            
            @Parameter(description = "Data inicial")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            
            @Parameter(description = "Data final")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            
            @Parameter(description = "Valor mínimo")
            @RequestParam(required = false) BigDecimal valorMinimo,
            
            @Parameter(description = "Valor máximo")
            @RequestParam(required = false) BigDecimal valorMaximo,
            
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataGasto") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        GastoFilterDTO filter = new GastoFilterDTO();
        filter.setDescricao(descricao);
        filter.setCategoriaId(categoriaId);
        filter.setDataInicio(dataInicio);
        filter.setDataFim(dataFim);
        filter.setValorMinimo(valorMinimo);
        filter.setValorMaximo(valorMaximo);
        filter.setOrdenarPor(sortBy);
        filter.setDirecao(direction);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<GastoResponseDTO> gastos = gastoService.filtrar(filter, pageable);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar gasto por ID")
    public ResponseEntity<GastoResponseDTO> buscarPorId(@PathVariable Long id) {
        GastoResponseDTO gasto = gastoService.buscarPorId(id);
        return ResponseEntity.ok(gasto);
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Buscar gastos por categoria")
    public ResponseEntity<Page<GastoResponseDTO>> buscarPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataGasto"));
        Page<GastoResponseDTO> gastos = gastoService.buscarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Buscar gastos por período")
    public ResponseEntity<Page<GastoResponseDTO>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataGasto"));
        Page<GastoResponseDTO> gastos = gastoService.buscarPorPeriodo(dataInicio, dataFim, pageable);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/total/periodo")
    @Operation(summary = "Calcular total de gastos por período")
    public ResponseEntity<Map<String, Object>> calcularTotalPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        BigDecimal total = gastoService.calcularTotalPorPeriodo(dataInicio, dataFim);
        Long quantidade = gastoService.contarGastosPorPeriodo(dataInicio, dataFim);
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("quantidade", quantidade);
        response.put("dataInicio", dataInicio);
        response.put("dataFim", dataFim);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Criar novo gasto")
    public ResponseEntity<GastoResponseDTO> criar(@Valid @RequestBody GastoRequestDTO requestDTO) {
        GastoResponseDTO gasto = gastoService.criar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(gasto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar gasto")
    public ResponseEntity<GastoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GastoRequestDTO requestDTO) {
        GastoResponseDTO gasto = gastoService.atualizar(id, requestDTO);
        return ResponseEntity.ok(gasto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar gasto")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        gastoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
