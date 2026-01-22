package com.example.controle.repository;

import com.example.controle.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNome(String nome);

    boolean existsByNome(String nome);

    @Query("SELECT c FROM Categoria c ORDER BY c.nome")
    List<Categoria> findAllOrdenadas();
}
