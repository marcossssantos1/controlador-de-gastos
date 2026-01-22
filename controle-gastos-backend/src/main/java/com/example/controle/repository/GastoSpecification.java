package com.example.controle.repository;

import com.example.controle.model.dto.GastoFilterDTO;
import com.example.controle.model.entity.Gasto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GastoSpecification {

    public static Specification<Gasto> comFiltros(Long usuarioId, GastoFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro obrigatório por usuário
            predicates.add(criteriaBuilder.equal(root.get("usuario").get("id"), usuarioId));

            // Filtro por descrição (like)
            if (filter.getDescricao() != null && !filter.getDescricao().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("descricao")),
                        "%" + filter.getDescricao().toLowerCase() + "%"
                ));
            }

            // Filtro por categoria
            if (filter.getCategoriaId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoria").get("id"), filter.getCategoriaId()));
            }

            // Filtro por período
            if (filter.getDataInicio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataGasto"), filter.getDataInicio()));
            }

            if (filter.getDataFim() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataGasto"), filter.getDataFim()));
            }

            // Filtro por valor mínimo
            if (filter.getValorMinimo() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("valor"), filter.getValorMinimo()));
            }

            // Filtro por valor máximo
            if (filter.getValorMaximo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("valor"), filter.getValorMaximo()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
