package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;

import java.util.List;

public class KnockbackStickItem extends ToolItem implements EnchantRestrict {
    private static final List<StatusEffectInstance> ATTACK_EFFECTS = List.of(
            new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 2),
            new StatusEffectInstance(StatusEffects.NAUSEA, 5 * 20, 0)
    );

    public KnockbackStickItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        for (StatusEffectInstance effectInstance : ATTACK_EFFECTS) {
            target.addStatusEffect(new StatusEffectInstance(effectInstance), attacker);
        }
        return true;
    }

    /*
    RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(Items.DIAMOND),
                        'b', Ingredient.ofItems(Items.BLAZE_ROD),
                        'c', Ingredient.ofItems(Items.STICK)
                ),
                "aba",
                "bcb",
                "aba");
     */
}
