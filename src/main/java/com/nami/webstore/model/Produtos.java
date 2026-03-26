package com.nami.webstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "produtos")
public class Produtos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categorias categoria;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private double preco;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Variante> variantes;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<ImagemProduto> imagens;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Categorias getCategoria() { return categoria; }
    public void setCategoria(Categorias categoria) { this.categoria = categoria; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }

    public List<Variante> getVariantes() { return variantes; }
    public void setVariantes(List<Variante> variantes) { this.variantes = variantes; }

    public List<ImagemProduto> getImagens() { return imagens; }
    public void setImagens(List<ImagemProduto> imagens) { this.imagens = imagens; }

    @PreUpdate
    public void preUpdate() { this.atualizadoEm = LocalDateTime.now(); }
}
