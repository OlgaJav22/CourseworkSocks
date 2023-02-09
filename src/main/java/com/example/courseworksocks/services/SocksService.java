package com.example.courseworksocks.services;

import com.example.courseworksocks.model.Socks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface SocksService {


    Socks addNewSocks(Socks socks);

    Collection<Socks> getChecklistSocks();

    List<Socks> getQuantitySocks(Integer size, String colors, Integer cotton);

    Path createSocks() throws IOException;
}
