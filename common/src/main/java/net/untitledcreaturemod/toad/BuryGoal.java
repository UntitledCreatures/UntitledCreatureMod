package net.untitledcreaturemod.toad;

import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class BuryGoal extends Goal {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ToadEntity toad;
    private int nextBuryTime;

    public BuryGoal(ToadEntity toad) {
        this.toad = toad;
        nextBuryTime = scheduleNextBuryTime();
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (nextBuryTime > toad.age
                || toad.getDespawnCounter() >= 100
                || toad.getCurrentAnimation() != ToadEntity.ToadAnimations.Idle) {
            return false;
        }
        return toad.getRandom().nextInt(40) == 0;
    }

    @Override
    public void start() {
        var buryBlock = getBuryBlock();
        if (buryBlock.isPresent()) {
            var pos = buryBlock.get();
            LOGGER.info("Burying into block at" + pos);
            toad.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
        } else {
            LOGGER.info("Could not find a block to bury in");
        }
        nextBuryTime = scheduleNextBuryTime();
    }

    private int scheduleNextBuryTime() {
        return toad.age + (10 * 20 + toad.getRandom().nextInt(10) * 20);
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }

    private final Predicate<BlockState> buryBlockPredicate = (state) -> {
        Block block = state.getBlock();
        return block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.SAND || block == Blocks.RED_SAND || block == Blocks.GRAVEL;
    };

    private Optional<BlockPos> getBuryBlock() {
        return this.findBlock(this.buryBlockPredicate, 5.0);
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
