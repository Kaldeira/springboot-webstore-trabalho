package com.nami.webstore.model;

import com.nami.webstore.enums.MetodoPagamento;
import com.nami.webstore.enums.StatusPagamento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private double valor;

    @Column(name = "id_transacao", length = 100)
    private String idTransacao;

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public MetodoPagamento getMetodo() { return metodo; }
    public void setMetodo(MetodoPagamento metodo) { this.metodo = metodo; }
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getIdTransacao() { return idTransacao; }
    public void setIdTransacao(String idTransacao) { this.idTransacao = idTransacao; }
    public LocalDateTime getPagoEm() { return pagoEm; }
    public void setPagoEm(LocalDateTime pagoEm) { this.pagoEm = pagoEm; }
}
