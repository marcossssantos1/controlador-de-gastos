package com.example.controle.mapper;

import com.example.controle.model.dto.GastoRequestDTO;
import com.example.controle.model.dto.GastoResponseDTO;
import com.example.controle.model.entity.Gasto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T19:31:05-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class GastoMapperImpl implements GastoMapper {

    @Autowired
    private CategoriaMapper categoriaMapper;

    @Override
    public GastoResponseDTO toResponseDTO(Gasto gasto) {
        if ( gasto == null ) {
            return null;
        }

        GastoResponseDTO gastoResponseDTO = new GastoResponseDTO();

        gastoResponseDTO.setCategoria( categoriaMapper.toResponseDTO( gasto.getCategoria() ) );
        gastoResponseDTO.setId( gasto.getId() );
        gastoResponseDTO.setDescricao( gasto.getDescricao() );
        gastoResponseDTO.setValor( gasto.getValor() );
        gastoResponseDTO.setDataGasto( gasto.getDataGasto() );
        gastoResponseDTO.setObservacao( gasto.getObservacao() );
        gastoResponseDTO.setCreatedAt( gasto.getCreatedAt() );
        gastoResponseDTO.setUpdatedAt( gasto.getUpdatedAt() );

        return gastoResponseDTO;
    }

    @Override
    public List<GastoResponseDTO> toResponseDTOList(List<Gasto> gastos) {
        if ( gastos == null ) {
            return null;
        }

        List<GastoResponseDTO> list = new ArrayList<GastoResponseDTO>( gastos.size() );
        for ( Gasto gasto : gastos ) {
            list.add( toResponseDTO( gasto ) );
        }

        return list;
    }

    @Override
    public Gasto toEntity(GastoRequestDTO requestDTO) {
        if ( requestDTO == null ) {
            return null;
        }

        Gasto gasto = new Gasto();

        gasto.setDescricao( requestDTO.getDescricao() );
        gasto.setValor( requestDTO.getValor() );
        gasto.setDataGasto( requestDTO.getDataGasto() );
        gasto.setObservacao( requestDTO.getObservacao() );

        return gasto;
    }

    @Override
    public void updateEntityFromDTO(GastoRequestDTO requestDTO, Gasto gasto) {
        if ( requestDTO == null ) {
            return;
        }

        if ( requestDTO.getDescricao() != null ) {
            gasto.setDescricao( requestDTO.getDescricao() );
        }
        if ( requestDTO.getValor() != null ) {
            gasto.setValor( requestDTO.getValor() );
        }
        if ( requestDTO.getDataGasto() != null ) {
            gasto.setDataGasto( requestDTO.getDataGasto() );
        }
        if ( requestDTO.getObservacao() != null ) {
            gasto.setObservacao( requestDTO.getObservacao() );
        }
    }
}
