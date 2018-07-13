package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractEntitySlime;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.spawn.DespawnHandler;
import de.ellpeck.rockbottom.api.entity.spawn.SpawnBehavior;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.entity.SlimeEntityRenderer;
import de.ellpeck.rockbottom.world.entity.ai.TaskSlimeJump;

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
            return 30;
        }

        @Override
        public double getMaxPlayerDistance(IWorld world, AbstractEntityPlayer player) {
            return 50;
        }

        @Override
        public int getSpawnTries(IWorld world) {
            return 10;
        }

        @Override
        public int getPackSize(IWorld world, double x, double y) {
            return 10;
        }

        @Override
        public boolean belongsToCap(Entity entity) {
            return entity instanceof AbstractEntitySlime;
        }

        @Override
        public int getEntityCap(IWorld world) {
            return 30;
        }

        @Override
        public double getEntityCapArea(IWorld world, AbstractEntityPlayer player) {
            return super.getEntityCapArea(world, player);
        }
    };

    private static final int VARIATION_COUNT = 8;
    private final IEntityRenderer renderer = new SlimeEntityRenderer();
    public TaskSlimeJump jumpTask = new TaskSlimeJump(0);
    private int variation = Util.RANDOM.nextInt(VARIATION_COUNT);
    private final DespawnHandler<EntitySlime> despawnHandler = new DespawnHandler<EntitySlime>() {
        @Override
        public boolean isReadyToDespawn(EntitySlime entity) {
            return true;
        }

        @Override
        public double getMaxPlayerDistance(EntitySlime entity) {
            return 100;
        }
    };

    public EntitySlime(IWorld world) {
        super(world);
        this.addAiTask(this.jumpTask);
    }

    @Override
    public IEntityRenderer getRenderer() {
        return this.renderer;
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
    public void save(DataSet set) {
        super.save(set);
        set.addInt("variation", this.variation);
    }

    @Override
    public void load(DataSet set) {
        super.load(set);
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
}
