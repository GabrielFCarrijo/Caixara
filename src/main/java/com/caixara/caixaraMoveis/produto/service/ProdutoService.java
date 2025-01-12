package com.caixara.caixaraMoveis.produto.service;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.produto.entity.Produto;
import com.caixara.caixaraMoveis.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto criarProduto(Produto produto) {
        validarProduto(produto);
        return produtoRepository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado com ID: " + id));
    }

    @Transactional
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        Produto produtoExistente = buscarPorId(id);

        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setDescricao(produtoAtualizado.getDescricao());
        produtoExistente.setPreco(produtoAtualizado.getPreco());
        produtoExistente.setCategoria(produtoAtualizado.getCategoria());
        produtoExistente.setQuantidade(produtoAtualizado.getQuantidade());
        validarProduto(produtoExistente);

        return produtoRepository.save(produtoExistente);
    }

    @Transactional
    public void reduzirEstoque(Long produtoId, int quantidade) {
        Produto produto = buscarPorId(produtoId);

        if (produto.getQuantidade() < quantidade) {
            throw new RegraNegocioException(
                    "Estoque insuficiente para o produto: " + produto.getNome() + ". Disponível: " + produto.getQuantidade());
        }

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        produtoRepository.save(produto);
    }

    @Transactional
    public void deletarProduto(Long id) {
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
    }

    public List<Produto> listarProdutos(String nome, String categoria, Double precoMinimo, Double precoMaximo) {
        return produtoRepository.listarComFiltros(nome, categoria, precoMinimo, precoMaximo);
    }

    private void validarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new RegraNegocioException("O nome do produto é obrigatório");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(0.0) <= 0) {
            throw new RegraNegocioException("O preço do produto deve ser maior que zero");
        }
        if (produto.getQuantidade() == null || produto.getQuantidade() < 0) {
            throw new RegraNegocioException("A quantidade do produto não pode ser negativa");
        }
    }
}
