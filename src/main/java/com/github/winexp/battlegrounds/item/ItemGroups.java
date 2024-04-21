package com.github.winexp.battlegrounds.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroups extends net.minecraft.item.ItemGroups {
    public static final ItemGroup ROOT_ITEM_GROUP = register("root_item_group", FabricItemGroup.builder()
            .icon(Items.PVP_PRO_SWORD::getDefaultStack)
            .displayName(Text.translatable("itemGroup.battlegrounds.root"))
            .entries((context, entries) -> {
                entries.add(Items.PVP_PRO_SWORD.getDefaultStack());
                entries.add(Items.SEVEN_ELEVEN_SWORD.getDefaultStack());
                entries.add(Items.STEVES_PAIN_SWORD.getDefaultStack());
                entries.add(Items.LEACHING_SWORD.getDefaultStack());
                entries.add(Items.MINERS_PICKAXE.getDefaultStack());
                entries.add(Items.BUTCHERS_AXE.getDefaultStack());
                entries.add(Items.CHANNELING_BOW.getDefaultStack());
                entries.add(Items.FLASH_BANG.getDefaultStack());
                entries.add(Items.MOLOTOV.getDefaultStack());
                entries.add(Items.RUPERTS_TEAR.getDefaultStack());
                entries.add(Items.KNOCKBACK_STICK.getDefaultStack());
                for (RegistryEntry.Reference<Enchantment> enchantmentEntry : Registries.ENCHANTMENT.streamEntries().toList()) {
                    if (enchantmentEntry.registryKey().getValue().getNamespace().equals("battlegrounds")) {
                        Enchantment enchantment = enchantmentEntry.value();
                        for (int level = 1; level <= enchantment.getMaxLevel(); level++) {
                            entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level)));
                        }
                    }
                }

                entries.add(Items.BEEF_NOODLE_SOUP.getDefaultStack());
            })
            .build());

    public static ItemGroup register(String name, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier("battlegrounds", name), itemGroup);
    }

    public static void registerItemGroups() {
    }
}
