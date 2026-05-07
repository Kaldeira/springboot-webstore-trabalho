package com.nami.webstore.repository;

import com.nami.webstore.model.ImagemProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ImagemRepository extends JpaRepository<ImagemProduto, Long> {
    // imagem principal de um produto
    @Query("SELECT i FROM ImagemProduto i WHERE i.produto.id = :produtoId AND i.principal = true")
    Optional<ImagemProduto> findPrincipalByProdutoId(Long produtoId);

    // todas as imagens de um produto
    @Query("SELECT i FROM ImagemProduto i WHERE i.produto.id = :produtoId")
    List<ImagemProduto> findByProdutoId(Long produtoId);
}
