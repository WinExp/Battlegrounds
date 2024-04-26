package com.github.winexp.battlegrounds.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
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
                addEntry(entries, Items.PVP_PRO_SWORD);
                addEntry(entries, Items.SEVEN_ELEVEN_SWORD);
                addEntry(entries, Items.STEVES_PAIN_SWORD);
                addEntry(entries, Items.MY_HOLY_SWORD);
                addEntry(entries, Items.LEACHING_SWORD);
                addEntry(entries, Items.MINERS_PICKAXE);
                addEntry(entries, Items.BUTCHERS_AXE);
                addEntry(entries, Items.CHANNELING_BOW);
                addEntry(entries, Items.FLASH_BANG);
                addEntry(entries, Items.MOLOTOV);
                addEntry(entries, Items.RUPERTS_TEAR);
                addEntry(entries, Items.KNOCKBACK_STICK);
                for (RegistryEntry.Reference<Enchantment> enchantmentEntry : Registries.ENCHANTMENT.streamEntries().toList()) {
                    if (enchantmentEntry.registryKey().getValue().getNamespace().equals("battlegrounds")) {
                        Enchantment enchantment = enchantmentEntry.value();
                        for (int level = 1; level <= enchantment.getMaxLevel(); level++) {
                            entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level)));
                        }
                    }
                }

                addEntry(entries, Items.PRECISION_CORE);
                addEntry(entries, Items.ADVANCED_PRECISION_CORE);

                addEntry(entries, Items.BEEF_NOODLE_SOUP);
                addEntry(entries, Items.SIX_FLAVOURED_DIHUANG_PILL);
            })
            .build());

    private static void addEntry(ItemGroup.Entries entries, Item item) {
        entries.add(item.getDefaultStack());
    }

    public static ItemGroup register(String name, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier("battlegrounds", name), itemGroup);
    }

    public static void registerItemGroups() {
    }
}
