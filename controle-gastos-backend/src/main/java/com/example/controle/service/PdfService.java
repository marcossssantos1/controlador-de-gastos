package com.example.controle.service;

import com.example.controle.model.entity.Gasto;
import com.example.controle.model.entity.Usuario;
import com.example.controle.repository.GastoRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final GastoRepository gastoRepository;

    public PdfService(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioGastos(LocalDate dataInicio, LocalDate dataFim) {
        Usuario usuario = getUsuarioLogado();
        log.info("Gerando relatório PDF para usuário: {} - período: {} a {}", 
            usuario.getEmail(), dataInicio, dataFim);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Cabeçalho
            adicionarCabecalho(document, usuario, dataInicio, dataFim);

            // Buscar gastos
            List<Gasto> gastos = gastoRepository.findByUsuarioAndPeriodo(
                usuario.getId(),
                dataInicio,
                dataFim,
                PageRequest.of(0, 1000, Sort.by("dataGasto").ascending())
            ).getContent();

            if (gastos.isEmpty()) {
                document.add(new Paragraph("Nenhum gasto encontrado no período.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20));
            } else {
                // Tabela de gastos
                adicionarTabelaGastos(document, gastos);

                // Resumo por categoria
                adicionarResumoPorCategoria(document, gastos);

                // Total geral
                adicionarTotalGeral(document, gastos);
            }

            // Rodapé
            adicionarRodape(document);

            document.close();
            log.info("Relatório PDF gerado com sucesso");
            
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erro ao gerar relatório PDF", e);
            throw new RuntimeException("Erro ao gerar relatório PDF", e);
        }
    }

    private void adicionarCabecalho(Document document, Usuario usuario, 
                                    LocalDate dataInicio, LocalDate dataFim) {
        Paragraph titulo = new Paragraph("RELATÓRIO DE GASTOS")
            .setFontSize(18)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);

        Paragraph info = new Paragraph(String.format("Usuário: %s", usuario.getNome()))
            .setFontSize(10)
            .setMarginTop(10);
        document.add(info);

        Paragraph periodo = new Paragraph(String.format("Período: %s a %s",
            dataInicio.format(DATE_FORMATTER),
            dataFim.format(DATE_FORMATTER)))
            .setFontSize(10)
            .setMarginBottom(20);
        document.add(periodo);
    }

    private void adicionarTabelaGastos(Document document, List<Gasto> gastos) {
        document.add(new Paragraph("Detalhamento dos Gastos")
            .setFontSize(14)
            .setBold()
            .setMarginTop(10)
            .setMarginBottom(10));

        float[] columnWidths = {2, 4, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Cabeçalho da tabela
        DeviceRgb headerColor = new DeviceRgb(52, 152, 219);
        table.addHeaderCell(criarCelulaCabecalho("Data", headerColor));
        table.addHeaderCell(criarCelulaCabecalho("Descrição", headerColor));
        table.addHeaderCell(criarCelulaCabecalho("Categoria", headerColor));
        table.addHeaderCell(criarCelulaCabecalho("Valor", headerColor));

        // Dados
        for (Gasto gasto : gastos) {
            table.addCell(new Cell().add(new Paragraph(gasto.getDataGasto().format(DATE_FORMATTER))));
            table.addCell(new Cell().add(new Paragraph(gasto.getDescricao())));
            table.addCell(new Cell().add(new Paragraph(gasto.getCategoria().getNome())));
            table.addCell(new Cell()
                .add(new Paragraph(String.format("R$ %.2f", gasto.getValor())))
                .setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(table);
    }

    private void adicionarResumoPorCategoria(Document document, List<Gasto> gastos) {
        document.add(new Paragraph("Resumo por Categoria")
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10));

        Map<String, BigDecimal> resumo = new HashMap<>();
        for (Gasto gasto : gastos) {
            String categoria = gasto.getCategoria().getNome();
            resumo.merge(categoria, gasto.getValor(), BigDecimal::add);
        }

        float[] columnWidths = {3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        DeviceRgb headerColor = new DeviceRgb(46, 204, 113);
        table.addHeaderCell(criarCelulaCabecalho("Categoria", headerColor));
        table.addHeaderCell(criarCelulaCabecalho("Total", headerColor));

        for (Map.Entry<String, BigDecimal> entry : resumo.entrySet()) {
            table.addCell(new Cell().add(new Paragraph(entry.getKey())));
            table.addCell(new Cell()
                .add(new Paragraph(String.format("R$ %.2f", entry.getValue())))
                .setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(table);
    }

    private void adicionarTotalGeral(Document document, List<Gasto> gastos) {
        BigDecimal total = gastos.stream()
            .map(Gasto::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Paragraph totalParagraph = new Paragraph(String.format("TOTAL GERAL: R$ %.2f", total))
            .setFontSize(16)
            .setBold()
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(20)
            .setFontColor(new DeviceRgb(231, 76, 60));

        document.add(totalParagraph);
    }

    private void adicionarRodape(Document document) {
        Paragraph rodape = new Paragraph(String.format("Gerado em: %s",
            LocalDate.now().format(DATE_FORMATTER)))
            .setFontSize(8)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(30)
            .setFontColor(ColorConstants.GRAY);

        document.add(rodape);
    }

    private Cell criarCelulaCabecalho(String texto, DeviceRgb cor) {
        return new Cell()
            .add(new Paragraph(texto).setBold())
            .setBackgroundColor(cor)
            .setFontColor(ColorConstants.WHITE)
            .setTextAlignment(TextAlignment.CENTER);
    }
}
