package com.nami.webstore.controller;

import com.nami.webstore.enums.StatusPedido;
import com.nami.webstore.enums.TipoUsuario;
import com.nami.webstore.model.*;
import com.nami.webstore.repository.*;
import com.nami.webstore.service.CategoriaService;
import com.nami.webstore.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/painel")
public class PainelController {
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ImagemRepository imagemRepository;
    @Autowired private VarianteRepository varianteRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProdutoService produtoService;
    @Autowired private CategoriaService categoriaService;


    private boolean semAcesso(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogado");
        return u == null || u.getTipo() == TipoUsuario.COMUM;
    }

    private boolean somenteAdmin(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogado");
        return u == null || u.getTipo() != TipoUsuario.ADMIN;
    }

    @GetMapping("/produtos")
    public String listarProdutos(HttpSession session, Model model) {
        if (semAcesso(session)) return "redirect:/";

        List<Produtos> produtos = produtoRepository.findAllComVariantes();
        model.addAttribute("produtos", produtos);

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

        return "painel/produtos";
    }

    @GetMapping("/produtos/novo")
    public String novoProduto(HttpSession session, Model model) {
        if (semAcesso(session)) return "redirect:/";
        model.addAttribute("produtoForm", new Produtos());
        model.addAttribute("allCategorias", categoriaService.listarTodas());
        return "painel/adicionarProdutos";
    }

    @GetMapping("/produtos/{id}/editar")
    public String editarProduto(@PathVariable Long id,
                                Model model,
                                HttpSession session,
                                RedirectAttributes ra) {

        if (semAcesso(session)) return "redirect:/";

        Optional<Produtos> opt = produtoRepository.findById(id);

        if (opt.isEmpty()) {
            ra.addFlashAttribute("flashErro", "Produto não encontrado.");
            return "redirect:/painel/produtos";
        }

        Produtos produto = opt.get();

        // categorias
        model.addAttribute("allCategorias", categoriaRepository.findAll());

        // variantes
        List<Variante> variantes = varianteRepository.findByProdutoId(id);
        produto.setVariantes(variantes);

        model.addAttribute("variantesJson", variantes);

        // produto
        model.addAttribute("produto", produto);

        return "painel/editarProduto";
    }

    @InitBinder("produto")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("imagens");
    }

    @PostMapping("/produtos/{id}/editar")
    public String salvarEdicao(@PathVariable Long id,
                               @ModelAttribute("produto") Produtos form,
                               @RequestParam(value = "imagens", required = false) List<MultipartFile> imagens,
                               HttpSession session,
                               RedirectAttributes ra) {
        if (semAcesso(session)) return "redirect:/";

        Optional<Produtos> opt = produtoRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("flashErro", "Produto não encontrado.");
            return "redirect:/painel/produtos";
        }

        try {
            Produtos produto = opt.get();
            produto.setNome(form.getNome());
            produto.setDescricao(form.getDescricao());
            produto.setPreco(form.getPreco());
            produto.setAtivo(form.getAtivo() != null ? form.getAtivo() : false);
            if (form.getCategoriaId() != null) produto.setCategoriaId(form.getCategoriaId());

            produtoRepository.save(produto);

            // substituir variantes: remove antigas, salva novas
            if (form.getVariantes() != null && !form.getVariantes().isEmpty()) {
                List<Variante> antigas = varianteRepository.findByProdutoId(id);
                varianteRepository.deleteAll(antigas);
                for (Variante v : form.getVariantes()) {
                    v.setProduto(produto);
                    varianteRepository.save(v);
                }
            }

            // salvar novas imagens enviadas
            if (imagens != null) {
                for (MultipartFile arquivo : imagens) {
                    if (!arquivo.isEmpty()) {
                        String nomeArquivo = salvarArquivo(arquivo);
                        ImagemProduto img = new ImagemProduto();
                        img.setProduto(produto);
                        img.setUrl("/uploads/" + nomeArquivo);
                        // se não há nenhuma principal, a primeira nova vira principal
                        boolean temPrincipal = imagemRepository.findPrincipalByProdutoId(id).isPresent();
                        img.setPrincipal(!temPrincipal);
                        imagemRepository.save(img);
                    }
                }
            }

            ra.addFlashAttribute("flashSucesso", "Produto \"" + produto.getNome() + "\" atualizado!");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("flashErro", "Erro ao salvar produto.");
        }

        return "redirect:/painel/produtos";
    }

    @PostMapping("/produtos/{id}/deletar")
    public String deletarProduto(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (semAcesso(session)) return "redirect:/";

        try {
            // apaga imagens e variantes primeiro (cascade não está ativo no modelo)
            imagemRepository.deleteAll(imagemRepository.findByProdutoId(id));
            varianteRepository.deleteAll(varianteRepository.findByProdutoId(id));
            produtoRepository.deleteById(id);
            ra.addFlashAttribute("flashSucesso", "Produto deletado.");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("flashErro", "Erro ao deletar produto. Verifique se há pedidos vinculados.");
        }

        return "redirect:/painel/produtos";
    }

    @GetMapping("/imagens/{id}/deletar")
    public String deletarImagem(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (semAcesso(session)) return "redirect:/";

        Optional<ImagemProduto> opt = imagemRepository.findById(id);
        if (opt.isPresent()) {
            Long produtoId = opt.get().getProduto().getId();
            imagemRepository.deleteById(id);
            ra.addFlashAttribute("flashSucesso", "Imagem removida.");
            return "redirect:/painel/produtos/" + produtoId + "/editar";
        }

        return "redirect:/painel/produtos";
    }

    @GetMapping("/categorias")
    public String listarCategorias(HttpSession session, Model model) {
        if (semAcesso(session)) return "redirect:/";
        model.addAttribute("categorias", categoriaService.listarTodasComProdutos());

        model.addAttribute("paginaAtiva", "categorias");
        return "painel/categorias";
    }

    @PostMapping("/categorias")
    public String criarCategoria(@RequestParam String nome,
                                 @RequestParam String colecao,
                                 @RequestParam(required = false) String descricao,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (semAcesso(session)) return "redirect:/";

        Categorias cat = new Categorias();
        cat.setNome(nome.trim());
        cat.setDescricao(descricao != null ? descricao.trim() : null);
        cat.setColecao(colecao.trim());
        categoriaRepository.save(cat);
        ra.addFlashAttribute("flashSucesso", "Categoria \"" + nome + "\" criada!");
        return "redirect:/painel/categorias";
    }

    @PostMapping("/categorias/{id}/editar")
    public String editarCategoria(@PathVariable Long id,
                                  @RequestParam String nome,
                                  @RequestParam String colecao,
                                  @RequestParam(required = false) String descricao,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        if (semAcesso(session)) return "redirect:/";

        categoriaRepository.findById(id).ifPresent(cat -> {
            cat.setNome(nome.trim());
            cat.setDescricao(descricao != null ? descricao.trim() : null);
            cat.setColecao(colecao.trim());
            categoriaRepository.save(cat);
        });
        ra.addFlashAttribute("flashSucesso", "Categoria atualizada!");
        return "redirect:/painel/categorias";
    }

    @PostMapping("/categorias/{id}/deletar")
    public String deletarCategoria(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        if (semAcesso(session)) return "redirect:/";

        try {
            categoriaRepository.deleteById(id);
            ra.addFlashAttribute("flashSucesso", "Categoria deletada.");
        } catch (Exception e) {
            ra.addFlashAttribute("flashErro", "Não foi possível deletar: existem produtos nesta categoria.");
        }

        return "redirect:/painel/categorias";
    }

    @GetMapping("/pedidos")
    public String listarPedidos(HttpSession session, Model model) {
        if (semAcesso(session)) return "redirect:/";
        model.addAttribute("pedidos", pedidoRepository.findAllComDetalhes());
        model.addAttribute("paginaAtiva", "pedidos");
        return "painel/pedidos";
    }

    @PostMapping("/pedidos/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam StatusPedido status,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        if (semAcesso(session)) return "redirect:/";

        pedidoRepository.findById(id).ifPresent(pedido -> {
            pedido.setStatus(status);
            pedidoRepository.save(pedido);
        });

        ra.addFlashAttribute("flashSucesso", "Status do pedido #" + id + " atualizado.");
        return "redirect:/painel/pedidos";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(HttpSession session, Model model) {
        if (somenteAdmin(session)) return "redirect:/";
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("paginaAtiva", "listarUsuarios");
        return "painel/usuarios";
    }

    @PostMapping("/usuarios")
    public String criarUsuario(@RequestParam String nome,
                               @RequestParam String email,
                               @RequestParam(required = false) String cpf,
                               @RequestParam(required = false) String telefone,
                               @RequestParam String senha,
                               @RequestParam TipoUsuario tipo,
                               HttpSession session,
                               RedirectAttributes ra) {
        if (somenteAdmin(session)) return "redirect:/";

        try {
            Usuario u = new Usuario();
            u.setNome(nome);
            u.setEmail(email);
            if (cpf != null && !cpf.isBlank()) u.setCpf(cpf);
            if (telefone != null && !telefone.isBlank()) u.setTelefone(telefone);
            u.setSenhaHash(senha);
            u.setTipo(tipo);
            usuarioRepository.save(u);
            ra.addFlashAttribute("flashSucesso", "Usuário \"" + nome + "\" criado!");
        } catch (Exception e) {
            ra.addFlashAttribute("flashErro", "Erro ao criar usuário. E-mail ou CPF já cadastrado?");
        }

        return "redirect:/painel/usuarios";
    }

    @PostMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id,
                                @RequestParam String nome,
                                @RequestParam String email,
                                @RequestParam(required = false) String cpf,
                                @RequestParam(required = false) String telefone,
                                @RequestParam(required = false) String senha,
                                @RequestParam TipoUsuario tipo,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (somenteAdmin(session)) return "redirect:/";

        usuarioRepository.findById(id).ifPresent(u -> {
            u.setNome(nome);
            u.setEmail(email);
            if (cpf != null && !cpf.isBlank()) u.setCpf(cpf);
            if (telefone != null && !telefone.isBlank()) u.setTelefone(telefone);
            if (senha != null && !senha.isBlank()) u.setSenhaHash(senha);
            u.setTipo(tipo);
            usuarioRepository.save(u);
        });

        ra.addFlashAttribute("flashSucesso", "Usuário atualizado.");
        return "redirect:/painel/usuarios";
    }

    @PostMapping("/usuarios/{id}/deletar")
    public String deletarUsuario(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (somenteAdmin(session)) return "redirect:/";

        // não deixa deletar a si mesmo
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado != null && logado.getId().equals(id)) {
            ra.addFlashAttribute("flashErro", "Você não pode deletar sua própria conta.");
            return "redirect:/painel/usuarios";
        }

        try {
            usuarioRepository.deleteById(id);
            ra.addFlashAttribute("flashSucesso", "Usuário deletado.");
        } catch (Exception e) {
            ra.addFlashAttribute("flashErro", "Não foi possível deletar: usuário possui pedidos vinculados.");
        }

        return "redirect:/painel/usuarios";
    }

    private String salvarArquivo(MultipartFile arquivo) throws Exception {
        String pastaUpload = System.getProperty("user.dir") + "/uploads/";//"C:/uploads/"; // ajustar para produção
        java.io.File pasta = new java.io.File(pastaUpload);
        if (!pasta.exists()) pasta.mkdirs();

        String nomeOriginal = arquivo.getOriginalFilename();
        String ext = (nomeOriginal != null && nomeOriginal.contains("."))
                ? nomeOriginal.substring(nomeOriginal.lastIndexOf("."))
                : "";
        String nomeArquivo = UUID.randomUUID() + ext;
        arquivo.transferTo(new java.io.File(pasta, nomeArquivo));
        return nomeArquivo;
    }
}
