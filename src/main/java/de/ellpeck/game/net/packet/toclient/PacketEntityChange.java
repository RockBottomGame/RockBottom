package de.ellpeck.game.net.packet.toclient;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.net.NetUtil;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;
import java.util.UUID;

public class PacketEntityChange implements IPacket{

    private final DataSet entitySet = new DataSet();
    private int id;

    private UUID uniqueId;
    private boolean remove;

    public PacketEntityChange(Entity entity, boolean remove){
        this.remove = remove;

        if(!this.remove){
            if(entity instanceof EntityPlayer){
                this.id = -2;
            }
            else{
                this.id = ContentRegistry.ENTITY_REGISTRY.getId(entity.getClass());
                if(this.id == -1){
                    Log.error("Entity with class "+entity.getClass()+" is being sent to the client without being registered!");
                }
            }

            entity.save(this.entitySet);
        }
        else{
            this.uniqueId = entity.getUniqueId();
        }
    }

    public PacketEntityChange(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeBoolean(this.remove);

        if(!this.remove){
            buf.writeInt(this.id);
            NetUtil.writeSetToBuffer(this.entitySet, buf);
        }
        else{
            buf.writeLong(this.uniqueId.getMostSignificantBits());
            buf.writeLong(this.uniqueId.getLeastSignificantBits());
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.remove = buf.readBoolean();

        if(!this.remove){
            this.id = buf.readInt();
            NetUtil.readSetFromBuffer(this.entitySet, buf);
        }
        else{
            this.uniqueId = new UUID(buf.readLong(), buf.readLong());
        }
    }

    @Override
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                if(this.remove){
                    Entity entity = game.world.getEntity(this.uniqueId);
                    if(entity != null){
                        game.world.removeEntity(entity);
                    }
                }
                else{
                    Entity entity;

                    if(this.id == -2){
                        entity = new EntityPlayer(game.world);
                    }else{
                        entity = Entity.create(this.id, game.world);
                    }

                    if(entity != null){
                        entity.load(this.entitySet);
                        game.world.addEntity(entity);
                    }
                }
            }
        });
    }
}
