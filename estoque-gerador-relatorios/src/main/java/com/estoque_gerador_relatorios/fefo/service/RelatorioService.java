package com.estoque_gerador_relatorios.fefo.service;

import com.estoque_gerador_relatorios.fefo.model.RegistroVenda;
import com.estoque_gerador_relatorios.fefo.repository.RegistroVendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private RegistroVendaRepository registroVendaRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(cron = "0 0 23 * * *")
    public void gerarEEnviarRelatorioDiario() {
        System.out.println("Iniciando a geração do relatório diário...");

        LocalDate hoje = LocalDate.now();
        List<RegistroVenda> vendasDoDia = registroVendaRepository.findByDataVenda(hoje);

        String relatorio = "Relatório de Vendas Diário - " + hoje + "\n\n";
        if (vendasDoDia.isEmpty()) {
            relatorio += "Nenhuma venda registrada hoje.";
        } else {
            for (RegistroVenda venda : vendasDoDia) {
                relatorio += String.format("Produto: %s, Quantidade: %d\n", venda.getCodigoProduto(), venda.getQuantidadeVendida());
            }
        }

        enviarEmail("lucasalvesbh2017@gmail.com", "Relatório de Vendas Diário", relatorio);
        System.out.println("Relatório gerado e enviado com sucesso!");
    }

    private void enviarEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}