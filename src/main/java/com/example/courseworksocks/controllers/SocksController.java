package com.example.courseworksocks.controllers;

import com.example.courseworksocks.model.Socks;
import com.example.courseworksocks.services.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "Добавление нового товара", description = "параметры товара: размер, цвет, состав, количество")
    public ResponseEntity<Socks> addNewSocks(@RequestBody Socks socks) {
        Socks socks1 = socksService.addNewSocks(socks);
        return new ResponseEntity<>(socks1, HttpStatus.CREATED);
    }

    @PutMapping("/move")
    @Operation(summary = " Перемещение товара на склад", description = "вводим необходимые данные: размер, цвет, состав, количество")
    public ResponseEntity<List<Socks>> moveSocks(@RequestParam(name = "Размер:") Integer size1,
                                                 @RequestParam(name = "Цвет") String colors1,
                                                 @RequestParam(name = "% хлопка") Integer cotton1,
                                                 @RequestParam(name = "Количество товара") Integer quantity1) {
        List<Socks> socksList = socksService.moveSocks(size1, colors1, cotton1, quantity1);
        return new ResponseEntity<>(socksList, HttpStatus.CREATED);
    }


    @GetMapping("/minCotton")
    @Operation(summary = "Поиск нужного товара", description = "вводим необходимые данные: размер, цвет, состав")
    public ResponseEntity<Object> getQuantitySocksMin(@RequestParam(name = "Размер: ") @Parameter(description = " от 36 до 40") Integer size,
                                                      @RequestParam(name = "Цвет:") @Parameter(description = "белый, черный, розовый, голубой, красный, разноцветный") String colors,
                                                      @RequestParam(name = "Содержание хлопка в % min") @Parameter(description = "от 0 до 100") Integer cotton) {
        List<Socks> list = socksService.getQuantitySocksMinCotton(size, colors, cotton);
        return new ResponseEntity<>(list, HttpStatus.CREATED);
    }

    @GetMapping("/maxCotton")
    @Operation(summary = "Поиск нужного товара", description = "вводим необходимые данные: размер, цвет, состав")
    public ResponseEntity<Object> getQuantitySocksMax(@RequestParam(name = "Размер: ") @Parameter(description = " от 36 до 40") Integer size,
                                                      @RequestParam(name = "Цвет:") @Parameter(description = "белый, черный, розовый, голубой, красный, разноцветный") String colors,
                                                      @RequestParam(name = "Содержание хлопка в % max") @Parameter(description = "от 0 до 100") Integer cotton) {
        List<Socks> list1 = socksService.getQuantitySocksMaxCotton(size, colors, cotton);
        return new ResponseEntity<>(list1, HttpStatus.CREATED);
    }

    @GetMapping("/totalQuantity")
    @Operation(summary = "Общее количество нужного товара", description = "вводим необходимые данные: размер, цвет, состав")
    public ResponseEntity<Object> getQuantityTotal(@RequestParam(name = "Размер: ") @Parameter(description = " от 36 до 40") Integer size,
                                                   @RequestParam(name = "Цвет:") @Parameter(description = "белый, черный, розовый, голубой, красный, разноцветный") String colors,
                                                   @RequestParam(name = "Содержание хлопка в % min") @Parameter(description = "от 0 до 100") Integer cotton) {
        Integer total = socksService.getQuantitySocksSize(size, colors, cotton);
        return new ResponseEntity<>(total, HttpStatus.CREATED);
    }

    @DeleteMapping("/faulty")
    @Operation(summary = "Удаление бракованного товара", description = "вводим необходимые данные: размер, цвет, состав, количество")
    public ResponseEntity<List<Socks>> deleteSocks(@RequestParam(name = "Размер:") Integer size1,
                                                   @RequestParam(name = "Цвет") String colors1,
                                                   @RequestParam(name = "% хлопка") Integer cotton1,
                                                   @RequestParam(name = "Количество товара") Integer quantity1) {
        List<Socks> socksList = socksService.deleteSocks(size1, colors1, cotton1, quantity1);
        return new ResponseEntity<>(socksList, HttpStatus.CREATED);
    }

    @GetMapping("/checklist")
    @Operation(summary = "Итоговый список товара",
            description = "выводит сразу весь товар в наличии")
    public ResponseEntity<Object> getChecklistSocks() throws Exception {
        Collection<Socks> socks = socksService.getChecklistSocks();
        return new ResponseEntity<>(socks, HttpStatus.CREATED);
    }


    @GetMapping("/catalog")
    @Operation(summary = "получение каталога носков", description = "без параметров запроса")
    public ResponseEntity<Object> getRecipesBook() {
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





