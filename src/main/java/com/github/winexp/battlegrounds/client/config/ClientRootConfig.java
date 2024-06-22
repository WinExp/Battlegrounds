package com.github.winexp.battlegrounds.client.config;

import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientRootConfig {
    public static final ConfigClassHandler<ClientRootConfig> HANDLER = ConfigClassHandler.createBuilder(ClientRootConfig.class)
            .id(new Identifier("battlegrounds", "client_root_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(ClientConstants.CONFIG_PATH.resolve("config.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    @AutoGen(category = "vote", group = "notification")
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry
    public boolean showVoteOpenedNotification = true;
    @AutoGen(category = "vote", group = "notification")
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry
    public boolean showVoteClosedNotification = true;
    @AutoGen(category = "vote", group = "sound")
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry
    public boolean playVoteOpenedSounds = true;
    @AutoGen(category = "vote", group = "sound")
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry
    public boolean playVoteClosedSounds = true;
}
