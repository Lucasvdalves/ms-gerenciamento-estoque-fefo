package com.estoque_gerador_relatorios.fefo.controller;

import com.estoque_gerador_relatorios.fefo.model.RegistroVenda;
import com.estoque_gerador_relatorios.fefo.repository.RegistroVendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    @Autowired
    private RegistroVendaRepository registroVendaRepository;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarVenda(@RequestBody RegistroVenda registroVenda) {
        registroVendaRepository.save(registroVenda);
        return ResponseEntity.ok("Registro de venda salvo com sucesso!");
    }
}