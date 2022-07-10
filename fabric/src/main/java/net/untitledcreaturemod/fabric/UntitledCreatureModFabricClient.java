package net.untitledcreaturemod.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.untitledcreaturemod.setup.ModEntities;
import net.untitledcreaturemod.toad.ToadRenderer;

public class UntitledCreatureModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //UntitledCreatureModFabricLikeClient.initClient();
        EntityRendererRegistry.register(ModEntities.TOAD.get(), ToadRenderer::new);
    }
}
