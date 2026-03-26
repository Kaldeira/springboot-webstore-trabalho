package com.nami.webstore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "variantes")
public class Variante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produtos produto;

    @Column(nullable = false, length = 20)
    private String tamanho;

    @Column(nullable = false, length = 50)
    private String cor;

    @Column(nullable = false)
    private Integer estoque = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Produtos getProduto() { return produto; }
    public void setProduto(Produtos produto) { this.produto = produto; }
    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
}
