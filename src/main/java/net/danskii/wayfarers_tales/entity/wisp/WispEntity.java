package net.danskii.wayfarers_tales.entity.wisp;

import net.danskii.wayfarers_tales.registry.ModSoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class WispEntity extends PathAwareEntity {
    public static final int VARIANT_COUNT = 6;

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(WispEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> WISP_SCALE = DataTracker.registerData(WispEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private static final String VARIANT_KEY = "Variant";
    private static final String WISP_SCALE_KEY = "WispScale";
    private static final float MIN_SCALE = 0.5F;
    private static final float MAX_SCALE = 1.0F;
    private static final int MAX_FLIGHT_HEIGHT_ABOVE_GROUND = 20;
    private static final int PANIC_TICKS_AFTER_DAMAGE = 100;
    private static final int[] VARIANT_PARTICLE_COLORS = {
            0xC299C6,
            0xC9C199,
            0x99BCC5,
            0x9AA4C7,
            0xC7BF9A,
            0x9ABDC3
    };

    private int panicTicks;

    public WispEntity(EntityType<? extends WispEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setNoGravity(true);
    }

    public static DefaultAttributeContainer.Builder createWispAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 8.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.FLYING_SPEED, 0.65D)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.SCALE, 1.0D);
    }

    public static boolean canSpawn(EntityType<WispEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        ServerWorld serverWorld = world.toServerWorld();
        BlockPos skyCheckPos = pos.up();
        return serverWorld.getRegistryKey() == World.OVERWORLD
                && serverWorld.isNight()
                && world.isSkyVisibleAllowingSea(skyCheckPos)
                && random.nextInt(18) == 0
                && MobEntity.canMobSpawn(type, world, spawnReason, pos, random);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FleeEntityGoal<>(this, HostileEntity.class, 12.0F, 1.35D, 1.85D));
        this.goalSelector.add(2, new WispPanicFlightGoal(this));
        this.goalSelector.add(3, new WispWanderGoal(this));
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation navigation = new BirdNavigation(this, world);
        navigation.setCanSwim(false);
        return navigation;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, 0);
        builder.add(WISP_SCALE, 1.0F);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData);
        this.setVariant(this.getRandom().nextInt(VARIANT_COUNT));
        this.setWispScale(MathHelper.nextFloat(this.getRandom(), MIN_SCALE, MAX_SCALE));
        return data;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.panicTicks > 0) {
            this.panicTicks--;
        }

        if (!this.getEntityWorld().isClient() && this.getEntityWorld() instanceof ServerWorld serverWorld && this.age % 4 == 0) {
            this.spawnTrailParticles(serverWorld);
        }

        if (!this.getEntityWorld().isClient() && this.isTooHighAboveGround()) {
            this.moveControl.moveTo(this.getX(), this.getGroundY() + MAX_FLIGHT_HEIGHT_ABOVE_GROUND - 2.0D, this.getZ(), 1.2D);
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        boolean damaged = super.damage(world, source, amount);
        if (damaged) {
            this.panicTicks = PANIC_TICKS_AFTER_DAMAGE;
            Entity attacker = source.getAttacker();
            if (attacker != null) {
                Vec3d awayFromAttacker = this.getEntityPos().subtract(attacker.getEntityPos()).normalize();
                if (awayFromAttacker.lengthSquared() > 0.0D) {
                    this.setVelocity(awayFromAttacker.multiply(0.45D).add(0.0D, 0.35D, 0.0D));
                    this.velocityDirty = true;
                }
            }
        }
        return damaged;
    }

    @Override
    public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundRegistry.WISP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundRegistry.WISP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundRegistry.WISP_HURT;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 160;
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.setVariant(MathHelper.clamp(view.getInt(VARIANT_KEY, 0), 0, VARIANT_COUNT - 1));
        this.setWispScale(MathHelper.clamp(view.getFloat(WISP_SCALE_KEY, 1.0F), MIN_SCALE, MAX_SCALE));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt(VARIANT_KEY, this.getVariant());
        view.putFloat(WISP_SCALE_KEY, this.getWispScale());
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public float getWispScale() {
        return this.dataTracker.get(WISP_SCALE);
    }

    private void setVariant(int variant) {
        this.dataTracker.set(VARIANT, MathHelper.floorMod(variant, VARIANT_COUNT));
    }

    private void setWispScale(float scale) {
        float clampedScale = MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);
        this.dataTracker.set(WISP_SCALE, clampedScale);
        EntityAttributeInstance scaleAttribute = this.getAttributeInstance(EntityAttributes.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(clampedScale);
        }
        this.calculateDimensions();
    }

    private void spawnTrailParticles(ServerWorld world) {
        int color = VARIANT_PARTICLE_COLORS[MathHelper.clamp(this.getVariant(), 0, VARIANT_PARTICLE_COLORS.length - 1)];
        Vec3d trailPos = this.getEntityPos()
                .subtract(this.getVelocity().normalize().multiply(0.25D))
                .add(0.0D, this.getHeight() * 0.25D, 0.0D);
        world.spawnParticles(
                new DustParticleEffect(color, 0.75F),
                trailPos.x,
                trailPos.y,
                trailPos.z,
                1,
                0.08D,
                0.08D,
                0.08D,
                0.01D
        );

        if (this.age % 12 == 0) {
            world.spawnParticles(
                    ParticleTypes.WITCH,
                    trailPos.x,
                    trailPos.y - 0.05D,
                    trailPos.z,
                    1,
                    0.06D,
                    0.06D,
                    0.06D,
                    0.0D
            );
        }
    }

    @Override
    public boolean isPanicking() {
        return this.panicTicks > 0;
    }

    private boolean isTooHighAboveGround() {
        return this.getY() > this.getGroundY() + MAX_FLIGHT_HEIGHT_ABOVE_GROUND;
    }

    private double getGroundY() {
        return this.getEntityWorld().getTopY(Heightmap.Type.WORLD_SURFACE, this.getBlockX(), this.getBlockZ());
    }

    private BlockPos getRandomFlightTarget(double horizontalRange, int minHeightAboveGround, int maxHeightAboveGround) {
        Random random = this.getRandom();
        int targetX = MathHelper.floor(this.getX() + MathHelper.nextDouble(random, -horizontalRange, horizontalRange));
        int targetZ = MathHelper.floor(this.getZ() + MathHelper.nextDouble(random, -horizontalRange, horizontalRange));
        int groundY = this.getEntityWorld().getTopY(Heightmap.Type.WORLD_SURFACE, targetX, targetZ);
        int targetY = groundY + random.nextBetween(minHeightAboveGround, maxHeightAboveGround);
        targetY = Math.min(targetY, groundY + MAX_FLIGHT_HEIGHT_ABOVE_GROUND);
        return new BlockPos(targetX, targetY, targetZ);
    }

    private static final class WispWanderGoal extends Goal {
        private final WispEntity wisp;

        private WispWanderGoal(WispEntity wisp) {
            this.wisp = wisp;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !this.wisp.isPanicking() && (this.wisp.getNavigation().isIdle() || this.wisp.getRandom().nextInt(40) == 0);
        }

        @Override
        public void start() {
            BlockPos target = this.wisp.getRandomFlightTarget(10.0D, 2, MAX_FLIGHT_HEIGHT_ABOVE_GROUND);
            this.wisp.getNavigation().startMovingTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 0.95D);
        }
    }

    private static final class WispPanicFlightGoal extends Goal {
        private final WispEntity wisp;

        private WispPanicFlightGoal(WispEntity wisp) {
            this.wisp = wisp;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return this.wisp.isPanicking();
        }

        @Override
        public boolean shouldContinue() {
            return this.wisp.isPanicking();
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.wisp.age % 10 == 0 || this.wisp.getNavigation().isIdle()) {
                BlockPos target = this.wisp.getRandomFlightTarget(14.0D, 4, MAX_FLIGHT_HEIGHT_ABOVE_GROUND);
                this.wisp.getNavigation().startMovingTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.65D);
            }
        }
    }
}
