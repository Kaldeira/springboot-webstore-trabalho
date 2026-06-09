package com.nami.webstore.controller;

import com.nami.webstore.model.Categorias;
import org.springframework.web.bind.annotation.ControllerAdvice;
import com.nami.webstore.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalController {
    private final CategoriaRepository categoriaRepository;

    public GlobalController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @ModelAttribute("categoriasNavbar")
    public List<Categorias> categoriasNavbar() {
        return categoriaRepository.findAll();
    }

    @ModelAttribute("colecoesNavbar")
    public List<String> colecoesNavbar() {
        return categoriaRepository.buscarColecoesNavbar();
    }
}
