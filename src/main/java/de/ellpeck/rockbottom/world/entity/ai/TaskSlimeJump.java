package de.ellpeck.rockbottom.world.entity.ai;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.ai.AITask;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.world.entity.EntitySlime;

public class TaskSlimeJump extends AITask<EntitySlime>{

    public long jumpStartTime;
    public int chargeTime;

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
        //TODO Somehow sync the start of the execution with the client rather than making the client execute the same AI code
        //Maybe have a method in AITask called isSynced, making a packet be sent every time a new AI task starts?
        this.chargeTime = 20;
        this.jumpStartTime = Util.getTimeMillis();
    }

    @Override
    public void execute(IGameInstance game, EntitySlime entity){
        this.chargeTime--;
        if(this.chargeTime <= 0){
            if(entity.jump(0.3D)){
                //TODO Make them move.. randomly?
            }
        }
    }
}
