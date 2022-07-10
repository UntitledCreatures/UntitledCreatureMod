package net.untitledcreaturemod.fabric;

import net.untitledcreaturemod.fabriclike.UntitledCreatureModFabricLike;
import net.fabricmc.api.ModInitializer;

public class UntitledCreatureModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UntitledCreatureModFabricLike.init();
    }
}
