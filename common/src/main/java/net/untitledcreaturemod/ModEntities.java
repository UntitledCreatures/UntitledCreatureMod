package net.untitledcreaturemod;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.untitledcreaturemod.toad.ToadEntity;

import java.util.function.Supplier;

import static net.untitledcreaturemod.Utils.memoize;

public class ModEntities {
    public final static class Builders {
        public final static Supplier<EntityType<ToadEntity>> TOAD = memoize(() -> (EntityType<ToadEntity>) build("toad", EntityType.Builder.create(ToadEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.6f).maxTrackingRange(10)));
        private static EntityType<?> build(String id, EntityType.Builder<?> builder) {
            String prefixedId = UntitledCreatureMod.MOD_ID + ":" + id;
            return builder.build(prefixedId);
        }
    }

    // TODO: Has to be set by Impl
    public static Supplier<EntityType<ToadEntity>> TOAD = () -> null;

    @ExpectPlatform
    public static void setup() {
        throw new AssertionError("Not implemented");
    }
}
