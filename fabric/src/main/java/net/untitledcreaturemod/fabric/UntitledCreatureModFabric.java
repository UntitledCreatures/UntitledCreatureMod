package net.untitledcreaturemod.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.fabricmc.api.ModInitializer;
import net.untitledcreaturemod.setup.ModEntities;
import net.untitledcreaturemod.toad.ToadEntity;
import software.bernie.geckolib3.GeckoLib;

public class UntitledCreatureModFabric implements ModInitializer {
    public static EntityType<ToadEntity> TOAD;

    @Override
    public void onInitialize() {
        //UntitledCreatureModFabricLike.init();
        UntitledCreatureMod.init();
        TOAD = Registry.register(Registry.ENTITY_TYPE, new Identifier(UntitledCreatureMod.MOD_ID, "toad"), ModEntities.TOAD.get());
        FabricDefaultAttributeRegistry.register(TOAD, ToadEntity.getDefaultAttributes());
        GeckoLib.initialize();
    }
}
