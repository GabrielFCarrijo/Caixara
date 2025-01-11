package com.caixara.caixaraMoveis.pagamento.repository;


import com.caixara.caixaraMoveis.pagamento.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
