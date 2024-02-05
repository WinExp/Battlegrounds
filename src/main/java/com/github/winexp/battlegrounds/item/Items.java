package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.item.tool.MinersPickaxeItem;
import com.github.winexp.battlegrounds.item.tool.NBTCrafting;
import com.github.winexp.battlegrounds.item.tool.PVPProSwordItem;
import com.github.winexp.battlegrounds.item.tool.ToolMaterials;
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
    public final static MinersPickaxeItem MINERS_PICKAXE = new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, 1, -2.8F, new Item.Settings().rarity(Rarity.RARE));

    public static void registerItems(){
        Registry.register(Registries.ITEM,
                new Identifier("battlegrounds", "pvp_pro_sword"),
                PVP_PRO_SWORD
        );
        Registry.register(Registries.ITEM,
                new Identifier("battlegrounds", "miners_pickaxe"),
                MINERS_PICKAXE
        );
    }

    public static void registerItemGroup(){
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(PVP_PRO_SWORD.getItemStack());
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(MINERS_PICKAXE.getItemStack());
        });
    }

    public static void addRecipes(){
        NBTCrafting[] items = new NBTCrafting[] { PVP_PRO_SWORD, MINERS_PICKAXE };
        for (NBTCrafting item : items){
            RecipeEntry<ShapedRecipe> entry = new RecipeEntry<>(
                    Registries.ITEM.getId((Item) item),
                    item.getRecipe()
            );
            RecipeUtil.addRecipe(entry);
        }
    }
}
