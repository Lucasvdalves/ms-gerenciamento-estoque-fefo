package com.processamento_vendas.fefo.vendas.fefo.listener;

import com.processamento_vendas.fefo.vendas.fefo.model.Estoque;
import com.processamento_vendas.fefo.vendas.fefo.repository.EstoqueRepository;
import com.processamento_vendas.fefo.vendas.fefo.service.VendaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VendaListener {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private VendaService vendaService;


    @RabbitListener(queues = "fila.vendas")
    public void processarVenda(String mensagem) {

        String[] dadosVenda = mensagem.split(",");
        String codigoProduto = dadosVenda[0];
        int quantidadeVendida = Integer.parseInt(dadosVenda[1]);

        System.out.println("Processando venda para o produto: " + codigoProduto + " - Quantidade: " + quantidadeVendida);

        List<Estoque> produtosEmEstoque = estoqueRepository.findByCodigoProdutoOrderByDataValidadeAsc(codigoProduto);

        for (Estoque estoque : produtosEmEstoque) {
            if (quantidadeVendida <= 0) {
                break;
            }

            if (estoque.getQuantidade() >= quantidadeVendida) {
                // Tem quantidade suficiente neste lote, vamos dar baixa
                estoque.setQuantidade(estoque.getQuantidade() - quantidadeVendida);
                estoqueRepository.save(estoque);
                quantidadeVendida = 0;
            } else {
                // Não tem quantidade suficiente, vamos zerar o lote e continuar
                quantidadeVendida -= estoque.getQuantidade();
                estoque.setQuantidade(0);
                estoqueRepository.save(estoque);
            }
        }

    }
    public void fallbackFEFO(String codigoProduto, int quantidadeVendida, Throwable t) {
        System.err.println("Circuit Breaker aberto! A chamada para a lógica FEFO falhou. Motivo: " + t.getMessage());
    }
}
