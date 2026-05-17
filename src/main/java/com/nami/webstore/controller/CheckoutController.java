package com.nami.webstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nami.webstore.enums.StatusPedido;
import com.nami.webstore.model.*;
import com.nami.webstore.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Controller
public class CheckoutController {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final PedidoRepository pedidoRepository;
    private final VarianteRepository varianteRepository;

    @Value("${mercadopago.access-token}")
    private String MP_ACCESS_TOKEN;

    public CheckoutController(UsuarioRepository usuarioRepository,
                              EnderecoRepository enderecoRepository,
                              PedidoRepository pedidoRepository,
                              VarianteRepository varianteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.pedidoRepository = pedidoRepository;
        this.varianteRepository = varianteRepository;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado != null) {
            Usuario usuario = usuarioRepository.findById(logado.getId()).orElse(logado);
            List<Endereco> enderecos = enderecoRepository.findByUsuarioId(usuario.getId());

            Endereco principal = enderecos.stream()
                    .filter(e -> Boolean.TRUE.equals(e.getPrincipal()))
                    .findFirst()
                    .orElse(enderecos.isEmpty() ? null : enderecos.get(0));

            model.addAttribute("enderecoSalvo", principal);
            model.addAttribute("enderecos", enderecos);
        }

        return "checkout";
    }

    @PostMapping("/checkout/iniciar")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> iniciarPagamento(
            @RequestBody Map<String,Object> payload,
            HttpSession session) {

        Map<String,Object> resp = new HashMap<>();

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) {
            resp.put("erro", "Faça login para continuar.");
            return ResponseEntity.ok(resp);
        }

        try {
            Usuario usuario = usuarioRepository.findById(logado.getId()).orElse(null);
            if (usuario == null) {
                resp.put("erro", "Usuário não encontrado.");
                return ResponseEntity.ok(resp);
            }

            Endereco endereco = resolverEndereco(payload, usuario);
            if (endereco == null) {
                resp.put("erro", "Endereço de entrega inválido.");
                return ResponseEntity.ok(resp);
            }

            @SuppressWarnings("unchecked")
            List<Map<String,Object>> itensPayload = (List<Map<String,Object>>) payload.get("itens");
            String metodo = String.valueOf(payload.get("metodo"));
            double total = ((Number) payload.get("total")).doubleValue();

            Pedido pedido = criarPedido(usuario, endereco, itensPayload, total);

            if ("PIX".equalsIgnoreCase(metodo)) {

                pedido.setStatus(StatusPedido.PAGO);

                pedidoRepository.save(pedido);

                resp.put("pixAprovado", true);
                resp.put("pedidoId", pedido.getId());

                return ResponseEntity.ok(resp);
            }

            String redirectUrl = chamarMercadoPago(pedido, metodo, itensPayload, usuario);

            resp.put("redirectUrl", redirectUrl);
            resp.put("pedidoId", pedido.getId());

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("erro", "Erro interno. Tente novamente.");
        }

        return ResponseEntity.ok(resp);
    }

    private Endereco resolverEndereco(Map<String,Object> payload, Usuario usuario) {
        Object endIdObj = payload.get("enderecoId");
        if (endIdObj != null && !endIdObj.toString().isBlank()) {
            try {
                Long endId = Long.parseLong(endIdObj.toString());
                return enderecoRepository.findById(endId)
                        .filter(e -> e.getCliente().getId().equals(usuario.getId()))
                        .orElse(null);
            } catch (NumberFormatException ignored) {}
        }

        @SuppressWarnings("unchecked")
        Map<String,Object> novoEnd = (Map<String,Object>) payload.get("novoEndereco");
        if (novoEnd == null) return null;

        Endereco end = new Endereco();
        end.setCliente(usuario);
        end.setApelido(str(novoEnd, "apelido"));
        end.setLogradouro(str(novoEnd, "logradouro"));
        end.setNumero(str(novoEnd, "numero"));
        end.setComplemento(str(novoEnd, "complemento"));
        end.setBairro(str(novoEnd, "bairro"));
        end.setCidade(str(novoEnd, "cidade"));
        String estado = str(novoEnd, "estado");
        end.setEstado(estado != null ? estado.toUpperCase() : "");
        end.setCep(str(novoEnd, "cep"));

        List<Endereco> existentes = enderecoRepository.findByUsuarioId(usuario.getId());
        end.setPrincipal(existentes.isEmpty());

        return enderecoRepository.save(end);
    }

    private Pedido criarPedido(Usuario usuario, Endereco endereco,
                               List<Map<String,Object>> itensPayload, double total) {
        Pedido pedido = new Pedido();
        pedido.setCliente(usuario);
        pedido.setEndereco(endereco);
        pedido.setTotal(BigDecimal.valueOf(total));

        List<ItemPedido> itens = new ArrayList<>();
        for (Map<String,Object> itemMap : itensPayload) {
            Object varIdObj = itemMap.get("varianteId");
            if (varIdObj != null && !varIdObj.toString().equals("null")) {
                try {
                    Long varId = Long.parseLong(varIdObj.toString());
                    varianteRepository.findById(varId).ifPresent(v -> {
                        ItemPedido item = new ItemPedido();
                        item.setPedido(pedido);
                        item.setVariante(v);
                        item.setQuantidade(((Number) itemMap.get("quantidade")).intValue());
                        item.setPrecoUnit(BigDecimal.valueOf(((Number) itemMap.get("preco")).doubleValue()));
                        itens.add(item);
                    });
                } catch (NumberFormatException ignored) {}
            } else {
                Object prodIdObj = itemMap.get("produtoId");
                if (prodIdObj != null) {
                    try {
                        Long prodId = Long.parseLong(prodIdObj.toString());
                        List<com.nami.webstore.model.Variante> vars = varianteRepository.findByProdutoId(prodId);
                        if (!vars.isEmpty()) {
                            com.nami.webstore.model.Variante v = vars.get(0);
                            ItemPedido item = new ItemPedido();
                            item.setPedido(pedido);
                            item.setVariante(v);
                            item.setQuantidade(((Number) itemMap.get("quantidade")).intValue());
                            item.setPrecoUnit(BigDecimal.valueOf(((Number) itemMap.get("preco")).doubleValue()));
                            itens.add(item);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        pedido.setItens(itens);
        return pedidoRepository.save(pedido);
    }

    private String chamarMercadoPago(Pedido pedido, String metodo,
                                     List<Map<String,Object>> itens, Usuario usuario) throws Exception {
        String baseUrl = System.getenv("APP_BASE_URL") != null
                ? System.getenv("APP_BASE_URL")
                : "http://localhost:8080";

        List<Map<String,Object>> mpItens = new ArrayList<>();
        for (Map<String,Object> item : itens) {
            Map<String,Object> mpItem = new HashMap<>();
            mpItem.put("id", String.valueOf(item.get("produtoId")));
            mpItem.put("title", item.get("nome"));
            mpItem.put("quantity", ((Number) item.get("quantidade")).intValue());
            mpItem.put("unit_price", ((Number) item.get("preco")).doubleValue());
            mpItem.put("currency_id", "BRL");
            mpItens.add(mpItem);
        }

        Map<String,Object> preference = new HashMap<>();
        preference.put("items", mpItens);
        preference.put("external_reference", String.valueOf(pedido.getId()));

        // status do pagamento vindo da api do mp
        Map<String,Object> backUrls = new HashMap<>();

        backUrls.put(
                "success",
                baseUrl + "/checkout/retorno?status=success&pedido=" + pedido.getId()
        );

        backUrls.put(
                "failure",
                baseUrl + "/checkout/retorno?status=failure&pedido=" + pedido.getId()
        );

        backUrls.put(
                "pending",
                baseUrl + "/checkout/retorno?status=pending&pedido=" + pedido.getId()
        );

        preference.put("back_urls", backUrls);

        //preference.put("auto_return", "approved"); //caso for subir no dominio isso aqui é necesasrio para voltar auto para o url

        Map<String,Object> payer = new HashMap<>();
        payer.put("email", usuario.getEmail());
        preference.put("payer", payer);

        String body = new ObjectMapper().writeValueAsString(preference);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mercadopago.com/checkout/preferences"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + MP_ACCESS_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        //HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("STATUS MP: " + response.statusCode());
        System.out.println("BODY MP: " + response.body());

        @SuppressWarnings("unchecked")
        Map<String,Object> mpResp = new ObjectMapper().readValue(response.body(), Map.class);

//        if (mpResp.containsKey("init_point")) { // modo produção
//            return (String) mpResp.get("init_point");
//        }

        if (mpResp.containsKey("sandbox_init_point")) { // modo teste / sandbox
            return (String) mpResp.get("sandbox_init_point");
        }


        return baseUrl + "/checkout/sucesso?pedido=" + pedido.getId();
    }

    @GetMapping("/checkout/retorno")
    public String retorno(@RequestParam String status,
                          @RequestParam(required = false) String pedido,
                          Model model) {
        model.addAttribute("status", status);
        model.addAttribute("pedidoId", pedido);
        return "checkout-sucesso";
    }

    @GetMapping("/checkout/sucesso")
    public String sucesso(@RequestParam(required = false) String pedido, Model model) {
        model.addAttribute("pedidoId", pedido);
        model.addAttribute("metodo", "");
        return "checkout-sucesso";
    }

    private String str(Map<String,Object> m, String key) {
        Object v = m.get(key);
        return v != null ? v.toString() : "";
    }
}
