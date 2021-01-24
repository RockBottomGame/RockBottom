package de.ellpeck.rockbottom.world.entity.ai;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.ai.AITask;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.world.entity.SlimeEntity;

public class SlimeTargetTask extends AITask<SlimeEntity> {

    private final double maxDistance;
    private final double attackDistance;
    public AbstractPlayerEntity target;
    public boolean isClose;

    public SlimeTargetTask(int priority, double maxDistance, double attackDistance) {
        super(priority);
        this.maxDistance = maxDistance;
        this.attackDistance = attackDistance;
    }

    @Override
    public boolean shouldStartExecution(SlimeEntity entity) {
        if (!entity.world.isClient()) {
            double x = entity.getX();
            double y = entity.getY();

            if (this.target == null) {
                AbstractPlayerEntity player = entity.world.getClosestPlayer(x, y);
                return player != null && !player.isDead() && Util.distanceSq(player.getX(), player.getY(), x, y) <= this.maxDistance * this.maxDistance;
            } else {
                return this.target.isDead() || Util.distanceSq(this.target.getX(), this.target.getY(), x, y) > this.maxDistance * this.maxDistance;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean shouldEndExecution(SlimeEntity entity) {
        return true;
    }

    @Override
    public void onExecutionStarted(AITask<SlimeEntity> previousTask, SlimeEntity entity) {
        double x = entity.getX();
        double y = entity.getY();

        if (!entity.world.isClient()) {
            this.target = entity.world.getClosestPlayer(x, y);
        }

        if (this.target != null) {
            if (!this.target.isDead()) {
                double dist = Util.distanceSq(this.target.getX(), this.target.getY(), x, y);

                if (dist <= this.maxDistance * this.maxDistance) {
                    this.isClose = dist <= this.attackDistance * this.attackDistance;
                } else {
                    this.target = null;
                    this.isClose = false;
                }
            } else {
                this.target = null;
            }
        }
    }

    @Override
    public void execute(IGameInstance game, SlimeEntity entity) {
    }

    @Override
    public AITask getNextTask(AITask<SlimeEntity> expectedNextTask, SlimeEntity entity) {
        return this.target != null ? entity.jumpTask : expectedNextTask;
    }

    @Override
    public void save(DataSet set, boolean forSync, SlimeEntity entity) {
        if (forSync && this.target != null) {
            set.addUniqueId("target", this.target.getUniqueId());
        }
    }

    @Override
    public void load(DataSet set, boolean forSync, SlimeEntity entity) {
        if (forSync && set.hasKey("target")) {
            Entity player = entity.world.getEntity(set.getUniqueId("target"));
            if (player instanceof AbstractPlayerEntity) {
                this.target = (AbstractPlayerEntity) player;
            }
        }
    }
}
