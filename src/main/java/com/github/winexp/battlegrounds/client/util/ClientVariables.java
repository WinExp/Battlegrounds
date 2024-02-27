package com.github.winexp.battlegrounds.client.util;

public class ClientVariables {
    public static ClientVariables INSTANCE = new ClientVariables();
    public float flashStrength = 0;
    public FlashMode flashMode = FlashMode.FILL;

    public enum FlashMode {
        FOG, FILL
    }
}
