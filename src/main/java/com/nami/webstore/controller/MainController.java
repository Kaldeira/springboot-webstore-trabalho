package com.nami.webstore.controller;

import com.nami.webstore.model.Produtos;
import com.nami.webstore.model.Slide;
import com.nami.webstore.model.Usuario;
import com.nami.webstore.repository.CategoriaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class MainController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("slides", getSlides());
        return "index";
    }

    private List<Slide> getSlides() {
        return List.of(
                new Slide("/imagens/alucard.png", "/colecao/1"),
                new Slide("/imagens/divino.png", "/colecao/2"),
                new Slide("/imagens/morte.png", "/colecao/3")
        );
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/login");
        modelAndView.addObject("usuario", new Usuario());
        return modelAndView;
    }

    @ModelAttribute("usuarioLogado")
    public Usuario usuario(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioLogado");
    }
}
