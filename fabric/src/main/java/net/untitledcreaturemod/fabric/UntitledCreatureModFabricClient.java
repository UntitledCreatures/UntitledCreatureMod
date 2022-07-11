package net.untitledcreaturemod.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.untitledcreaturemod.ModEntities;
import net.untitledcreaturemod.toad.ToadRenderer;

public class UntitledCreatureModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.TOAD.get(), ToadRenderer::new);
    }
}
