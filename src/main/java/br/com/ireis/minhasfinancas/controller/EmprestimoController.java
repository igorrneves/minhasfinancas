package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.Emprestimo;
import br.com.ireis.minhasfinancas.servico.BancoService;
import br.com.ireis.minhasfinancas.servico.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {
    @Autowired
    private EmprestimoService emprestimoService;
    @Autowired
    private BancoService bancoService;
    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model){
        model.addAttribute("emprestimo", new Emprestimo());
        model.addAttribute("listaBancos", bancoService.buscarTodosBancos());
        if (id != null){
            var emprestimo = emprestimoService.obterPorId(id);
            model.addAttribute("emprestimo", emprestimo.get());
        }
        return "/emprestimos/formulario";
    }

    @GetMapping
    public String carregarPaginaListagem(Model model){
        model.addAttribute("listaDeEmprestimos",emprestimoService.buscarTodos());
        return "/emprestimos/listagem";
    }

    @PostMapping("/cadastrar")
    public String cadastrarEmprestimo(@ModelAttribute Emprestimo emprestimo) {

        emprestimoService.salvar(emprestimo);
        return "redirect:/emprestimos";
    }
}
