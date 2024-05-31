package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.Emprestimo;
import br.com.ireis.minhasfinancas.modelo.Investimento;
import br.com.ireis.minhasfinancas.servico.BancoService;
import br.com.ireis.minhasfinancas.servico.InvestimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/investimentos")
public class InvestimentoController {
    @Autowired
    private InvestimentoService investimentoService;
    @Autowired
    private BancoService bancoService;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        model.addAttribute("investimento", new Investimento());
        model.addAttribute("listaBancos", bancoService.buscarTodosBancos());
        if (id != null) {
            var investimento = investimentoService.obterPorId(id);
            model.addAttribute("investimento", investimento.get());
        }
        return "/investimentos/formulario";
    }

    @GetMapping
    public String carregarPaginaListagem(Model model){
        model.addAttribute("investimento", new Investimento());
        List<Investimento> insvestimentos = investimentoService.buscarTodos();

        model.addAttribute("listaInvestimentos", insvestimentos);
        return "/investimentos/listagem";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute Investimento investimento) {

        investimentoService.salvar(investimento);
        return "redirect:/investimentos";
    }
}
