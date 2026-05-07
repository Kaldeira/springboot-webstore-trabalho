package com.nami.webstore.controller;

import com.nami.webstore.model.*;
import com.nami.webstore.repository.CategoriaRepository;
import com.nami.webstore.repository.ImagemRepository;
import com.nami.webstore.repository.ProdutoRepository;
import com.nami.webstore.repository.VarianteRepository;
import com.nami.webstore.service.CategoriaService;
import com.nami.webstore.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProdutosController {

    @Autowired
    private ProdutoService produtoService;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ImagemRepository imagemRepository;
    @Autowired
    private VarianteRepository varianteRepository;

    @PostMapping("/painel/produtos")
    public String salvar(@ModelAttribute("produtoForm") Produtos form, @RequestParam(value = "imagens", required = false) List<MultipartFile> imagens, HttpSession session, RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getTipo().name().equals("COMUM")) {
            return "redirect:/";
        }

        if (form.getNome() == null || form.getNome().isBlank()) {
            redirectAttributes.addFlashAttribute("flashErro", "Nome do produto é obrigatório.");
            return "redirect:/panelProdutos";
        }

        if (form.getPreco() == null) {
            redirectAttributes.addFlashAttribute("flashErro", "Preço do produto é obrigatório.");
            return "redirect:/panelProdutos";
        }

        if (form.getVariantes() == null || form.getVariantes().isEmpty()) {
            redirectAttributes.addFlashAttribute("flashErro", "Adicione ao menos uma variante.");
            return "redirect:/panelProdutos";
        }

        try {
            produtoService.salvar(form, imagens);
            redirectAttributes.addFlashAttribute("flashSucesso", "Produto \"" + form.getNome() + "\" salvo com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("flashErro", "Erro ao salvar produto: " + e.getMessage());
        }

        return "redirect:/panelProdutos";
    }

    @GetMapping("/panelProdutos")
    public ModelAndView panelProdutos(HttpSession session, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("painel/adicionarProdutos");
        modelAndView.addObject("produtoForm", new Produtos());
        modelAndView.addObject("allCategorias", categoriaService.listarTodas());

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        //check de permissao
        if (usuario == null || usuario.getTipo().toString().equals("COMUM")) {
            System.out.println("Usuario não é valido");
            return new ModelAndView("redirect:/");
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("paginaAtiva", "addProdutos");
        return modelAndView;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("imagens");
    }

    @GetMapping("/categoria/{nome}")
    public String categoria(@PathVariable String nome, Model model) {

        List<Produtos> produtos = produtoRepository.buscarPorCategoria(nome);

        model.addAttribute("produtos", produtos);

        // tamanhos disponíveis
        List<String> tamanhos = varianteRepository.buscarTamanhosPorCategoria(nome);

        model.addAttribute("tamanhos", tamanhos);

        // coleções disponíveis
        List<String> colecoes = categoriaRepository.buscarColecoesPorNome(nome);

        model.addAttribute("colecoes", colecoes);

        Map<Long, String> imagemMap = new HashMap<>();

        Map<Long, String> tamanhosProdutoMap = new HashMap<>();

        Map<Long, String> colecaoProdutoMap = new HashMap<>();

        for (Produtos p : produtos) {

            // imagem
            List<ImagemProduto> imgs = imagemRepository.findByProdutoId(p.getId());

            if (!imgs.isEmpty()) {
                imagemMap.put(p.getId(), imgs.get(0).getUrl());
            }

            // tamanhos
            String tamanhosStr = p.getVariantes().stream().map(Variante::getTamanho).distinct().reduce((a, b) -> a + "," + b).orElse("");

            tamanhosProdutoMap.put(p.getId(), tamanhosStr);

            // coleção
            Categorias cat = categoriaRepository.findById(p.getCategoriaId()).orElse(null);

            if (cat != null) {

                colecaoProdutoMap.put(p.getId(), cat.getColecao());

            }
        }

        model.addAttribute("imagemMap", imagemMap);

        model.addAttribute("tamanhosProdutoMap", tamanhosProdutoMap);

        model.addAttribute("colecaoProdutoMap", colecaoProdutoMap);

        model.addAttribute("categoriaNome", nome);

        return "categoria";
    }
}
