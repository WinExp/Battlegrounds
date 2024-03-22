package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.FileUtil;

import java.nio.file.Path;

public class GameUtil {

    public static void deleteWorld() {
        Path savePath = Path.of(FileUtil.readString(Constants.DELETE_WORLD_TMP_FILE_PATH).trim());
        FileUtil.delete(savePath, false,
                GameManager.PERSISTENT_STATE_ID + ".dat", "stats");
    }

    public static void createDeleteWorldTmpFile(Path savePath) {
        FileUtil.writeString(Constants.DELETE_WORLD_TMP_FILE_PATH, savePath.toString());
    }
}
