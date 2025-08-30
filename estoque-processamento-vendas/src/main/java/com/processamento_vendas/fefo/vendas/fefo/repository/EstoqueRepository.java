package com.processamento_vendas.fefo.vendas.fefo.repository;

import com.processamento_vendas.fefo.vendas.fefo.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    List<Estoque> findByCodigoProdutoOrderByDataValidadeAsc(String codigoProduto);


}

