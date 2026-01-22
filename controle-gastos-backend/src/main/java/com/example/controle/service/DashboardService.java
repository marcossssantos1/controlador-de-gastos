package com.example.controle.service;

import com.example.controle.mapper.GastoMapper;
import com.example.controle.model.dto.DashboardDTO;
import com.example.controle.model.dto.GastoResponseDTO;
import com.example.controle.model.entity.Gasto;
import com.example.controle.model.entity.Usuario;
import com.example.controle.repository.GastoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final GastoRepository gastoRepository;
    private final GastoMapper gastoMapper;

    public DashboardService(GastoRepository gastoRepository, GastoMapper gastoMapper) {
        this.gastoRepository = gastoRepository;
        this.gastoMapper = gastoMapper;
    }

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    @Transactional(readOnly = true)
    public DashboardDTO getDashboard(YearMonth mesReferencia) {
        Usuario usuario = getUsuarioLogado();
        log.info("Gerando dashboard para usuário: {} - mês: {}", usuario.getEmail(), mesReferencia);

        LocalDate dataInicio = mesReferencia.atDay(1);
        LocalDate dataFim = mesReferencia.atEndOfMonth();

        YearMonth mesAnterior = mesReferencia.minusMonths(1);
        LocalDate dataInicioAnterior = mesAnterior.atDay(1);
        LocalDate dataFimAnterior = mesAnterior.atEndOfMonth();

        DashboardDTO dashboard = new DashboardDTO();

        // Total do mês
        BigDecimal totalMes = gastoRepository.somarGastosPorUsuarioEPeriodo(
            usuario.getId(), dataInicio, dataFim
        );
        dashboard.setTotalMes(totalMes != null ? totalMes : BigDecimal.ZERO);

        // Total do mês anterior
        BigDecimal totalMesAnterior = gastoRepository.somarGastosPorUsuarioEPeriodo(
            usuario.getId(), dataInicioAnterior, dataFimAnterior
        );
        dashboard.setTotalMesAnterior(totalMesAnterior != null ? totalMesAnterior : BigDecimal.ZERO);

        // Percentual de variação
        BigDecimal percentualVariacao = calcularPercentualVariacao(
            dashboard.getTotalMes(), 
            dashboard.getTotalMesAnterior()
        );
        dashboard.setPercentualVariacao(percentualVariacao);

        // Quantidade de gastos
        Long quantidade = gastoRepository.contarGastosPorUsuarioEPeriodo(
            usuario.getId(), dataInicio, dataFim
        );
        dashboard.setQuantidadeGastos(quantidade != null ? quantidade : 0L);

        // Ticket médio
        BigDecimal ticketMedio = BigDecimal.ZERO;
        if (dashboard.getQuantidadeGastos() > 0) {
            ticketMedio = dashboard.getTotalMes()
                .divide(new BigDecimal(dashboard.getQuantidadeGastos()), 2, RoundingMode.HALF_UP);
        }
        dashboard.setTicketMedio(ticketMedio);

        // Gastos por categoria
        List<DashboardDTO.GastoPorCategoriaDTO> gastosPorCategoria = 
            obterGastosPorCategoria(usuario.getId(), dataInicio, dataFim, dashboard.getTotalMes());
        dashboard.setGastosPorCategoria(gastosPorCategoria);

        // Maiores gastos
        Pageable topGastos = PageRequest.of(0, 5);
        List<Gasto> maioresGastos = gastoRepository.findTopGastosByUsuarioAndPeriodo(
            usuario.getId(), dataInicio, dataFim, topGastos
        );
        List<GastoResponseDTO> maioresGastosDTO = gastoMapper.toResponseDTOList(maioresGastos);
        dashboard.setMaioresGastos(maioresGastosDTO);

        // Gastos por dia
        Map<String, BigDecimal> gastosPorDia = obterGastosPorDia(
            usuario.getId(), dataInicio, dataFim
        );
        dashboard.setGastosPorDia(gastosPorDia);

        log.info("Dashboard gerado com sucesso para usuário: {}", usuario.getEmail());
        return dashboard;
    }

    private BigDecimal calcularPercentualVariacao(BigDecimal valorAtual, BigDecimal valorAnterior) {
        if (valorAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return valorAtual.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
        }

        BigDecimal diferenca = valorAtual.subtract(valorAnterior);
        BigDecimal percentual = diferenca
            .divide(valorAnterior, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"))
            .setScale(2, RoundingMode.HALF_UP);

        return percentual;
    }

    private List<DashboardDTO.GastoPorCategoriaDTO> obterGastosPorCategoria(
            Long usuarioId, LocalDate dataInicio, LocalDate dataFim, BigDecimal totalGeral) {
        
        List<Object[]> resultados = gastoRepository.agruparPorCategoriaEPeriodo(
            usuarioId, dataInicio, dataFim
        );

        List<DashboardDTO.GastoPorCategoriaDTO> gastosPorCategoria = new ArrayList<>();

        for (Object[] resultado : resultados) {
            String categoria = (String) resultado[0];
            String cor = (String) resultado[1];
            BigDecimal total = (BigDecimal) resultado[2];
            Long quantidade = ((Number) resultado[3]).longValue();

            DashboardDTO.GastoPorCategoriaDTO dto = new DashboardDTO.GastoPorCategoriaDTO();
            dto.setCategoria(categoria);
            dto.setCor(cor);
            dto.setTotal(total);
            dto.setQuantidade(quantidade);

            // Calcular percentual
            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentual = total
                    .divide(totalGeral, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
                dto.setPercentual(percentual);
            } else {
                dto.setPercentual(BigDecimal.ZERO);
            }

            gastosPorCategoria.add(dto);
        }

        return gastosPorCategoria;
    }

    private Map<String, BigDecimal> obterGastosPorDia(
            Long usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        
        List<Object[]> resultados = gastoRepository.agruparPorDiaEPeriodo(
            usuarioId, dataInicio, dataFim
        );

        Map<String, BigDecimal> gastosPorDia = new LinkedHashMap<>();

        for (Object[] resultado : resultados) {
            String dia = (String) resultado[0];
            BigDecimal total = (BigDecimal) resultado[1];
            gastosPorDia.put(dia, total);
        }

        return gastosPorDia;
    }
}
