package com.example.courseworksocks.controllers;

import com.example.courseworksocks.model.Socks;
import com.example.courseworksocks.services.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.ServerException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/socks")
@Tag(name = "Носки", description = "CRUD - операции для работы с учетом носков")
public class SocksController {

    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    @GetMapping
    @Operation(summary = "Название главной страницы")
    public String Socks() {
        return "Интернет-магазин носков";
    }


    @PostMapping("/add")
    @Operation(summary = "Добавление нового товара", description = "каждой паре носков присваивается : id")
    public ResponseEntity<Socks> addNewSocks(@RequestBody Socks socks) throws Exception {
        Socks socks1 = socksService.addNewSocks(socks);
        if (socks1 == null) {
            throw new ServerException("Некорректные данные");
        } else {
            return new ResponseEntity<>(socks1, HttpStatus.CREATED);
        }
    }

    @GetMapping("/{size}")
    @Operation(summary = "Поиск нужного товара", description = "вводим необходимые данные: размер, цвет, состав")
    public ResponseEntity<Object> getQuantitySocks(@PathVariable @RequestParam(name = "Размер: ") Integer size,
                                                   @PathVariable @RequestParam(name = "Цвет:") String colors,
                                                   @PathVariable @RequestParam(name = "Содержание хлопка в %") Integer cotton) {
        List<Socks> list = socksService.getQuantitySocks(size, colors, cotton);
        if (list == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(list, HttpStatus.CREATED);
    }

    @GetMapping("/checklist")
    @Operation(summary = "Итоговый список товара",
            description = "выводит сразу весь товар в наличии")
    public ResponseEntity<Object> getChecklistSocks() throws Exception {
        Collection<Socks> socks = socksService.getChecklistSocks();
        if (socks == null) {
            throw new IllegalArgumentException("Данные отсутствуют");
        } else {
            return new ResponseEntity<>(socks, HttpStatus.CREATED);
        }
    }


    @GetMapping("/catalog")
    @Operation(summary = "получение каталога носков", description = "без параметров запроса")
    public ResponseEntity <Object> getRecipesBook () {
        try {
            Path path = socksService.createSocks();
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(path))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"-socks-catalog.txt\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }
}





