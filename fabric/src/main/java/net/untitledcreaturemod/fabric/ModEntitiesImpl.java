package net.untitledcreaturemod.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import net.untitledcreaturemod.ModEntities;
import net.untitledcreaturemod.setup.ModIdentifiers;
import net.untitledcreaturemod.toad.ToadEntity;

public class ModEntitiesImpl {
    public static EntityType<ToadEntity> TOAD;

    public static void setup() {
        // Register Entity Types
        TOAD = Registry.register(Registry.ENTITY_TYPE, ModIdentifiers.Toad.ENTITY, ModEntities.Builders.TOAD.get());
        ModEntities.TOAD = () -> TOAD;

        // Register attributes
        FabricDefaultAttributeRegistry.register(TOAD, ToadEntity.getDefaultAttributes());
    }
}
