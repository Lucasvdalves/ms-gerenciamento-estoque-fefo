package com.estoque_gerador_relatorios.fefo.repository;

import com.estoque_gerador_relatorios.fefo.model.RegistroVenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RegistroVendaRepository extends JpaRepository<RegistroVenda, Long> {
    List<RegistroVenda> findByDataVenda(LocalDate data);

}

