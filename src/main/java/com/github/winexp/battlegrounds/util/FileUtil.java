package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.Battlegrounds;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileUtil {
    public static boolean isFileExists(Path path){
        File file = path.toFile();
        return file.exists() && file.isFile();
    }

    public static boolean isDirectoryExists(Path path){
        File file = path.toFile();
        return file.exists() && file.isDirectory();
    }

    public static void delete(Path path, String... excludes){
        if (!Files.exists(path)) return;
        File file = path.toFile();
        if (Stream.of(excludes).anyMatch((exclude) -> file.getName().endsWith(exclude))) return;
        if (file.isDirectory()){
            for (File f : file.listFiles()){
                delete(f.toPath(), excludes);
            }
        }
        file.delete();
    }

    public static String readString(Path fileName){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName.toFile()))){
            while (reader.ready()){
                sb.append(reader.readLine());
                sb.append('\n');
            }
        } catch (IOException e) {
            Battlegrounds.logger.error("无法读取文件", e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static void writeString(Path fileName, String content){
        try {
            Files.createDirectories(fileName.getParent());
        } catch (IOException e){
            Battlegrounds.logger.error("无法创建目录", e);
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName.toFile()))){
            writer.write(content);
        } catch (IOException e) {
            Battlegrounds.logger.error("无法写入文件", e);
            throw new RuntimeException(e);
        }
    }
}
