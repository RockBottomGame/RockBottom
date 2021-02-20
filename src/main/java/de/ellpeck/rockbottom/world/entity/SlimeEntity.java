package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractSlimeEntity;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.LivingEntity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.entity.spawn.DespawnHandler;
import de.ellpeck.rockbottom.api.entity.spawn.SpawnBehavior;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.entity.SlimeEntityRenderer;
import de.ellpeck.rockbottom.world.entity.ai.SlimeJumpTask;
import de.ellpeck.rockbottom.world.entity.ai.SlimeTargetTask;

public class SlimeEntity extends AbstractSlimeEntity {

    public static final ResourceName ID = ResourceName.intern("slime");

    public static final SpawnBehavior<SlimeEntity> SPAWN_BEHAVIOR = new SpawnBehavior<SlimeEntity>(ResourceName.intern("slime")) {
        @Override
        public SlimeEntity createEntity(IWorld world, double x, double y) {
            SlimeEntity slime = new SlimeEntity(world);
            slime.setPos(x, y);
            return slime;
        }

        @Override
        public double getMinPlayerDistance(IWorld world, AbstractPlayerEntity player) {
            return 20;
        }

        @Override
        public double getMaxPlayerDistance(IWorld world, AbstractPlayerEntity player) {
            return 30;
        }

        @Override
        public int getSpawnTries(IWorld world) {
            return 5;
        }

        @Override
        public int getPackSize(IWorld world, double x, double y) {
            return 3;
        }

        @Override
        public boolean belongsToCap(Entity entity) {
            return entity instanceof LivingEntity;
        }

        @Override
        public double getEntityCapArea(IWorld world, AbstractPlayerEntity player) {
            return 15;
        }

        @Override
        public int getEntityCap(IWorld world) {
            return 3;
        }

        @Override
        public boolean canSpawnHere(IWorld world, double x, double y) {
            return world.getCombinedLight(Util.floor(x), Util.floor(y)) <= 25 && super.canSpawnHere(world, x, y);
        }
    };

    private static final int VARIATION_COUNT = 8;
    public final SlimeJumpTask jumpTask = new SlimeJumpTask(0);
    public final SlimeTargetTask targetTask = new SlimeTargetTask(10, 10D, 2D);
    private final SlimeEntityRenderer renderer = new SlimeEntityRenderer();
    private final DespawnHandler<SlimeEntity> despawnHandler = new DespawnHandler<SlimeEntity>() {
        @Override
        public double getMaxPlayerDistance(SlimeEntity entity) {
            return 50;
        }
    };
    private int variation = Util.RANDOM.nextInt(VARIATION_COUNT);
    private int attackCooldown;

    public SlimeEntity(IWorld world) {
        super(world);
        this.addAiTask(this.jumpTask);
        this.addAiTask(this.targetTask);
    }

    @Override
    public ResourceName getRegistryName() {
        return ID;
    }

    @Override
    public SlimeEntityRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        if (this.isFalling && this.targetTask.target != null && this.targetTask.target.getY() < this.getY())
            this.isDropping = true;
    }

    @Override
    public void applyMotion() {
        if (!this.isClimbing) {
            this.motionY -= 0.025;
        }

        this.motionX *= 0.9D;
        this.motionY *= this.isClimbing ? 0.5 : 0.98;
    }

    @Override
    public int getInitialMaxHealth() {
        return 20;
    }

    @Override
    public int getRegenRate() {
        return 50;
    }

    @Override
    public float getKillReward(AbstractPlayerEntity player) {
        return 0.1F;
    }

    @Override
    public int getVariation() {
        return this.variation;
    }

    @Override
    public int getRenderPriority() {
        return 10;
    }

    @Override
    protected int getJumpTimeout() {
        return 40;
    }

    @Override
    public void save(DataSet set, boolean forFullSync) {
        super.save(set, forFullSync);
        set.addInt("variation", this.variation);
    }

    @Override
    public void load(DataSet set, boolean forFullSync) {
        super.load(set, forFullSync);
        this.variation = set.getInt("variation");
    }

    @Override
    public int getSyncFrequency() {
        return 15;
    }

    @Override
    public float getWidth() {
        return 0.65F;
    }

    @Override
    public float getHeight() {
        return 0.65F;
    }

    @Override
    public DespawnHandler<SlimeEntity> getDespawnHandler() {
        return this.despawnHandler;
    }

    @Override
    public void onIntersectWithEntity(Entity otherEntity, BoundingBox thisBox, BoundingBox thisBoxMotion, BoundingBox otherBox, BoundingBox otherBoxMotion) {
        if (!this.isDead() && this.attackCooldown <= 0 && Math.abs(this.motionX) > 0.01D) {
            if (!otherEntity.isDead() && otherEntity instanceof LivingEntity && !(otherEntity instanceof AbstractSlimeEntity)) {
                if (((LivingEntity) otherEntity).takeDamage(Util.RANDOM.nextInt(15) + 5)) {
                    otherEntity.applyKnockback(this, 0.25D);
                }
                this.attackCooldown = 30;
            }
        }
    }
}
