package com.example.controle.model.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDTO {

    private BigDecimal totalMes;
    private BigDecimal totalMesAnterior;
    private BigDecimal percentualVariacao;
    private Long quantidadeGastos;
    private BigDecimal ticketMedio;
    private List<GastoPorCategoriaDTO> gastosPorCategoria;
    private List<GastoResponseDTO> maioresGastos;
    private Map<String, BigDecimal> gastosPorDia;

    public DashboardDTO() {
    }

    public BigDecimal getTotalMes() {
        return totalMes;
    }

    public void setTotalMes(BigDecimal totalMes) {
        this.totalMes = totalMes;
    }

    public BigDecimal getTotalMesAnterior() {
        return totalMesAnterior;
    }

    public void setTotalMesAnterior(BigDecimal totalMesAnterior) {
        this.totalMesAnterior = totalMesAnterior;
    }

    public BigDecimal getPercentualVariacao() {
        return percentualVariacao;
    }

    public void setPercentualVariacao(BigDecimal percentualVariacao) {
        this.percentualVariacao = percentualVariacao;
    }

    public Long getQuantidadeGastos() {
        return quantidadeGastos;
    }

    public void setQuantidadeGastos(Long quantidadeGastos) {
        this.quantidadeGastos = quantidadeGastos;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }

    public List<GastoPorCategoriaDTO> getGastosPorCategoria() {
        return gastosPorCategoria;
    }

    public void setGastosPorCategoria(List<GastoPorCategoriaDTO> gastosPorCategoria) {
        this.gastosPorCategoria = gastosPorCategoria;
    }

    public List<GastoResponseDTO> getMaioresGastos() {
        return maioresGastos;
    }

    public void setMaioresGastos(List<GastoResponseDTO> maioresGastos) {
        this.maioresGastos = maioresGastos;
    }

    public Map<String, BigDecimal> getGastosPorDia() {
        return gastosPorDia;
    }

    public void setGastosPorDia(Map<String, BigDecimal> gastosPorDia) {
        this.gastosPorDia = gastosPorDia;
    }

    public static class GastoPorCategoriaDTO {
        private String categoria;
        private String cor;
        private BigDecimal total;
        private Long quantidade;
        private BigDecimal percentual;

        public GastoPorCategoriaDTO() {
        }

        public GastoPorCategoriaDTO(String categoria, BigDecimal total, Long quantidade) {
            this.categoria = categoria;
            this.total = total;
            this.quantidade = quantidade;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getCor() {
            return cor;
        }

        public void setCor(String cor) {
            this.cor = cor;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public Long getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Long quantidade) {
            this.quantidade = quantidade;
        }

        public BigDecimal getPercentual() {
            return percentual;
        }

        public void setPercentual(BigDecimal percentual) {
            this.percentual = percentual;
        }
    }
}
