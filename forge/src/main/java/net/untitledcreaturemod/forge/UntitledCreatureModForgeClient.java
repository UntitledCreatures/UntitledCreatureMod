package net.untitledcreaturemod.forge;

import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.untitledcreaturemod.ModEntities;
import net.untitledcreaturemod.toad.ToadRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class UntitledCreatureModForgeClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.TOAD.get(), ToadRenderer::new);
    }
}
