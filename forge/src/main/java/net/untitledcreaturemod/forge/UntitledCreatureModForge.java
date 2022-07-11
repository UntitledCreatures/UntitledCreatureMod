package net.untitledcreaturemod.forge;

import net.minecraftforge.fml.common.Mod;
import net.untitledcreaturemod.UntitledCreatureMod;
import software.bernie.geckolib3.GeckoLib;

@Mod(UntitledCreatureMod.MOD_ID)
public class UntitledCreatureModForge {
    public UntitledCreatureModForge() {
        UntitledCreatureMod.init();
        GeckoLib.initialize();
    }
}
