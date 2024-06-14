package com.github.winexp.battlegrounds.command;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.entity.projectile.thrown.FlashBangEntity;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BattlegroundsCommand {
    public static void registerRoot(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        var cRoot = literal("battlegrounds");
        var cReload = literal("reload").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeReload);
        var cFlash = registerSummonFlash();
        var cNode = dispatcher.register(cRoot.then(cReload).then(cFlash));
        // 命令缩写
        var cRoot_redir = literal("bg").redirect(cNode);
        dispatcher.register(cRoot_redir);
    }

    private static ArgumentBuilder<ServerCommandSource, ?> registerSummonFlash() {
        var cSummon = literal("flash").requires(source ->
                source.hasPermissionLevel(2)).executes(context -> executeFlash(context, true));
        var aPos = argument("pos", Vec3ArgumentType.vec3()).executes(context -> executeFlash(context, false));
        return cSummon.then(aPos);
    }

    private static int executeFlash(CommandContext<ServerCommandSource> context, boolean isSelf) throws CommandSyntaxException {
        if (isSelf && context.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            World world = player.getWorld();
            Vec3d pos = player.getPos();
            FlashBangEntity.summonFlash(world, pos);
        } else if (isSelf && !context.getSource().isExecutedByPlayer()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        } else {
            Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");
            World world = Variables.server.getOverworld();
            FlashBangEntity.summonFlash(world, pos);
        }
        return 1;
    }

    private static int executeReload(CommandContext<ServerCommandSource> context) {
        Battlegrounds.INSTANCE.reload();
        context.getSource().sendFeedback(() -> Text.translatable("commands.battlegrounds.reload.feedback")
                .formatted(Formatting.GREEN), true);

        return 1;
    }
}
