package com.example.controle.mapper;

import com.example.controle.model.dto.CategoriaRequestDTO;
import com.example.controle.model.dto.CategoriaResponseDTO;
import com.example.controle.model.entity.Categoria;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T19:31:03-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class CategoriaMapperImpl implements CategoriaMapper {

    @Override
    public CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        if ( categoria == null ) {
            return null;
        }

        CategoriaResponseDTO categoriaResponseDTO = new CategoriaResponseDTO();

        categoriaResponseDTO.setId( categoria.getId() );
        categoriaResponseDTO.setNome( categoria.getNome() );
        categoriaResponseDTO.setDescricao( categoria.getDescricao() );
        categoriaResponseDTO.setCor( categoria.getCor() );
        categoriaResponseDTO.setIcone( categoria.getIcone() );
        categoriaResponseDTO.setCreatedAt( categoria.getCreatedAt() );
        categoriaResponseDTO.setUpdatedAt( categoria.getUpdatedAt() );

        return categoriaResponseDTO;
    }

    @Override
    public List<CategoriaResponseDTO> toResponseDTOList(List<Categoria> categorias) {
        if ( categorias == null ) {
            return null;
        }

        List<CategoriaResponseDTO> list = new ArrayList<CategoriaResponseDTO>( categorias.size() );
        for ( Categoria categoria : categorias ) {
            list.add( toResponseDTO( categoria ) );
        }

        return list;
    }

    @Override
    public Categoria toEntity(CategoriaRequestDTO requestDTO) {
        if ( requestDTO == null ) {
            return null;
        }

        Categoria categoria = new Categoria();

        categoria.setNome( requestDTO.getNome() );
        categoria.setDescricao( requestDTO.getDescricao() );
        categoria.setCor( requestDTO.getCor() );
        categoria.setIcone( requestDTO.getIcone() );

        return categoria;
    }

    @Override
    public void updateEntityFromDTO(CategoriaRequestDTO requestDTO, Categoria categoria) {
        if ( requestDTO == null ) {
            return;
        }

        if ( requestDTO.getNome() != null ) {
            categoria.setNome( requestDTO.getNome() );
        }
        if ( requestDTO.getDescricao() != null ) {
            categoria.setDescricao( requestDTO.getDescricao() );
        }
        if ( requestDTO.getCor() != null ) {
            categoria.setCor( requestDTO.getCor() );
        }
        if ( requestDTO.getIcone() != null ) {
            categoria.setIcone( requestDTO.getIcone() );
        }
    }
}
