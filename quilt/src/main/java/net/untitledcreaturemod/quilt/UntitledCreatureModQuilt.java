package net.untitledcreaturemod.quilt;

import net.untitledcreaturemod.fabriclike.UntitledCreatureModFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class UntitledCreatureModQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        UntitledCreatureModFabricLike.init();
    }
}
