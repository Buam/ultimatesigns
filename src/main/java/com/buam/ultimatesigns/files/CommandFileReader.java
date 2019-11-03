package com.buam.ultimatesigns.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandFileReader {

    public static List<String> read(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

        List<String> commands = new ArrayList<>();

        String line;
        while((line = reader.readLine()) != null) {
            commands.add(line);
        }
        return commands;

    }

}
