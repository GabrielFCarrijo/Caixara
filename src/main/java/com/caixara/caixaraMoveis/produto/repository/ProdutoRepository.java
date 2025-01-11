package com.caixara.caixaraMoveis.produto.repository;


import com.caixara.caixaraMoveis.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByCategoria(String categoria);

    @Query("select p from Produto p where p.preco between :minPreco and :maxPreco")
    List<Produto> findByPrecoRange(@Param("minPreco") BigDecimal minPreco, @Param("maxPreco") BigDecimal maxPreco);
}
