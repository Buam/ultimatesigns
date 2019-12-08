package com.buam.ultimatesigns.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandFileReader {

    private CommandFileReader() {}

    /**
     * Reads all lines from a file
     * @param path The path of the file
     * @return All lines from this file
     * @throws IOException When a file was not found or something else broke
     */
    public static List<String> read(final String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

        List<String> commands = new ArrayList<>();

        String line;
        while((line = reader.readLine()) != null) {
            commands.add(line);
        }
        return commands;

    }

}
