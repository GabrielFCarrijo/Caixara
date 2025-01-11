package com.caixara.caixaraMoveis.pagamento.service;

import com.caixara.caixaraMoveis.pagamento.entity.Pagamento;
import com.caixara.caixaraMoveis.pagamento.entity.enums.TipoPagamento;
import com.caixara.caixaraMoveis.pedido.service.PedidoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessamentoDePagamentoService {

    private final PagamentoService pagamentoService;

    public ProcessamentoDePagamentoService(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    public List<Pagamento> processarPagamento(Long pedidoId, TipoPagamento tipoPagamento, Double valorPagamento, Integer numeroParcelas) {
        return pagamentoService.realizarPagamento(pedidoId, tipoPagamento, valorPagamento, numeroParcelas);
    }
}
