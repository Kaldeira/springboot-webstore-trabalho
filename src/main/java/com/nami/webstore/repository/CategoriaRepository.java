package com.nami.webstore.repository;

import com.nami.webstore.model.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categorias, Long> {

    @Query("SELECT COUNT(p) FROM Produtos p WHERE p.categoriaId = :categoriaId")
    long countByCategoriaId(Long categoriaId);

    @Query("SELECT p FROM Produtos p WHERE p.categoriaId = :categoriaId")
    List<com.nami.webstore.model.Produtos> findProdutosByCategoriaId(Long categoriaId);

    @Query("""
    SELECT DISTINCT c.colecao
    FROM Categorias c
    WHERE LOWER(c.nome) = LOWER(:categoria)
    ORDER BY c.colecao ASC
""")
    List<String> buscarColecoesPorNome(
            @Param("categoria") String categoria
    );
}