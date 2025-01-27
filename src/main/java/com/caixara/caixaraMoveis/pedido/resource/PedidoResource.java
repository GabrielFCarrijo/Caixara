package com.caixara.caixaraMoveis.pedido.resource;

import com.caixara.caixaraMoveis.pagamento.entity.enums.TipoPagamento;
import com.caixara.caixaraMoveis.pedido.entity.Pedido;
import com.caixara.caixaraMoveis.pedido.entity.dto.PedidoDTO;
import com.caixara.caixaraMoveis.pedido.entity.enums.StatusPedido;
import com.caixara.caixaraMoveis.pedido.service.PedidoService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoResource {

    private final PedidoService pedidoService;

    public PedidoResource(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(pedidoDTO.getClienteId());
        pedido.setStatus(StatusPedido.valueOf(pedidoDTO.getStatus()));
        pedido.setDataCriacao(pedidoDTO.getDataCriacao());

        Pedido novoPedido = pedidoService.criarPedido(pedido, pedidoDTO.getItens(), pedidoDTO.getTipoPagamento(), pedidoDTO.getValorPagamento(), pedidoDTO.getNumeroParcelas());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos(
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) LocalDateTime inicio,
            @RequestParam(required = false) LocalDateTime fim,
            @RequestParam(required = false) Double minimo) {
        List<Pedido> pedidos = pedidoService.listarPedidos(status, inicio, fim, minimo);
        return ResponseEntity.ok(pedidos);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable Long id, @RequestBody Pedido pedidoAtualizado) {
        Pedido pedidoAtualizadoResponse = pedidoService.atualizarPedido(id, pedidoAtualizado);
        return ResponseEntity.ok(pedidoAtualizadoResponse);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPedido(@PathVariable Long id) {
        pedidoService.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorio")
    public void gerarRelatorio(
            @RequestParam LocalDateTime dataInicio,
            @RequestParam LocalDateTime dataFim,
            @RequestParam(required = false) StatusPedido status,
            HttpServletResponse response) throws IOException {

        List<Pedido> relatorio = pedidoService.gerarRelatorio(dataInicio, dataFim, status);

        PedidoService.geradorRelatorio(response, relatorio);
    }


}

