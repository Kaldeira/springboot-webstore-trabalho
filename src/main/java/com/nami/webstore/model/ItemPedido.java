package com.nami.webstore.model;
import jakarta.persistence.*;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "variante_id", nullable = false)
    private Variante variante;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unit", nullable = false, precision = 10, scale = 2)
    private double precoUnit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Variante getVariante() { return variante; }
    public void setVariante(Variante variante) { this.variante = variante; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public double getPrecoUnit() { return precoUnit; }
    public void setPrecoUnit(double precoUnit) { this.precoUnit = precoUnit; }
}
