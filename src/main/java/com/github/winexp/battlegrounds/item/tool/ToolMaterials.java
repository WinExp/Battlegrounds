package com.github.winexp.battlegrounds.item.tool;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum ToolMaterials implements ToolMaterial {
    PVP_PRO_SWORD(2573, 9.0F, 5.0F, 4, 0),
    SEVEN_ELEVEN_SWORD(2081, 9.0F, 4.0F, 4, 0),
    STEVES_PAIN_SWORD(1837, 9.0F, 3.0F, 4, 0),
    LEACHING_SWORD(1792, 8.0F, 3.0F, 3, 0),
    MINERS_PICKAXE(998, 8.0F, 3.0F, 3, 0),
    BUTCHERS_AXE(3, 8.0F, 14.0F, 3, 0),
    RUPERTS_TEAR(20, 0, 0, 0, 0),
    KNOCKBACK_STICK(15, 0, 0, 0, 0);

    private final int durability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int miningLevel;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    ToolMaterials(int durability, float miningSpeed, float attackDamage, int miningLevel, int enchantability) {
        this(durability, miningSpeed, attackDamage, miningLevel, enchantability, Ingredient::empty);
    }

    ToolMaterials(int durability, float miningSpeed, float attackDamage, int miningLevel, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.miningLevel = miningLevel;
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
    public int getMiningLevel() {
        return this.miningLevel;
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
