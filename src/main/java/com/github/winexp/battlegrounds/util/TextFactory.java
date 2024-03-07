package com.github.winexp.battlegrounds.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextFactory {
    public static final Text LINEFEED = Text.of("\n");

    public static final MutableText ACCEPT_BUTTON = TextUtil.translatableWithColor(
            "battlegrounds.vote.button.accept", TextUtil.GREEN).styled(style -> style.withClickEvent(
                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bg accept"))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.translatable("battlegrounds.vote.button.accept.description")))
    );

    public static final MutableText DENY_BUTTON = TextUtil.translatableWithColor(
            "battlegrounds.vote.button.deny", TextUtil.GOLD).styled(style -> style.withClickEvent(
                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bg deny"))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.translatable("battlegrounds.vote.button.deny.description")))
    );
}
