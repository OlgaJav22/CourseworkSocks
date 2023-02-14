package com.example.courseworksocks.services;

import java.io.File;
import java.nio.file.Path;

public interface FileSocksService {

    boolean saveToFile(String json);

    String readerFromFile();

    File getDataFile();

    boolean cleanDataFile();

    Path createTempFile(String syffix);
}
