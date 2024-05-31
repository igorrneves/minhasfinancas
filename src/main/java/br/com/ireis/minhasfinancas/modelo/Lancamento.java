package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.enumeracao.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lancamento", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Lancamento implements Cloneable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;

    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    @Column(name = "data_lancamento")
    private LocalDate dataDoLancamento = LocalDate.now();
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String descricaoLancamento;

    private BigDecimal valorLancamento;

    private BigDecimal valorParcelaLancamento;
    @Column(name = "numero_parcelas")
    private int numeroDeParcelas = 1; //1-representa à vista
    @Column(name = "numero_parcela",nullable = true)
    private int numeroDaParcela = 1;

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "lancamento_id", referencedColumnName = "id")
    private Lancamento parcela;

    @OneToMany(targetEntity = Lancamento.class, mappedBy = "parcela", cascade=CascadeType.ALL)
    @Column(name = "lancamento_id",  nullable = true)
    private List<Lancamento> parcelas = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    @Column(name = "tipo_lancamento")
    private TipoDeLancamento tipoDeLancamento = TipoDeLancamento.DESPESA;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "tipo_despesa")
    private TipoDeDespesa tipoDeDespesa= TipoDeDespesa.EVENTUAL;
    @ManyToOne
    @JoinColumn(name = "banco_id")
    private Banco banco;

    @Column(name = "forma_pagamento")
    @Enumerated(value = EnumType.STRING)
    private FormaDePagamento formaDePagamento= FormaDePagamento.CREDITO;

    @Column(name = "forma_recebimento_credito")
    @Enumerated(value = EnumType.STRING)
    private FormaDeRecebimentoCredito formaDeRecebimentoCredito= FormaDeRecebimentoCredito.CONTA_CORRENTE;
    @ManyToOne
    @JoinColumn(name = "grupo_lancamento_id")
    private GrupoLancamento grupoLancamento;
    @ManyToOne
    @JoinColumn(name = "cartao_credito_id")
    private CartaoDeCredito cartaoDeCredito;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "fatura_cartao_credito_id")
    private FaturaCartao faturaCartao;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "estado_lancamento")
    private EstadoDoLancamento estadoDoLancamento= EstadoDoLancamento.ABERTO;
    private String numeroCheque="0";

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "investimento_id")
    private Investimento investimento;
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "emprestimo_id")
    private Emprestimo emprestimo;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @Transient
    private int numeroDeRepeticaoDoLancamento;

    @Transient
    private String fatura="";

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void adicionarParcela(Lancamento lancamento){
        this.getParcelas().add(lancamento);
    }
}
