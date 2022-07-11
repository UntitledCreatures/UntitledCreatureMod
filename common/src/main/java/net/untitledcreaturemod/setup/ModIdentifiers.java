package net.untitledcreaturemod.setup;

import net.minecraft.util.Identifier;
import net.untitledcreaturemod.UntitledCreatureMod;

public class ModIdentifiers {
    public static class Toad {
        public static final Identifier ENTITY = new Identifier(UntitledCreatureMod.MOD_ID, "toad");
        public static final Identifier MODEL = new Identifier(UntitledCreatureMod.MOD_ID, "geo/toad.geo.json");
        public static final Identifier TEXTURE = new Identifier(UntitledCreatureMod.MOD_ID, "textures/toad.png");
        public static final Identifier ANIMATIONS = new Identifier(UntitledCreatureMod.MOD_ID, "animations/toad.animation.json");
    }
}
