package com.github.winexp.battlegrounds.item.tool;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public enum ToolMaterials implements ToolMaterial {
    PVP_PRO_SWORD(2573, 9.0F, 5.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 0, Ingredient::empty),
    SEVEN_ELEVEN_SWORD(2081, 9.0F, 4.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 0, Ingredient::empty),
    STEVES_PAIN_SWORD(1837, 9.0F, 6.0F, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 0, Ingredient::empty),
    MY_HOLY_SWORD(1923, 9.0F, 5.0F, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 0, Ingredient::empty),
    LEACHING_SWORD(1792, 8.0F, 3.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 0, Ingredient::empty),
    MINERS_PICKAXE(998, 8.0F, 3.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 0, Ingredient::empty),
    BUTCHERS_AXE(3, 8.0F, 24.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 0, Ingredient::empty),
    RUPERTS_TEAR(12, 0, 0, BlockTags.INCORRECT_FOR_WOODEN_TOOL, 0, Ingredient::empty),
    KNOCKBACK_STICK(15, 0, 0, BlockTags.INCORRECT_FOR_WOODEN_TOOL, 0, Ingredient::empty);

    private final int durability;
    private final float miningSpeed;
    private final float attackDamage;
    private final TagKey<Block> inverseTag;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    ToolMaterials(int durability, float miningSpeed, float attackDamage, TagKey<Block> inverseTag, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.inverseTag = inverseTag;
        this.enchantability = enchantability;
        this.repairIngredient = Suppliers.memoize(repairIngredient);
    }

    @Override
    public int getDurability() {
        return this.durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return this.inverseTag;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
