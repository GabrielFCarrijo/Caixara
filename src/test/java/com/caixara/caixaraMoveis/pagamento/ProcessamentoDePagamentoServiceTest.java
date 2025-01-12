package com.caixara.caixaraMoveis.pagamento;

import com.caixara.caixaraMoveis.pagamento.entity.Pagamento;
import com.caixara.caixaraMoveis.pagamento.entity.enums.TipoPagamento;
import com.caixara.caixaraMoveis.pagamento.service.PagamentoService;
import com.caixara.caixaraMoveis.pagamento.service.ProcessamentoDePagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProcessamentoDePagamentoServiceTest {

    @InjectMocks
    private ProcessamentoDePagamentoService processamentoDePagamentoService;

    @Mock
    private PagamentoService pagamentoService;

    @BeforeEach
    void inicializacao() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processarPagamentoDeveDelegarParaPagamentoService() {
        Long pedidoId = 1L;
        TipoPagamento tipoPagamento = TipoPagamento.CARTAO_CREDITO;
        Double valorPagamento = 100.0;
        Integer numeroParcelas = 3;

        Pagamento pagamentoMock = new Pagamento();
        pagamentoMock.setValor(33.33);
        List<Pagamento> pagamentoEsperado = Collections.singletonList(pagamentoMock);

        when(pagamentoService.realizarPagamento(eq(pedidoId), eq(tipoPagamento), eq(valorPagamento), eq(numeroParcelas)))
                .thenReturn(pagamentoEsperado);

        List<Pagamento> resultado = processamentoDePagamentoService.processarPagamento(pedidoId, tipoPagamento, valorPagamento, numeroParcelas);

        verify(pagamentoService, times(1)).realizarPagamento(pedidoId, tipoPagamento, valorPagamento, numeroParcelas);
        assertEquals(pagamentoEsperado, resultado);
    }
}
