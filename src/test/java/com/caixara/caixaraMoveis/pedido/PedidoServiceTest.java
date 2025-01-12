package com.caixara.caixaraMoveis.pedido;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.pagamento.entity.Pagamento;
import com.caixara.caixaraMoveis.pagamento.entity.enums.TipoPagamento;
import com.caixara.caixaraMoveis.pagamento.service.ProcessamentoDePagamentoService;
import com.caixara.caixaraMoveis.pedido.entity.ItemPedido;
import com.caixara.caixaraMoveis.pedido.entity.Pedido;
import com.caixara.caixaraMoveis.pedido.entity.dto.ItemPedidoDTO;
import com.caixara.caixaraMoveis.pedido.entity.enums.StatusPedido;
import com.caixara.caixaraMoveis.pedido.repository.PedidoRepository;
import com.caixara.caixaraMoveis.pedido.service.PedidoService;
import com.caixara.caixaraMoveis.produto.entity.Produto;
import com.caixara.caixaraMoveis.produto.repository.ProdutoRepository;
import com.caixara.caixaraMoveis.produto.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private ProcessamentoDePagamentoService processamentoDePagamentoService;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido;
    private Produto produto;
    private ItemPedidoDTO itemPedidoDTO;

    @BeforeEach
    void criaPedido() {
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setClienteId(123L);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Cadeira");
        produto.setPreco(100.0);
        produto.setQuantidade(10);

        itemPedidoDTO = new ItemPedidoDTO();
        itemPedidoDTO.setProdutoId(produto.getId());
        itemPedidoDTO.setQuantidade(2);
    }

    @Test
    void criarPedidoDeveCriarPedidoComSucesso() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(processamentoDePagamentoService.processarPagamento(anyLong(), any(TipoPagamento.class), anyDouble(), anyInt()))
                .thenReturn(List.of(new Pagamento()));

        Pedido pedidoCriado = pedidoService.criarPedido(
                pedido, List.of(itemPedidoDTO), TipoPagamento.CARTAO_CREDITO, 200.0, 2
        );

        assertNotNull(pedidoCriado);
        assertEquals(1, pedidoCriado.getItens().size());
        assertEquals(200.0, pedidoCriado.getTotal());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void criarPedidoDeveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            pedidoService.criarPedido(
                    pedido, List.of(itemPedidoDTO), TipoPagamento.CARTAO_CREDITO, 200.0, 2
            );
        });

        assertEquals("Produto não encontrado com o ID: 1", exception.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void criarPedidoDeveLancarExcecaoQuandoValorPagamentoMaiorQueTotal() {
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            pedidoService.criarPedido(
                    pedido, List.of(itemPedidoDTO), TipoPagamento.CARTAO_CREDITO, 300.0, 2
            );
        });

        assertEquals("O valor do pagamento não pode ser superior ao total do pedido", exception.getMessage());
    }

    @Test
    void buscarPorIdDeveRetornarPedidoQuandoEncontrado() {
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        Pedido pedidoEncontrado = pedidoService.buscarPorId(pedido.getId());

        assertNotNull(pedidoEncontrado);
        assertEquals(123L, pedidoEncontrado.getClienteId());
        verify(pedidoRepository, times(1)).findById(pedido.getId());
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoEncontrado() {
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            pedidoService.buscarPorId(pedido.getId());
        });

        assertEquals("Pedido não encontrado", exception.getMessage());
        verify(pedidoRepository, times(1)).findById(pedido.getId());
    }

    @Test
    void deveDeletarPedidoComSucesso() {
        Long pedidoId = 1L;
        Pedido pedido = criarPedidoExistente();

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        pedidoService.deletarPedido(pedidoId);

        verify(pedidoRepository).delete(pedido);
    }

    @Test
    void deveListarPedidosComFiltros() {
        StatusPedido status = StatusPedido.CONCLUIDO;
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(10);
        LocalDateTime dataFim = LocalDateTime.now();
        Double valorMinimo = 100.0;

        List<Pedido> pedidosEsperados = List.of(criarPedidoExistente());

        when(pedidoRepository.listarComFiltros(status, dataInicio, dataFim, valorMinimo)).thenReturn(pedidosEsperados);

        List<Pedido> resultado = pedidoService.listarPedidos(status, dataInicio, dataFim, valorMinimo);

        assertEquals(pedidosEsperados, resultado);
        verify(pedidoRepository).listarComFiltros(status, dataInicio, dataFim, valorMinimo);
    }

    @Test
    void deveGerarRelatorioComSucesso() {
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
        LocalDateTime dataFim = LocalDateTime.now();
        StatusPedido status = StatusPedido.PENDENTE;

        List<Pedido> pedidosEsperados = List.of(criarPedidoExistente());

        when(pedidoRepository.listarPorPeriodoEStatus(dataInicio, dataFim, status)).thenReturn(pedidosEsperados);

        List<Pedido> resultado = pedidoService.gerarRelatorio(dataInicio, dataFim, status);

        assertEquals(pedidosEsperados, resultado);
        verify(pedidoRepository).listarPorPeriodoEStatus(dataInicio, dataFim, status);
    }


    private Pedido criarPedidoExistente() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setItens(List.of(criarItemPedido()));
        pedido.setTotal(100.0);
        pedido.setStatus(StatusPedido.CONCLUIDO);
        return pedido;
    }

    private Pedido criarPedidoAtualizado() {
        Pedido pedido = new Pedido();
        pedido.setItens(List.of(criarItemPedido()));
        pedido.setStatus(StatusPedido.PENDENTE);
        return pedido;
    }

    private ItemPedido criarItemPedido() {
        ItemPedido item = new ItemPedido();
        item.setProduto(criarProduto());
        item.setQuantidade(2);
        return item;
    }

    private Produto criarProduto() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(50.0);
        produto.setQuantidade(10);
        return produto;
    }


}