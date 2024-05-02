package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ModItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        // vanilla
        this.getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(Items.PVP_PRO_SWORD)
                .add(Items.SEVEN_ELEVEN_SWORD)
                .add(Items.STEVES_PAIN_SWORD)
                .add(Items.MY_HOLY_SWORD)
                .add(Items.LEACHING_SWORD);
        this.getOrCreateTagBuilder(ItemTags.PICKAXES)
                .add(Items.MINERS_PICKAXE);
        this.getOrCreateTagBuilder(ItemTags.AXES)
                .add(Items.BUTCHERS_AXE);
        // conventional
        this.getOrCreateTagBuilder(ConventionalItemTags.BOWS)
                .add(Items.CHANNELING_BOW);
        this.getOrCreateTagBuilder(ConventionalItemTags.FOODS)
                .add(Items.BEEF_NOODLE_SOUP)
                .add(Items.SIX_FLAVOURED_DIHUANG_PILL);
    }
}
