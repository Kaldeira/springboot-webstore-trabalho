package com.nami.webstore.controller;

import com.nami.webstore.model.Categorias;
import com.nami.webstore.model.Produtos;
import com.nami.webstore.model.Usuario;
import com.nami.webstore.repository.CategoriaRepository;
import com.nami.webstore.service.CategoriaService;
import com.nami.webstore.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class ProdutosController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public ProdutosController(ProdutoService produtoService,
                                  CategoriaService categoriaService) {
        this.produtoService  = produtoService;
        this.categoriaService = categoriaService;
    }

    @Autowired
    private CategoriaRepository categoriaRepository;

    // GET /admin/produtos/novo — exibe o formulário em branco
    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("produtoForm", new Produtos());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin-produto"; // resolve para templates/admin-produto.html
    }

    // POST /admin/produtos — processa o envio do formulário
    @PostMapping("/admin/produtos")
    public String salvar(
            @Valid @ModelAttribute("produtoForm") Produtos form,
            BindingResult bindingResult,
            @RequestParam(value = "imagens", required = false) List<MultipartFile> imagens,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (form.getVariantes() == null || form.getVariantes().isEmpty()) {
            bindingResult.rejectValue("variantes", "variantes.empty",
                    "Adicione ao menos uma variante.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("produtoForm", form);
            return "panelProdutos";
        }

        try {
            produtoService.salvar(form, imagens);

            redirectAttributes.addFlashAttribute("flashSucesso",
                    "Produto \"" + form.getNome() + "\" salvo com sucesso!");

            return "redirect:/admin/produtos/novo";

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("produtoForm", form);
            model.addAttribute("flashErro", "Erro ao salvar produto.");

            return "panelProdutos";
        }
    }

    @GetMapping("/panelProdutos")
    public ModelAndView panelProdutos(HttpSession session, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/panelProdutos");
        modelAndView.addObject("produtoForm", new Produtos());
        modelAndView.addObject("allCategorias", categoriaRepository.findAll());

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null || usuario.getTipo().ordinal() == 1) {
            return new ModelAndView("redirect:/");
        }

        model.addAttribute("usuario", usuario);
        return modelAndView;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
