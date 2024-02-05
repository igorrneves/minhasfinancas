package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeDespesa;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Lancamento {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    @Column(name = "data_lancamento")
    private LocalDate dataDoLancamento;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @Column(name = "descricao_lancamento")
    private String descricaoLancamento;
    @Column(name = "valor_lancamento")
    private BigDecimal valorLancamento;
    @Column(name = "numero_parcelas")
    private int numeroDeParcelas; //0-representa à vista
    @Enumerated(value = EnumType.STRING)
    @Column(name = "tipo_lancamento")
    private TipoDeLancamento tipoDeLancamento;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "tipo_despesa")
    private TipoDeDespesa tipoDeDespesa;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "forma_pagamento")
    private FormaDePagamento formaDePagamento;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "grupo_lancamento")
    private GrupoLancamento grupoLancamento;
}
