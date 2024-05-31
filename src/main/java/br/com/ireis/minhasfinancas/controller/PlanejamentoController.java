package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.Planejamento;
import br.com.ireis.minhasfinancas.modelo.PlanejamentoGrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.registro.DadosCadastroPlanejamento;
import br.com.ireis.minhasfinancas.servico.GrupoLancamentoService;
import br.com.ireis.minhasfinancas.servico.LancamentoService;
import br.com.ireis.minhasfinancas.servico.PlanejamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/planejamento")
public class PlanejamentoController {
    @Autowired
    private PlanejamentoService planejamentoService;
    @Autowired
    private GrupoLancamentoService grupoLancamentoService;
    @Autowired
    private LancamentoService lancamentoService;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model){
        Planejamento planejamento = new Planejamento();
        model.addAttribute("planejamento", planejamento);
        if (id != null){
            planejamento = planejamentoService.obterPorId(id).get();
            planejamento.setGrupoLancamentos(grupoLancamentoService.buscarTodos());
            for (PlanejamentoGrupoLancamento gruposPlanejados : planejamento.getGruposPlanejados()) {
                for (GrupoLancamento grupo : planejamento.getGrupoLancamentos()) {
                    if (gruposPlanejados.getGrupoLancamento().getId() == grupo.getId()){
                        grupo.setValorPlanejado(gruposPlanejados.getValorPlanejado());
                        break;
                    }
                }
            }
            model.addAttribute("planejamento", planejamento);

        }

        return "/planejamento/formulario";
    }

    @GetMapping("/acompanhamentoMensal")
    public String carregaPaginaAcompanhamentoMensal(Long id, Model model) {

        Planejamento planejamento =  planejamentoService.obterPorId(id).get();
        planejamentoService.calcularRealizadoMensal(planejamento.getAno(), planejamento.getMes());

        model.addAttribute("planejamento", planejamento);
        return "/planejamento/acompanhamentoMensal";
    }



    @GetMapping
    public String carregarPaginaListagem(Model model){
        model.addAttribute("listaPlanejamentos", planejamentoService.buscarTodos());
        return "/planejamento/listagem";
    }


    @PostMapping
    public String buscarPlanejamento(Model model, DadosCadastroPlanejamento dadosCadastroPlanejamento){
        model.addAttribute("listaPlanejamento", planejamentoService.buscar(new Planejamento(dadosCadastroPlanejamento)));
        return "/planejamento/listagem";
    }
    @PostMapping("/cadastrar")
    public String cadastrar(DadosCadastroPlanejamento dadosCadastroPlanejamento){
        Planejamento planejamento = new Planejamento(dadosCadastroPlanejamento);
       planejamentoService.salvar(planejamento);
        return "redirect:/planejamento";
    }
    @PutMapping
    @Transactional
    public String atualizar(@ModelAttribute Planejamento form){

        for  (GrupoLancamento grupoLancamento : form.getGrupoLancamentos()){
            if (grupoLancamento.getValorPlanejado() != null){
                PlanejamentoGrupoLancamento planejamentoGrupoLancamento = new PlanejamentoGrupoLancamento();
                planejamentoGrupoLancamento.setPlanejamento(form);
                planejamentoGrupoLancamento.setGrupoLancamento(grupoLancamento);
                planejamentoGrupoLancamento.setValorPlanejado(grupoLancamento.getValorPlanejado());
                form.getGruposPlanejados().add(planejamentoGrupoLancamento);
            }
        }

        //System.out.printf("Planejamento: "+form);

        planejamentoService.atualizar(form);
        return "redirect:/planejamento";
    }
    @DeleteMapping
    @Transactional
    public String excluirPorId(Long id){
        planejamentoService.deletarPorId(id);
        return "redirect:/planejamento";
    }
}
