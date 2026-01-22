package com.example.controle.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.controle.exception.ResourceNotFoundException;
import com.example.controle.mapper.GastoMapper;
import com.example.controle.model.dto.GastoFilterDTO;
import com.example.controle.model.dto.GastoRequestDTO;
import com.example.controle.model.dto.GastoResponseDTO;
import com.example.controle.model.entity.Categoria;
import com.example.controle.model.entity.Gasto;
import com.example.controle.model.entity.Usuario;
import com.example.controle.repository.GastoRepository;
import com.example.controle.repository.GastoSpecification;

@Service
public class GastoService {

    private static final Logger log = LoggerFactory.getLogger(GastoService.class);

    private final GastoRepository gastoRepository;
    private final GastoMapper gastoMapper;
    private final CategoriaService categoriaService;

    public GastoService(GastoRepository gastoRepository,
                       GastoMapper gastoMapper,
                       CategoriaService categoriaService) {
        this.gastoRepository = gastoRepository;
        this.gastoMapper = gastoMapper;
        this.categoriaService = categoriaService;
    }

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    @Transactional(readOnly = true)
    public Page<GastoResponseDTO> listarTodos(Pageable pageable) {
        Usuario usuario = getUsuarioLogado();
        log.debug("Listando gastos do usuário: {}", usuario.getEmail());
        
        Page<Gasto> gastos = gastoRepository.findByUsuarioId(usuario.getId(), pageable);
        return gastos.map(gastoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<GastoResponseDTO> filtrar(GastoFilterDTO filter, Pageable pageable) {
        Usuario usuario = getUsuarioLogado();
        log.debug("Filtrando gastos do usuário: {} com filtros: {}", usuario.getEmail(), filter);
        
        Page<Gasto> gastos = gastoRepository.findAll(
            GastoSpecification.comFiltros(usuario.getId(), filter),
            pageable
        );
        return gastos.map(gastoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public GastoResponseDTO buscarPorId(Long id) {
        Usuario usuario = getUsuarioLogado();
        log.debug("Buscando gasto ID: {} do usuário: {}", id, usuario.getEmail());
        
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto", "id", id));
        
        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Gasto", "id", id);
        }
        
        return gastoMapper.toResponseDTO(gasto);
    }

    @Transactional(readOnly = true)
    public Page<GastoResponseDTO> buscarPorCategoria(Long categoriaId, Pageable pageable) {
        Usuario usuario = getUsuarioLogado();
        log.debug("Buscando gastos da categoria: {} do usuário: {}", categoriaId, usuario.getEmail());
        
        categoriaService.buscarEntidadePorId(categoriaId);
        Page<Gasto> gastos = gastoRepository.findByUsuarioIdAndCategoriaId(
            usuario.getId(), categoriaId, pageable
        );
        return gastos.map(gastoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<GastoResponseDTO> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        Usuario usuario = getUsuarioLogado();
        log.debug("Buscando gastos entre {} e {} do usuário: {}", dataInicio, dataFim, usuario.getEmail());
        
        Page<Gasto> gastos = gastoRepository.findByUsuarioAndPeriodo(
            usuario.getId(), dataInicio, dataFim, pageable
        );
        return gastos.map(gastoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Usuario usuario = getUsuarioLogado();
        log.debug("Calculando total entre {} e {} do usuário: {}", dataInicio, dataFim, usuario.getEmail());
        
        BigDecimal total = gastoRepository.somarGastosPorUsuarioEPeriodo(
            usuario.getId(), dataInicio, dataFim
        );
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long contarGastosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Usuario usuario = getUsuarioLogado();
        Long count = gastoRepository.contarGastosPorUsuarioEPeriodo(
            usuario.getId(), dataInicio, dataFim
        );
        return count != null ? count : 0L;
    }

    @Transactional
    public GastoResponseDTO criar(GastoRequestDTO requestDTO) {
        Usuario usuario = getUsuarioLogado();
        log.info("Criando novo gasto para usuário: {}", usuario.getEmail());
        
        Categoria categoria = categoriaService.buscarEntidadePorId(requestDTO.getCategoriaId());

        Gasto gasto = gastoMapper.toEntity(requestDTO);
        gasto.setCategoria(categoria);
        gasto.setUsuario(usuario);

        Gasto gastoSalvo = gastoRepository.save(gasto);
        log.info("Gasto criado com ID: {}", gastoSalvo.getId());
        
        return gastoMapper.toResponseDTO(gastoSalvo);
    }

    @Transactional
    public GastoResponseDTO atualizar(Long id, GastoRequestDTO requestDTO) {
        Usuario usuario = getUsuarioLogado();
        log.info("Atualizando gasto ID: {} do usuário: {}", id, usuario.getEmail());
        
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto", "id", id));

        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Gasto", "id", id);
        }

        Categoria categoria = categoriaService.buscarEntidadePorId(requestDTO.getCategoriaId());

        gastoMapper.updateEntityFromDTO(requestDTO, gasto);
        gasto.setCategoria(categoria);

        Gasto gastoAtualizado = gastoRepository.save(gasto);
        log.info("Gasto atualizado: {}", id);
        
        return gastoMapper.toResponseDTO(gastoAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = getUsuarioLogado();
        log.info("Deletando gasto ID: {} do usuário: {}", id, usuario.getEmail());
        
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto", "id", id));

        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Gasto", "id", id);
        }

        gastoRepository.delete(gasto);
        log.info("Gasto deletado: {}", id);
    }
}
