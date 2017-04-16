package de.ellpeck.game.net.packet.toclient;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.net.NetUtil;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.world.entity.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

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
            this.id = ContentRegistry.ENTITY_REGISTRY.getId(entity.getClass());
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
                    Entity entity = Entity.create(this.id, game.world);
                    if(entity != null){
                        entity.load(this.entitySet);
                        game.world.addEntity(entity);
                    }
                }
            }
        });
    }
}
