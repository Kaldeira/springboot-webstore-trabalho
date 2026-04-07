package com.nami.webstore.repository;

import com.nami.webstore.model.ImagemProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemRepository extends JpaRepository<ImagemProduto, Long> {
}
