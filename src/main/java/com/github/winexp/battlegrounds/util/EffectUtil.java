package com.github.winexp.battlegrounds.util;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.UUID;

public class EffectUtil {
    public static void addAttribute(EntityAttributeInstance attribute, String id, double value, EntityAttributeModifier.Operation operation) {
        UUID modifierUUID = UUID.nameUUIDFromBytes(id.getBytes());
        if (attribute.getModifier(modifierUUID) == null) {
            EntityAttributeModifier healthModifier = new EntityAttributeModifier(modifierUUID, modifierUUID.toString(),
                    value, operation);
            attribute.addPersistentModifier(healthModifier);
        }
    }

    public static void removeAttribute(EntityAttributeInstance attribute, String id) {
        UUID modifierUUID = UUID.nameUUIDFromBytes(id.getBytes());
        if (attribute.getModifier(modifierUUID) != null) {
            attribute.removeModifier(modifierUUID);
        }
    }
}
