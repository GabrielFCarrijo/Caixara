package com.caixara.caixaraMoveis.pedido.repository;

import com.caixara.caixaraMoveis.pedido.entity.Pedido;
import com.caixara.caixaraMoveis.pedido.entity.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("select p from Pedido p where p.total >= :minimo and p.total <= :maximo")
    List<Pedido> findByValorBetween(@Param("minimo") BigDecimal minimo, @Param("maximo") BigDecimal maximo);

    @Query("select p from Pedido p where p.status = :status and p.dataCriacao >= :dataInicio and p.dataCriacao <= :dataFim and p.total >= :valorMinimo")
    List<Pedido> listarComFiltros(@Param("status") StatusPedido status,
                                  @Param("dataInicio") LocalDateTime dataInicio,
                                  @Param("dataFim") LocalDateTime dataFim,
                                  @Param("valorMinimo") BigDecimal valorMinimo);

    @Query("select p from Pedido p where " +
            "p.dataCriacao between :dataInicio and :dataFim and " +
            "(:status is null or p.status = :status)")
    List<Pedido> listarPorPeriodoEStatus(@Param("dataInicio") LocalDateTime dataInicio,
                                         @Param("dataFim") LocalDateTime dataFim,
                                         @Param("status") StatusPedido status);
}
