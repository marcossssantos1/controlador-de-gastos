package com.example.controle.repository;

import com.example.controle.model.entity.Gasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long>, JpaSpecificationExecutor<Gasto> {

    Page<Gasto> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Gasto> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId, Pageable pageable);

    @Query("SELECT g FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.dataGasto BETWEEN :dataInicio AND :dataFim")
    Page<Gasto> findByUsuarioAndPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable
    );

    @Query("SELECT SUM(g.valor) FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.dataGasto BETWEEN :dataInicio AND :dataFim")
    BigDecimal somarGastosPorUsuarioEPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT COUNT(g) FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.dataGasto BETWEEN :dataInicio AND :dataFim")
    Long contarGastosPorUsuarioEPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT g FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.dataGasto BETWEEN :dataInicio AND :dataFim ORDER BY g.valor DESC")
    List<Gasto> findTopGastosByUsuarioAndPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable
    );

    @Query("SELECT c.nome as categoria, c.cor as cor, SUM(g.valor) as total, COUNT(g) as quantidade " +
           "FROM Gasto g JOIN g.categoria c " +
           "WHERE g.usuario.id = :usuarioId AND g.dataGasto BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY c.id, c.nome, c.cor " +
           "ORDER BY total DESC")
    List<Object[]> agruparPorCategoriaEPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT CAST(g.dataGasto AS string) as dia, SUM(g.valor) as total " +
           "FROM Gasto g " +
           "WHERE g.usuario.id = :usuarioId AND g.dataGasto BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY g.dataGasto " +
           "ORDER BY g.dataGasto")
    List<Object[]> agruparPorDiaEPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );
}
