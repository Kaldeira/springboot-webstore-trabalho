package com.nami.webstore.service;

import com.nami.webstore.model.Categorias;
import com.nami.webstore.model.ImagemProduto;
import com.nami.webstore.model.Produtos;
import com.nami.webstore.repository.CategoriaRepository;
import com.nami.webstore.repository.ImagemRepository;
import com.nami.webstore.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtosRepository;
    private final CategoriaRepository categoriasRepository;
    private final ImagemRepository imagemProdutoRepository;

    public ProdutoService(
            ProdutoRepository produtosRepository,
            CategoriaRepository categoriasRepository,
            ImagemRepository imagemProdutoRepository
    ) {
        this.produtosRepository = produtosRepository;
        this.categoriasRepository = categoriasRepository;
        this.imagemProdutoRepository = imagemProdutoRepository;
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

        // SALVAR IMAGENS
        if (imagens != null) {
            for (MultipartFile arquivo : imagens) {

                if (!arquivo.isEmpty()) {

                    String nomeArquivo = arquivo.getOriginalFilename();

                    ImagemProduto img = new ImagemProduto();
                    img.setProduto(produto);
                    img.setUrl("/uploads/" + nomeArquivo);

                    imagemProdutoRepository.save(img);

                    java.io.File pasta = new java.io.File("uploads");
                    if (!pasta.exists()) {
                        pasta.mkdirs();
                    }

                    arquivo.transferTo(
                            new java.io.File("uploads/" + nomeArquivo)
                    );
                }
            }
        }
    }

}
