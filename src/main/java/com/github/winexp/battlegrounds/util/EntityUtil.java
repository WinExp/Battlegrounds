package com.github.winexp.battlegrounds.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class EntityUtil {
    public static Vec3d getEntitySidePos(Entity entity, Vec3d pos, Direction side) {
        Box box = entity.getBoundingBox();
        Direction.Axis axis = side.getAxis();
        return pos.withAxis(axis,
                axis.choose(pos.x, pos.y, pos.z)
                + (axis.choose(box.getLengthX(), box.getLengthY(), box.getLengthZ()))
                        * axis.choose(side.getOffsetX(), side.getOffsetY(), side.getOffsetZ()) / 2);
    }

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
