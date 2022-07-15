package net.untitledcreaturemod.toad;

import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.untitledcreaturemod.ModEntities;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;


public class ToadEntity extends AnimalEntity implements IAnimatable {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(ToadEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Byte> VARIANT = DataTracker.registerData(ToadEntity.class, TrackedDataHandlerRegistry.BYTE);
    public boolean isDugIn;


    public enum ToadVariant {
        Cavernous, Mountainous, Abyssal;

        private static final ToadVariant[] allValues = values();
        public byte toByte() {
            return (byte)this.ordinal();
        }
        public static ToadVariant fromByte(byte idx) {
            return allValues[idx];
        }
    }

    public void setVariant(ToadVariant variant) {
        this.dataTracker.set(VARIANT, variant.toByte());
    }

    public ToadVariant getVariant() {
        return ToadVariant.fromByte(this.dataTracker.get(VARIANT));
    }

    protected enum ToadAnimation {
        Idle, Walk,
        SwimIdle, Swim,
        DigIn, DigIdle, DigBite, DigOut;

        private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle", true);
        private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk", true);
        private static final AnimationBuilder SWIM_IDLE_ANIM = new AnimationBuilder().addAnimation("idle_swim", true);
        private static final AnimationBuilder SWIM_ANIM = new AnimationBuilder().addAnimation("swim", true);

        private static final AnimationBuilder DIG_IN_ANIM = new AnimationBuilder().addAnimation("dig_in", false).addAnimation("dig_idle", true);
        private static final AnimationBuilder DIG_IDLE_ANIM = new AnimationBuilder().addAnimation("dig_idle", true);
        private static final AnimationBuilder DIG_BITE_ANIM = new AnimationBuilder().addAnimation("dig_bite", false).addAnimation("dig_idle", false);
        private static final AnimationBuilder DIG_OUT_ANIM = new AnimationBuilder().addAnimation("dig_out", false).addAnimation("idle", false);

        private static final ToadAnimation[] allValues = values();
        public byte toByte() {
            return (byte)this.ordinal();
        }
        public static ToadAnimation fromByte(byte idx) {
            return allValues[idx];
        }

        public AnimationBuilder toAnimation() {
            return switch (this) {
                case Idle -> IDLE_ANIM;
                case Walk -> WALK_ANIM;
                case SwimIdle -> SWIM_IDLE_ANIM;
                case Swim -> SWIM_ANIM;
                case DigIn -> DIG_IN_ANIM;
                case DigIdle -> DIG_IDLE_ANIM;
                case DigBite -> DIG_BITE_ANIM;
                case DigOut -> DIG_OUT_ANIM;
            };
        }

        public boolean isSpecial() {
            return this != Idle && this != Walk && this != SwimIdle && this != Swim;
        }

        public static AnimationBuilder defaultAnimation(boolean isMoving, boolean inWater) {
            if (inWater) {
                return isMoving ? ToadAnimation.SWIM_ANIM : ToadAnimation.SWIM_IDLE_ANIM;
            } else {
                return isMoving ? ToadAnimation.WALK_ANIM : ToadAnimation.IDLE_ANIM;
            }
        }
    }
    private record ScheduledAnimation (ToadAnimation animation, int ticks) {}
    private ScheduledAnimation nextAnimation = null;
    private final AnimationFactory factory = new AnimationFactory(this);

    protected ToadAnimation getCurrentAnimation() {
        return ToadAnimation.fromByte(dataTracker.get(ANIMATION));
    }

    public void setAnimation(ToadAnimation animation) {
        LOGGER.info("setAnimation: " + animation);
        dataTracker.set(ANIMATION, animation.toByte());
    }

    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.SPIDER_EYE);
    public ToadEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    }

    public static DefaultAttributeContainer.Builder getDefaultAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 7.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIMATION, ToadAnimation.Idle.toByte());
        this.dataTracker.startTracking(VARIANT, ToadVariant.Cavernous.toByte());
    }

    public static final String IS_DUG_IN_TAG = "dugIn";
    public static final String VARIANT_TAG = "variant";

    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putByte(VARIANT_TAG, getVariant().toByte());
        tag.putBoolean(IS_DUG_IN_TAG, isDugIn);
    }

    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        setVariant(ToadVariant.fromByte(tag.getByte(VARIANT_TAG)));
        setDigIn(isDugIn, false);
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        if (this.isDugIn) {
            return;
        }
        super.move(movementType, movement);
    }

    public void setDigIn(boolean digIn, boolean animate) {
        assert !world.isClient;
        LOGGER.info("SetDigIn: " + digIn + ", " + animate);
        if (animate) {
            if (digIn) {
                setAnimation(ToadAnimation.DigIn);
                nextAnimation = new ScheduledAnimation(ToadAnimation.DigIdle, age + 40);
            } else {
                // TODO: This causes the toad to move while digging out
                isDugIn = false;
                setAnimation(ToadAnimation.DigOut);
                nextAnimation = new ScheduledAnimation(ToadAnimation.Idle, age + 40);
            }
        } else {
            isDugIn = digIn;
            setAnimation(digIn ? ToadAnimation.DigIdle : ToadAnimation.Idle);
        }
    }

    public void playAttackAnim() {
        assert !world.isClient;
        setAnimation(ToadAnimation.DigBite);
        nextAnimation = new ScheduledAnimation(ToadAnimation.DigIdle, age + 20);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        // TODO: Remove and replace with laying eggs/toadspawn
        return ModEntities.TOAD.get().create(world);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!world.isClient) {
            if (nextAnimation != null) {
                if (age >= nextAnimation.ticks) {
                    var lastAnimation = getCurrentAnimation();
                    isDugIn = lastAnimation == ToadAnimation.DigIn || lastAnimation == ToadAnimation.DigBite;
                    setAnimation(nextAnimation.animation);
                    nextAnimation = null;
                }
            }
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BuriedGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.6D));
        this.goalSelector.add(4, new BuryInGoal(this));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, BREEDING_INGREDIENT, false));
        this.goalSelector.add(4, new BuryInGoal(this));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    public boolean canLookAround() {
        return !getCurrentAnimation().isSpecial();
    }

    @Override
    public void registerControllers(AnimationData data) {
        var controller = new AnimationController<>(this, "controller", 2, this::predicate);
        data.addAnimationController(controller);
        controller.registerSoundListener(this::soundListener);
    }

    private void soundListener(SoundKeyframeEvent<ToadEntity> event) {
        assert world.isClient;
        if (event.sound == null) {
            return;
        }
        var randomPitch = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
        var pos = getPos();
        switch (event.sound) {
            case "eat" -> {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 1.0f, randomPitch, false);
                playParticle(getPos(), Blocks.NETHER_WART_BLOCK);
            }
            case "burp" -> {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.NEUTRAL, 1.0f, randomPitch, false);
            }
            case "plop" -> {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1.0f, randomPitch, false);
            }
            case "dig" -> {
                BlockState blockBelowState = world.getBlockState(getBlockPos().down());
                LOGGER.info("Play effect for " + blockBelowState);
                if (!blockBelowState.isAir()) {
                    Block blockBelow = blockBelowState.getBlock();
                    BlockSoundGroup soundGroup = blockBelow.getSoundGroup(blockBelowState);
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), soundGroup.getBreakSound(), SoundCategory.NEUTRAL, 1.0f, randomPitch, false);
                    playParticle(getPos(), blockBelow);
                }
            }
        }
    }

    private void playParticle(Vec3d pos, Block block) {
        Vec3d vel = new Vec3d(((double)this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
        vel = vel.rotateX(-this.getPitch() * 0.017453292F);
        vel = vel.rotateY(-this.getYaw() * 0.017453292F);

        Vec3d rotationVec = Vec3d.fromPolar(0, bodyYaw);
        Vec3d spawnPos = new Vec3d(pos.getX() + rotationVec.x / 2.0D, pos.getY(), pos.getZ() + rotationVec.z/2.0D);
        this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, block.getDefaultState()),
                spawnPos.x, spawnPos.y, spawnPos.z,
                vel.x, vel.y + 0.05D, vel.z);
    }

    @SuppressWarnings("rawtypes")
    private <T extends IAnimatable> PlayState predicate(AnimationEvent<T> event) {
        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);
        boolean inWater = isTouchingWater();
        AnimationController controller = event.getController();
        ToadAnimation currentAnimation = getCurrentAnimation();

        if (currentAnimation.isSpecial()) {
            // TODO: Optimize: Only set if changed?
            controller.setAnimation(currentAnimation.toAnimation());
        } else {
            controller.setAnimation(ToadAnimation.defaultAnimation(isMoving, inWater));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
