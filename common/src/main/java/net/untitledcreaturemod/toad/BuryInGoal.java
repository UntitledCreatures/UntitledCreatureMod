package net.untitledcreaturemod.toad;

import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class BuryInGoal extends Goal {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ToadEntity toad;
    private int nextBuryTime;
    private int buryTicks = 0;
    private static final int BURY_DURATION = 20;
    private Optional<BlockPos> buryGoal = Optional.empty();
    private boolean lastDugIn;

    public BuryInGoal(ToadEntity toad) {
        this.toad = toad;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.nextBuryTime = scheduleNextBuryTime();
    }

    @Override
    public boolean canStart() {
        ToadEntity.ToadAnimation anim = toad.getCurrentAnimation();
        if (lastDugIn && !toad.isDugIn) {
            // We dug out -> reschedule time
            nextBuryTime = scheduleNextBuryTime();
            lastDugIn = toad.isDugIn;
        }

        if (nextBuryTime > toad.age
                || toad.getDespawnCounter() >= 100
                || anim != ToadEntity.ToadAnimation.Idle
                || toad.isDugIn) {
            return false;
        }
        return toad.getRandom().nextInt(40) == 0;
    }

    @Override
    public void start() {
        var buryBlock = findBuryBlock();

        if (buryBlock.isPresent()) {
            // When found a block to bury in start moving to it
            var aboveBlock = buryBlock.get().add(0,1,0);
            LOGGER.info("Moving to " + aboveBlock);
            toad.getNavigation().startMovingTo(aboveBlock.getX(), aboveBlock.getY(), aboveBlock.getZ(), 1.0D);
            buryGoal = buryBlock;
        } else {
            LOGGER.info("Could not find a block to bury in");
        }
        nextBuryTime = scheduleNextBuryTime();
    }

    private int scheduleNextBuryTime() {
        //return toad.age + (10 * 20 + toad.getRandom().nextInt(10) * 20);
        return toad.age + 7 * 20;
    }

    @Override
    public boolean shouldContinue() {
        return buryGoal.isPresent() || buryTicks > 0;
    }

    @Override
    public void tick() {
        if (buryGoal.isPresent()) {
            // Move to destination if goal is present
            var buryBlockPos = buryGoal.get();
            var distance = this.toad.squaredDistanceTo(buryBlockPos.getX(), buryBlockPos.getY()+1, buryBlockPos.getZ());
            LOGGER.info("Distance: " + distance);

            // Start animation once goal was reached
            if (toad.getNavigation().isIdle() || this.toad.getBlockPos().down() == buryBlockPos) {
                var aboveBlock = buryBlockPos.add(0,1,0);
                toad.setPos(aboveBlock.getX(), aboveBlock.getY(), aboveBlock.getZ());
                toad.refreshPositionAndAngles((double)aboveBlock.getX() + 0.5D, (double)aboveBlock.getY() + 0.4D, (double)aboveBlock.getZ() + 0.5D, toad.bodyYaw, 0.0F);
                toad.setVelocity(Vec3d.ZERO);

                toad.setDigIn(true, true);
                buryTicks = BURY_DURATION;
                buryGoal = Optional.empty();
            } else {
                // Restart navigation
                var aboveBlock = buryBlockPos.add(0,1,0);
                toad.getNavigation().startMovingTo(aboveBlock.getX(), aboveBlock.getY(), aboveBlock.getZ(), 1.0D);
            }
        } else if (buryTicks > 0) {
            buryTicks--;
        }
    }

    @Override
    public void stop() {
        this.buryGoal = Optional.empty();
        this.buryTicks = 0;
    }

    public static final Predicate<BlockState> buryBlockPredicate = (state) -> {
        Block block = state.getBlock();
        return block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.SAND || block == Blocks.RED_SAND || block == Blocks.GRAVEL;
    };

    private Optional<BlockPos> findBuryBlock() {
        return this.findBlock(buryBlockPredicate, 5.0);
    }

    // Adapted from BeeEntity.findFlower()
    private Optional<BlockPos> findBlock(Predicate<BlockState> predicate, double searchDistance) {
        BlockPos blockPos = toad.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int i = 0; (double)i <= searchDistance; i = i > 0 ? -i : 1 - i) {
            for(int j = 0; (double)j < searchDistance; ++j) {
                for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                    for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                        mutable.set(blockPos, k, i - 1, l);
                        if (blockPos.isWithinDistance(mutable, searchDistance) && predicate.test(toad.world.getBlockState(mutable))) {
                            return Optional.of(mutable);
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }

}
