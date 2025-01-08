package br.com.projeto.biblioteca.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogGenerator {

    public static void generateLog(String message) throws IOException {

        String userHome = System.getProperty("user.home");

        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        Path path = Paths.get(userHome + "/Desktop/logs/");

        if(!Files.exists(path)) {

            Files.createDirectory(path);

        }

        File log = new File(userHome + "/Desktop/logs/logs.txt");

        if(!log.exists()) {

            log.createNewFile();

        }

        FileWriter fw = new FileWriter(log, true);
        BufferedWriter bw = new BufferedWriter(fw);

        String logMessage = "[" + dateTime + "] " +  message;

        bw.write(logMessage);
        bw.newLine();

        bw.close();
        fw.close();

    }

}
