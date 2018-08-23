package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractEntitySlime;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.EntityLiving;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.spawn.DespawnHandler;
import de.ellpeck.rockbottom.api.entity.spawn.SpawnBehavior;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.entity.SlimeEntityRenderer;
import de.ellpeck.rockbottom.world.entity.ai.TaskSlimeJump;
import de.ellpeck.rockbottom.world.entity.ai.TaskSlimeTarget;

public class EntitySlime extends AbstractEntitySlime {

    public static final SpawnBehavior<EntitySlime> SPAWN_BEHAVIOR = new SpawnBehavior<EntitySlime>(ResourceName.intern("slime")) {
        @Override
        public EntitySlime createEntity(IWorld world, double x, double y) {
            EntitySlime slime = new EntitySlime(world);
            slime.setPos(x, y);
            return slime;
        }

        @Override
        public double getMinPlayerDistance(IWorld world, AbstractEntityPlayer player) {
            return 20;
        }

        @Override
        public double getMaxPlayerDistance(IWorld world, AbstractEntityPlayer player) {
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
            return entity instanceof EntityLiving;
        }

        @Override
        public double getEntityCapArea(IWorld world, AbstractEntityPlayer player) {
            return 15;
        }

        @Override
        public int getEntityCap(IWorld world) {
            return 3;
        }
    };

    private static final int VARIATION_COUNT = 8;
    private final IEntityRenderer renderer = new SlimeEntityRenderer();
    public final TaskSlimeJump jumpTask = new TaskSlimeJump(0);
    public final TaskSlimeTarget targetTask = new TaskSlimeTarget(10, 10D, 2D);
    private int variation = Util.RANDOM.nextInt(VARIATION_COUNT);
    private int attackCooldown;
    private final DespawnHandler<EntitySlime> despawnHandler = new DespawnHandler<EntitySlime>() {
        @Override
        public double getMaxPlayerDistance(EntitySlime entity) {
            return 50;
        }
    };

    public EntitySlime(IWorld world) {
        super(world);
        this.addAiTask(this.jumpTask);
        this.addAiTask(this.targetTask);
    }

    @Override
    public IEntityRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
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
    public float getKillReward(AbstractEntityPlayer player) {
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
    public DespawnHandler getDespawnHandler() {
        return this.despawnHandler;
    }

    @Override
    public void onIntersectWithEntity(Entity otherEntity, BoundBox thisBox, BoundBox thisBoxMotion, BoundBox otherBox, BoundBox otherBoxMotion) {
        if (this.attackCooldown <= 0 && Math.abs(this.motionX) > 0.01D) {
            if (!otherEntity.isDead() && otherEntity instanceof EntityLiving && !(otherEntity instanceof AbstractEntitySlime)) {
                otherEntity.applyKnockback(this, 0.25D);

                if (!this.world.isClient()) {
                    ((EntityLiving) otherEntity).takeDamage(Util.RANDOM.nextInt(15) + 5);
                }

                this.attackCooldown = 30;
            }
        }
    }
}
