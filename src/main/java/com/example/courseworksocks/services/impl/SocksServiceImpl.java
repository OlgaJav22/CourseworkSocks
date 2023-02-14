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

@Service
public class SocksServiceImpl implements SocksService {

    final private FileSocksService fileSocksService;

    private static List<Socks> socksList = new LinkedList<>();


    public SocksServiceImpl(FileSocksService fileSocksService) {
        this.fileSocksService = fileSocksService;
    }

    @Override
    public Socks addNewSocks(Socks socks) {
        if (socks.getSize() != null && socks.getColors() != null && socks.getCotton() > 0) {

            List<Socks> collect = socksList.stream()
                    .filter(socks1 -> (socks.getSize().size.equals(socks1.getSize().size)) && (socks.getCotton().equals(socks1.getCotton()))
                            && (socks.getColors().colors.equals(socks1.getColors().colors))).distinct().toList();
            if (!collect.isEmpty()) {
                collect.stream()
                        .peek(socks1 -> socks1.setQuantity(socks.getQuantity() + socks1.getQuantity()))
                        .collect(Collectors.toList());
                saveToFile();
            } else {
                socksList.add(socks);
                saveToFile();
            }
        }

        return socks;
    }

    @Override
    public List<Socks> moveSocks(Integer size, String colors, Integer cotton, Integer quantity) {

        List<Socks> collect = socksList.stream()
                .filter(socks -> socks.getSize().size.equals(size) && socks.getCotton().equals(cotton))
                .filter(socks -> socks.getColors().colors.equals(colors))
                .filter(socks -> socks.getQuantity() >= (quantity)).toList();

        if (!collect.isEmpty()) {
            collect.stream()
                    .peek(socks5 -> socks5.setQuantity(socks5.getQuantity() - quantity))
                    .collect(Collectors.toUnmodifiableList());
            saveToFile();
        }
        return collect;
    }


    @Override
    public List<Socks> deleteSocks(Integer size, String colors, Integer cotton, Integer quantity) {

        List<Socks> collect = socksList.stream()
                .filter(socks -> socks.getSize().size.equals(size) && socks.getCotton().equals(cotton))
                .filter(socks -> socks.getColors().colors.equals(colors))
                .filter(socks -> socks.getQuantity() >= (quantity)).toList();

        if (!collect.isEmpty()) {
            collect.stream()
                    .peek(socks5 -> socks5.setQuantity(socks5.getQuantity() - quantity))
                    .collect(Collectors.toUnmodifiableList());
            saveToFile();
        }
        return collect;
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
    public List<Socks> getQuantitySocksMinCotton(Integer size, String colors, Integer cotton) {

        List<Socks> collect = socksList.stream()
                .filter(socks -> socks.getSize().size.equals(size) && socks.getCotton() >= cotton)
                .filter(socks -> socks.getColors().colors.equals(colors))
                .collect(Collectors.toList());
        return collect;

    }

    @Override
    public List<Socks> getQuantitySocksMaxCotton(Integer size, String colors, Integer cotton) {

        List<Socks> collect = socksList.stream()
                .filter(socks -> socks.getSize().size.equals(size) && socks.getCotton() <= cotton)
                .filter(socks -> socks.getColors().colors.equals(colors))
                .collect(Collectors.toList());
        return collect;

    }

    @Override
    public Integer getQuantitySocksSize(Integer size, String colors, Integer cotton) {
        Integer collect = socksList.stream()
                .filter(socks -> socks.getSize().size.equals(size) && socks.getCotton() >= cotton && socks.getColors().colors.equals(colors))
                .map(socks -> socks.getQuantity())
                .reduce((q1, q2) -> q1 + q2)
                .get();
        return collect;
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

    private void readFromFile() throws FileProcessingException {
        String json = fileSocksService.readerFromFile();
        try {
            socksList = new ObjectMapper().readValue(json, new TypeReference<LinkedList<Socks>>() {
            });
        } catch (JsonProcessingException e) {
            throw new FileProcessingException("Ошибка при чтении файла");
        }
    }

}

