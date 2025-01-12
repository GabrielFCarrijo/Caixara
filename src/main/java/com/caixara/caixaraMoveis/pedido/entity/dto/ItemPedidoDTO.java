package com.caixara.caixaraMoveis.pedido.entity.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class ItemPedidoDTO {
    private Long produtoId;
    private Integer quantidade;
    private Double precoUnitario;
    private Double precoTotal;

    public ItemPedidoDTO(Long produtoId, Integer quantidade, Double valorUnitario, Double valorTotal) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = valorUnitario;
        this.precoTotal = valorTotal;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(Double precoTotal) {
        this.precoTotal = precoTotal;
    }
}
