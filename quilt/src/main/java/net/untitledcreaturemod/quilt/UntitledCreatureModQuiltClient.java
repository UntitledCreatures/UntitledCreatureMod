package net.untitledcreaturemod.quilt;

import net.untitledcreaturemod.fabriclike.UntitledCreatureModFabricLikeClient;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class UntitledCreatureModQuiltClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        UntitledCreatureModFabricLikeClient.initClient();
    }
}
