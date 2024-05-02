package com.github.winexp.battlegrounds.command.argument;

import com.github.winexp.battlegrounds.game.PVPMode;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class PVPModeArgumentType extends net.minecraft.command.argument.EnumArgumentType<PVPMode> {
    private PVPModeArgumentType() {
        super(PVPMode.CODEC, PVPMode::values);
    }

    public static PVPModeArgumentType pvpMode() {
        return new PVPModeArgumentType();
    }

    public static PVPMode getPVPMode(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, PVPMode.class);
    }
}
