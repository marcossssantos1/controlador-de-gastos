package com.example.controle.mapper;

import com.example.controle.model.dto.GastoRequestDTO;
import com.example.controle.model.dto.GastoResponseDTO;
import com.example.controle.model.entity.Gasto;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {CategoriaMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GastoMapper {

    @Mapping(target = "categoria", source = "categoria")
    GastoResponseDTO toResponseDTO(Gasto gasto);

    List<GastoResponseDTO> toResponseDTOList(List<Gasto> gastos);

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Gasto toEntity(GastoRequestDTO requestDTO);

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(GastoRequestDTO requestDTO, @MappingTarget Gasto gasto);
}
