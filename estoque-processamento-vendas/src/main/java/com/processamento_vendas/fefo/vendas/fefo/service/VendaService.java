package com.processamento_vendas.fefo.vendas.fefo.service;

import com.processamento_vendas.fefo.vendas.fefo.model.RegistroVenda;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class VendaService {
    private final RestTemplate restTemplate = new RestTemplate();

    public void registrarVenda(String codigoProduto, int quantidade) {
        RegistroVenda registro = new RegistroVenda();
        registro.setCodigoProduto(codigoProduto);
        registro.setQuantidadeVendida(quantidade);
        registro.setDataVenda(LocalDate.now());

        String url = "http://gerador-relatorios:8082/api/vendas/registrar";        restTemplate.postForObject(url, registro, Void.class);
    }
}