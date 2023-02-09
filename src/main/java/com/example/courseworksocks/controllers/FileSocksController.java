package com.example.courseworksocks.controllers;

import com.example.courseworksocks.services.FileSocksService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@RestController
@RequestMapping("/fileSocks")
@Tag(name = "Файл список товара", description = "Готовые файлы для скачивания")

public class FileSocksController {

    public class FilesSocksController {
        private final FileSocksService fileSocksService;

        public FilesSocksController(FileSocksService fileSocksService) {
            this.fileSocksService = fileSocksService;
        }


        @GetMapping("/export")
        public ResponseEntity<InputStreamResource> downloadDataFile() throws FileNotFoundException {
            File file = fileSocksService.getDataFile();

            if (file.exists()) {
                InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentLength(file.length())
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"SocksList.json\"")
                        .body(inputStreamResource);
            } else {

                return ResponseEntity.noContent().build();
            }

        }

        @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Void> uploadDataFile(@RequestParam MultipartFile file) {
            fileSocksService.cleanDataFile();
            File dataFile = fileSocksService.getDataFile();
            try (FileOutputStream fos = new FileOutputStream(dataFile)) {
                IOUtils.copy(file.getInputStream(), fos);

                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
