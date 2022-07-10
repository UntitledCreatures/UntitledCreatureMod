package net.untitledcreaturemod.forge;

import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.minecraftforge.fml.common.Mod;
import net.untitledcreaturemod.setup.ModEntities;
import net.untitledcreaturemod.toad.ToadEntity;
import net.untitledcreaturemod.toad.ToadRenderer;
import software.bernie.geckolib3.GeckoLib;

@Mod(UntitledCreatureMod.MOD_ID)
public class UntitledCreatureModForge {
    public UntitledCreatureModForge() {
        UntitledCreatureMod.init();
        GeckoLib.initialize();
        ModSetup.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModSetup {
        private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, UntitledCreatureMod.MOD_ID);
        public static final RegistryObject<EntityType<ToadEntity>> TOAD = ENTITIES.register("duck", ModEntities.TOAD);

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onRegisterAttributes(final EntityAttributeCreationEvent event) {
            event.put(TOAD.get(), ToadEntity.getDefaultAttributes().add(ForgeMod.SWIM_SPEED.get(), 2.0D).build());
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            // TODO: Check if this work on severside
            EntityRenderers.register(TOAD.get(), ToadRenderer::new);
        }

    }
}
