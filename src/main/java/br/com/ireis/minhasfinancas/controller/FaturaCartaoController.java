package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.FaturaCartao;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.EstadoDaFaturaCartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.servico.CartaoDeCreditoService;
import br.com.ireis.minhasfinancas.servico.FaturaCartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/faturaCartao")
public class FaturaCartaoController {
    @Autowired
    private FaturaCartaoService faturaCartaoService;
    @Autowired
    private CartaoDeCreditoService cartaoDeCreditoService;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        model.addAttribute("faturaCartao", new FaturaCartao());
        model.addAttribute("listaCartoesDeCredito", cartaoDeCreditoService.buscarTodos());

        if (id != null) {
            var faturaCartao = faturaCartaoService.obterPorId(id);
            faturaCartaoService.calcularValorTotalDaFatura(faturaCartao.get());
            model.addAttribute("faturaCartao", faturaCartao.get());
            model.addAttribute("listaLancamentos", faturaCartao.get().getLancamentos().stream().sorted(Comparator.comparing(Lancamento::getDataDoLancamento)));
        }
        return "/faturaCartao/formulario";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute FaturaCartao faturaCartao) {
        faturaCartaoService.salvar(faturaCartao);
        return "redirect:/faturaCartao";
    }

    @GetMapping
    public String carregarPaginaListagem(Model model) {
        model.addAttribute("faturaCartao", new FaturaCartao());
        model.addAttribute("listaCartoesDeCredito", cartaoDeCreditoService.buscarTodos());
        model.addAttribute("listaFaturas", faturaCartaoService.buscarTodos());
        return "/faturaCartao/listagem";
    }

    @GetMapping("/atualizarValorTotalDaFatura")
    public String atualizarValorTotalDaFatura(Long id) {
        faturaCartaoService.calcularValorTotalDaFatura(faturaCartaoService.obterPorId(id).get());
        return "redirect:/faturaCartao/formulario?id="+id;
    }
    @GetMapping("/fecharFatura")
    public String fecharFatura(Long id){
        FaturaCartao faturaCartao = faturaCartaoService.obterPorId(id).get();
        faturaCartao.setEstado(EstadoDaFaturaCartaoDeCredito.FECHADA);
        faturaCartaoService.atualizar(faturaCartao);
        return "redirect:/faturaCartao";
    }

    @PostMapping
    public String carregarPaginaListagemFiltrada(Model model, @ModelAttribute FaturaCartao faturaCartao){
        model.addAttribute("faturaCartao", faturaCartao);
        model.addAttribute("listaFaturas", faturaCartaoService.buscarComFiltro(faturaCartao));
        return "/faturaCartao/listagem";
    }

    @DeleteMapping
    @Transactional
    public String excluirPorId(Long id) {
        faturaCartaoService.deletarPorId(id);
        return "redirect:/faturaCartao";
    }

    @PutMapping
    @Transactional
    public String atualizar(@ModelAttribute FaturaCartao faturaCartao) {
        faturaCartaoService.atualizar(faturaCartao);
        return "redirect:/faturaCartao/formulario?id=" + faturaCartao.getId();
    }
}
