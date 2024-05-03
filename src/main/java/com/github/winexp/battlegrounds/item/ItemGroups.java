package com.github.winexp.battlegrounds.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroups extends net.minecraft.item.ItemGroups {
    public static final ItemGroup ROOT_ITEM_GROUP = register("root_item_group", FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.PVP_PRO_SWORD))
            .displayName(Text.translatable("itemGroup.battlegrounds.root"))
            .entries((context, entries) -> {
                entries.add(Items.PVP_PRO_SWORD);
                entries.add(Items.SEVEN_ELEVEN_SWORD);
                entries.add(Items.STEVES_PAIN_SWORD);
                entries.add(Items.MY_HOLY_SWORD);
                entries.add(Items.LEACHING_SWORD);
                entries.add(Items.MINERS_PICKAXE);
                entries.add(Items.BUTCHERS_AXE);
                entries.add(Items.CHANNELING_BOW);
                entries.add(Items.FLASH_BANG);
                entries.add(Items.MOLOTOV);
                entries.add(Items.RUPERTS_TEAR);
                entries.add(Items.KNOCKBACK_STICK);
                for (RegistryEntry.Reference<Enchantment> enchantmentEntry : Registries.ENCHANTMENT.streamEntries().toList()) {
                    if (enchantmentEntry.registryKey().getValue().getNamespace().equals("battlegrounds")) {
                        Enchantment enchantment = enchantmentEntry.value();
                        for (int level = 1; level <= enchantment.getMaxLevel(); level++) {
                            entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level)));
                        }
                    }
                }

                entries.add(Items.PRECISION_CORE);
                entries.add(Items.ADVANCED_PRECISION_CORE);
                entries.add(Items.SEVEN_ELEVEN_PRECISION_CORE);

                entries.add(Items.BEEF_NOODLE_SOUP);
                entries.add(Items.SIX_FLAVOURED_DIHUANG_PILL);
            })
            .build());

    public static ItemGroup register(String name, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier("battlegrounds", name), itemGroup);
    }

    public static void bootstrap() {
    }
}
