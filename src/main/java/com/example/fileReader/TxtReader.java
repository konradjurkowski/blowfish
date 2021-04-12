package com.example.fileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TxtReader {
    public static List<String> createTxtFile(File file) throws FileNotFoundException {
        List<String> strings = new ArrayList<>();
        try(Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(" ");
                strings.addAll(Arrays.asList(data));
            }
        }
        return strings;
    }

    public static void saveTxtFile(String content, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.close();
    }
}
