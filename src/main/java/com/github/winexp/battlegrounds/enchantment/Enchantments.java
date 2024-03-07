package com.github.winexp.battlegrounds.enchantment;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Enchantments extends net.minecraft.enchantment.Enchantments {
    public static final SmeltingEnchantment SMELTING = (SmeltingEnchantment) register("smelting", new SmeltingEnchantment());
    public static final ChannelingProEnchantment CHANNELING_PRO = (ChannelingProEnchantment) register("channeling_pro", new ChannelingProEnchantment());
    public static final StevesPainEnchantment STEVES_PAIN = (StevesPainEnchantment) register("steves_pain", new StevesPainEnchantment());
    public static final VitalityEnchantment VITALITY = (VitalityEnchantment) register("vitality", new VitalityEnchantment());
    public static final LeachingEnchantment LEACHING = (LeachingEnchantment) register("leaching", new LeachingEnchantment());

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier("battlegrounds", name), enchantment);
    }

    public static void registerEnchantments() {
    }

    public static void registerItemGroup() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.add(EnchantedBookItem.forEnchantment(
                    new EnchantmentLevelEntry(SMELTING, 1)));
            content.add(EnchantedBookItem.forEnchantment(
                    new EnchantmentLevelEntry(CHANNELING_PRO, 1)));
            content.add(EnchantedBookItem.forEnchantment(
                    new EnchantmentLevelEntry(STEVES_PAIN, 1)));
            content.add(EnchantedBookItem.forEnchantment(
                    new EnchantmentLevelEntry(VITALITY, 3)));
            content.add(EnchantedBookItem.forEnchantment(
                    new EnchantmentLevelEntry(LEACHING, 1)));
        });
    }
}
