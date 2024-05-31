package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.CartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.registro.DadosCadastroCartaoDeCredito;
import br.com.ireis.minhasfinancas.servico.BancoService;
import br.com.ireis.minhasfinancas.servico.CartaoDeCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cartoesDeCredito")
public class CartaoDeCreditoController {
    @Autowired
    private CartaoDeCreditoService service;
    @Autowired
    private BancoService bancoService;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        model.addAttribute("cartaoDeCredito", new CartaoDeCredito());
        model.addAttribute("listaBancos", bancoService.buscarTodosBancos());
        if (id != null) {
            var cartaoDeCredito = service.obterPorId(id);
            model.addAttribute("cartaoDeCredito", cartaoDeCredito.get());
        }
        return "/cartoesDeCredito/formulario";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute CartaoDeCredito cartaoDeCredito) {
        //CartaoDeCredito cartaoDeCredito = new CartaoDeCredito(dadosCadastroCartaoDeCredito);
        service.salvar(cartaoDeCredito);
        return "redirect:/cartoesDeCredito";
    }

    @GetMapping
    public String carregarPaginaListagem(Model model) {
        model.addAttribute("lista", service.buscarTodos());
        return "/cartoesDeCredito/listagem";
    }

    @PutMapping
    @Transactional
    public String atualizar(@ModelAttribute CartaoDeCredito cartaoDeCredito) {
        service.atualizar(cartaoDeCredito);
        return "redirect:/cartoesDeCredito";
    }
    @DeleteMapping
    @Transactional
    public String excluirPorId(Long id) {
        service.deletarPorId(id);
        return "redirect:/cartoesDeCredito";
    }
}
