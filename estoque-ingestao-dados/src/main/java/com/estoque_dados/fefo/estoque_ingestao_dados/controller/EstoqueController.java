package com.estoque_dados.fefo.estoque_ingestao_dados.controller;

import com.estoque_dados.fefo.estoque_ingestao_dados.model.Produto;
import com.estoque_dados.fefo.estoque_ingestao_dados.repository.ProdutoRepository;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProdutoRepository produtoRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPlanilha(@RequestParam("file") MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Por favor, selecione um arquivo.");
        }

        try (InputStream inputStream = arquivo.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            List<Produto> produtos = new ArrayList<>();
            // Pular o cabeçalho
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Produto produto = new Produto();
                produto.setCodigoProduto(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                produto.setQuantidade((int) row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());

                // Assegura que a data é tratada como um tipo de data
                if (row.getCell(2) != null && row.getCell(2).getCellType() == CellType.NUMERIC) {
                    produto.setDataCompra(row.getCell(2).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } else {
                    continue;
                }

                produto.setLocal(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                produto.setLote(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                produto.setSku(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());

                produtos.add(produto);
            }

            produtoRepository.saveAll(produtos);
            for (Produto produto : produtos) {
                String mensagem = produto.getCodigoProduto() + "," + produto.getQuantidade();
                rabbitTemplate.convertAndSend("fila.vendas", mensagem);
            }

            return ResponseEntity.ok("Planilha processada e dados salvos com sucesso!");


        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a planilha: " + e.getMessage());
        }
    }
}