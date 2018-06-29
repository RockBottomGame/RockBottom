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

            this.jumpRight = Util.RANDOM.nextBoolean();
            entity.facing = this.jumpRight ? Direction.RIGHT : Direction.LEFT;
        }

        this.jumpStartTime = Util.getTimeMillis();
    }

    @Override
    public void execute(IGameInstance game, EntitySlime entity) {
        this.chargeTime--;
        if (this.chargeTime <= 0) {
            if (entity.jump(0.3D)) {
                if (this.jumpRight) {
                    entity.motionX += 0.25D;
                } else {
                    entity.motionX -= 0.25D;
                }
            }
        }
    }

    @Override
    public void save(DataSet set, boolean forSync) {
        set.addInt("charge", this.chargeTime);
        set.addBoolean("right", this.jumpRight);
    }

    @Override
    public void load(DataSet set, boolean forSync) {
        this.chargeTime = set.getInt("charge");
        this.jumpRight = set.getBoolean("right");
    }
}
