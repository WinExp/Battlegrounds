package com.github.winexp.battlegrounds.command;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.discussion.vote.Vote;
import com.github.winexp.battlegrounds.discussion.vote.VotePresets;
import com.github.winexp.battlegrounds.entity.projectile.thrown.FlashBangEntity;
import com.github.winexp.battlegrounds.util.*;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.*;

public class BattlegroundsCommand implements CommandRegistrationCallback {
    private Vote startGameVote = Vote.EMPTY;

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        var cRoot = literal("battlegrounds");
        var cStart = literal("start").executes(this::executeStart);
        var cStop = literal("stop").executes(this::executeStop);
        var cFlash = literal("flash").requires(source -> source.hasPermissionLevel(2)).executes(context -> executeFlash(context, true))
                .then(argument("pos", Vec3ArgumentType.vec3()).executes(context -> executeFlash(context, false)));
        var cReload = literal("reload").requires(source ->
                source.hasPermissionLevel(2)).executes(this::executeReload);
        var cNode = dispatcher.register(cRoot.then(cStart).then(cStop).then(cFlash).then(cReload));
        // 命令缩写
        var cRoot_redir = literal("bg").redirect(cNode);
        dispatcher.register(cRoot_redir);
    }

    private int executeStart(CommandContext<ServerCommandSource> context) {
        //if (this.startGameVote.isOpened()) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        this.startGameVote = new Vote(VotePresets.START_GAME);
        this.startGameVote.open(context.getSource().getServer().getPlayerManager().getPlayerList().stream().map(PlayerUtil::getAuthUUID).toList(),
                Duration.ofSeconds(60));
        return 1;
    }

    private int executeStop(CommandContext<ServerCommandSource> context) {
        return 1;
    }

    private int executeFlash(CommandContext<ServerCommandSource> context, boolean isSelf) throws CommandSyntaxException {
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

    private int executeReload(CommandContext<ServerCommandSource> context) {
        Battlegrounds.INSTANCE.reload();
        context.getSource().sendFeedback(() -> Text.translatable("commands.battlegrounds.reload.feedback")
                .formatted(Formatting.GREEN), true);

        return 1;
    }
}
