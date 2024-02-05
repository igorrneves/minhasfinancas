package br.com.ireis.minhasfinancas.excecoes;

public class RegraNegocioException extends RuntimeException{

    public RegraNegocioException(String email) {
        super(email);
    }

}
