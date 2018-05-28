package de.ellpeck.rockbottom.world.entity.ai;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.ai.AITask;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.world.entity.EntitySlime;

public class TaskSlimeJump extends AITask<EntitySlime>{

    public long jumpStartTime;
    public int chargeTime;
    private boolean jumpRight;

    public TaskSlimeJump(int priority){
        super(priority);
    }

    @Override
    public boolean shouldStartExecution(EntitySlime entity){
        return !entity.jumping && entity.jumpTimeout <= 0;
    }

    @Override
    public boolean shouldEndExecution(EntitySlime entity){
        return this.chargeTime <= 0;
    }

    @Override
    public void onExecutionStarted(AITask<EntitySlime> previousTask, EntitySlime entity){
        this.chargeTime = 20;
        this.jumpStartTime = Util.getTimeMillis();

        this.jumpRight = Util.RANDOM.nextBoolean();
        entity.facing = this.jumpRight ? Direction.RIGHT : Direction.LEFT;
    }

    @Override
    public void execute(IGameInstance game, EntitySlime entity){
        this.chargeTime--;
        if(this.chargeTime <= 0){
            if(entity.jump(0.3D)){
                if(this.jumpRight){
                    entity.motionX += 0.25D;
                }
                else{
                    entity.motionX -= 0.25D;
                }
            }
        }
    }
}
