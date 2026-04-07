package com.nami.webstore.service;

import com.nami.webstore.exception.ServerExc;
import com.nami.webstore.model.Usuario;
import com.nami.webstore.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    public Usuario loginUser(String email, String senha) throws ServerExc {
        return usuarioRepo.buscarLogin(email, senha);
    }

    public void salvarUsuario(Usuario user) throws Exception {

        try {
            user.setSenhaHash(user.getSenhaHash());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        usuarioRepo.save(user);
    }
}
