package com.github.winexp.battlegrounds.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;

// TODO: 需要重写
public class ConfigUtil {
    public static boolean isConfigExists(Path path, String configName){
        return FileUtil.isFileExists(path.resolve(configName + ".json"));
    }

    public static void saveConfig(Path path, String configName, Object config){
        if (config == null){
            return;
        }
        buildConfig(path, configName, config);
    }

    public static <T> T createOrLoadConfig(Path path, String configName, Class<T> clazz){
        if (isConfigExists(path, configName)){
            return readConfig(path, configName, clazz);
        }
        else{
            try{
                T instance = clazz.getDeclaredConstructor().newInstance();
                buildConfig(path, configName, instance);
                return instance;
            } catch (Exception e){
                Environment.LOGGER.error("无法创建配置", e);
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> T readConfig(Path path, String configName, Class<T> clazz){
        Path fileName = path.resolve(configName + ".json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = FileUtil.readString(fileName);
        return gson.fromJson(json, clazz);
    }

    public static void buildConfig(Path path, String configName, Object config){
        Path fileName = path.resolve(configName + ".json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(config);
        FileUtil.writeString(fileName, json);
    }
}
