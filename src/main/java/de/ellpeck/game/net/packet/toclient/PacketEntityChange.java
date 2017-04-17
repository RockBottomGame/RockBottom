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
        this.uniqueId = entity.getUniqueId();

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
    }

    public PacketEntityChange(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.uniqueId.getMostSignificantBits());
        buf.writeLong(this.uniqueId.getLeastSignificantBits());
        buf.writeBoolean(this.remove);

        if(!this.remove){
            buf.writeInt(this.id);
            NetUtil.writeSetToBuffer(this.entitySet, buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.uniqueId = new UUID(buf.readLong(), buf.readLong());
        this.remove = buf.readBoolean();

        if(!this.remove){
            this.id = buf.readInt();
            NetUtil.readSetFromBuffer(this.entitySet, buf);
        }
    }

    @Override
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                Entity entity = game.world.getEntity(this.uniqueId);

                if(this.remove){
                    if(entity != null){
                        game.world.removeEntity(entity);
                    }
                }
                else{
                    if(entity == null){
                        if(this.id == -2){
                            entity = new EntityPlayer(game.world);
                        }
                        else{
                            entity = Entity.create(this.id, game.world);
                        }

                        if(entity != null){
                            entity.load(this.entitySet);
                            game.world.addEntity(entity);
                        }
                    }
                    else{
                        entity.load(this.entitySet);
                    }
                }
                return true;
            }
            else{
                return false;
            }
        });
    }
}
