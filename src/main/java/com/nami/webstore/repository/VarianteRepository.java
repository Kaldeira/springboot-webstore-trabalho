package com.nami.webstore.repository;

import com.nami.webstore.model.Variante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VarianteRepository extends JpaRepository<Variante, Long> {

    @Query("SELECT v FROM Variante v WHERE v.produto.id = :produtoId")
    List<Variante> findByProdutoId(Long produtoId);

    @Query("""
    SELECT DISTINCT v.tamanho
    FROM Variante v
    JOIN v.produto p
    JOIN Categorias c ON c.id = p.categoriaId
    WHERE LOWER(c.nome) = LOWER(:categoria)
    AND v.estoque > 0
    ORDER BY v.tamanho ASC
""")
    List<String> buscarTamanhosPorCategoria(@Param("categoria") String categoria);

    @Query("""
    SELECT DISTINCT v.tamanho
    FROM Variante v
    JOIN v.produto p
    JOIN Categorias c ON c.id = p.categoriaId
    WHERE LOWER(c.colecao) = LOWER(:colecao)
    AND v.estoque > 0
    ORDER BY v.tamanho ASC
""")
    List<String> buscarTamanhosPorColecao(@Param("colecao") String colecao);
}
