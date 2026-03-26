package com.nami.webstore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "imagens_produto")
public class ImagemProduto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produtos produto;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Boolean principal = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Produtos getProduto() { return produto; }
    public void setProduto(Produtos produto) { this.produto = produto; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Boolean getPrincipal() { return principal; }
    public void setPrincipal(Boolean principal) { this.principal = principal; }
}
