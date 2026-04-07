package com.nami.webstore.service;

import com.nami.webstore.model.Categorias;
import com.nami.webstore.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepo;

    public List<Categorias> listarTodas() {
        return categoriaRepo.findAll();
    }
}
