package de.ellpeck.rockbottom.world.entity.ai;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.ai.AITask;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.world.entity.EntitySlime;

public class TaskSlimeJump extends AITask<EntitySlime> {

    public long jumpStartTime;
    public int chargeTime;
    private boolean jumpRight;
    private double motionX;
    private double motionY;

    public TaskSlimeJump(int priority) {
        super(priority);
    }

    @Override
    public boolean shouldStartExecution(EntitySlime entity) {
        return !entity.jumping && entity.jumpTimeout <= 0;
    }

    @Override
    public boolean shouldEndExecution(EntitySlime entity) {
        return this.chargeTime <= 0;
    }

    @Override
    public void onExecutionStarted(AITask<EntitySlime> previousTask, EntitySlime entity) {
        if (!entity.world.isClient()) {
            this.chargeTime = 20;

            if (entity.targetTask.target != null) {
                this.jumpRight = entity.targetTask.target.getX() > entity.getX();
                this.motionX = entity.targetTask.isClose ? 0.5D : 0.35D;
                this.motionY = entity.targetTask.isClose ? 0.3D : 0.35D;
            } else {
                this.jumpRight = Util.RANDOM.nextBoolean();
                this.motionX = 0.1D + 0.25D * Util.RANDOM.nextDouble();
                this.motionY = 0.1D + 0.3D * Util.RANDOM.nextDouble();
            }
            entity.facing = this.jumpRight ? Direction.RIGHT : Direction.LEFT;
        }

        this.jumpStartTime = Util.getTimeMillis();
    }

    @Override
    public void execute(IGameInstance game, EntitySlime entity) {
        this.chargeTime--;
        if (this.chargeTime <= 0) {
            if (entity.jump(this.motionY)) {
                if (this.jumpRight) {
                    entity.motionX += this.motionX;
                } else {
                    entity.motionX -= this.motionX;
                }
            }
        }
    }

    @Override
    public void save(DataSet set, boolean forSync, EntitySlime entity) {
        set.addInt("charge", this.chargeTime);
        set.addBoolean("right", this.jumpRight);
        set.addDouble("motionX", this.motionX);
        set.addDouble("motionY", this.motionY);
    }

    @Override
    public void load(DataSet set, boolean forSync, EntitySlime entity) {
        this.chargeTime = set.getInt("charge");
        this.jumpRight = set.getBoolean("right");
        this.motionX = set.getDouble("motionX");
        this.motionY = set.getDouble("motionY");
    }
}
