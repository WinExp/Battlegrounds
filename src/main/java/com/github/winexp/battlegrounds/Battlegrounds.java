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
import com.github.winexp.battlegrounds.events.server.WorldLoadedCallback;
import com.github.winexp.battlegrounds.events.vote.PlayerVotedCallback;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.github.winexp.battlegrounds.helper.GameHelper;
import com.github.winexp.battlegrounds.helper.VoteHelper;
import com.github.winexp.battlegrounds.helper.task.TaskCountdown;
import com.github.winexp.battlegrounds.helper.task.TaskScheduler;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.ConfigUtil;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.TextUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

import static com.github.winexp.battlegrounds.util.Environment.*;

public class Battlegrounds implements ModInitializer {
    public final static Logger logger = LogManager.getLogger(MOD_ID);
    public static TaskScheduler taskScheduler;
    public static MinecraftServer server;
    public static RootConfig config;
    public static GameProgress progress;
    public static TaskCountdown stopTask = TaskCountdown.NONE_TASK;
    public static TaskCountdown startTask = TaskCountdown.NONE_TASK;
    public static GameHelper gameHelper = new GameHelper();

    private ActionResult onServerLoaded(MinecraftServer server){
        Battlegrounds.server = server;
        gameHelper.setServer(server);
        taskScheduler = new TaskScheduler();
        loadConfigs();
        saveConfigs();

        if (progress.progress.isResetWorld()){
            if (server.getSavePath(WorldSavePath.ROOT) == null){
                logger.error("无法重置地图：savePath 不能为 null");
            }
            else{
                gameHelper.resetWorlds();
            }
        }

        return ActionResult.PASS;
    }

    private ActionResult onWorldLoaded(MinecraftServer server){
        if (progress.progress.isResetWorld()){
            progress.progress = GameProgress.Progress.WAIT_PLAYER;
            gameHelper.setInitialProgress();
        }

        gameHelper.setKeepInventory(progress.progress != GameProgress.Progress.DEATHMATCH);
        if (progress.resizeLapTimer <= 0
                && progress.progress != GameProgress.Progress.DEATHMATCH) Battlegrounds.progress.resizeLapTimer = Battlegrounds.config.border.resizeDelayTicks;
        if (progress.progress.isStarted()){
            gameHelper.resumeGame();
        }

        return ActionResult.PASS;
    }

    private ActionResult onPlayerDeath(DamageSource source, ServerPlayerEntity player){
        if (progress.progress == GameProgress.Progress.DEATHMATCH){
            player.changeGameMode(GameMode.SPECTATOR);
            if (gameHelper.getInGamePlayers() == 1){
                for (ServerPlayerEntity p1 : server.getPlayerManager().getPlayerList()){
                    p1.changeGameMode(GameMode.ADVENTURE);
                    p1.getInventory().clear();
                }
                ServerPlayerEntity p = gameHelper.getFirstPlayer();
                server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.end.broadcast", TextUtil.GOLD, p.getName()), false);

                FireworkRocketEntity firework = EntityType.FIREWORK_ROCKET.create(server.getOverworld());
                if (firework != null) {
                    firework.refreshPositionAfterTeleport(p.getPos());
                }

                gameHelper.stopGame();
            }
        }

        return ActionResult.PASS;
    }

    private ActionResult onPlayerVote(ServerPlayerEntity player, VoteHelper voter, boolean result){
        if (result){
            server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.accept.broadcast", TextUtil.GREEN,
                    player.getName(), voter.getAccepted(), voter.getTotal()), false);
        }
        else{
            server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.deny.broadcast", TextUtil.GOLD,
                    player.getName(), voter.getAccepted(), voter.getTotal()), false);
        }
        return ActionResult.PASS;
    }

    private ActionResult onVoteStop(VoteHelper voter, VoteCompletedCallback.Reason reason){
        if (reason == VoteCompletedCallback.Reason.TIMEOUT){
            server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                            "battlegrounds.vote.timeout.broadcast", TextUtil.GOLD), false);
        }
        else if (reason == VoteCompletedCallback.Reason.MANUAL){
            server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                            "battlegrounds.vote.manual.broadcast", TextUtil.GOLD), false);
        }
        else if (reason == VoteCompletedCallback.Reason.ACCEPT){
            server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.game.start.broadcast", TextUtil.GREEN, config.serverCloseDelaySeconds), false);
            stopTask = new TaskCountdown(
                    () -> {
                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
                            PlayerUtil.sendTitle(player, TextUtil.withColor(
                                    Text.literal(String.valueOf(stopTask.getCount())), TextUtil.GREEN));
                            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7f, 1.0f);
                        }
                    },
                    () -> gameHelper.stopServer(TextUtil.translatableWithColor(
                            "battlegrounds.game.server.stop", TextUtil.GREEN)),
                    0, 20, config.serverCloseDelaySeconds
            );
            taskScheduler.runTask(stopTask);
            progress.players = Arrays.stream(voter.getProfiles()).map((GameProfile profile) ->
                    profile.getId().toString()).toList();
            progress.progress = GameProgress.Progress.RESET_WORLD;
        }

        return ActionResult.PASS;
    }

    private ActionResult onPlayerDamaged(DamageSource source, PlayerEntity instance){
        if (progress.pvpMode == GameProgress.PVPMode.PEACEFUL){
            return ActionResult.FAIL;
        }
        else if (progress.pvpMode == GameProgress.PVPMode.NO_PVP){
            if (source.getSource() != null && source.getSource().isPlayer()){
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }

    public static void saveConfigs(){
        ConfigUtil.saveConfig(CONFIG_PATH, "config", config);
        if (server != null){
            if (gameHelper.reduceTask.getDelay() >= 0) progress.resizeLapTimer = gameHelper.reduceTask.getDelay();
            ConfigUtil.saveConfig(server.getSavePath(WorldSavePath.ROOT), "bg_progress", progress);
        }
    }

    public static void loadConfigs(){
        config = ConfigUtil.createOrLoadConfig(CONFIG_PATH, "config", RootConfig.class);
        if (server != null) {
            progress = ConfigUtil.createOrLoadConfig(server.getSavePath(WorldSavePath.ROOT), "bg_progress", GameProgress.class);
        }
    }

    public static void reload(){
        loadConfigs();
        gameHelper.reduceTask.cancel();
        if (progress.progress.isStarted() && progress.progress != GameProgress.Progress.DEATHMATCH){
            gameHelper.runTasks();
        }
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment){
        if (environment.dedicated){
            BattlegroundsCommand.register(dispatcher);
            RandomTpCommand.register(dispatcher);
        }
        else{
            logger.error("Battlegrounds 只可在专用服务器 (Dedicated Server) 上运行！");
        }
    }

    private ActionResult onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfoReturnable<Text> cir){
        if (progress.progress.isPreparing()){
            if (!progress.players.contains(player.getGameProfile().getId().toString())){
                player.changeGameMode(GameMode.SPECTATOR);
                cir.setReturnValue(TextUtil.translatableWithColor("battlegrounds.game.join.spectator.broadcast",
                        TextUtil.DARK_GRAY, player.getName()));
            }
            else{
                cir.setReturnValue(TextUtil.translatableWithColor("battlegrounds.game.join.broadcast",
                        TextUtil.GREEN, player.getName(), gameHelper.getInGamePlayers() + 1, progress.players.size()));
                if (server.getPlayerManager().getPlayerList().size() + 1 == progress.players.size()){
                    server.getPlayerManager().broadcast(TextUtil.translatableWithColor("battlegrounds.game.already.broadcast",
                            TextUtil.GREEN), false);
                    startTask = new TaskCountdown(
                            () -> {
                                for (ServerPlayerEntity player1 : server.getPlayerManager().getPlayerList()){
                                    PlayerUtil.sendTitle(player1, TextUtil.withColor(
                                            Text.literal(String.valueOf(startTask.getCount())), TextUtil.GREEN));
                                    player1.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7f, 1.0f);
                                }
                            },
                            () -> gameHelper.startGame(),
                            0, 20, config.gameStartDelaySeconds
                    );
                    taskScheduler.runTask(startTask);
                }
            }
        }
        else if (progress.progress == GameProgress.Progress.DEATHMATCH){
            player.sendMessage(TextUtil.translatableWithColor(
                    "battlegrounds.game.deathmatch.start.broadcast", TextUtil.GOLD), false);
        }

        return ActionResult.PASS;
    }

    @Override
    public void onInitialize() {
        logger.info("正在加载 Battlegrounds");

        // 注册指令
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        // 加载配置
        loadConfigs();
        // 注册事件
        ServerLoadedCallback.EVENT.register(this::onServerLoaded);
        WorldLoadedCallback.EVENT.register(this::onWorldLoaded);
        ServerSavingCallback.EVENT.register((server, suppressLogs) -> {
            saveConfigs();
            loadConfigs();
            if (!suppressLogs) logger.info("已保存游戏配置");

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
    }
}
