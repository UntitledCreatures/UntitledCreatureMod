package net.untitledcreaturemod.toad;

import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import org.slf4j.Logger;

import java.util.EnumSet;

public class BuriedGoal extends Goal {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ToadEntity toad;
    private BlockState buryBlock;
    private static final int UPDATE_FREQ_TICK = 5;
    private int tickCounter = UPDATE_FREQ_TICK;
    private int nextBuryOutTime;

    public BuriedGoal(ToadEntity toad) {
        this.toad = toad;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private int scheduleNextBuryOutTime() {
        //return toad.age + (10 * 20 + toad.getRandom().nextInt(10) * 20);
        return toad.age + 10 * 20;
    }

    @Override
    public boolean canStart() {
        return toad.isDugIn;
    }

    @Override
    public void start() {
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
        if (tickCounter-- > 0) {
            return;
        }
        tickCounter = UPDATE_FREQ_TICK;

        // TODO: Check if mob is above to eat

        // TODO: Check if bury block was broken to stop
        this.buryBlock = toad.world.getBlockState(toad.getBlockPos().down());
        LOGGER.info("buryBlockState: " + buryBlock);
        if (buryBlock.isAir()) {
            toad.setDigIn(false, false);
        }
    }

    @Override
    public void stop() {
        toad.setDigIn(false, true);
    }
}
