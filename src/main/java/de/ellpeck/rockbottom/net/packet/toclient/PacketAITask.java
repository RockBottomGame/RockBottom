package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.ai.AITask;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketAITask implements IPacket{

    private UUID entityId;
    private int taskId;

    public PacketAITask(UUID entityId, int taskId){
        this.entityId = entityId;
        this.taskId = taskId;
    }

    public PacketAITask(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeLong(this.entityId.getMostSignificantBits());
        buf.writeLong(this.entityId.getLeastSignificantBits());
        buf.writeInt(this.taskId);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.entityId = new UUID(buf.readLong(), buf.readLong());
        this.taskId = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();
        if(world != null){
            Entity entity = world.getEntity(this.entityId);
            if(entity != null){
                AITask task = entity.getTask(this.taskId);
                if(task == null || task.shouldStartExecution(entity)){
                    setNewTask(entity, entity.currentAiTask, task);
                }
            }
        }
    }

    public static void setNewTask(Entity entity, AITask currTask, AITask newTask){
        if(currTask != null){
            newTask = currTask.getNextTask(newTask, entity);
            currTask.onExecutionEnded(newTask, entity);
        }
        entity.currentAiTask = newTask;
        if(newTask != null){
            newTask.onExecutionStarted(currTask, entity);
        }
    }
}
