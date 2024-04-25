package com.github.winexp.battlegrounds.util;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class EntityUtil {
    public static void addAttributeModifier(EntityAttributeInstance attribute, Identifier id, double value, EntityAttributeModifier.Operation operation) {
        UUID modifierUUID = UUID.nameUUIDFromBytes(id.toString().getBytes());
        if (attribute.getModifier(modifierUUID) == null) {
            EntityAttributeModifier attributeModifier = new EntityAttributeModifier(modifierUUID, modifierUUID.toString(),
                    value, operation);
            attribute.addPersistentModifier(attributeModifier);
        }
    }

    public static void removeAttributeModifier(EntityAttributeInstance attribute, Identifier id) {
        UUID modifierUUID = UUID.nameUUIDFromBytes(id.toString().getBytes());
        if (attribute.getModifier(modifierUUID) != null) {
            attribute.removeModifier(modifierUUID);
        }
    }
}
