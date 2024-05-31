package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.enumeracao.EstadoDaFaturaCartaoDeCredito;
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
@Table(name = "fatura_cartao_credito", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FaturaCartao {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;

    private int mes = LocalDate.now().getMonthValue();
    private int ano = LocalDate.now().getYear();
    @Column(name = "estado_fatura")
    @Enumerated(value = EnumType.STRING)
    private EstadoDaFaturaCartaoDeCredito estado = EstadoDaFaturaCartaoDeCredito.ABERTA;
    @ManyToOne
    @JoinColumn(name = "cartao_credito_id")
    private CartaoDeCredito cartaoDeCredito;

    @OneToMany(targetEntity = Lancamento.class, mappedBy = "faturaCartao", cascade=CascadeType.PERSIST)
    @Column(name = "fatura_cartao_credito_id",  nullable = true)
    private List<Lancamento> lancamentos = new ArrayList<>();

    private BigDecimal valorTotalFatura = BigDecimal.ZERO;
    private BigDecimal ValorPagoFatura = BigDecimal.ZERO;
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataPagamentoFatura = LocalDate.now();

    private BigDecimal saldoCreditoRotativo = BigDecimal.ZERO;

}
