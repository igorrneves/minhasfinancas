package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.Contrato;
import br.com.ireis.minhasfinancas.servico.ContratoService;
import br.com.ireis.minhasfinancas.servico.GrupoLancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contratos")
public class ContratoController {
    @Autowired
    private ContratoService contratoService;
    @Autowired
    private GrupoLancamentoService grupoLancamentoService;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        model.addAttribute("contrato", new Contrato());
        model.addAttribute("listaGruposLancamentos", grupoLancamentoService.buscarTodos());
        if (id != null) {
            var contrato = contratoService.obterPorId(id);
            model.addAttribute("contrato", contrato.get());
        }
        return "/contratos/formulario";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute Contrato contrato) {
        contratoService.salvar(contrato);
        return "redirect:/contratos";
    }

    @GetMapping
    public String carregarPaginaListagem(Model model) {
        model.addAttribute("listaDeContratos", contratoService.buscarTodos());
        return "/contratos/listagem";
    }

    @PutMapping
    @Transactional
    public String atualizarBanco(@ModelAttribute Contrato contrato) {
        contratoService.atualizar(contrato);
        return "redirect:/contratos";
    }

}
