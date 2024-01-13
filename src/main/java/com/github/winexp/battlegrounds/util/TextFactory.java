package com.github.winexp.battlegrounds.util;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class TextFactory {
    public final static Text LINEFEED = Text.of("\n");

    public final static MutableText HELP_TEXT = Text.literal("[Battlegrounds]").formatted(Formatting.GOLD)
            .append(LINEFEED)
            .append(Text.translatable("battlegrounds.command.start.description"))
            .append(LINEFEED)
            .append(Text.translatable("battlegrounds.command.stop.description"))
            .append(LINEFEED)
            .append(Text.translatable("battlegrounds.command.accept.description"))
            .append(LINEFEED)
            .append(Text.translatable("battlegrounds.command.deny.description"))
            .append(LINEFEED)
            .append(Text.translatable("battlegrounds.command.help.description"));

    public final static MutableText ACCEPT_BUTTON = TextUtil.translatableWithColor(
            "battlegrounds.vote.button.accept", TextUtil.GREEN).styled(style -> style.withClickEvent(
                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bg accept"))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.translatable("battlegrounds.vote.button.accept.description")))
    );

    public final static MutableText DENY_BUTTON = TextUtil.translatableWithColor(
            "battlegrounds.vote.button.deny", TextUtil.GOLD).styled(style -> style.withClickEvent(
                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bg deny"))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.translatable("battlegrounds.vote.button.deny.description")))
    );
}
