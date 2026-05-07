package com.nami.webstore.repository;

import com.nami.webstore.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    @Query("SELECT e FROM Endereco e WHERE e.cliente.id = :usuarioId")
    List<Endereco> findByUsuarioId(Long usuarioId);
}
