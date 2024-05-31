package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.Banco;
import br.com.ireis.minhasfinancas.servico.BancoService;
import br.com.ireis.minhasfinancas.servico.CartaoDeCreditoService;
import br.com.ireis.minhasfinancas.servico.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bancos")
public class BancoController {
    @Autowired
    private BancoService service;
    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private CartaoDeCreditoService cartaoDeCreditoService;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        model.addAttribute("banco", new Banco());
        if (id != null) {
            var banco = service.obterPorId(id);
            model.addAttribute("banco", banco.get());
        }
        return "/bancos/formulario";
    }

    @PostMapping("/cadastrar")
    public String cadastrarBanco(@ModelAttribute Banco banco) {

        service.salvar(banco);
        return "redirect:/bancos";
    }

    @GetMapping
    public String carregarPaginaListagem(Model model) {
        model.addAttribute("lista", service.buscarTodosBancos());
        return "/bancos/listagem";
    }
    @PutMapping
    @Transactional
    public String atualizarBanco(@ModelAttribute Banco banco) {
        service.atualizar(banco);
        return "redirect:/bancos";
    }
    @DeleteMapping
    @Transactional
    public String excluirBancoPorId(Long id) {
        service.deletarPorId(id);
        return "redirect:/bancos";
    }
}