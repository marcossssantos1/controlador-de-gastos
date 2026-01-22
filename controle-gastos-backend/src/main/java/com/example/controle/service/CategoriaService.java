package com.example.controle.service;

import com.example.controle.exception.BusinessException;
import com.example.controle.exception.ResourceNotFoundException;
import com.example.controle.mapper.CategoriaMapper;
import com.example.controle.model.dto.CategoriaRequestDTO;
import com.example.controle.model.dto.CategoriaResponseDTO;
import com.example.controle.model.entity.Categoria;
import com.example.controle.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        log.debug("Listando todas as categorias");
        List<Categoria> categorias = categoriaRepository.findAllOrdenadas();
        return categoriaMapper.toResponseDTOList(categorias);
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        log.debug("Buscando categoria por ID: {}", id);
        Categoria categoria = buscarEntidadePorId(id);
        return categoriaMapper.toResponseDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO requestDTO) {
        log.info("Criando nova categoria: {}", requestDTO.getNome());

        if (categoriaRepository.existsByNome(requestDTO.getNome())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + requestDTO.getNome());
        }

        Categoria categoria = categoriaMapper.toEntity(requestDTO);
        Categoria categoriaSalva = categoriaRepository.save(categoria);

        log.info("Categoria criada com ID: {}", categoriaSalva.getId());
        return categoriaMapper.toResponseDTO(categoriaSalva);
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO requestDTO) {
        log.info("Atualizando categoria ID: {}", id);

        Categoria categoria = buscarEntidadePorId(id);

        if (!categoria.getNome().equals(requestDTO.getNome()) &&
            categoriaRepository.existsByNome(requestDTO.getNome())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + requestDTO.getNome());
        }

        categoriaMapper.updateEntityFromDTO(requestDTO, categoria);
        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        log.info("Categoria atualizada: {}", id);
        return categoriaMapper.toResponseDTO(categoriaAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando categoria ID: {}", id);

        Categoria categoria = buscarEntidadePorId(id);

        if (!categoria.getGastos().isEmpty()) {
            throw new BusinessException("Não é possível deletar categoria com gastos associados");
        }

        categoriaRepository.delete(categoria);
        log.info("Categoria deletada: {}", id);
    }

    public Categoria buscarEntidadePorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
    }
}
