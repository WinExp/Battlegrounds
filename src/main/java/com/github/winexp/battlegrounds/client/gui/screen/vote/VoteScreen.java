package com.github.winexp.battlegrounds.client.gui.screen.vote;

import com.github.winexp.battlegrounds.client.KeyBindings;
import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.UpdateVoteInfoPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.VotePayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.SyncVoteInfosPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.UpdateVoteInfoPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.SyncVoteInfosPayloadS2C;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class VoteScreen extends Screen {
    private static final List<VoteInstance> voteInstancesCache = new CopyOnWriteArrayList<>();
    private static long lastRefreshTime = 0;
    private VoteListWidget voteList;
    private ButtonWidget acceptButton;
    private ButtonWidget denyButton;
    private ButtonWidget refreshButton;

    public VoteScreen() {
        super(Text.translatable("gui.battlegrounds.vote.title"));
    }

    public static void globalTick(MinecraftClient client) {
        // 按键绑定
        while (KeyBindings.VOTE_SCREEN.wasPressed()) {
            client.setScreen(new VoteScreen());
        }
    }

    public void updateButtonState() {
        this.acceptButton.active = false;
        this.denyButton.active = false;
        this.refreshButton.active = lastRefreshTime + 2000 <= System.currentTimeMillis();
        VoteListWidget.Entry entry = this.voteList.getSelectedOrNull();
        if (entry != null) {
            this.acceptButton.active = true;
            this.denyButton.active = true;
        }
    }

    public static void syncVoteInfos() {
        ClientPlayNetworking.send(new SyncVoteInfosPayloadC2S());
    }

    public static void onVoteOpened(MinecraftClient client, VoteInstance voteInstance) {
        voteInstancesCache.add(voteInstance);
        if (client.currentScreen instanceof VoteScreen voteScreen) {
            voteScreen.updateVotesGUI();
        }
    }

    public static void onVoteClosed(MinecraftClient client, VoteInstance voteInstance) {
        voteInstancesCache.removeIf(voteInstance1 -> voteInstance1.equals(voteInstance));
        if (client.currentScreen instanceof VoteScreen voteScreen) {
            voteScreen.updateVotesGUI();
        }
    }

    public static void onVoteInfosReceived(MinecraftClient client, SyncVoteInfosPayloadS2C packet) {
        voteInstancesCache.clear();
        voteInstancesCache.addAll(packet.voteInstances());
        if (client.currentScreen instanceof VoteScreen voteScreen) {
            voteScreen.updateVotesGUI();
        }
    }

    public static void onVoteExpired(VoteInstance voteInstance) {
        updateVoteInfo(voteInstance.getIdentifier());
    }

    public static void updateVoteInfo(Identifier identifier) {
        UpdateVoteInfoPayloadC2S packet = new UpdateVoteInfoPayloadC2S(identifier);
        ClientPlayNetworking.send(packet);
    }

    public static void onUpdateVoteInfo(MinecraftClient client, UpdateVoteInfoPayloadS2C packet) {
        voteInstancesCache.removeIf(voteInstance1 -> voteInstance1.getIdentifier().equals(packet.identifier()));
        if (packet.voteInstance().isPresent()) {
            voteInstancesCache.add(packet.voteInstance().get());
        }
        if (client.currentScreen instanceof VoteScreen voteScreen) {
            voteScreen.updateVotesGUI();
        }
    }

    public void updateVotesGUI() {
        this.voteList.setEntries(voteInstancesCache.stream()
                .map(VoteListWidget.VoteEntry::new)
                .collect(Collectors.toList()));
        this.updateButtonState();
    }

    private void onVoteButtonClicked(boolean result) {
        VoteListWidget.Entry entry = this.voteList.getSelectedOrNull();
        if (entry instanceof VoteListWidget.VoteEntry voteEntry) {
            Identifier identifier = voteEntry.voteInstance.getIdentifier();
            ClientPlayNetworking.send(new VotePayloadC2S(identifier, result));
            voteEntry.setSelectable(false);
        }
        this.voteList.setSelected(null);
        this.updateButtonState();
    }

    private void refresh() {
        syncVoteInfos();
        lastRefreshTime = System.currentTimeMillis();
        this.updateButtonState();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        this.refreshButton.active = lastRefreshTime + 2000 <= System.currentTimeMillis();
    }

    @Override
    protected void init() {
        this.voteList = this.addDrawableChild(new VoteListWidget(this, this.client, this.width, this.height / 2 - 20, 50));
        this.acceptButton = this.addDrawableChild(ButtonWidget
                .builder(Text.translatable("gui.battlegrounds.vote.accept"),
                        button -> this.onVoteButtonClicked(true))
                .dimensions(this.width / 2 - 85, this.height / 2 + 50, 80, 20)
                .build());
        this.denyButton = this.addDrawableChild(ButtonWidget
                .builder(Text.translatable("gui.battlegrounds.vote.deny"),
                        button -> this.onVoteButtonClicked(false))
                .dimensions(this.width / 2 + 5, this.height / 2 + 50, 80, 20)
                .build());
        this.refreshButton = this.addDrawableChild(ButtonWidget
                .builder(Text.translatable("gui.battlegrounds.vote.refresh"),
                        button -> this.refresh())
                .dimensions(this.width / 2 - 40, this.height / 2 + 80, 80, 20)
                .build());
        this.updateButtonState();
        this.updateVotesGUI();
    }
}
