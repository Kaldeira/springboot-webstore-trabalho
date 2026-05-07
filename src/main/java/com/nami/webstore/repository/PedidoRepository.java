package com.nami.webstore.repository;

import com.nami.webstore.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // todos os pedidos com cliente, endereço e itens (admin)
    @Query("SELECT DISTINCT p FROM Pedido p " +
           "LEFT JOIN FETCH p.cliente " +
           "LEFT JOIN FETCH p.endereco " +
           "LEFT JOIN FETCH p.itens i " +
           "LEFT JOIN FETCH i.variante v " +
           "LEFT JOIN FETCH v.produto " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findAllComDetalhes();

    // pedidos de um usuário específico (perfil)
    @Query("SELECT DISTINCT p FROM Pedido p " +
           "LEFT JOIN FETCH p.itens i " +
           "LEFT JOIN FETCH i.variante v " +
           "LEFT JOIN FETCH v.produto " +
           "WHERE p.cliente.id = :usuarioId " +
           "ORDER BY p.criadoEm DESC")
    List<Pedido> findByUsuarioIdComItens(Long usuarioId);
}
