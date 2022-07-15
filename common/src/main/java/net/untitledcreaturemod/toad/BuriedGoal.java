package net.untitledcreaturemod.toad;

import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.List;

public class BuriedGoal extends Goal {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ToadEntity toad;
    private BlockState buryBlock;
    private static final int UPDATE_FREQ_TICK = 5;
    private int idleTickCounter = UPDATE_FREQ_TICK;

    // The time in the bite animation when we should eat the target
    private static final int BITE_ANIM_LEN = 20;
    private static final int BITE_ANIM_EAT_POS = 5;
    private int eatTickCounter = 0;
    private LivingEntity target;
    private boolean animateDigOut = true;

    private int nextBuryOutTime;

    public BuriedGoal(ToadEntity toad) {
        this.toad = toad;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private int scheduleNextBuryOutTime() {
        int nextTime = toad.age + (20 * 20) + toad.getRandom().nextInt(10) * 20;
        LOGGER.info("Scheduled next bury time to " + nextTime + ", age: " + toad.age);
        return nextTime;
    }

    @Override
    public boolean canStart() {
        return toad.isDugIn;
    }

    @Override
    public void start() {
        this.animateDigOut = true;
        this.buryBlock = toad.world.getBlockState(toad.getBlockPos().down());
        this.nextBuryOutTime = scheduleNextBuryOutTime();
    }

    @Override
    public boolean shouldContinue() {
        if (!toad.isDugIn) {
            return false;
        } else return toad.age <= nextBuryOutTime;
    }

    @Override
    public void tick() {
        // Eat target
        if (eatTickCounter > 0) {
            eatTickCounter--;
            if (eatTickCounter == (BITE_ANIM_LEN-BITE_ANIM_EAT_POS)) {
                if (target != null) {
                    target.onDeath(DamageSource.mob(toad));
                    target.remove(Entity.RemovalReason.KILLED);
                    if (target instanceof EndermiteEntity) {
                        randomTeleport(toad);
                    }
                }
                target = null;
            }
            return;
        }

        // Throttle updates when being idle
        if (idleTickCounter > 0) {
            idleTickCounter--;
            return;
        } else {
            idleTickCounter = UPDATE_FREQ_TICK;
        }

        // Check if bury block was broken and dig out/cancel behaviour
        this.buryBlock = toad.world.getBlockState(toad.getBlockPos().down());
        LOGGER.info("buryBlockState: " + buryBlock);
        if (buryBlock.isAir()) {
            animateDigOut = false;
            toad.setDigIn(false, false);
            return;
        }

        target = getPotentialTarget();
        if (target != null && target.isAlive()) {
            LOGGER.info("Eat " + target);
            toad.playAttackAnim();
            // TODO: Figure out why this doesn't work
            toad.lookAtEntity(target, 360, 180);
            toad.refreshPositionAndAngles(toad.getX(), toad.getY(), toad.getZ(), toad.getYaw(), 0.0F);
            eatTickCounter = BITE_ANIM_LEN;
        }
    }

    private final List<Class<? extends LivingEntity>> targetClasses = List.of(CaveSpiderEntity.class, EndermiteEntity.class, SilverfishEntity.class);

    private LivingEntity getPotentialTarget() {
        var blockPos = toad.getBlockPos().up();
        for (Class<? extends LivingEntity> targetClass : targetClasses) {
            var foundTarget = toad.world.getClosestEntity(targetClass, TargetPredicate.DEFAULT, this.toad,
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(), new Box(blockPos).expand(0.5, 1, 0.5));
            if (foundTarget != null) {
                return foundTarget;
            }
        }
        return null;
    }

    // This was copied and adapted from ChorusFruitItem.finishUsing()
    private void randomTeleport(LivingEntity user) {
        assert !toad.world.isClient;
        World world = toad.world;
        for(int i = 0; i < 16; ++i) {
            double g = user.getX() + (user.getRandom().nextDouble() - 0.5) * 16.0;
            double h = MathHelper.clamp(user.getY() + (double)(user.getRandom().nextInt(16) - 8), world.getBottomY(), world.getBottomY() + ((ServerWorld)world).getLogicalHeight() - 1);
            double j = user.getZ() + (user.getRandom().nextDouble() - 0.5) * 16.0;
            if (user.hasVehicle()) {
                user.stopRiding();
            }

            if (user.teleport(g, h, j, true)) {
                SoundEvent soundEvent = user instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                user.playSound(soundEvent, 1.0F, 1.0F);
                break;
            }
        }

    }

    @Override
    public void stop() {
        if (animateDigOut) {
            toad.setDigIn(false, animateDigOut);
        }
    }
}
