package com.example.courseworksocks.services.impl;

import com.example.courseworksocks.exception.FileProcessingException;
import com.example.courseworksocks.model.Socks;
import com.example.courseworksocks.services.FileSocksService;
import com.example.courseworksocks.services.SocksService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SocksServiceImpl implements SocksService {

    final private FileSocksService fileSocksService;
    private static Integer id=0;

    private static List<Socks> socksList = new LinkedList<>();

    Stream<Socks> stream = socksList.stream();

    public SocksServiceImpl(FileSocksService fileSocksService) {
        this.fileSocksService = fileSocksService;
    }

    @Override
    public Socks addNewSocks(Socks socks) {
        if (socks.getSize()!=null && socks.getColors()!=null && socks.getCotton() > 0) {
            socksList.add(socks);
            saveToFile();
        }
        return socks;
    }


    @Override
    public Collection<Socks> getChecklistSocks() {
        Collections.sort(socksList, Comparator.comparing(Socks::getSize));
        return socksList;

    }

    @PostConstruct
    private void init() {

//        try {
//            readFromFile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public List<Socks>  getQuantitySocks(Integer size, String colors, Integer cotton) {

        socksList.stream()
                .filter(socks -> socks.getSize().size.equals(size) && socks.getCotton()>=cotton)
                .filter(socks -> socks.getColors().colors.equals(colors))
                .toList();
        return socksList.stream().toList();

    }
    @Override
    public Path createSocks() throws IOException {

        Path path = fileSocksService.createTempFile("recipesBook");
        for (Socks socks : socksList) {
            try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append("\nРазмер " + "'" + socks.getSize() + "'" + ",\tцвет - " + socks.getColors().colors + ", \tсостав: хлопок - " + socks.getCotton() + "%" +
                        ", \tкол-во: " + socks.getQuantity() + " шт.");

            }
        }
        return path;
    }
    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(socksList);
            fileSocksService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при записи файла");
        }
    }

    private void readFromFile() throws FileProcessingException{
        String json = fileSocksService.readerFromFile();
        try {
            socksList = new ObjectMapper().readValue(json, new TypeReference<LinkedList<Socks>>() {
            });
        } catch (JsonProcessingException e) {
            throw new FileProcessingException("Ошибка при чтении файла");
        }
    }

}

