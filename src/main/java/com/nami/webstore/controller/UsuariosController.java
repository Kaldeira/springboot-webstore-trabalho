package com.nami.webstore.controller;

import com.nami.webstore.exception.ServerExc;
import com.nami.webstore.service.UsuarioService;
import com.nami.webstore.model.Usuario;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.NoSuchAlgorithmException;

@Controller
public class UsuariosController {

    @Autowired
    private UsuarioService userService;

    @PostMapping("/login")
    public ModelAndView login(Usuario usuario, BindingResult br,
                              HttpSession session) throws NoSuchAlgorithmException, ServerExc {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("usuario", new Usuario());

        Usuario userLogin = userService.loginUser(usuario.getEmail(), usuario.getSenhaHash());

        if (userLogin == null) {
            return new ModelAndView("redirect:/login?error");
        }

        session.setAttribute("usuarioLogado", userLogin);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/cadastro")
    public ModelAndView cadastrar() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("usuario", new Usuario());
        modelAndView.setViewName("/cadastro");
        return modelAndView;
    }

    @PostMapping("/salvarUsuario")
    public ModelAndView cadastrar(Usuario usuario) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        userService.salvarUsuario(usuario);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }
}