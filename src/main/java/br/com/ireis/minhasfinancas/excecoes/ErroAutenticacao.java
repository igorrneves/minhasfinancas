package br.com.ireis.minhasfinancas.excecoes;

public class ErroAutenticacao extends RuntimeException{
    public ErroAutenticacao(String mensagem){
        super(mensagem);
    }
}
