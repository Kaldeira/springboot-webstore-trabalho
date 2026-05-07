package com.nami.webstore.repository;

import com.nami.webstore.model.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produtos, Long> {
    // 6 últimos produtos ativos para o index
    @Query("SELECT p FROM Produtos p WHERE p.ativo = true ORDER BY p.criadoEm DESC")
    List<Produtos> findUltimosAtivos(Pageable pageable);

    @Query("SELECT p FROM Produtos p ORDER BY p.criadoEm DESC")
    List<Produtos> findAllComVariantes();

    @Query("""
                SELECT p
                FROM Produtos p
                JOIN Categorias c ON c.id = p.categoriaId
                WHERE LOWER(c.colecao) = LOWER(:nomeColecao)
                AND p.ativo = true
                ORDER BY p.criadoEm DESC
            """)
    List<Produtos> buscarProColecao(String nomeColecao);

    @Query("""
                SELECT p
                FROM Produtos p
                JOIN Categorias c ON c.id = p.categoriaId
                WHERE LOWER(c.nome) = LOWER(:nomeCategoria)
                AND p.ativo = true
                ORDER BY p.criadoEm DESC
            """)
    List<Produtos> buscarPorCategoria(String nomeCategoria);

    @Query("""
            SELECT DISTINCT p
            FROM Produtos p
            JOIN Variante v ON v.produto.id = p.id
            JOIN Categorias c ON c.id = p.categoriaId
            
            WHERE LOWER(c.nome) = LOWER(:categoria)
            AND p.ativo = true
            
            AND (
                :tamanhos IS NULL
                OR v.tamanho IN :tamanhos
            )
            
            AND (
                :colecoes IS NULL
                OR c.colecao IN :colecoes
            )
            """)
    List<Produtos> buscarComFiltros(@Param("categoria") String categoria, @Param("tamanhos") List<String> tamanhos, @Param("colecoes") List<String> colecoes);


}
