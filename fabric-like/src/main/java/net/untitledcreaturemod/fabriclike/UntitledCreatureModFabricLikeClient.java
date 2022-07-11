package net.untitledcreaturemod.fabriclike;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.untitledcreaturemod.ModEntities;
import net.untitledcreaturemod.toad.ToadRenderer;

public class UntitledCreatureModFabricLikeClient {
    public static void initClient() {
        EntityRendererRegistry.register(ModEntities.TOAD.get(), ToadRenderer::new);
    }
}
