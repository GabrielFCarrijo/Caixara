package com.caixara.caixaraMoveis.usuario.service;


import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.usuario.entity.Usuario;
import com.caixara.caixaraMoveis.usuario.entity.gerenciametoAcesso.Role;
import com.caixara.caixaraMoveis.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criarUsuario(Usuario usuario) {
        validarDuplicidadeUsername(usuario.getUsername());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        validarCriacaoDeUsuarioPorRole(usuario);
        return usuarioRepository.save(usuario);
    }

    private static void validarCriacaoDeUsuarioPorRole(Usuario usuario) {
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            usuario.getRoles().add(Role.CLIENTE);
        }
    }

    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorId(id);
        usuarioExistente.setUsername(usuarioAtualizado.getUsername());
        if (usuarioAtualizado.getPassword() != null) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioAtualizado.getPassword()));
        }
        if (usuarioAtualizado.getRoles() != null && !usuarioAtualizado.getRoles().isEmpty()) {
            usuarioExistente.setRoles(usuarioAtualizado.getRoles());
        }
        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    private void validarDuplicidadeUsername(String username) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(username);
        if (usuarioExistente.isPresent()) {
            throw new RegraNegocioException("Nome de usuário já cadastrado");
        }
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));
    }

}