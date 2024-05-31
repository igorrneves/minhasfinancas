package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.modelo.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Usuario autenticar(String email, String senha);
    Usuario salvarUsuario(Usuario usuario);
    void validarEmail(String email);
    Optional<Usuario> obterPorId(Long id);
}
