package com.github.winexp.battlegrounds.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

public class TextUtil {
    public static final TextColor GREEN = TextColor.fromFormatting(Formatting.GREEN);
    public static final TextColor GOLD = TextColor.fromFormatting(Formatting.GOLD);
    public static final TextColor RED = TextColor.fromFormatting(Formatting.RED);
    public static final TextColor DARK_GRAY = TextColor.fromFormatting(Formatting.DARK_GRAY);
    public static final TextColor GRAY = TextColor.fromFormatting(Formatting.GRAY);

    public static MutableText withColor(MutableText text, TextColor color) {
        return text.styled(style -> style.withColor(color));
    }

    public static MutableText translatableWithColor(String key, TextColor color, Object... args) {
        return withColor(MutableText.of(new TranslatableTextContent(key, null, args)), color);
    }
}
