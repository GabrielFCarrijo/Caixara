package com.caixara.caixaraMoveis.pedido.service;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.pedido.entity.ItemPedido;
import com.caixara.caixaraMoveis.pedido.entity.Pedido;
import com.caixara.caixaraMoveis.pedido.entity.enums.StatusPedido;
import com.caixara.caixaraMoveis.pedido.repository.PedidoRepository;
import com.caixara.caixaraMoveis.produto.entity.Produto;
import com.caixara.caixaraMoveis.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    public Pedido criarPedido(Pedido pedido) {
        calcularTotalPedido(pedido);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataCriacao(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado"));
    }

    @Transactional
    public Pedido atualizarPedido(Long id, Pedido pedidoAtualizado) {
        Pedido pedidoExistente = buscarPorId(id);

        pedidoExistente.setItens(pedidoAtualizado.getItens());
        calcularTotalPedido(pedidoExistente);
        pedidoExistente.setStatus(pedidoAtualizado.getStatus());
        return pedidoRepository.save(pedidoExistente);
    }


    public void deletarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        pedidoRepository.delete(pedido);
    }

    public List<Pedido> listarPedidos(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim, BigDecimal valorMinimo) {
        return pedidoRepository.listarComFiltros(status, dataInicio, dataFim, valorMinimo);
    }

    private void calcularTotalPedido(Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            pedido.setTotal(total);
            return;
        }

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RegraNegocioException("Produto não encontrado: " + item.getProduto().getId()));

            BigDecimal precoUnitario = BigDecimal.valueOf(produto.getPreco());
            BigDecimal quantidade = BigDecimal.valueOf(item.getQuantidade());

            item.setPrecoUnitario(precoUnitario);
            item.setPrecoTotal(precoUnitario.multiply(quantidade));
            total = total.add(item.getPrecoTotal());
        }
        pedido.setTotal(total);
    }


    public List<Pedido> gerarRelatorio(LocalDateTime dataInicio, LocalDateTime dataFim, StatusPedido status) {
        return pedidoRepository.listarPorPeriodoEStatus(dataInicio, dataFim, status);
    }
}
