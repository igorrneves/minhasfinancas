package br.com.ireis.minhasfinancas.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "banco", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Banco {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;
    private String nome;
    private String agencia;
    private String conta;

    @Transient
    private BigDecimal totalDebito= BigDecimal.ZERO;
    @Transient
    private BigDecimal totalCredito= BigDecimal.ZERO;
    @Transient
    private BigDecimal saldoDisponivel = BigDecimal.ZERO;
    @Transient
    private BigDecimal totalInvestimento = BigDecimal.ZERO;

    @Transient
    private List<Lancamento> lancamentosDebitosRealizados = new ArrayList<>();

    @Transient
    private List<Lancamento> lancamentosCreditoRealizados = new ArrayList<>();

    @Transient
    private List<Lancamento> lancamentosInvestimentoRealizados = new ArrayList<>();

}
