package com.buam.ultimatesigns.files;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    private FileReader() {}

    /**
     * Reads all lines from a file
     * @param path The path of the file
     * @return All lines from this file
     * @throws IOException When a file was not found or something else broke
     */
    public static List<String> read(final String path) throws IOException {

        BufferedReader reader = new BufferedReader(new java.io.FileReader(path));

        List<String> commands = new ArrayList<>();

        String line;
        while((line = reader.readLine()) != null) {
            commands.add(line);
        }

        reader.close();

        return commands;

    }

}
