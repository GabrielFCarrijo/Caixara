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

    @Query("select p from Produto p where " +
            "(:nome is null or lower(p.nome) like lower(concat('%', :nome, '%'))) and " +
            "(:categoria is null or p.categoria = :categoria) and " +
            "(:precoMinimo is null or p.preco >= :precoMinimo) and " +
            "(:precoMaximo is null or p.preco <= :precoMaximo)")
    List<Produto> listarComFiltros(@Param("nome") String nome,
                                   @Param("categoria") String categoria,
                                   @Param("precoMinimo") BigDecimal precoMinimo,
                                   @Param("precoMaximo") BigDecimal precoMaximo);
}
