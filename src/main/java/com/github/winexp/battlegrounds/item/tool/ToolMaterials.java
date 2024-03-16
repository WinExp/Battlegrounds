package com.github.winexp.battlegrounds.item.tool;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum ToolMaterials implements ToolMaterial {
    PVP_PRO_SWORD(2573, 9.0F, 5.0F, 4, 0, Ingredient.empty()),
    SEVEN_ELEVEN_SWORD(2081, 9.0F, 4.0F, 4, 0, Ingredient.empty()),
    STEVES_PAIN_SWORD(1837, 9.0F, 3.0F, 4, 0, Ingredient.empty()),
    LEACHING_SWORD(1792, 8.0F, 3.0F, 3, 0, Ingredient.empty()),
    MINERS_PICKAXE(998, 8.0F, 3.0F, 3, 0, Ingredient.empty()),
    BUTCHERS_AXE(3, 8.0F, 14.0F, 3, 0, Ingredient.empty()),
    RUPERTS_TEAR(20, 0, 0, 0, 0, Ingredient.empty()),
    KNOCKBACK_STICK(15, 0, 0, 0, 0, Ingredient.empty());

    private final int durability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int miningLevel;
    private final int enchantability;
    private final Ingredient repairIngredient;

    ToolMaterials(int durability, float miningSpeed, float attackDamage, int miningLevel, int enchantability, Ingredient repairIngredient) {
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.miningLevel = miningLevel;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return miningLevel;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient;
    }
}
