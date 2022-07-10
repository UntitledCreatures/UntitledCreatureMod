package net.untitledcreaturemod.forge;

import net.untitledcreaturemod.UntitledCreatureMod;
import net.minecraftforge.fml.common.Mod;

@Mod(UntitledCreatureMod.MOD_ID)
public class UntitledCreatureModForge {
    public UntitledCreatureModForge() {
        // Submit our event bus to let architectury register our content on the right time
        UntitledCreatureMod.init();
    }
}
