package br.com.projeto.biblioteca.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LogGenerator {

    public static void generateLog(String message){
        Path path = Paths.get("C:/Users/Rafael");

        if (!Files.exists(path)){
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException("Erro 1");
            }
        }

        File log = new File("C:/Users/Rafael/Desktop/logs/logs.txt");

        if (!log.exists()){
            try {
                log.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Erro 2");
            }
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(log, true);
        } catch (IOException e) {
            throw new RuntimeException("Erro 3");
        }

        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Erro 4");
        }

        try {
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Erro 5");
        }

    }
}
