package com.example.controle.mapper;

import com.example.controle.model.dto.CategoriaRequestDTO;
import com.example.controle.model.dto.CategoriaResponseDTO;
import com.example.controle.model.entity.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.beans.JavaBean;
import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoriaMapper {

    CategoriaResponseDTO toResponseDTO(Categoria categoria);

    List<CategoriaResponseDTO> toResponseDTOList(List<Categoria> categorias);

    Categoria toEntity(CategoriaRequestDTO requestDTO);

    void updateEntityFromDTO(CategoriaRequestDTO requestDTO, @MappingTarget Categoria categoria);
}
