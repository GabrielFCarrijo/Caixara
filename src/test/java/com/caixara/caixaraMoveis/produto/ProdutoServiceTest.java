package com.caixara.caixaraMoveis.produto;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.produto.entity.Produto;
import com.caixara.caixaraMoveis.produto.repository.ProdutoRepository;
import com.caixara.caixaraMoveis.produto.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void criaProdutoPadrao() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Mesa");
        produto.setDescricao("Mesa de madeira");
        produto.setPreco(100.00);
        produto.setCategoria("TAUBUA");
        produto.setQuantidade(10);
    }

    @Test
    void deveCriarProdutoComSucesso() {
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto criado = produtoService.criarProduto(produto);

        assertNotNull(criado);
        assertEquals(produto.getNome(), criado.getNome());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveLancarErroAoCriarProdutoComNomeVazio() {
        produto.setNome("");

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            produtoService.criarProduto(produto);
        });

        assertEquals("O nome do produto é obrigatório", exception.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        Produto encontrado = produtoService.buscarPorId(produto.getId());

        assertNotNull(encontrado);
        assertEquals(produto.getId(), encontrado.getId());
        verify(produtoRepository, times(1)).findById(produto.getId());
    }

    @Test
    void deveLancarErroAoBuscarProdutoPorIdNaoExistente() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            produtoService.buscarPorId(produto.getId());
        });

        assertEquals("Produto não encontrado com ID: " + produto.getId(), exception.getMessage());
        verify(produtoRepository, times(1)).findById(produto.getId());
    }

    @Test
    void deveAtualizarProdutoComSucesso() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        Produto atualizado = produtoService.atualizarProduto(produto.getId(), produto);

        assertNotNull(atualizado);
        assertEquals(produto.getNome(), atualizado.getNome());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveLancarErroAoAtualizarProdutoNaoExistente() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            produtoService.atualizarProduto(produto.getId(), produto);
        });

        assertEquals("Produto não encontrado com ID: " + produto.getId(), exception.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deveReduzirEstoqueComSucesso() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        produtoService.reduzirEstoque(produto.getId(), 5);

        assertEquals(5, produto.getQuantidade());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveLancarErroQuandoEstoqueInsuficiente() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            produtoService.reduzirEstoque(produto.getId(), 15);
        });

        assertEquals("Estoque insuficiente para o produto: Mesa. Disponível: 10", exception.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deveDeletarProdutoComSucesso() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        produtoService.deletarProduto(produto.getId());

        verify(produtoRepository, times(1)).delete(produto);
    }

    @Test
    void deveLancarErroAoDeletarProdutoNaoExistente() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            produtoService.deletarProduto(produto.getId());
        });

        assertEquals("Produto não encontrado com ID: " + produto.getId(), exception.getMessage());
        verify(produtoRepository, never()).delete(any());
    }

    @Test
    void deveListarProdutosComFiltrosComSucesso() {
        List<Produto> produtos = List.of(produto);
        when(produtoRepository.listarComFiltros("Mesa", "Móveis", 50.0, 150.00))
                .thenReturn(produtos);

        List<Produto> resultado = produtoService.listarProdutos("Mesa", "Móveis", 50.0, 150.00);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(produto.getNome(), resultado.get(0).getNome());
        verify(produtoRepository, times(1))
                .listarComFiltros("Mesa", "Móveis",50.0, 150.00);
    }

}
