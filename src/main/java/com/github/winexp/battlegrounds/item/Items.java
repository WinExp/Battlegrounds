package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.item.tool.PVPProSwordItem;
import com.github.winexp.battlegrounds.item.tool.materials.ToolMaterials;
import com.github.winexp.battlegrounds.util.RecipeUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class Items extends net.minecraft.item.Items {
    public final static PVPProSwordItem PVP_PRO_SWORD = new PVPProSwordItem(ToolMaterials.PVP_PRO, 3, -2.4F, new Item.Settings().rarity(Rarity.RARE));

    public static void registerItems(){
        Registry.register(Registries.ITEM,
                new Identifier("battlegrounds", "pvp_pro_sword"),
                PVP_PRO_SWORD
        );
    }

    public static void registerItemGroup(){
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(PVP_PRO_SWORD.getItemStack());
        });
    }

    public static void addRecipes(){
        ShapedRecipe recipe = PVPProSwordItem.getRecipe();
        RecipeEntry<ShapedRecipe> entry = new RecipeEntry<>(
                new Identifier("battlegrounds", "pvp_pro_sword"),
                recipe
        );
        RecipeUtil.addRecipe(entry);
    }
}
