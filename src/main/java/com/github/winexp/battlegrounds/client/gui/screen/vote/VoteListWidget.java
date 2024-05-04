package com.github.winexp.battlegrounds.client.gui.screen.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public class VoteListWidget extends AlwaysSelectedEntryListWidget<VoteListWidget.Entry> {
    private static final int LINES = 4;
    private static final int LINE_HEIGHT = 10;
    private final VoteScreen screen;
    private final List<Entry> entries = new CopyOnWriteArrayList<>();

    public VoteListWidget(VoteScreen screen, MinecraftClient minecraftClient, int width, int height, int y) {
        super(minecraftClient, width, height, y, LINES * LINE_HEIGHT + 4);
        this.screen = screen;
    }

    private void updateEntries() {
        this.clearEntries();
        for (Entry entry : this.entries) {
            this.addEntry(entry);
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 20;
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.screen.updateButtonState();
    }

    public void setEntries(Collection<Entry> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
        this.updateEntries();
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> implements AutoCloseable {
        public Entry() {
        }

        @Override
        public void close() {
        }
    }

    @Environment(EnvType.CLIENT)
    public static final class VoteEntry extends Entry {
        private final MinecraftClient client;
        public final VoteInfo voteInfo;
        private boolean selectable = true;

        public VoteEntry(VoteInfo voteInfo) {
            this.client = MinecraftClient.getInstance();
            this.voteInfo = voteInfo;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.isSelectable();
        }

        @Override
        public Text getNarration() {
            MutableText text = Text.empty();
            text.append(Text.translatable("narrator.select", this.voteInfo.name));
            text.append(ScreenTexts.SENTENCE_SEPARATOR);
            text.append(this.voteInfo.description);
            text.append(ScreenTexts.SENTENCE_SEPARATOR);
            text.append(Text.translatable("vote.battlegrounds.initiator", this.getInitiatorName()));
            text.append(ScreenTexts.SENTENCE_SEPARATOR);
            text.append(Text.translatable("gui.battlegrounds.vote.time_left", this.voteInfo.timeLeft / 20));
            return text;
        }

        private Text getInitiatorName() {
            return Text.translatableWithFallback("vote.battlegrounds.initiator." + this.voteInfo.initiatorName.getString(), this.voteInfo.initiatorName.getString());
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(this.client.textRenderer, this.voteInfo.name, x + 3, y + 1, Colors.WHITE, false);
            context.drawText(this.client.textRenderer, this.voteInfo.description, x + 3, y + 1 + LINE_HEIGHT, Colors.GRAY, false);
            context.drawText(this.client.textRenderer,
                    Text.translatable("vote.battlegrounds.initiator", this.getInitiatorName()),
                    x + 3, y + 1 + LINE_HEIGHT * 2, Colors.GRAY, false);
            context.drawText(this.client.textRenderer,
                    Text.translatable("gui.battlegrounds.vote.time_left", this.voteInfo.timeLeft / 20),
                    x + 3, y + 1 + LINE_HEIGHT * 3, Colors.GRAY, false);
        }

        public boolean isSelectable() {
            return this.selectable && this.voteInfo.available;
        }

        public void setSelectable(boolean selectable) {
            this.selectable = selectable;
        }
    }
}
