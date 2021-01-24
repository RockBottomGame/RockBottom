package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.ai.AITask;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class AITaskPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("ai_task");

    private UUID entityId;
    private DataSet data;
    private int taskId;

    public AITaskPacket(UUID entityId, DataSet data, int taskId) {
        this.entityId = entityId;
        this.data = data;
        this.taskId = taskId;
    }

    public AITaskPacket() {
    }

    public static void setNewTask(Entity entity, AITask currTask, AITask newTask) {
        if (currTask != null) {
            currTask.onExecutionEnded(newTask, entity);
        }
        entity.currentAiTask = newTask;
        if (newTask != null) {
            newTask.onExecutionStarted(currTask, entity);
        }
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.entityId.getMostSignificantBits());
        buf.writeLong(this.entityId.getLeastSignificantBits());
        buf.writeInt(this.taskId);
        NetUtil.writeSetToBuffer(this.data, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.entityId = new UUID(buf.readLong(), buf.readLong());
        this.taskId = buf.readInt();
        this.data = new DataSet();
        NetUtil.readSetFromBuffer(this.data, buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            Entity entity = world.getEntity(this.entityId);
            if (entity != null) {
                AITask task = entity.getTask(this.taskId);
                if (task != null) {
                    task.load(this.data, true, entity);
                }

                if (task == null || task.shouldStartExecution(entity)) {
                    setNewTask(entity, entity.currentAiTask, task);
                }
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
