package com.github.winexp.battlegrounds.client.gui.screen;

import com.github.winexp.battlegrounds.client.gui.widget.TextScaledButtonWidget;
import com.github.winexp.battlegrounds.client.util.render.TextDrawer;
import com.github.winexp.battlegrounds.client.vote.ClientVoteManager;
import com.github.winexp.battlegrounds.discussion.vote.Vote;
import com.github.winexp.battlegrounds.discussion.vote.VotePreset;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class VoteScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/inworld_menu_list_background.png");
    private static final int REFRESH_DELAY = 2000;
    private boolean initialized = false;
    private VoteListWidget voteListWidget;
    private VoteDetailWidget voteDetailWidget;
    private TextScaledButtonWidget refreshButton;
    private static long lastRefreshTime = System.currentTimeMillis() - REFRESH_DELAY;

    static {
        ClientVoteManager.INSTANCE.registerListener(new ClientVoteManager.Listener() {
            @Nullable
            private VoteScreen getVoteScreen() {
                if (MinecraftClient.getInstance().currentScreen instanceof VoteScreen voteScreen && voteScreen.initialized) return voteScreen;
                else return null;
            }

            @Override
            public void onSyncVotes(Collection<Vote> votes) {
                VoteScreen voteScreen = this.getVoteScreen();
                if (voteScreen != null) {
                    voteScreen.voteListWidget.replaceVotes(votes);
                }
            }

            @Override
            public void onVoteAdded(Vote vote) {
                VoteScreen voteScreen = this.getVoteScreen();
                if (voteScreen != null) {
                    voteScreen.voteListWidget.addVote(vote);
                }
            }

            @Override
            public void onVoteUpdated(Vote vote) {
                VoteScreen voteScreen = this.getVoteScreen();
                if (voteScreen != null) {
                    voteScreen.voteListWidget.setVote(vote);
                }
            }

            @Override
            public void onVoteRemoved(UUID uuid) {
                VoteScreen voteScreen = this.getVoteScreen();
                if (voteScreen != null) {
                    voteScreen.voteListWidget.removeVote(uuid);
                }
            }
        });
    }

    public VoteScreen() {
        super(Text.translatable("gui.battlegrounds.vote.title"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        RenderSystem.enableBlend();
        context.drawTexture(BACKGROUND_TEXTURE, 0, 45, 0, 0, this.width, this.height - 90, 32, 32);
    }

    @Override
    public void tick() {
        this.updateButtonState();
    }

    private void updateButtonState() {
        this.refreshButton.active = lastRefreshTime + REFRESH_DELAY <= System.currentTimeMillis();
    }

    @Override
    protected void init() {
        this.initialized = false;
        this.voteListWidget = this.addDrawableChild(new VoteListWidget(this.client, (this.width - 50) / 2, this.height - 120, 60));
        this.voteDetailWidget = this.addDrawableChild(new VoteDetailWidget((this.width + 50) / 2, 80, (this.width - 50) / 2, this.height - 160));
        this.refreshButton = this.addDrawableChild(TextScaledButtonWidget.builder(Text.of("↻"), button -> {
            ClientVoteManager.INSTANCE.syncVotes();
            lastRefreshTime = System.currentTimeMillis();
            this.voteDetailWidget.clear();
            this.updateButtonState();
        }).dimensions(10, 55, 20, 20).textScale(2.0F).build());
        this.updateButtonState();
        this.voteListWidget.addAllVotes(ClientVoteManager.INSTANCE.getVotes());
        this.initialized = true;
    }

    @Environment(EnvType.CLIENT)
    private class VoteListWidget extends ElementListWidget<VoteListWidget.Entry> {
        private static final int ITEM_HEIGHT = 40;
        private final Map<UUID, Entry> voteEntryMap = new Object2ObjectOpenHashMap<>();

        private VoteListWidget(MinecraftClient minecraftClient, int width, int height, int y) {
            super(minecraftClient, width, height, y, ITEM_HEIGHT);
        }

        public void replaceVotes(Collection<Vote> votes) {
            this.clearVotes();
            this.addAllVotes(votes);
        }

        public void clearVotes() {
            this.clearEntries();
            this.voteEntryMap.clear();
        }

        public void addVote(Vote vote) {
            Objects.requireNonNull(vote);
            Entry entry = new Entry(vote);
            this.addEntry(entry);
            this.voteEntryMap.put(vote.getUuid(), entry);
        }

        public void addAllVotes(Collection<Vote> votes) {
            for (Vote vote : votes) {
                this.addVote(vote);
            }
        }

        public void removeVote(UUID uuid) {
            this.removeEntryWithoutScrolling(this.voteEntryMap.get(uuid));
            this.voteEntryMap.remove(uuid);
            VoteScreen.this.voteDetailWidget.update(uuid, null);
        }

        private void setVote(@NotNull Vote vote) {
            Objects.requireNonNull(vote);
            Objects.requireNonNull(this.voteEntryMap.get(vote.getUuid())).vote = vote;
            VoteScreen.this.voteDetailWidget.update(vote.getUuid(), vote);
        }

        @Override
        protected boolean isSelectedEntry(int index) {
            return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            if (entry != null) {
                VoteScreen.this.voteDetailWidget.vote = entry.vote;
            }
        }

        @Override
        public int getRowWidth() {
            return 200;
        }

        @Override
        protected void drawHeaderAndFooterSeparators(DrawContext context) {
        }

        @Override
        protected void drawMenuListBackground(DrawContext context) {
        }

        @Override
        protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
            int entryLeft = this.getRowLeft();
            int entryRight = this.getRowRight();
            context.drawBorder(entryLeft - 2, y - 2, entryWidth, entryHeight + 4, ColorHelper.Argb.withAlpha(150, borderColor));
            context.fill(entryLeft - 1, y - 1, entryRight - 3, y + entryHeight + 1, ColorHelper.Argb.withAlpha(130, fillColor));
        }

        @Environment(EnvType.CLIENT)
        private static class Entry extends ElementListWidget.Entry<Entry> {
            private static final ButtonTextures ACCEPT_BUTTON_TEXTURES = new ButtonTextures(new Identifier("widget/button"), new Identifier("widget/button_disabled"), new Identifier("widget/button_highlighted"));
            private final TextDrawer textDrawer = new TextDrawer();
            private final ButtonWidget acceptButton;
            private final ButtonWidget declineButton;
            private final List<ClickableWidget> widgets;
            private Vote vote;

            private Entry(Vote vote) {
                this.vote = vote;
                this.acceptButton = new VoteButtonWidget(20, 20, ACCEPT_BUTTON_TEXTURES, button ->
                        ClientVoteManager.INSTANCE.vote(this.vote.getUuid(), true), Text.translatable("vote.action.accept"));
                this.declineButton = new VoteButtonWidget(20, 20, ACCEPT_BUTTON_TEXTURES, button ->
                        ClientVoteManager.INSTANCE.vote(this.vote.getUuid(), false), Text.translatable("vote.action.accept"));
                this.widgets = ImmutableList.of(this.acceptButton, this.declineButton);
            }

            private void renderWidgets(DrawContext context, int x, int y, int mouseX, int mouseY, float tickDelta) {
                this.acceptButton.setPosition(x + 149, y + 8);
                this.acceptButton.render(context, mouseX, mouseY, tickDelta);
                this.declineButton.setPosition(x + 172, y + 8);
                this.declineButton.render(context, mouseX, mouseY, tickDelta);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return !super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                this.textDrawer.setLineY(y + 9.5F);
                this.textDrawer.draw(context, TextDrawer.trim(this.vote.getName(), 137, 2.0F), x + 5, Colors.WHITE, 2.0F, true);
                this.renderWidgets(context, x, y, mouseX, mouseY, tickDelta);
            }

            @Override
            public List<? extends Element> children() {
                return this.widgets;
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                return this.widgets;
            }

            private static class VoteButtonWidget extends TexturedButtonWidget {
                public VoteButtonWidget(int width, int height, ButtonTextures textures, PressAction pressAction, Text message) {
                    super(width, height, textures, pressAction, message);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private static class VoteDetailWidget extends ContainerWidget {
        private final TextDrawer textDrawer = new TextDrawer();
        private final int textMaxWidth;
        private Vote vote;

        public VoteDetailWidget(int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty());
            this.textMaxWidth = width - 50;
        }

        private void clear() {
            this.vote = null;
        }

        private void update(UUID uuid, @Nullable Vote vote) {
            if (this.vote != null && this.vote.getUuid().equals(uuid)) this.vote = vote;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = this.getX();
            int y = this.getY();
            if (this.vote == null) return;

            this.textDrawer.setLineY(y + 3);
            this.textDrawer.drawWrap(context, this.vote.getName(), x + 3, this.textMaxWidth, -1, 2.4F, true);
            this.textDrawer.drawWrap(context, this.vote.getDescription(), x + 3, this.textMaxWidth, -1, 1.4F, false);
            this.textDrawer.drawWrap(context, Text.translatable("gui.battlegrounds.vote.remaining", this.vote.getExpirationTicksRemaining() / 20), x + 3, this.textMaxWidth, Colors.GRAY, 1.4F, false);
            this.textDrawer.wrap(2, 1.0F);
            this.textDrawer.drawWrap(context, Text.of("UUID: " + this.vote.getUuid().toString()), x + 3, this.textMaxWidth, -1, 1.0F, false);
            Identifier votePresetId = VotePreset.getIdentifier(this.vote.getInfo());
            if (votePresetId != null) {
                this.textDrawer.drawWrap(context, Text.of("Preset: " + votePresetId), x + 3, this.textMaxWidth, -1, 1.0F, false);
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }
    }
}
