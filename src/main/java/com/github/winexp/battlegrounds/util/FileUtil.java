package com.github.winexp.battlegrounds.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class FileUtil {
    public static Collection<File> listFiles(Path path) {
        return Arrays.stream(Objects.requireNonNull(path.toFile().listFiles())).toList();
    }

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
            Constants.LOGGER.error("无法删除文件", e);
        }
    }

    public static String readString(File file) {
        Objects.requireNonNull(file);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                sb.append(reader.readLine());
                sb.append('\n');
            }
        } catch (IOException e) {
            Constants.LOGGER.error("无法读取文件", e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static String readString(Path fileName) {
        return readString(fileName.toFile());
    }

    public static void writeString(Path fileName, String content) {
        writeString(fileName.toFile(), content);
    }

    public static void writeString(File file, String content) {
        try {
            Files.createDirectories(file.toPath().getParent());
        } catch (IOException e) {
            Constants.LOGGER.error("无法创建目录", e);
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException e) {
            Constants.LOGGER.error("无法写入文件", e);
            throw new RuntimeException(e);
        }
    }
}
