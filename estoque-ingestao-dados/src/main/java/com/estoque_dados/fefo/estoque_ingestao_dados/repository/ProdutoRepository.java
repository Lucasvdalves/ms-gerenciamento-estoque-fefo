package com.estoque_dados.fefo.estoque_ingestao_dados.repository;

import com.estoque_dados.fefo.estoque_ingestao_dados.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
