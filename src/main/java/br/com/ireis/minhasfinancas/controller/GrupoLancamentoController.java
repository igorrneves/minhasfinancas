package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.registro.DadosCadastroGrupoLancamento;
import br.com.ireis.minhasfinancas.servico.GrupoLancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/gruposLancamentos")
public class GrupoLancamentoController {
    @Autowired
    private GrupoLancamentoService service;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        model.addAttribute("grupoLancamento", new GrupoLancamento());
        if (id != null) {
            var grupoLancamento = service.obterPorId(id);
            model.addAttribute("grupoLancamento", grupoLancamento.get());
        }
        return "/gruposLancamentos/formulario";
    }

    @PostMapping("/cadastrar")
    public String cadastrarGrupoLancamento(DadosCadastroGrupoLancamento dadosCadastroGrupoLancamento) {
        GrupoLancamento grupoLancamento = new GrupoLancamento(dadosCadastroGrupoLancamento);
        service.salvar(grupoLancamento);
        return "redirect:/gruposLancamentos";
    }

    /*@GetMapping
    public String carregarPaginaListagem(Model model) {
        model.addAttribute("lista", service.buscarTodos());
        return "/gruposLancamentos/listagem";
    }*/

    @PutMapping
    @Transactional
    public String atualizarGrupoLancamento(DadosCadastroGrupoLancamento dadosCadastroGrupoLancamento) {
        service.atualizar(new GrupoLancamento(dadosCadastroGrupoLancamento));
        return "redirect:/gruposLancamentos";
    }
    @DeleteMapping
    @Transactional
    public String excluirGrupoLancamentoPorId(Long id) {
        service.deletarPorId(id);
        return "redirect:/gruposLancamentos";
    }

    @GetMapping
    public String listaPaginadaDeGruposLancamentos(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<GrupoLancamento> bookPage = service.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("lista", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "/gruposLancamentos/listagem";
    }
}
