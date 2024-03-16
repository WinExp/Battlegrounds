package com.github.winexp.battlegrounds.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigUtil {
    public static boolean isConfigExists(Path path, String configName) {
        return Files.isRegularFile(path.resolve(configName + ".json"));
    }

    public static void saveConfig(Path path, String configName, Object config) {
        if (config == null) {
            return;
        }
        buildConfig(path, configName, config);
    }

    public static <T> T readOrCreateConfig(Path path, String configName, Class<T> clazz) {
        if (isConfigExists(path, configName)) {
            return readConfig(path, configName, clazz);
        } else {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                buildConfig(path, configName, instance);
                return instance;
            } catch (Exception e) {
                Constants.LOGGER.error("无法创建配置", e);
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> T readConfig(Path path, String configName, Class<T> clazz) {
        Path fileName = path.resolve(configName + ".json");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = FileUtil.readString(fileName);
        return gson.fromJson(json, clazz);
    }

    public static <T> void buildConfig(Path path, String configName, T config) {
        Path fileName = path.resolve(configName + ".json");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(config);
        FileUtil.writeString(fileName, json);
    }
}
