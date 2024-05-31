package br.com.ireis.minhasfinancas.modelo.enumeracao;

public enum TipoDeDespesa {
    FIXA("FIXA"),
    EVENTUAL("EVENTUAL");

    private final String descricao;

    TipoDeDespesa(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao(){
        return this.descricao;
    }
}
