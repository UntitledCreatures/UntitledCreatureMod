package net.untitledcreaturemod.setup;

import net.minecraft.util.Identifier;
import net.untitledcreaturemod.UntitledCreatureMod;

public class ModIdentifiers {
    public static class Toad {
        public static final Identifier ENTITY = new Identifier(UntitledCreatureMod.MOD_ID, "toad");
        public static final Identifier MODEL = new Identifier(UntitledCreatureMod.MOD_ID, "geo/toad.geo.json");
        public static final Identifier ANIMATIONS = new Identifier(UntitledCreatureMod.MOD_ID, "animations/toad.animation.json");

        public static final Identifier TEXTURE_CAVERNOUS = new Identifier(UntitledCreatureMod.MOD_ID, "textures/cavernous_toad.png");
        public static final Identifier TEXTURE_MOUNTAINOUS = new Identifier(UntitledCreatureMod.MOD_ID, "textures/mountainous_toad.png");
        public static final Identifier TEXTURE_ABYSSAL = new Identifier(UntitledCreatureMod.MOD_ID, "textures/abyssal_toad.png");
        public static final Identifier TEXTURE_OLD = new Identifier(UntitledCreatureMod.MOD_ID, "textures/old_toad.png");
    }
}
