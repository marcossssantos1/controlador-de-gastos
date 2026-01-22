package com.example.controle.controller;

import com.example.controle.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/relatorios")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Relatórios", description = "Geração de relatórios em PDF")
public class RelatorioController {

    private final PdfService pdfService;

    public RelatorioController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/pdf")
    @Operation(
        summary = "Gerar relatório PDF de gastos",
        description = "Gera um relatório em PDF com todos os gastos do período especificado, " +
                     "incluindo detalhamento, resumo por categoria e total geral"
    )
    public ResponseEntity<byte[]> gerarRelatorioPdf(
            @Parameter(description = "Data inicial do período", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            
            @Parameter(description = "Data final do período", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        byte[] pdfBytes = pdfService.gerarRelatorioGastos(dataInicio, dataFim);
        
        String nomeArquivo = String.format("relatorio-gastos_%s_a_%s.pdf",
            dataInicio.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            dataFim.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", nomeArquivo);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(pdfBytes);
    }
}
