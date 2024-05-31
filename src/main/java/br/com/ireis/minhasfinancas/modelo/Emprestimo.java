package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.enumeracao.SituacaoDoEmprestimo;
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
@Table(name = "emprestimo", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Emprestimo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;

    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    @Column(name = "data_abertura")
    private LocalDate dataAbertura = LocalDate.now();

    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataPrimeiraParcela = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    private String contrato;
    private int quantidadeParcelas=0;
    private int diaVencimento=0;


    private BigDecimal valorEmprestimo=BigDecimal.ZERO;
    private BigDecimal valorEmprestimoParcela=BigDecimal.ZERO;
    private BigDecimal taxaJuros=BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "banco_id")
    private Banco banco;

    @Enumerated(value = EnumType.STRING)
    private SituacaoDoEmprestimo situacaoEmprestimo=SituacaoDoEmprestimo.ABERTO;

    @OneToMany(targetEntity = Lancamento.class, mappedBy = "emprestimo", cascade=CascadeType.PERSIST)
    @Column(name = "emprestimo_id",  nullable = true)
    private List<Lancamento> lancamentos = new ArrayList<>();
}
