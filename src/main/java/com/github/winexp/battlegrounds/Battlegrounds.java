package com.github.winexp.battlegrounds;

import com.github.winexp.battlegrounds.commands.BattlegroundsCommand;
import com.github.winexp.battlegrounds.commands.RandomTpCommand;
import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.configs.RootConfig;
import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.events.player.PlayerDamagedCallback;
import com.github.winexp.battlegrounds.events.player.PlayerDeathCallback;
import com.github.winexp.battlegrounds.events.player.PlayerJoinedGameCallback;
import com.github.winexp.battlegrounds.events.server.ServerLoadedCallback;
import com.github.winexp.battlegrounds.events.server.ServerSavingCallback;
import com.github.winexp.battlegrounds.events.server.WorldsLoadedCallback;
import com.github.winexp.battlegrounds.events.vote.PlayerVotedCallback;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.github.winexp.battlegrounds.helper.GameHelper;
import com.github.winexp.battlegrounds.helper.VoteHelper;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("SameReturnValue")
public class Battlegrounds implements ModInitializer {
    public static void saveConfigs() {
        ConfigUtil.saveConfig(Environment.CONFIG_PATH, "config", Variable.INSTANCE.config);
        if (Variable.INSTANCE.server != null) {
            if (GameHelper.INSTANCE.reduceTask.getDelay() >= 0) {
                Variable.INSTANCE.progress.resizeLapTimer = GameHelper.INSTANCE.reduceTask.getDelay();
            }
            ConfigUtil.saveConfig(Variable.INSTANCE.server.getSavePath(WorldSavePath.ROOT), "bg_progress", Variable.INSTANCE.progress);
        }
    }

    public static void loadConfigs() {
        Variable.INSTANCE.config = ConfigUtil.createOrLoadConfig(Environment.CONFIG_PATH, "config", RootConfig.class);
        if (Variable.INSTANCE.server != null) {
            Variable.INSTANCE.progress = ConfigUtil.createOrLoadConfig(Variable.INSTANCE.server.getSavePath(WorldSavePath.ROOT),
                    "bg_progress",
                    GameProgress.class);
        }
    }

    public static void reload() {
        loadConfigs();
        GameHelper.INSTANCE.reduceTask.cancel();
        if (Variable.INSTANCE.progress.gameStage.isStarted() && !Variable.INSTANCE.progress.gameStage.isDeathmatch()) {
            GameHelper.INSTANCE.runTasks();
        }
    }

    private ActionResult onServerLoaded(MinecraftServer server) {
        Variable.INSTANCE.server = server;
        GameHelper.INSTANCE.setServer(server);
        loadConfigs();

        return ActionResult.PASS;
    }

    private ActionResult onWorldLoaded(MinecraftServer server) {
        GameHelper.INSTANCE.initialize();

        return ActionResult.PASS;
    }

    private ActionResult onPlayerDeath(DamageSource source, ServerPlayerEntity player) {
        GameHelper.INSTANCE.onPlayerDeath(player);

        return ActionResult.PASS;
    }

    private ActionResult onPlayerVote(VoteHelper voter, ServerPlayerEntity player, boolean result) {
        if (result) {
            Variable.INSTANCE.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.accept.broadcast", TextUtil.GREEN,
                    player.getName(), voter.getAccepted(), voter.getTotal()), false);
        } else {
            Variable.INSTANCE.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.deny.broadcast", TextUtil.GOLD,
                    player.getName(), voter.getAccepted(), voter.getTotal()), false);
        }
        return ActionResult.PASS;
    }

    private ActionResult onVoteStop(VoteHelper voter, VoteCompletedCallback.Reason reason) {
        if (reason == VoteCompletedCallback.Reason.TIMEOUT) {
            Variable.INSTANCE.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.vote.timeout.broadcast", TextUtil.GOLD), false);
        } else if (reason == VoteCompletedCallback.Reason.MANUAL) {
            Variable.INSTANCE.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.vote.manual.broadcast", TextUtil.GOLD), false);
        } else if (reason == VoteCompletedCallback.Reason.ACCEPT) {
            GameHelper.INSTANCE.prepareResetWorlds(voter);
        }

        return ActionResult.PASS;
    }

    private ActionResult onPlayerDamaged(DamageSource source, ServerPlayerEntity instance) {
        return GameHelper.INSTANCE.onPlayerDamaged(source);
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        if (environment.dedicated) {
            BattlegroundsCommand.registerRoot(dispatcher);
            RandomTpCommand.register(dispatcher);
        } else {
            Environment.LOGGER.error("Battlegrounds 只可在专用服务器 (Dedicated Server) 上运行！");
        }
    }

    private ActionResult onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfoReturnable<Text> cir) {
        if (Variable.INSTANCE.progress.gameStage.isPreparing()) {
            if (!Variable.INSTANCE.progress.players.containsKey(player.getGameProfile().getId().toString())) {
                player.changeGameMode(GameMode.SPECTATOR);
                cir.setReturnValue(TextUtil.translatableWithColor("battlegrounds.game.join.spectator.broadcast",
                        TextUtil.DARK_GRAY, player.getName()));
            } else {
                cir.setReturnValue(TextUtil.translatableWithColor(
                        "battlegrounds.game.join.broadcast",
                        TextUtil.GREEN,
                        player.getName(),
                        GameHelper.INSTANCE.getInGamePlayers() + 1, Variable.INSTANCE.progress.players.size()));
                if (Variable.INSTANCE.server.getPlayerManager().getPlayerList().size() + 1 == Variable.INSTANCE.progress.players.size()) {
                    GameHelper.INSTANCE.prepareStartGame();
                }
            }
        } else if (Variable.INSTANCE.progress.gameStage.isDeathmatch()) {
            player.sendMessage(TextUtil.translatableWithColor(
                    "battlegrounds.game.deathmatch.start.broadcast", TextUtil.GOLD), false);
        }
        PlayerUtil.setGameModeWithMap(player);

        return ActionResult.PASS;
    }

    @Override
    public void onInitialize() {
        Environment.LOGGER.info("正在加载 Battlegrounds");

        // 注册指令
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        // 加载配置
        loadConfigs();
        // 注册事件
        ServerLoadedCallback.EVENT.register(this::onServerLoaded);
        WorldsLoadedCallback.EVENT.register(this::onWorldLoaded);
        ServerSavingCallback.EVENT.register((server, suppressLogs) -> {
            saveConfigs();
            loadConfigs();
            if (!suppressLogs) Environment.LOGGER.info("已保存游戏配置");

            return ActionResult.PASS;
        });
        PlayerVotedCallback.EVENT.register(this::onPlayerVote);
        VoteCompletedCallback.EVENT.register(this::onVoteStop);
        PlayerDamagedCallback.EVENT.register(this::onPlayerDamaged);
        PlayerJoinedGameCallback.EVENT.register(this::onPlayerJoin);
        PlayerDeathCallback.EVENT.register(this::onPlayerDeath);

        // 注册实体
        EntityTypes.registerEntities();

        // 注册物品
        Items.registerItems();

        // 注册附魔
        Enchantments.registerEnchantments();

        // 注册物品组
        Items.registerItemGroup();
        Enchantments.registerItemGroup();

        // 添加配方
        Items.addRecipes();

        // 重置存档
        GameHelper.INSTANCE.tryResetWorlds();
    }
}
