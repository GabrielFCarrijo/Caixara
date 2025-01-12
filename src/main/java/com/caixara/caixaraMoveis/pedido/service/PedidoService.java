package com.caixara.caixaraMoveis.pedido.service;

import com.caixara.caixaraMoveis.exception.RegraNegocioException;
import com.caixara.caixaraMoveis.pagamento.entity.Pagamento;
import com.caixara.caixaraMoveis.pagamento.entity.enums.TipoPagamento;
import com.caixara.caixaraMoveis.pagamento.service.PagamentoService;
import com.caixara.caixaraMoveis.pagamento.service.ProcessamentoDePagamentoService;
import com.caixara.caixaraMoveis.pedido.entity.ItemPedido;
import com.caixara.caixaraMoveis.pedido.entity.Pedido;
import com.caixara.caixaraMoveis.pedido.entity.dto.ItemPedidoDTO;
import com.caixara.caixaraMoveis.pedido.entity.enums.StatusPedido;
import com.caixara.caixaraMoveis.pedido.repository.PedidoRepository;
import com.caixara.caixaraMoveis.produto.entity.Produto;
import com.caixara.caixaraMoveis.produto.repository.ProdutoRepository;
import com.caixara.caixaraMoveis.produto.service.ProdutoService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {


    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoService produtoService;

    @Lazy
    private final ProcessamentoDePagamentoService processamentoDePagamentoService;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, ProdutoService produtoService, PagamentoService pagamentoService, ProcessamentoDePagamentoService processamentoDePagamentoService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.produtoService = produtoService;
        this.processamentoDePagamentoService = processamentoDePagamentoService;
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido, List<ItemPedidoDTO> itensDTO, TipoPagamento tipoPagamento, Double valorPagamento, Integer numeroParcelas) {
        List<ItemPedido> itens = itensDTO.stream().map(dto -> {
            Produto produto = produtoRepository.findById(dto.getProdutoId())
                    .orElseThrow(() -> new RegraNegocioException("Produto não encontrado com o ID: " + dto.getProdutoId()));

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(dto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setPrecoTotal(produto.getPreco() * dto.getQuantidade());
            item.setPedido(pedido);
            return item;
        }).toList();

        if (pedido.getItens() == null) {
            pedido.setItens(new ArrayList<>());
        }

        pedido.getItens().addAll(itens);

        double totalPedido = itens.stream().mapToDouble(ItemPedido::getPrecoTotal).sum();
        pedido.setTotal(totalPedido);

        if (pedido.getPagamentos() == null) {
            pedido.setPagamentos(new ArrayList<Pagamento>());
        }

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        if (valorPagamento > totalPedido) {
            throw new RegraNegocioException("O valor do pagamento não pode ser superior ao total do pedido");
        }

        if (numeroParcelas == null || numeroParcelas <= 0) {
            numeroParcelas = 1;
        }

        Double valorParcela = totalPedido / numeroParcelas;

        List<Pagamento> pagamentos = processamentoDePagamentoService.processarPagamento(pedidoSalvo.getId(), tipoPagamento, valorPagamento, numeroParcelas);

        for (Pagamento pagamento : pagamentos) {
            pagamento.setNumeroParcelas(numeroParcelas);
            pagamento.setValorParcela(valorParcela);

            pagamento.setPedido(pedidoSalvo);
            pedidoSalvo.getPagamentos().add(pagamento);
        }

        return pedidoSalvo;
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

    public static void geradorRelatorio(HttpServletResponse response, List<Pedido> relatorio) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Relatório de Pedidos");

        String[] headers = {"ID", "Cliente", "Status", "Data Criação", "Total"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int rowNum = 1;
        for (Pedido pedido : relatorio) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(pedido.getId());
            row.createCell(1).setCellValue(pedido.getClienteId());
            row.createCell(2).setCellValue(pedido.getStatus().toString());
            row.createCell(3).setCellValue(pedido.getDataCriacao().format(dateFormatter));
            row.createCell(4).setCellValue(pedido.getTotal().toString());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_pedidos.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
