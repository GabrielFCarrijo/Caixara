package com.caixara.caixaraMoveis.security;

import com.caixara.caixaraMoveis.usuario.entity.Usuario;
import com.caixara.caixaraMoveis.usuario.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AcessoUsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AcessoUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();
    }
}
