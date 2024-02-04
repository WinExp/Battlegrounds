package com.github.winexp.battlegrounds.enchantment;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Enchantments {
    public final static SmeltingEnchantment SMELTING = new SmeltingEnchantment();
    public final static ChannelingProEnchantment CHANNELING_PRO = new ChannelingProEnchantment();
    public final static StevesPainEnchantment STEVES_PAIN = new StevesPainEnchantment();
    public final static VitalityEnchantment VITALITY = new VitalityEnchantment();
    public final static LeachingEnchantment LEACHING = new LeachingEnchantment();

    public static void registerEnchantments(){
        Registry.register(Registries.ENCHANTMENT, new Identifier("battlegrounds", "smelting"),
                SMELTING);
        Registry.register(Registries.ENCHANTMENT, new Identifier("battlegrounds", "channeling_pro"),
                CHANNELING_PRO);
        Registry.register(Registries.ENCHANTMENT, new Identifier("battlegrounds", "steves_pain"),
                STEVES_PAIN);
        Registry.register(Registries.ENCHANTMENT, new Identifier("battlegrounds", "vitality"),
                VITALITY);
        Registry.register(Registries.ENCHANTMENT, new Identifier("battlegrounds", "leaching"),
                LEACHING);
    }

    public static void registerItemGroup(){
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
