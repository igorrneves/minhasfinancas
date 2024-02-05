package br.com.ireis.minhasfinancas.controller;

import br.com.ireis.minhasfinancas.modelo.FaturaCartao;
import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.Investimento;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.EstadoDoLancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import br.com.ireis.minhasfinancas.servico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/lancamentos")
public class LancamentoController {
    @Autowired
    private LancamentoService service;
    @Autowired
    private GrupoLancamentoService grupoLancamentoService;
    @Autowired
    private BancoService bancoService;
    @Autowired
    private CartaoDeCreditoService cartaoDeCreditoService;
    @Autowired
    private FaturaCartaoService faturaCartaoService;
    @Autowired
    private InvestimentoService investimentoService;
    @Autowired
    private EmprestimoService emprestimoService;
    @Autowired
    private ContratoService contratoService;

    public void carregaCombosDosFiltros(Model model) {
        model.addAttribute("listaGruposLancamentos", grupoLancamentoService.buscarTodos());
        model.addAttribute("listaBancos", bancoService.buscarTodosBancos());
        model.addAttribute("listaCartoesDeCredito", cartaoDeCreditoService.buscarTodos());
    }

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {

        model.addAttribute("lancamento", new Lancamento());
        this.carregaCombosDosFiltros(model);
        if (id != null) {
            var lancamento = service.obterPorId(id);
            if (lancamento.get().getFormaDePagamento()==FormaDePagamento.CREDITO){
                lancamento.get().setFatura(lancamento.get().getFaturaCartao().getMes()+"/"+lancamento.get().getFaturaCartao().getAno());
            }
            model.addAttribute("lancamento", lancamento.get());
            if(lancamento.get().getNumeroDeParcelas()>1) {
                if (lancamento.get().getParcela() != null) { // precisa testar, pois a primeira parcela não possui o campo lancamento_id preenchido
                    model.addAttribute("idParcela", lancamento.get().getParcela().getId());
                }else {
                    model.addAttribute("idParcela", lancamento.get().getId());
                }
            }else {
                model.addAttribute("idParcela", 0);
            }
        }
        return "/lancamentos/formulario";
    }

    @GetMapping("/formEmprestimo")
    public String carregaPaginaFormularioEmprestimo(Long idEmprestimo, Model model) {
        Lancamento lancamento = new Lancamento();
        var emprestimo = emprestimoService.obterPorId(idEmprestimo);
        lancamento.setEmprestimo(emprestimo.get());
        lancamento.setValorLancamento(emprestimo.get().getValorEmprestimoParcela());
        model.addAttribute("lancamento", lancamento);
        return "/lancamentos/formEmprestimo";
    }

    @GetMapping("/formPagamentoContrato")
    public String carregaPaginaFormularioPagamentoContrato(Long idContrato, Model model) {
        this.carregaCombosDosFiltros(model);
        Lancamento lancamento = new Lancamento();
        var contrato = contratoService.obterPorId(idContrato);
        lancamento.setContrato(contrato.get());
        model.addAttribute("lancamento", lancamento);
        return "/lancamentos/formPagContrato";
    }
    @GetMapping("/formPagamentoFaturaCartao")
    public String carregaPaginaFormularioPagamentoFaturaCartao(Long idfatura, Model model) {
        this.carregaCombosDosFiltros(model);
        Lancamento lancamento = new Lancamento();
        var faturaCartao = faturaCartaoService.obterPorId(idfatura);
        lancamento.setCartaoDeCredito(faturaCartao.get().getCartaoDeCredito());
        lancamento.setFaturaCartao(faturaCartao.get());
        model.addAttribute("lancamento", lancamento);
        return "/lancamentos/formPagFaturaCartao";
    }
    @GetMapping("/formInvestimento")
    public String carregaPaginaFormularioInvestimento(Long idInvestimento, Model model) {
        Lancamento lancamento = new Lancamento();
        var investimento = investimentoService.obterPorId(idInvestimento);
        lancamento.setInvestimento(investimento.get());
        model.addAttribute("lancamento", lancamento);
        return "/lancamentos/formInvestimento";
    }
    @GetMapping("/listaLancamentosEmprestimos")
    public String carregaListaDeLancamentosDeUmEmprestimo(Long idEmprestimo, Model model) {
        model.addAttribute("lancamento", new Lancamento());
        this.carregaCombosDosFiltros(model);
        var emprestimo = emprestimoService.obterPorId(idEmprestimo);
        model.addAttribute("listaLancamentos", emprestimo.get().getLancamentos());
        return "/lancamentos/listagem";
    }
    @GetMapping("/listaLancamentosContratos")
    public String carregaListaDeLancamentosDeUmContrato(Long idContrato, Model model) {
        model.addAttribute("lancamento", new Lancamento());
        this.carregaCombosDosFiltros(model);
        var contrato = contratoService.obterPorId(idContrato);
        model.addAttribute("listaLancamentos", contrato.get().getLancamentos());
        return "/lancamentos/listagem";
    }
    @GetMapping("/listaLancamentosFaturaCartao")
    public String carregaListaDeLancamentosDeUmaFatura(Long idfatura, Model model) {
        model.addAttribute("lancamento", new Lancamento());
        this.carregaCombosDosFiltros(model);
        var faturaCartao = faturaCartaoService.obterPorId(idfatura);
        model.addAttribute("listaLancamentos", faturaCartao.get().getLancamentos());
        return "/lancamentos/listagem";
    }
    @GetMapping("/listaLancamentosInvestimento")
    public String carregaListaDeLancamentosDeUmInvestimento(Long idInvestimento, Model model) {
        model.addAttribute("lancamento", new Lancamento());
        this.carregaCombosDosFiltros(model);
        var investimento = investimentoService.obterPorId(idInvestimento);
        model.addAttribute("listaLancamentos", investimento.get().getLancamentos());
        return "/lancamentos/listagem";
    }
    @GetMapping
    public String carregarPaginaListagem(Model model) {
        model.addAttribute("lancamento", new Lancamento());
        model.addAttribute("listaLancamentos", service.buscarPorEstadoLancamento(EstadoDoLancamento.ABERTO));
        this.carregaCombosDosFiltros(model);
        return "/lancamentos/listagem";
    }

    @GetMapping("/listaParcelasDeUmLancamento")
    public String listaParcelasDeUmLancamento(Long id, Model model) {
        model.addAttribute("lancamento", new Lancamento());
        var lancamento = service.obterPorId(id);
        if (lancamento.get().getParcela() != null){
            lancamento = service.obterPorId(lancamento.get().getParcela().getId());
        }
        model.addAttribute("listaLancamentos", lancamento.get().getParcelas());
        this.carregaCombosDosFiltros(model);
        return "/lancamentos/listagem";
    }

    @GetMapping("/listaLancamentosSemFaturaAssociada")
    public String listarLancamentosSemFaturaAssociada(Long idFaturaCartao, Model model) {
        Lancamento lancamento = new Lancamento();
        lancamento.setCartaoDeCredito(faturaCartaoService.obterPorId(idFaturaCartao).get().getCartaoDeCredito());
        lancamento.setEstadoDoLancamento(EstadoDoLancamento.ABERTO);
        model.addAttribute("lancamento", lancamento);
        model.addAttribute("listaLancamentos", service.buscarPorCartaoDeCreditoEstado(lancamento));
        model.addAttribute("idFaturaCartao", idFaturaCartao);

        return "/lancamentos/listagem";
    }

    @GetMapping("/adicionarLancamentoNaFatura")
    public String adicionarLancamentoNaFatura(Long id, Long idFaturaCartao) {
        Lancamento lancamento = (Lancamento) service.obterPorId(id).get();
        FaturaCartao faturaCartao = (FaturaCartao) faturaCartaoService.obterPorId(idFaturaCartao).get();
        lancamento.setFaturaCartao(faturaCartao);
        service.atualizar(lancamento);

        return "redirect:/faturaCartao/formulario?id=" + idFaturaCartao;
    }

    @GetMapping("/removerLancamentoDaFatura")
    public String removerLancamentoDaFatura(Long id, Long idFaturaCartao) {
        Lancamento lancamento = (Lancamento) service.obterPorId(id).get();
        lancamento.setFaturaCartao(null);

        service.atualizar(lancamento);

        return "/lancamentos/listagem";
    }

    @GetMapping("/atualizarEstadoDoLancamento")
    public String atualizarEstadoDoLancamento(Long id, EstadoDoLancamento estado) {
        Lancamento lancamento = (Lancamento) service.obterPorId(id).get();
        lancamento.setEstadoDoLancamento(estado);
        service.atualizar(lancamento);
        if (lancamento.getFaturaCartao() != null) {
            return "redirect:/faturaCartao/formulario?id=" + lancamento.getFaturaCartao().getId();
        } else {
            return "redirect:/lancamentos";
        }
    }

    @PostMapping
    public String buscarLancamento(Model model, @ModelAttribute Lancamento lancamento) {
        model.addAttribute("listaLancamentos", service.buscaFiltrada(lancamento));
        model.addAttribute("lancamento", lancamento);
        model.addAttribute("listaGruposLancamentos", grupoLancamentoService.buscarTodos());
        model.addAttribute("listaBancos", bancoService.buscarTodosBancos());
        model.addAttribute("listaCartoesDeCredito", cartaoDeCreditoService.buscarTodos());
        return "/lancamentos/listagem";
    }

    @PostMapping("/cadastrar")
    public String cadastrarLancamento(@ModelAttribute Lancamento lancamento) {
        try {
            service.salvar(lancamento);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/lancamentos/formulario";
    }

    @PostMapping("/cadastrarLancamentoInvestimento")
    public String cadastrarLancamentoInvestimento(@ModelAttribute Lancamento lancamento) {
        try {
            lancamento.setInvestimento(investimentoService.obterPorId(lancamento.getInvestimento().getId()).get());
            service.ajustaPropriedadesLancamentoInvestimento(lancamento);
            lancamento.setGrupoLancamento(grupoLancamentoService.obterPorId(3000L).get()); //3000 - representa o Grupo: Investimento
            service.salvar(lancamento);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/investimentos";
    }

    @PostMapping("/cadastrarLancamentoEmprestimo")
    public String cadastrarLancamentoEmprestimo(@ModelAttribute Lancamento lancamento) {
        try {
            lancamento.setEmprestimo(emprestimoService.obterPorId(lancamento.getEmprestimo().getId()).get());
            service.ajustaPropriedadesLancamentoEmprestimo(lancamento);
            lancamento.setGrupoLancamento(grupoLancamentoService.obterPorId(2000L).get()); //2000 - representa o Grupo: Empréstimo
            service.salvar(lancamento);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/emprestimos";
    }

    @PostMapping("/cadastrarLancamentoPagamentoContrato")
    public String cadastrarLancamentoPagamentoContrato(@ModelAttribute Lancamento lancamento) {
        try {
            lancamento.setContrato(contratoService.obterPorId(lancamento.getContrato().getId()).get());
            service.ajustaPropriedadesLancamentoContrato(lancamento);
            service.salvar(lancamento);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/contratos";
    }

    @PostMapping("/cadastrarLancamentoPagamentoFaturaCartao")
    public String cadastrarLancamentoPagamentoFaturaCartao(@ModelAttribute Lancamento lancamento) {
        try {
            var faturaCartao = faturaCartaoService.obterPorId(lancamento.getFaturaCartao().getId());
            lancamento.setFaturaCartao(faturaCartao.get());
            //lancamento.setCartaoDeCredito(null);
            lancamento.setGrupoLancamento(grupoLancamentoService.obterPorId(1000L).get()); //1000 - representa o Grupo: cartão de crédito
            service.ajustaPropriedadesLancamentoPagamentoFaturaCartao(lancamento);

            service.salvar(lancamento);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/faturaCartao";
    }

    @PutMapping
    @Transactional
    public String atualizarLancamento(@ModelAttribute Lancamento lancamento,Long idParcela) {
        lancamento.setParcela(service.obterPorId(idParcela).get());
        service.atualizar(lancamento);
        return "redirect:/lancamentos";
    }

    @DeleteMapping
    @Transactional
    public String excluirLancamentoPorId(Long id) {
        service.deletarPorId(id);
        return "redirect:/lancamentos";
    }

    @GetMapping("/excluirLancamentoPorId")
    public String excluirLancamentoPorIdGet(Long id) {
        service.deletarPorId(id);
        return "redirect:/lancamentos";
    }

    @PostMapping("/gerarLancamentoPagamentoFatura")
    public String gerarLancamentoPagamentoFatura(@ModelAttribute FaturaCartao faturaCartao) {

        Optional<FaturaCartao> fatura = faturaCartaoService.obterPorId(faturaCartao.getId());
        fatura.get().setValorPagoFatura(faturaCartao.getValorPagoFatura());
        fatura.get().setDataPagamentoFatura(faturaCartao.getDataPagamentoFatura());
        faturaCartaoService.pagar(fatura.get());

        Optional<GrupoLancamento> grupoLancamento = grupoLancamentoService.obterPorId(Long.valueOf("303")); //Cartão de crédito
        Lancamento lancamento = new Lancamento();
        lancamento.setFaturaCartao(fatura.get());
        lancamento.setCartaoDeCredito(fatura.get().getCartaoDeCredito());
        lancamento.setBanco(fatura.get().getCartaoDeCredito().getBanco());
        lancamento.setGrupoLancamento(grupoLancamento.get());
        lancamento.setEstadoDoLancamento(EstadoDoLancamento.ABERTO);
        lancamento.setUsuario(fatura.get().getCartaoDeCredito().getUsuario());
        lancamento.setTipoDeLancamento(TipoDeLancamento.DESPESA);
        lancamento.setFormaDePagamento(FormaDePagamento.DEBITO);
        lancamento.setValorLancamento(faturaCartao.getValorPagoFatura());
        lancamento.setDescricaoLancamento("PAGAMENTO FATURA: " + fatura.get().getMes() + "/" + fatura.get().getAno() + " - CARTÃO: " + fatura.get().getCartaoDeCredito().getNumero());
        try {
            service.salvar(lancamento);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/faturaCartao";
    }
}
