package com.github.winexp.battlegrounds.config;

import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.FileUtil;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Util;

import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unchecked")
public class ConfigUtil {
    public static boolean isConfigExists(Path path, String configName) {
        return Files.isRegularFile(path.resolve(configName + ".json"));
    }

    public static <T extends Record> T readOrCreateConfig(Path path, String configName, Codec<T> configCodec, IConfig<T> defaultValue) {
        try {
            if (isConfigExists(path, configName)) {
                return readConfig(path, configName, configCodec);
            } else {
                writeConfig(path, configName, defaultValue);
                return (T) defaultValue;
            }
        } catch (Exception e) {
            try {
                writeConfig(path, configName, defaultValue);
                return (T) defaultValue;
            } catch (Exception e1) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T extends Record> T readConfig(Path path, String configName, Codec<T> configCodec) {
        Path fileName = path.resolve(configName + ".json");
        JsonElement json = Constants.GSON.fromJson(FileUtil.readString(fileName), JsonElement.class);
        return Util.getResult(configCodec.parse(JsonOps.INSTANCE, json), IllegalStateException::new);
    }

    public static <T extends Record> void writeConfig(Path path, String configName, IConfig<T> config) {
        Path fileName = path.resolve(configName + ".json");
        JsonElement json = Util.getResult(config.getCodec().encodeStart(JsonOps.INSTANCE, (T) config), IllegalStateException::new);
        FileUtil.writeString(fileName, Constants.GSON.toJson(json));
    }
}
