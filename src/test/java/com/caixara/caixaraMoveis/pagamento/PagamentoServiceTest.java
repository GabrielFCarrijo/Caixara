package com.caixara.caixaraMoveis.pagamento;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.pagamento.entity.Pagamento;
import com.caixara.caixaraMoveis.pagamento.entity.enums.StatusPagamento;
import com.caixara.caixaraMoveis.pagamento.entity.enums.TipoPagamento;
import com.caixara.caixaraMoveis.pagamento.repository.PagamentoRepository;
import com.caixara.caixaraMoveis.pagamento.service.PagamentoService;
import com.caixara.caixaraMoveis.pedido.entity.Pedido;
import com.caixara.caixaraMoveis.pedido.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    @BeforeEach
    void inicializacao() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void realizarPagamentoDeveRetornarListaDePagamentosQuandoParcelado() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setTotal(1000.0);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        List<Pagamento> pagamentos = pagamentoService.realizarPagamento(1L, TipoPagamento.CARTAO_CREDITO, 500.0, 2);

        assertNotNull(pagamentos);
        assertEquals(2, pagamentos.size());
        assertEquals(250.0, pagamentos.get(0).getValorParcela());
        assertEquals(StatusPagamento.PENDENTE, pagamentos.get(0).getStatusPagamento());
        verify(pagamentoRepository, times(1)).saveAll(anyList());
    }

    @Test
    void realizarPagamentoDeveRetornarPagamentoQuandoValorIgualAoTotal() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setTotal(1000.0);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        List<Pagamento> pagamentos = pagamentoService.realizarPagamento(1L, TipoPagamento.BOLETO, 1000.0, null);

        assertNotNull(pagamentos);
        assertEquals(1, pagamentos.size());
        assertEquals(1000.0, pagamentos.get(0).getValor());
        assertEquals(StatusPagamento.APROVADO, pagamentos.get(0).getStatusPagamento());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void realizarPagamentoDeveLancarExcecaoQuandoPedidoNaoEncontrado() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () ->
                pagamentoService.realizarPagamento(1L, TipoPagamento.CARTAO_CREDITO, 1000.0, 1));

        assertEquals("Pedido não encontrado", exception.getMessage());
    }

    @Test
    void realizarPagamentoDeveLancarExcecaoQuandoParcelasInvalidasParaCartao() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setTotal(1000.0);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () ->
                pagamentoService.realizarPagamento(1L, TipoPagamento.CARTAO_CREDITO, 500.0, null));

        assertEquals("Para pagamento com Carto de Crédito, o número de parcelas deve ser informado", exception.getMessage());
    }

    @Test
    void realizarPagamentoDeveLancarExcecaoQuandoValorInferiorSemParcelas() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setTotal(1000.0);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () ->
                pagamentoService.realizarPagamento(1L, TipoPagamento.CARTAO_CREDITO, 500.0, 0));

        assertEquals("Para pagamento com Carto de Crédito, o número de parcelas deve ser informado", exception.getMessage());
    }
}
