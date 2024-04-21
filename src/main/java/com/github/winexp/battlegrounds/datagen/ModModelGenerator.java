package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(Items.PVP_PRO_SWORD, Models.HANDHELD);
        itemModelGenerator.register(Items.SEVEN_ELEVEN_SWORD, Models.HANDHELD);
        itemModelGenerator.register(Items.STEVES_PAIN_SWORD, Models.HANDHELD);
        itemModelGenerator.register(Items.LEACHING_SWORD, Models.HANDHELD);
        itemModelGenerator.register(Items.MINERS_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(Items.BUTCHERS_AXE, Models.HANDHELD);
        itemModelGenerator.register(Items.FLASH_BANG, Models.GENERATED);
        itemModelGenerator.register(Items.MOLOTOV, Models.GENERATED);
        itemModelGenerator.register(Items.RUPERTS_TEAR, Models.GENERATED);
        itemModelGenerator.register(Items.KNOCKBACK_STICK, Models.GENERATED);
        itemModelGenerator.register(Items.BEEF_NOODLE_SOUP, Models.GENERATED);
    }
}
