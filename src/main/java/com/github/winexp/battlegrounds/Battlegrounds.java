package com.github.winexp.battlegrounds;

import com.github.winexp.battlegrounds.commands.BattlegroundsCommand;
import com.github.winexp.battlegrounds.commands.RandomTpCommand;
import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.configs.RootConfig;
import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteSettings;
import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.events.PlayerEvents;
import com.github.winexp.battlegrounds.events.vote.PlayerVotedCallback;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.github.winexp.battlegrounds.helper.GameHelper;
import com.github.winexp.battlegrounds.discussion.vote.VoteHelper;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;

import java.util.Arrays;

@SuppressWarnings("SameReturnValue")
public class Battlegrounds implements ModInitializer {
    public static void saveConfigs() {
        ConfigUtil.saveConfig(Constants.CONFIG_PATH, "config", Variables.config);
        if (Variables.server != null) {
            if (GameHelper.INSTANCE.reduceTask.getDelay() >= 0) {
                Variables.progress.resizeLapTimer = GameHelper.INSTANCE.reduceTask.getDelay();
            }
            ConfigUtil.saveConfig(Variables.server.getSavePath(WorldSavePath.ROOT), "bg_progress", Variables.progress);
        }
    }

    public static void loadConfigs() {
        Variables.config = ConfigUtil.createOrLoadConfig(Constants.CONFIG_PATH, "config", RootConfig.class);
        if (Variables.server != null) {
            Variables.progress = ConfigUtil.createOrLoadConfig(Variables.server.getSavePath(WorldSavePath.ROOT),
                    "bg_progress",
                    GameProgress.class);
        }
    }

    public static void reload() {
        loadConfigs();
        Items.addRecipes();
        GameHelper.INSTANCE.reduceTask.cancel();
        if (Variables.progress.gameStage.isStarted() && !Variables.progress.gameStage.isDeathmatch()) {
            GameHelper.INSTANCE.runTasks();
        }
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        BattlegroundsCommand.registerRoot(dispatcher);
        RandomTpCommand.register(dispatcher);
    }

    private void onServerStarting(MinecraftServer server) {
        Variables.server = server;
        GameHelper.INSTANCE.setServer(server);
        loadConfigs();
    }

    private void onServerStarted(MinecraftServer server) {
        GameHelper.INSTANCE.initialize();
    }

    private void onSaving(MinecraftServer server, boolean flush, boolean force) {
        saveConfigs();
    }

    private void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        PlayerUtil.setGameModeWithMap(newPlayer);
    }

    private void onLivingEntityDeath(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof ServerPlayerEntity player) {
            GameHelper.INSTANCE.onPlayerDeath(player);
        }
    }

    public static void onStartGameVoteClosed(VoteInstance voteInstance, VoteSettings.CloseReason closeReason) {
        if (closeReason == VoteSettings.CloseReason.TIMEOUT) {
            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.vote.timeout.broadcast", TextUtil.GOLD), false);
        } else if (closeReason == VoteSettings.CloseReason.MANUAL) {
            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.vote.manual.broadcast", TextUtil.GOLD), false);
        } else if (closeReason == VoteSettings.CloseReason.ACCEPTED) {
            GameHelper.INSTANCE.prepareResetWorlds(voteInstance.getParticipants());
        }
    }

    public static void onStartGamePlayerVoted(VoteInstance voteInstance, ServerPlayerEntity player, boolean result) {
        if (result) {
            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.accept.broadcast", TextUtil.GREEN,
                    player.getName(), voteInstance.getAcceptedNum(), voteInstance.getTotal()), false);
        } else {
            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.deny.broadcast", TextUtil.GOLD,
                    player.getName(), voteInstance.getAcceptedNum(), voteInstance.getTotal()), false);
        }
    }

    private ActionResult onPlayerVote(VoteHelper voter, ServerPlayerEntity player, boolean result) {
        if (result) {
            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.accept.broadcast", TextUtil.GREEN,
                    player.getName(), voter.getAccepted(), voter.getTotal()), false);
        } else {
            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.deny.broadcast", TextUtil.GOLD,
                    player.getName(), voter.getAccepted(), voter.getTotal()), false);
        }
        return ActionResult.PASS;
    }

    private ActionResult onVoteStop(VoteHelper voter, VoteCompletedCallback.Reason reason) {
        if (reason == VoteCompletedCallback.Reason.TIMEOUT) {
            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.vote.timeout.broadcast", TextUtil.GOLD), false);
        } else if (reason == VoteCompletedCallback.Reason.MANUAL) {
            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.vote.manual.broadcast", TextUtil.GOLD), false);
        } else if (reason == VoteCompletedCallback.Reason.ACCEPT) {
            GameHelper.INSTANCE.prepareResetWorlds(Arrays.stream(voter.getPlayerProfiles())
                    .map((GameProfile::getId)).toList());
        }

        return ActionResult.PASS;
    }

    private boolean allowLivingEntityDamaged(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof ServerPlayerEntity player) {
            if (Variables.progress.pvpMode == GameProgress.PVPMode.PEACEFUL) {
                return false;
            } else if (Variables.progress.pvpMode == GameProgress.PVPMode.NO_PVP) {
                if (source.getSource() != null && source.getSource().isPlayer()) {
                    return false;
                }
            }
            if (Variables.progress.gameStage.isStarted() && source.getSource() != null
                    && source.getSource().isPlayer() && Variables.progress.isInGame(PlayerUtil.getUUID(player))) {
                RandomTpCommand.setCooldown(player, Variables.config.cooldown.randomTpDamagedCooldownTicks);
            }
        }
        return true;
    }

    private void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        Text joinText = Text.empty();
        if (Variables.progress.gameStage.isPreparing()) {
            GameProgress.PlayerPermission permission = Variables.progress.players.get(PlayerUtil.getUUID(player));
            if (permission != null && !permission.inGame) {
                joinText = TextUtil.translatableWithColor("battlegrounds.game.join.spectator.broadcast",
                        TextUtil.DARK_GRAY, player.getName());
            } else {
                joinText = TextUtil.translatableWithColor(
                        "battlegrounds.game.join.broadcast",
                        TextUtil.GREEN,
                        player.getName(),
                        Variables.server
                                .getPlayerManager().getCurrentPlayerCount() + 1,
                        Variables.progress.players.size()
                );
                Variables.server.getPlayerManager().getWhitelist().add(new WhitelistEntry(player.getGameProfile()));
                if (Variables.server.getPlayerManager().getPlayerList().size() + 1 == Variables.progress.players.size()) {
                    GameHelper.INSTANCE.prepareStartGame();
                }
            }
        } else if (Variables.progress.gameStage.isDeathmatch()) {
            player.sendMessage(TextUtil.translatableWithColor(
                    "battlegrounds.game.deathmatch.start.broadcast", TextUtil.GOLD), false);
        }
        server.getPlayerManager().broadcast(joinText, false);
        PlayerUtil.setGameModeWithMap(player);
    }

    private boolean allowPlayerNaturalRegen(PlayerEntity player) {
        if (player.getWorld().isClient) return true;
        GameProgress.PlayerPermission permission = Variables.progress.players.get(PlayerUtil.getUUID(player));
        return permission == null || permission.naturalRegen;
    }

    @Override
    public void onInitialize() {
        Constants.LOGGER.info("Loading {}", Constants.MOD_ID);

        // 注册指令
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        // 加载配置
        loadConfigs();
        // 注册事件
        // Fabric API
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.AFTER_SAVE.register(this::onSaving);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::allowLivingEntityDamaged);
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onLivingEntityDeath);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::onPlayerRespawn);
        // 自定义
        PlayerEvents.ALLOW_NATURAL_REGEN.register(this::allowPlayerNaturalRegen);
        PlayerVotedCallback.EVENT.register(this::onPlayerVote);
        VoteCompletedCallback.EVENT.register(this::onVoteStop);

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
