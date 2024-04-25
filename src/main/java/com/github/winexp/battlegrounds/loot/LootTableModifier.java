package com.github.winexp.battlegrounds.loot;

import com.github.winexp.battlegrounds.block.BlockSmeltableRegistry;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;

public class LootTableModifier implements LootTableEvents.Modify {
    @Override
    public void modifyLootTable(RegistryKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source) {
        this.modifySmelting(key, tableBuilder); // 自动冶炼
    }

    public void modifySmelting(RegistryKey<LootTable> key, LootTable.Builder tableBuilder) {
        if (BlockSmeltableRegistry.isSmeltable(key)) {
            tableBuilder.apply(BlockSmeltableRegistry.getLootFunction(key));
        }
    }
}
