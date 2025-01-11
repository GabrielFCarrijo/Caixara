package com.caixara.caixaraMoveis.usuario;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.usuario.entity.Usuario;
import com.caixara.caixaraMoveis.usuario.entity.gerenciametoAcesso.Role;
import com.caixara.caixaraMoveis.usuario.repository.UsuarioRepository;
import com.caixara.caixaraMoveis.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void criaUsuarioPadrao() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testUser");
        usuario.setPassword("password123");
        usuario.setRoles(Collections.singleton(Role.CLIENTE));
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuario.getPassword())).thenReturn("encodedPassword");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario criado = usuarioService.criarUsuario(usuario);

        assertNotNull(criado);
        assertEquals(usuario.getUsername(), criado.getUsername());
        assertEquals("encodedPassword", criado.getPassword());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void deveLancarErroAoCriarUsuarioComUsernameDuplicado() {
        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            usuarioService.criarUsuario(usuario);
        });

        assertEquals("Nome de usuário já cadastrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        usuario.setPassword("newPassword");

        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        Usuario atualizado = usuarioService.atualizarUsuario(usuario.getId(), usuario);

        assertNotNull(atualizado);
        assertEquals("encodedNewPassword", atualizado.getPassword());
        verify(usuarioRepository, times(1)).save(usuario);
    }


    @Test
    void deveLancarErroAoAtualizarUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            usuarioService.atualizarUsuario(usuario.getId(), usuario);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        usuarioService.deletarUsuario(usuario.getId());

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    void deveLancarErroAoDeletarUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            usuarioService.deletarUsuario(usuario.getId());
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, never()).delete(any());
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        Usuario encontrado = usuarioService.buscarPorId(usuario.getId());

        assertNotNull(encontrado);
        assertEquals(usuario.getId(), encontrado.getId());
    }

    @Test
    void deveLancarErroAoBuscarUsuarioPorIdNaoExistente() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            usuarioService.buscarPorId(usuario.getId());
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void deveListarUsuariosComSucesso() {
        List<Usuario> usuarios = List.of(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuario.getUsername(), resultado.get(0).getUsername());
        verify(usuarioRepository, times(1)).findAll();
    }


    @Test
    void deveBuscarUsuarioPorUsernameComSucesso() {
        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));

        Usuario encontrado = usuarioService.buscarPorUsername(usuario.getUsername());

        assertNotNull(encontrado);
        assertEquals(usuario.getUsername(), encontrado.getUsername());
        verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
    }

    @Test
    void deveLancarErroAoBuscarUsuarioPorUsernameNaoExistente() {
        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            usuarioService.buscarPorUsername(usuario.getUsername());
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
    }


}
