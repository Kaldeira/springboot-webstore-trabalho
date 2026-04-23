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

    @PostMapping("/admin/produtos")
    public String salvar(
            @Valid @ModelAttribute("produtoForm") Produtos form,
            BindingResult bindingResult,
            @RequestParam("imagens") List<MultipartFile> imagens,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (form.getVariantes() == null || form.getVariantes().isEmpty()) {
            redirectAttributes.addFlashAttribute("flashErro",
                    "Adicione ao menos uma variante.");

            for (var v : form.getVariantes())
            {
                if (v.getTamanho().isEmpty() || v.getEstoque() <= 0 || v.getCor().isEmpty()) {
                    redirectAttributes.addFlashAttribute("flashErro",
                            "Algo de errado com as variantes.");

                    return "redirect:/panelProdutos";
                }
            }

            model.addAttribute("allCategorias", categoriaService.listarTodas());
            model.addAttribute("produtoForm", form);

            return "redirect:/panelProdutos";
        }

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(err -> {
                System.out.println(err.toString());
            });

            redirectAttributes.addFlashAttribute("flashErro",
                    "Falha ao salvar produto!");

            model.addAttribute("allCategorias", categoriaService.listarTodas());
            model.addAttribute("produtoForm", form);

            return "redirect:/panelProdutos";
        }

        try {
            produtoService.salvar(form, imagens);

            redirectAttributes.addFlashAttribute("flashSucesso",
                    "Produto \"" + form.getNome() + "\" salvo com sucesso!");

            model.addAttribute("allCategorias", categoriaService.listarTodas()); // tem q passar as categorias de novo sempre
          //  model.addAttribute("flashSucesso", "Produto salvo com sucesso!");

            return "redirect:/panelProdutos";

        } catch (Exception e) {
            System.out.println("Erro ao salvar:");
            e.printStackTrace();

            model.addAttribute("allCategorias", categoriaService.listarTodas());
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
        modelAndView.addObject("allCategorias", categoriaService.listarTodas());

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        //check de permissao
        if (usuario == null || usuario.getTipo().toString().equals("COMUM")) {
            System.out.println("Usuario não é valido");
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
