package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.enumeracao.BandeiraCartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.enumeracao.SituacaoCartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.registro.DadosCadastroCartaoDeCredito;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cartao_credito", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CartaoDeCredito {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;

    @ManyToOne
    @JoinColumn(name = "banco_id")
    private Banco banco;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(value = EnumType.STRING)
    private BandeiraCartaoDeCredito bandeira;
    private String numero;
    private int mesValidade;
    private int anoValidade;
    private int codigoSeguranca;
    private int diaVencimento;

    @Enumerated(value = EnumType.STRING)
    private SituacaoCartaoDeCredito situacao;

    @Transient
    private BigDecimal saldo;
    public CartaoDeCredito(DadosCadastroCartaoDeCredito dadosCadastroCartaoDeCredito){
        Role role=new Role();
        role.setName("ROLE_ADMIN");
        //this.usuario = new Usuario(1l,"igor","igor@gmail.com","123", (Set<Role>) role);
        this.banco = dadosCadastroCartaoDeCredito.banco();
        this.bandeira = dadosCadastroCartaoDeCredito.bandeira();
        this.numero = dadosCadastroCartaoDeCredito.numero();
        this.mesValidade = dadosCadastroCartaoDeCredito.mesValidade();
        this.anoValidade = dadosCadastroCartaoDeCredito.anoValidade();
        this.codigoSeguranca = dadosCadastroCartaoDeCredito.codigoSeguranca();
        this.situacao = dadosCadastroCartaoDeCredito.situacao();
        this.diaVencimento = dadosCadastroCartaoDeCredito.diaVencimento();
    }
}
