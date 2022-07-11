package net.untitledcreaturemod.forge;

import net.minecraft.entity.EntityType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.untitledcreaturemod.ModEntities;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.untitledcreaturemod.toad.ToadEntity;

public class ModEntitiesImpl {
    public static void setup() {
        ModSetup.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntities.TOAD = ModSetup.TOAD;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModSetup {
        private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, UntitledCreatureMod.MOD_ID);
        public static final RegistryObject<EntityType<ToadEntity>> TOAD = ENTITIES.register("duck", ModEntities.Builders.TOAD);

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onRegisterAttributes(final EntityAttributeCreationEvent event) {
            event.put(TOAD.get(), ToadEntity.getDefaultAttributes().add(ForgeMod.SWIM_SPEED.get(), 2.0D).build());
        }
    }

}
