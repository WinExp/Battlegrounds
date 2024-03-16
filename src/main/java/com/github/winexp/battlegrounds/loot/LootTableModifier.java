package com.github.winexp.battlegrounds.loot;

import com.github.winexp.battlegrounds.block.BlockSmeltableRegistry;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class LootTableModifier implements LootTableEvents.Modify {
    @Override
    public void modifyLootTable(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source) {
        this.modifySmelting(id, tableBuilder); // 自动冶炼
    }

    public void modifySmelting(Identifier id, LootTable.Builder tableBuilder) {
        if (BlockSmeltableRegistry.isSmeltable(id)) {
            tableBuilder.apply(BlockSmeltableRegistry.getLootFunction(id));
        }
    }
}
