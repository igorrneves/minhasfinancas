package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.*;
import br.com.ireis.minhasfinancas.modelo.enumeracao.EstadoDoLancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import br.com.ireis.minhasfinancas.servico.*;
import org.apache.logging.log4j.util.PropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Controller
@RequestMapping("/extrato")
public class ExtratoController {
    @Autowired
    private ExtratoService extratoService;
    @Autowired
    private PlanejamentoService planejamentoService;

    @GetMapping()
    public String carregaPaginaConsulta(Model model) {
        model.addAttribute("extrato", new Extrato());
        return "/extrato/extrato";
    }
    @PostMapping("/carregarPaginaExtrato")
    public String carregarPaginaExtrato(@ModelAttribute Extrato extrato, Model model) {
        if(extrato.getAno() == 0 && extrato.getMes() == 0){
            extrato.setAno(LocalDate.now().getYear());
            extrato.setMes(LocalDate.now().getMonthValue());
        }

        model.addAttribute("planejamento", planejamentoService.calcularRealizadoMensal(extrato.getAno(),extrato.getMes()));
        model.addAttribute("extrato", extrato);
        model.addAttribute("listaBancos", extratoService.calcularTotalMensalDebitoCreditoPorBanco(extrato.getAno(),extrato.getMes()));
        model.addAttribute("listaCartoes", extratoService.calcularTotalMensalDebitoCreditoPorCartao(extrato.getAno(),extrato.getMes()));
        return "/extrato/extrato";

    }
    @GetMapping("/carregarPaginaExtratoDetalhadoPorGrupo")
    public String carregarPaginaExtratoDetalhadoPorGrupo(Model model, int ano, int mes, long grupoId) {
        PlanejamentoGrupoLancamento grupo = planejamentoService.calcularRealizadoMensalPorGrupo(ano,mes,grupoId);
        model.addAttribute("grupo", grupo);
        model.addAttribute("listaLancamentos", grupo.getLancamentosRealizados());
        model.addAttribute("extrato", new Extrato(mes,ano));
        return "/extrato/extratoDosLancamentosPorGrupo";
    }
    @GetMapping("/carregarPaginaExtratoDetalhadoPorContaCorrente")
    public String carregarPaginaExtratoDetalhadoPorContaCorrente(Model model, int ano, int mes, long bancoId, String operacao) {
        Banco banco = extratoService.calcularRealizadoMensalPorBanco(ano,mes,bancoId);
        model.addAttribute("banco", banco);
        if (operacao.equals("debito")){
            model.addAttribute("listaLancamentos", banco.getLancamentosDebitosRealizados());
        }else if (operacao.equals("credito")){
            model.addAttribute("listaLancamentos", banco.getLancamentosCreditoRealizados());
        }else if (operacao.equals("investimento")){
            model.addAttribute("listaLancamentos", banco.getLancamentosInvestimentoRealizados());
        }
        model.addAttribute("extrato", new Extrato(mes,ano));
        return "/extrato/extratoDosLancamentosPorConta";
    }
    @GetMapping("/carregarPaginaExtratoDetalhadoPorLancamentoRealizado")
    public String carregarPaginaExtratoDetalhadoPorLancamentoRealizado(Model model, int ano, int mes) {
        model.addAttribute("listaLancamentos", extratoService.listaMensalDebitoCreditoRealizado(ano,mes));
        model.addAttribute("extrato", new Extrato(mes,ano));
        return "/extrato/extratoDosLancamentosRealizados";
    }
}
