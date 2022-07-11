package net.untitledcreaturemod.fabric;

import net.fabricmc.api.ModInitializer;
import net.untitledcreaturemod.UntitledCreatureMod;
import software.bernie.geckolib3.GeckoLib;

public class UntitledCreatureModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        UntitledCreatureMod.init();
        GeckoLib.initialize();
    }
}
