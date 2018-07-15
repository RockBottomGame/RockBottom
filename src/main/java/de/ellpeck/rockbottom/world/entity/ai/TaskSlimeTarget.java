package de.ellpeck.rockbottom.world.entity.ai;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.ai.AITask;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.world.entity.EntitySlime;

public class TaskSlimeTarget extends AITask<EntitySlime> {

    private final double maxDistance;
    private final double attackDistance;
    public AbstractEntityPlayer target;
    public boolean isClose;

    public TaskSlimeTarget(int priority, double maxDistance, double attackDistance) {
        super(priority);
        this.maxDistance = maxDistance;
        this.attackDistance = attackDistance;
    }

    @Override
    public boolean shouldStartExecution(EntitySlime entity) {
        if (!entity.world.isClient()) {
            double x = entity.getX();
            double y = entity.getY();

            if (this.target == null) {
                AbstractEntityPlayer player = entity.world.getClosestPlayer(x, y);
                return player != null && !player.isDead() && Util.distanceSq(player.getX(), player.getY(), x, y) <= this.maxDistance * this.maxDistance;
            } else {
                return this.target.isDead() || Util.distanceSq(this.target.getX(), this.target.getY(), x, y) > this.maxDistance * this.maxDistance;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean shouldEndExecution(EntitySlime entity) {
        return true;
    }

    @Override
    public void onExecutionStarted(AITask<EntitySlime> previousTask, EntitySlime entity) {
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
    public void execute(IGameInstance game, EntitySlime entity) {
    }

    @Override
    public AITask getNextTask(AITask<EntitySlime> expectedNextTask, EntitySlime entity) {
        return this.target != null ? entity.jumpTask : expectedNextTask;
    }

    @Override
    public void save(DataSet set, boolean forSync, EntitySlime entity) {
        if (forSync && this.target != null) {
            set.addUniqueId("target", this.target.getUniqueId());
        }
    }

    @Override
    public void load(DataSet set, boolean forSync, EntitySlime entity) {
        if (forSync && set.hasKey("target")) {
            Entity player = entity.world.getEntity(set.getUniqueId("target"));
            if (player instanceof AbstractEntityPlayer) {
                this.target = (AbstractEntityPlayer) player;
            }
        }
    }
}
