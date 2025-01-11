package com.caixara.caixaraMoveis.pedido.service;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.pedido.entity.ItemPedido;
import com.caixara.caixaraMoveis.pedido.entity.Pedido;
import com.caixara.caixaraMoveis.pedido.entity.dto.ItemPedidoDTO;
import com.caixara.caixaraMoveis.pedido.entity.enums.StatusPedido;
import com.caixara.caixaraMoveis.pedido.repository.PedidoRepository;
import com.caixara.caixaraMoveis.produto.entity.Produto;
import com.caixara.caixaraMoveis.produto.repository.ProdutoRepository;
import com.caixara.caixaraMoveis.produto.service.ProdutoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoService produtoService;
    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, ProdutoService produtoService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.produtoService = produtoService;
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido, List<ItemPedidoDTO> itensDTO) {
        List<ItemPedido> itens = itensDTO.stream().map(dto -> {
            Produto produto = produtoRepository.findById(dto.getProdutoId())
                    .orElseThrow(() -> new RegraNegocioException("Produto não encontrado com o ID: " + dto.getProdutoId()));

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(dto.getQuantidade());

            Double precoUnitario = produto.getPreco();
            item.setPrecoUnitario(precoUnitario);

            Double precoTotal = precoUnitario * dto.getQuantidade();
            item.setPrecoTotal(precoTotal);

            item.setPedido(pedido);
            return item;
        }).toList();

        pedido.setItens(itens);
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

    public List<Pedido> listarPedidos(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim, Double valorMinimo) {
        return pedidoRepository.listarComFiltros(status, dataInicio, dataFim, valorMinimo);
    }

    private void calcularTotalPedido(Pedido pedido) {
        Double total = 0.0;

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            pedido.setTotal(total);
            return;
        }

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoService.buscarPorId(item.getProduto().getId());

            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new RegraNegocioException(
                        "Estoque insuficiente para o produto: " + produto.getNome() + ". Disponível: " + produto.getQuantidade());
            }

            produtoService.reduzirEstoque(produto.getId(), item.getQuantidade());

            Double precoUnitario = produto.getPreco();
            Double precoTotal = precoUnitario * item.getQuantidade();
            item.setPrecoUnitario(precoUnitario);
            item.setPrecoTotal(precoTotal);

            total += precoTotal;
        }

        pedido.setTotal(total);
    }

    public List<Pedido> gerarRelatorio(LocalDateTime dataInicio, LocalDateTime dataFim, StatusPedido status) {
        return pedidoRepository.listarPorPeriodoEStatus(dataInicio, dataFim, status);
    }
}
