package com.nami.webstore.repository;

import com.nami.webstore.model.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProdutoRepository extends JpaRepository<Produtos, Long> {

}
