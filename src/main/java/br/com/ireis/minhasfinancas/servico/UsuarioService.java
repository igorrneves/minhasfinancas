package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.modelo.Usuario;

public interface UsuarioService {
    Usuario autenticar(String email, String senha);
    Usuario salvarUsuario(Usuario usuario);
    void validarEmail(String email);
}
