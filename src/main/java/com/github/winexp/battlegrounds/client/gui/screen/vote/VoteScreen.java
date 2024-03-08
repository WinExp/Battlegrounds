package com.github.winexp.battlegrounds.client.gui.screen.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.network.packet.c2s.VoteC2SPacket;
import com.github.winexp.battlegrounds.network.packet.c2s.SyncVoteInfoC2SPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.SyncVoteInfoS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class VoteScreen extends Screen {
    private static final CopyOnWriteArrayList<VoteInfo> voteInfosCache = new CopyOnWriteArrayList<>();
    private static long lastRefreshTime = 0;
    private VoteListWidget voteList;
    private ButtonWidget acceptButton;
    private ButtonWidget denyButton;
    private ButtonWidget refreshButton;

    public VoteScreen() {
        super(Text.literal("Vote Screen"));
    }

    static {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            for (VoteInfo voteInfo : voteInfosCache) {
                if (voteInfo.timeLeft > 0) voteInfo.timeLeft--;
            }
        });
    }

    public void updateButtonState() {
        this.acceptButton.active = false;
        this.denyButton.active = false;
        this.refreshButton.active = lastRefreshTime + 2000 <= System.currentTimeMillis();
        VoteListWidget.Entry entry = this.voteList.getSelectedOrNull();
        if (entry != null) {
            if (entry instanceof VoteListWidget.VoteEntry voteEntry
                    && !voteEntry.isSelectable()) return;
            this.acceptButton.active = true;
            this.denyButton.active = true;
        }
    }

    public static void getVoteInfos() {
        ClientPlayNetworking.send(new SyncVoteInfoC2SPacket());
    }

    public static void voteInfoCallback(MinecraftClient client, SyncVoteInfoS2CPacket packet) {
        voteInfosCache.clear();
        voteInfosCache.addAll(packet.voteInfos());
        if (client.currentScreen instanceof VoteScreen voteScreen) {
            voteScreen.updateVotesGUI();
        }
    }

    public void updateVotesGUI() {
        this.voteList.setEntries(voteInfosCache.stream().map((voteInfo ->
                new VoteListWidget.VoteEntry(this.voteList, voteInfo))).collect(Collectors.toList()));
    }

    private void onVoteButtonClicked(boolean result) {
        VoteListWidget.Entry entry = this.voteList.getSelectedOrNull();
        if (entry instanceof VoteListWidget.VoteEntry voteEntry) {
            Identifier identifier = voteEntry.voteInfo.identifier;
            ClientPlayNetworking.send(new VoteC2SPacket(identifier, result));
            voteEntry.setSelectable(false);
        }
        this.updateButtonState();
    }

    private void refresh() {
        getVoteInfos();
        lastRefreshTime = System.currentTimeMillis();
        this.updateButtonState();
    }

    @Override
    public void tick() {
        this.refreshButton.active = lastRefreshTime + 2000 <= System.currentTimeMillis();
    }

    @Override
    protected void init() {
        this.voteList = this.addDrawableChild(new VoteListWidget(this, this.client, this.width, this.height / 2 - 20, 50, 36));
        this.voteList.setRenderBackground(false);
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
