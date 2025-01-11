package com.caixara.caixaraMoveis.pagamento.service;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.pagamento.entity.Pagamento;
import com.caixara.caixaraMoveis.pagamento.entity.enums.StatusPagamento;
import com.caixara.caixaraMoveis.pagamento.entity.enums.TipoPagamento;
import com.caixara.caixaraMoveis.pagamento.repository.PagamentoRepository;
import com.caixara.caixaraMoveis.pedido.entity.Pedido;
import com.caixara.caixaraMoveis.pedido.repository.PedidoRepository;
import com.caixara.caixaraMoveis.pedido.service.PedidoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public List<Pagamento> realizarPagamento(Long pedidoId, TipoPagamento tipoPagamento, Double valorPagamento, Integer numeroParcelas) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado"));

        if (tipoPagamento == TipoPagamento.CARTAO_CREDITO && (numeroParcelas == null || numeroParcelas <= 0)) {
            throw new RegraNegocioException("Para pagamento com Carto de Crédito, o número de parcelas deve ser informado");
        }

        if (valorPagamento < pedido.getTotal()) {
            if (numeroParcelas == null || numeroParcelas <= 0) {
                throw new RegraNegocioException("Se o valor for menor que o total, o número de parcelas deve ser informado");
            }

            Double valorParcela = valorPagamento / numeroParcelas;

            List<Pagamento> pagamentos = new ArrayList<>();
            for (int i = 0; i < numeroParcelas; i++) {
                Pagamento pagamentoParcela = new Pagamento();
                pagamentoParcela.setPedido(pedido);
                pagamentoParcela.setTipoPagamento(tipoPagamento);
                pagamentoParcela.setValor(valorParcela);
                pagamentoParcela.setDataPagamento(LocalDateTime.now().plusMonths(i));
                pagamentoParcela.setStatusPagamento(StatusPagamento.PENDENTE);
                pagamentoParcela.setNumeroParcelas(numeroParcelas);
                pagamentoParcela.setValorParcela(valorParcela);
                pagamentos.add(pagamentoParcela);
            }

            pagamentoRepository.saveAll(pagamentos);
            return pagamentos;
        } else {
            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setTipoPagamento(tipoPagamento);
            pagamento.setValor(valorPagamento);
            pagamento.setDataPagamento(LocalDateTime.now());
            pagamento.setStatusPagamento(StatusPagamento.APROVADO);
            pagamentoRepository.save(pagamento);
            return List.of(pagamento);
        }
    }
}
