package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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

                entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.SMELTING, 1)));
                entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.STEVES_PAIN, 1)));
                entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.LEACHING, 1)));
                entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.CHANNELING_PRO, 1)));
                entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.VITALITY, 3)));
            })
            .build());

    public static ItemGroup register(String name, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier("battlegrounds", name), itemGroup);
    }

    public static void registerItemGroups() {
    }
}
