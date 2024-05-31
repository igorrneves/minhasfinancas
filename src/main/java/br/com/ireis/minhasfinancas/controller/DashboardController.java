package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import br.com.ireis.minhasfinancas.servico.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
     @Autowired
    private LancamentoService lancamentoService;
    @GetMapping
    public String carregaPaginaInicial(Model model){
        model.addAttribute("chartData", getChartData());
        return "/dashboard/dashboard";
    }

    private List<List<Object>> getChartData() {
        int ano = 2024;
        String[] mes = {"Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez"};
        List listaTotal = new ArrayList<>();

        BigDecimal totalDespesa = BigDecimal.ZERO;
        BigDecimal totalReceita = BigDecimal.ZERO;
        for (int i=0; i < 12; i++){
            totalReceita = lancamentoService.buscarTotalPorTipoMesAno(ano,i+1, TipoDeLancamento.RECEITA);
            totalDespesa = lancamentoService.buscarTotalPorTipoMesAno(ano,i+1, TipoDeLancamento.DESPESA);
            if (totalReceita == null){
                totalReceita = BigDecimal.ZERO;
            }
            if (totalDespesa == null){
                totalDespesa = BigDecimal.ZERO;
            }
            listaTotal.add(List.of(mes[i],totalReceita,totalDespesa));
        }

        return listaTotal;
    }
}
