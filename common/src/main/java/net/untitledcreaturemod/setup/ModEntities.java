package net.untitledcreaturemod.setup;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.untitledcreaturemod.UntitledCreatureMod;
import net.untitledcreaturemod.toad.ToadEntity;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ModEntities {
    // TODO: Move
    public static <T> Supplier<T> memoize(Supplier<T> delegate) {
        AtomicReference<T> value = new AtomicReference<>();
        return () -> {
            T val = value.get();
            if (val == null) {
                val = value.updateAndGet(cur -> cur == null ?
                        Objects.requireNonNull(delegate.get()) : cur);
            }
            return val;
        };
    }

    // TODO
    public final static Supplier<EntityType<ToadEntity>> TOAD = memoize(() -> (EntityType<ToadEntity>) build("toad", EntityType.Builder.create(ToadEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.6f).maxTrackingRange(10)));

    private static EntityType<?> build(String id, EntityType.Builder<?> builder) {
        String prefixedId = UntitledCreatureMod.MOD_ID + ":" + id;
        return builder.build(prefixedId);
    }
}
