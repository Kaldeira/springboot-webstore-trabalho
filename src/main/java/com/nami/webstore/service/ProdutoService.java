package com.nami.webstore.service;

import com.nami.webstore.model.Categorias;
import com.nami.webstore.model.ImagemProduto;
import com.nami.webstore.model.Produtos;
import com.nami.webstore.model.Variante;
import com.nami.webstore.repository.CategoriaRepository;
import com.nami.webstore.repository.ImagemRepository;
import com.nami.webstore.repository.ProdutoRepository;
import com.nami.webstore.repository.VarianteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProdutoService {
    private final ProdutoRepository produtosRepository;
    private final CategoriaRepository categoriasRepository;
    private final ImagemRepository imagemProdutoRepository;
    private final VarianteRepository varianteRepository;

    public ProdutoService(
            ProdutoRepository produtosRepository,
            CategoriaRepository categoriasRepository,
            ImagemRepository imagemProdutoRepository,
            VarianteRepository varianteRepository
    ) {
        this.produtosRepository = produtosRepository;
        this.categoriasRepository = categoriasRepository;
        this.imagemProdutoRepository = imagemProdutoRepository;
        this.varianteRepository = varianteRepository;
    }

    public void salvar(Produtos form, List<MultipartFile> imagens) throws Exception {

        Produtos produto = new Produtos();

        produto.setNome(form.getNome());
        produto.setDescricao(form.getDescricao());
        produto.setPreco(form.getPreco());

        if (form.getCategoriaId() == null) {
            throw new RuntimeException("Categoria inválida");
        }

        Categorias categoria = categoriasRepository.findById(form.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        produto.setCategoriaId(categoria.getId());

        produto = produtosRepository.save(produto);

        if (form.getVariantes() != null) {
            for (var v : form.getVariantes()) {

                Variante variante = new Variante();
                variante.setTamanho(v.getTamanho());
                variante.setCor(v.getCor());
                variante.setEstoque(v.getEstoque());

                variante.setProduto(produto);

                varianteRepository.save(variante);
            }
        }

        if (imagens != null && !imagens.isEmpty()) {

            String upload_producao = "C:/uploads"; //salvando localmente no pc para testes obviamente
            java.io.File pasta = new java.io.File(upload_producao);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            for (MultipartFile arquivo : imagens) {

                if (!arquivo.isEmpty()) {

                    // gera nome único
                    String nomeOriginal = arquivo.getOriginalFilename();
                    String extensao = "";

                    if (nomeOriginal != null && nomeOriginal.contains(".")) {
                        extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
                    }

                    String nomeArquivo = java.util.UUID.randomUUID() + extensao;

                    java.io.File destino = new java.io.File(pasta, nomeArquivo);

                    arquivo.transferTo(destino);

                    ImagemProduto img = new ImagemProduto();
                    img.setProduto(produto);
                    img.setUrl("/uploads/" + nomeArquivo);

                    imagemProdutoRepository.save(img);
                }
            }
        }
    }

}
