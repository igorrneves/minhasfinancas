package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeInvestimento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeRisco;
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
@Table(name = "investimento", schema = "financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Investimento {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = null;
    @ManyToOne
    @JoinColumn(name = "banco_id")
    private Banco banco;
    private String titulo;
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate vencimento;
    private TipoDeInvestimento tipoInvestimento = TipoDeInvestimento.RENDA_FIXA;
    private String indice;
    @Transient
    private BigDecimal valorAplicado;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "tipo_risco")
    private TipoDeRisco risco = TipoDeRisco.MODERADO;
    @OneToMany(targetEntity = Lancamento.class, mappedBy = "investimento", cascade=CascadeType.PERSIST)
    @Column(name = "investimento_id",  nullable = true)
    private List<Lancamento> lancamentos = new ArrayList<>();

}
