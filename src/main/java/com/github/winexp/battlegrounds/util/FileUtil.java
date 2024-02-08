package com.github.winexp.battlegrounds.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class FileUtil {
    public static void delete(Path path, boolean deleteRoot, String... excludes) {
        if (!Files.exists(path)) return;
        if (Stream.of(excludes).anyMatch(path::endsWith)) return;
        if (Files.isDirectory(path)) {
            for (File f : Objects.requireNonNull(path.toFile().listFiles())) {
                delete(f.toPath(), true, excludes);
            }
        }
        try {
            if (deleteRoot) {
                Files.delete(path);
            }
        } catch (IOException e) {
            Environment.LOGGER.error("无法删除文件", e);
        }
    }

    public static String readString(Path fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName.toFile()))) {
            while (reader.ready()) {
                sb.append(reader.readLine());
                sb.append('\n');
            }
        } catch (IOException e) {
            Environment.LOGGER.error("无法读取文件", e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static void writeString(Path fileName, String content) {
        try {
            Files.createDirectories(fileName.getParent());
        } catch (IOException e) {
            Environment.LOGGER.error("无法创建目录", e);
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName.toFile()))) {
            writer.write(content);
        } catch (IOException e) {
            Environment.LOGGER.error("无法写入文件", e);
            throw new RuntimeException(e);
        }
    }
}
