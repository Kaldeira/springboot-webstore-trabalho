package com.nami.webstore.controller;

import com.nami.webstore.model.*;
import com.nami.webstore.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final EnderecoRepository enderecoRepository;
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ImagemRepository imagemRepository;

    public MainController(UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository, EnderecoRepository enderecoRepository, ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, ImagemRepository imagemRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.enderecoRepository = enderecoRepository;
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.imagemRepository = imagemRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("slides", getSlides());

        model.addAttribute("telaAtiva", "home");

        {// ultimos produtos
            List<Produtos> produtos = produtoRepository.findUltimosAtivos(PageRequest.of(0, 5));
            model.addAttribute("ultimos_produtos", produtos);

            Map<Long, String> catMap = new HashMap<>();
            categoriaRepository.findAll().forEach(c -> catMap.put(c.getId(), c.getNome()));
            model.addAttribute("catMap", catMap);

            Map<Long, String> imagemMap = new HashMap<>();
            for (Produtos p : produtos) {

                List<ImagemProduto> imgs = imagemRepository.findByProdutoId(p.getId());

                if (!imgs.isEmpty()) {
                    imagemMap.put(p.getId(), imgs.get(0).getUrl());
                }
            }
            model.addAttribute("imagemMap", imagemMap);

            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("paginaAtiva", "listarProdutos");

            System.out.println(produtos.size());
        }

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


    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        Usuario usuario = usuarioRepository.findById(logado.getId())
                .orElse(logado);
        model.addAttribute("usuario", usuario);

        if (usuario.getTipo().name().equals("COMUM")) {
            List<Pedido> pedidos = pedidoRepository.findByUsuarioIdComItens(usuario.getId());
            model.addAttribute("pedidos", pedidos);
        }

        return "perfil";
    }

    @PostMapping("/perfil/dados")
    public String salvarDados(HttpSession session,
                              @RequestParam String nome,
                              @RequestParam String email,
                              @RequestParam(required = false) String cpf,
                              @RequestParam(required = false) String telefone,
                              @RequestParam(required = false) String senha,
                              RedirectAttributes ra) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        Usuario usuario = usuarioRepository.findById(logado.getId()).orElse(null);
        if (usuario == null) return "redirect:/login";

        usuario.setNome(nome);
        usuario.setEmail(email);
        if (cpf != null && !cpf.isBlank()) usuario.setCpf(cpf);
        if (telefone != null && !telefone.isBlank()) usuario.setTelefone(telefone);
        if (senha != null && senha.length() >= 6) usuario.setSenhaHash(senha);

        usuarioRepository.save(usuario);
        session.setAttribute("usuarioLogado", usuario);
        ra.addFlashAttribute("flashSucesso", "Dados atualizados com sucesso!");
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/enderecos")
    public String adicionarEndereco(HttpSession session,
                                    @RequestParam(required = false) String apelido,
                                    @RequestParam String logradouro,
                                    @RequestParam String numero,
                                    @RequestParam(required = false) String complemento,
                                    @RequestParam String bairro,
                                    @RequestParam String cidade,
                                    @RequestParam String estado,
                                    @RequestParam String cep,
                                    RedirectAttributes ra) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        Usuario usuario = usuarioRepository.findById(logado.getId()).orElse(null);
        if (usuario == null) return "redirect:/login";

        Endereco end = new Endereco();
        end.setCliente(usuario);
        end.setApelido(apelido);
        end.setLogradouro(logradouro);
        end.setNumero(numero);
        end.setComplemento(complemento);
        end.setBairro(bairro);
        end.setCidade(cidade);
        end.setEstado(estado.toUpperCase());
        end.setCep(cep);

        // primeiro endereço vira o principal
        List<Endereco> existentes = enderecoRepository.findByUsuarioId(usuario.getId());
        end.setPrincipal(existentes.isEmpty());

        enderecoRepository.save(end);
        ra.addFlashAttribute("flashSucesso", "Endereço adicionado!");
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/enderecos/{id}/deletar")
    public String deletarEndereco(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        enderecoRepository.findById(id).ifPresent(end -> {
            // só deleta se pertence ao usuário logado
            if (end.getCliente().getId().equals(logado.getId())) {
                enderecoRepository.delete(end);
            }
        });
        ra.addFlashAttribute("flashSucesso", "Endereço removido.");
        return "redirect:/perfil";
    }
}
