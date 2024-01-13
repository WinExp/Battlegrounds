package com.github.winexp.battlegrounds.configs;

import com.mojang.authlib.GameProfile;

import java.util.List;

public class GameProgress {
    public PVPMode pvpMode = PVPMode.PEACEFUL;
    public boolean gaming = false;
    public int currentLap = 0;
    public long resizeLapTimer = 0;
    public List<String> players = List.of();

    public enum PVPMode{
        PEACEFUL, NO_PVP, PVP_MODE
    }
}
