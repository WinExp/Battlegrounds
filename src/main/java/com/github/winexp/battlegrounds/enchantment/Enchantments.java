package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Enchantments extends net.minecraft.enchantment.Enchantments {
    public static final SmeltingEnchantment SMELTING = register("smelting", new SmeltingEnchantment());
    public static final ChannelingProEnchantment CHANNELING_PRO = register("channeling_pro", new ChannelingProEnchantment());
    public static final StevesPainEnchantment STEVES_PAIN = register("steves_pain", new StevesPainEnchantment());
    public static final VitalityEnchantment VITALITY = register("vitality", new VitalityEnchantment());
    public static final LeachingEnchantment LEACHING = register("leaching", new LeachingEnchantment());

    public static <T extends Enchantment> T register(String name, T enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier("battlegrounds", name), enchantment);
    }

    public static void bootstrap() {
    }
}
